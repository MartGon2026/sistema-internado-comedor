package com.example.jetcompos

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetcompos.conexion_DB.Repositorio.ObtenerFotoPerfil
import com.example.jetcompos.guardarDatosTelefono.datosEnMemoria
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun PantallaPerfilContenido(alIrAConfiguracion: () -> Unit, alCerrarSesion: () -> Unit) {
    val context = LocalContext.current
    val datos = remember { datosEnMemoria.obtener(context) }
    var fotoBitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(datos?.carnet) {
        datos?.carnet?.let { carnet ->
            val bytes = withContext(Dispatchers.IO) {
                ObtenerFotoPerfil(carnet)
            }
            if (bytes != null && bytes.isNotEmpty()) {
                fotoBitmap = withContext(Dispatchers.IO) {
                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0033CC)) // Fondo azul para el header
    ) {
        // HEADER: Título "Mi perfil"
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(horizontal = 25.dp),
            contentAlignment = Alignment.CenterStart



            ) {
            Text(
                text = "Mi perfil",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // CUERPO BLANCO REDONDEADO
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 35.dp, topEnd = 35.dp))
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(30.dp)
            ) {
                // BLOQUE DE IDENTIDAD (Estilo Tarjeta de la Imagen)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Foto de Perfil a la izquierda
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF3F4F6)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (fotoBitmap != null) {
                            Image(
                                bitmap = fotoBitmap!!.asImageBitmap(),
                                contentDescription = "Foto de perfil",
                                modifier = Modifier.fillMaxSize().clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(50.dp),
                                tint = Color(0xFF0033CC)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(20.dp))

                    // Nombre del Estudiante a la derecha
                    Column {
                        Text(
                            text = datos?.nameUser ?: "Nombre Usuario",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1F2937)
                        )
                        Text(
                            text = datos?.rol ?: "Estudiante",
                            fontSize = 14.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // TARJETA DE OPCIONES (Configuración y Cerrar Sesión)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(30.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = BorderStroke(1.dp, Color(0xFFF0F0F0))
                ) {
                    Column {
                        ItemFilaPerfil(
                            titulo = "Ajustes de Cuenta",
                            subtitulo = "Seguridad y datos personales",
                            icono = Icons.Default.Settings,
                            colorIcono = Color(0xFF0052D4),
                            onClick = alIrAConfiguracion
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp), color = Color(0xFFF3F4F6))
                        ItemFilaPerfil(
                            titulo = "Cerrar Sesión",
                            subtitulo = "Salir de la cuenta de forma segura",
                            icono = Icons.AutoMirrored.Filled.Logout,
                            colorIcono = Color(0xFFD32F2F),
                            onClick = alCerrarSesion
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ItemFilaPerfil(
    titulo: String,
    subtitulo: String,
    icono: ImageVector,
    colorIcono: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(45.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(colorIcono.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icono, contentDescription = null, tint = colorIcono, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = titulo, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1F2937))
            Text(text = subtitulo, fontSize = 12.sp, color = Color(0xFF6B7280))
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color(0xFF9CA3AF)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPantallaPerfil() {
    PantallaPerfilContenido(
        alIrAConfiguracion = {},
        alCerrarSesion = {}
    )
}
