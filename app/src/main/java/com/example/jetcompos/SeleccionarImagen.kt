package com.example.jetcompos

import android.net.Uri
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.jetcompos.conexion_DB.Repositorio.ObtenerFotoPerfil
import com.example.jetcompos.conexion_DB.Repositorio.Usuario_R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
@Composable
fun SeleccionarImagen(CarnetUser: String, onFotoConvertida: (String) -> Unit) {
    var imagenPerfilFinal by remember { mutableStateOf<Uri?>(null) }
    var imagenTemporal by remember { mutableStateOf<Uri?>(null) }
    var mostrarDialogoPrevisualizacion by remember { mutableStateOf(false) }

    // Aquí guardamos el Bitmap transformado listo para pintar
    var fotoBitmapDB by remember { mutableStateOf<Bitmap?>(null) }

    var isSaving by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Carga inicial de la foto de la base de datos
    // Carga inicial de la foto de la base de datos (¡AHORA ULTRA RÁPIDA!)
    LaunchedEffect(CarnetUser) {
        scope.launch {
            try {
                // 1. Descargamos los bytes en segundo plano
                val bytes = ObtenerFotoPerfil(CarnetUser)

                if (bytes != null && bytes.isNotEmpty()) {
                    // 2. Forzamos a que la conversión pesada a Bitmap se haga en el hilo IO (segundo plano)
                    // Esto evita que la interfaz de usuario (UI) se trabe o vaya lento
                    val bitmapOptimizado = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    }

                    // 3. Pasamos el resultado a la UI ya masticado y procesado
                    if (bitmapOptimizado != null) {
                        fotoBitmapDB = bitmapOptimizado
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val abrirGaleria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            imagenTemporal = uri
            mostrarDialogoPrevisualizacion = true
        }
    }

    Box(
        modifier = Modifier
            .size(130.dp)
            .clickable { abrirGaleria.launch("image/*") },
        contentAlignment = Alignment.BottomEnd
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(coloress.GrayBg),
            contentAlignment = Alignment.Center
        ) {
            if (imagenPerfilFinal != null) {
                // 1. Prioridad: Mostrar la nueva foto local recién seleccionada
                AsyncImage(
                    model = imagenPerfilFinal,
                    contentDescription = "Foto de perfil",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else if (fotoBitmapDB != null) {
                // 2. Segunda opción: Si no hay cambio local pero sí hay foto guardada en base de datos
                AsyncImage(
                    model = fotoBitmapDB,
                    contentDescription = "Foto desde Base de datos",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                // 3. Por defecto: Si la base de datos vino vacía
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Perfil vacío",
                    tint = coloress.TextSub,
                    modifier = Modifier.size(100.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(coloress.PrimaryGreen)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PhotoCamera,
                contentDescription = "Editar foto",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }

    if (mostrarDialogoPrevisualizacion && imagenTemporal != null) {
        AlertDialog(
            onDismissRequest = {
                if (!isSaving) mostrarDialogoPrevisualizacion = false
            },
            title = {
                Text(
                    text = if (isSaving) "Guardando foto de perfil..." else "¿Cómo se ve tu nueva foto?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(10.dp))

                    // REEMPLÁZALO POR ESTE BLOQUE CORREGIDO:
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                color = coloress.PrimaryGreen,
                                modifier = Modifier.size(50.dp)
                            )
                        } else {
                            AsyncImage(
                                model = imagenTemporal,
                                contentDescription = "Previsualización redonda", // <- Corregido
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    Text(
                        text = if (isSaving) "Por favor, espera un momento mientras se suben los cambios." else "Así es como los demás te verán en el Comedor Universitario.",
                        fontSize = 13.sp,
                        color = coloress.TextSub,
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                if (!isSaving) {
                    Button(
                        onClick = {
                            isSaving = true // 1. Activamos el estado de "Guardando..." en la UI
                            scope.launch {
                                try {
                                    val inputStream = context.contentResolver.openInputStream(imagenTemporal!!)
                                    val bitmapOriginal = BitmapFactory.decodeStream(inputStream)
                                    inputStream?.close()

                                    if (bitmapOriginal != null) {
                                        // 2. RECORTE INTELIGENTE: Cortamos un cuadrado perfecto desde el centro
                                        val ancho = bitmapOriginal.width
                                        val alto = bitmapOriginal.height
                                        val tamanoCuadrado = minOf(ancho, alto)

                                        val xOffset = (ancho - tamanoCuadrado) / 2
                                        val yOffset = (alto - tamanoCuadrado) / 2

                                        // Creamos un bitmap cuadrado sin deformar la imagen
                                        val bitmapCuadrado = Bitmap.createBitmap(
                                            bitmapOriginal,
                                            xOffset,
                                            yOffset,
                                            tamanoCuadrado,
                                            tamanoCuadrado
                                        )

                                        // 3. REDUCCIÓN: Ahora sí lo bajamos a 250x250 de forma limpia
                                        val bitmapMini = Bitmap.createScaledBitmap(bitmapCuadrado, 250, 250, true)

                                        // 4. COMPRESIÓN: Formato JPEG ligero
                                        val outputStream = java.io.ByteArrayOutputStream()
                                        bitmapMini.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
                                        val bytesComprimidos = outputStream.toByteArray()

                                        // 5. CONVERSIÓN Y SUBIDA
                                        val fotoEnString = Base64.encode(bytesComprimidos)
                                        onFotoConvertida(fotoEnString)

                                        // Seteamos el bitmap para la UI local
                                        fotoBitmapDB = bitmapMini
                                    }

                                    // 6. TIEMPO DE ESPERA VISUAL: Dejamos el "Procesando..." en pantalla por 1.5 segundos
                                    // para que el usuario note el cambio y la animación corra limpia
                                    delay(1500)

                                } catch (e: Exception) {
                                    e.printStackTrace()
                                } finally {
                                    // 7. Una vez terminado el delay, cerramos todo de golpe
                                    isSaving = false
                                    imagenPerfilFinal = imagenTemporal
                                    mostrarDialogoPrevisualizacion = false
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = coloress.PrimaryGreen),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Guardar", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
            },
            dismissButton = {
                if (!isSaving) {
                    OutlinedButton(
                        onClick = {
                            imagenTemporal = null
                            mostrarDialogoPrevisualizacion = false
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Cancelar", color = Color.Red)
                    }
                }
            }
        )
    }
}