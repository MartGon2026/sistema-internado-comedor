package com.example.jetcompos

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.jetcompos.Escanner.EscanerQRActivity
import com.example.jetcompos.Escanner.PermiCamara
import com.example.jetcompos.conexion_DB.Repositorio.ObtenerFotoPerfil
import com.example.jetcompos.conexion_DB.coloress
import com.example.jetcompos.fecha_Hora_y_TipoComida.FechaHoraUtil
import com.example.jetcompos.guardarDatosTelefono.datosEnMemoria
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPrincipal(
    alCerrarSesion: () -> Unit = {}
) {
    var mostrarModalQr by remember { mutableStateOf(false) }
    var verConfiguracion by remember { mutableStateOf(false) }
    val segundoPlano = rememberCoroutineScope()
    var nombreUsuario by remember { mutableStateOf("") }
    var carnerUser by remember { mutableStateOf("") }
    var listaHistorial by remember { mutableStateOf<List<ModeloHistorial>>(emptyList()) }
    var selectedTab by remember { mutableStateOf(0) }
    var fotoBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val context = LocalContext.current
    val datos = remember { datosEnMemoria.obtener(context) }
    nombreUsuario = datos?.nameUser ?: "Usuario"
    carnerUser = datos?.carnet ?: ""

    LaunchedEffect(carnerUser) {
        if (carnerUser.isNotEmpty()) {
            val bytes = withContext(Dispatchers.IO) {
                ObtenerFotoPerfil(carnerUser)
            }
            if (bytes != null && bytes.isNotEmpty()) {
                fotoBitmap = withContext(Dispatchers.IO) {
                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                }
            }
        }
    }

    if (verConfiguracion) {
        // PANTALLA DE CONFIGURACIÓN (Cubre TODO, incluyendo la barra inferior)
        Configuracion(
            usuario = DatosUsuarios(
                nombre = datos?.nameUser ?: "",
                Apellido = "",
                carnet = datos?.carnet ?: "",
                carrera = "",
                edad = 0,
                usuario = datos?.usuario ?: "",
                carreraAno = ""
            ),
            alVolver = { verConfiguracion = false }
        )
    } else {
        val pedirPermisoCamara = PermiCamara(
            context = context,
            onPermissionGranted = {
                val intent = Intent(context, EscanerQRActivity::class.java)
                context.startActivity(intent)
            },
            onPermissionDenied = {}
        )

        // Gradientes
        val gradienteHeader = Brush.verticalGradient(
            colors = listOf(Color(0xFF0033CC), Color(0xFF001A66))
        )
        val gradienteAzulBoton = Brush.horizontalGradient(
            colors = listOf(Color(0xFF0052D4), Color(0xFF4364F7))
        )
        val gradienteVerdeBoton = Brush.horizontalGradient(
            colors = listOf(Color(0xFF00B09B), Color(0xFF96C93D))
        )

        Scaffold(
            bottomBar = {
                val navigationBarItemColors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF0052D4),
                    selectedTextColor = Color(0xFF0052D4),
                    indicatorColor = Color(0xFFE8F0FE),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
                NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        label = { Text("Inicio") },
                        colors = navigationBarItemColors
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = { Icon(Icons.Default.History, contentDescription = null) },
                        label = { Text("Historial") },
                        colors = navigationBarItemColors
                    )
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        icon = { Icon(Icons.Default.Person, contentDescription = null) },
                        label = { Text("Perfil") },
                        colors = navigationBarItemColors
                    )
                }
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = padding.calculateBottomPadding())
            ) {
                if (selectedTab == 2) {
                    // PANTALLA DE PERFIL (Cubre todo el teléfono menos la barra inferior)
                    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF3F4F6))) {
                        PantallaPerfilContenido(
                            alIrAConfiguracion = { verConfiguracion = true },
                            alCerrarSesion = alCerrarSesion
                        )
                    }
                } else {
                    // PANTALLA DE INICIO E HISTORIAL (Con el diseño de Header Azul)
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(gradienteHeader)
                    ) {
                        // HEADER
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 40.dp, start = 25.dp, end = 25.dp, bottom = 30.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "Hola, ", color = Color.White.copy(alpha = 0.8f), fontSize = 18.sp)
                                Text(text = nombreUsuario, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                                Text(text = "Bienvenido de nuevo", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                            }

                            Box {
                                Box(
                                    modifier = Modifier
                                        .size(90.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (fotoBitmap != null) {
                                        Image(
                                            bitmap = fotoBitmap!!.asImageBitmap(),
                                            contentDescription = "Perfil",
                                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = "Sin Foto",
                                            tint = Color.White,
                                            modifier = Modifier.size(50.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // CONTENEDOR BLANCO
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                                .background(Color.White)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .verticalScroll(rememberScrollState())
                                    .padding(25.dp)
                            ) {
                                when (selectedTab) {
                                    0 -> {
                                        // Tarjeta Escanear
                                        TarjetaAccion(
                                            icono = Icons.Default.Restaurant,
                                            fondoIcono = Color(0xFFE8F0FE),
                                            tinteIcono = Color(0xFF1A73E8),
                                            titulo = "Escanear comedor",
                                            descripcion = "Escanea el QR fijo del comedor para registrar tu entrada.",
                                            textoBoton = "Escanear ahora",
                                            gradienteBoton = gradienteAzulBoton,
                                            iconoBoton = Icons.Default.QrCodeScanner,
                                            alHacerClick = { pedirPermisoCamara() }
                                        )

                                        Spacer(modifier = Modifier.height(25.dp))

                                        // Tarjeta Mi QR
                                        TarjetaAccion(
                                            icono = Icons.Default.QrCode2,
                                            fondoIcono = Color(0xFFE6F4EA),
                                            tinteIcono = Color(0xFF1E8E3E),
                                            titulo = "Mi código QR",
                                            descripcion = "Muestra tu código al guardia para registrar tu salida o entrada.",
                                            textoBoton = "Mostrar mi QR",
                                            gradienteBoton = gradienteVerdeBoton,
                                            iconoBoton = Icons.Default.QrCode2,
                                            alHacerClick = { mostrarModalQr = true }
                                        )
                                    }
                                    1 -> {
                                        Text(
                                            text = "Historial",
                                            color = Color(0xFF1F2937),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 20.sp,
                                            modifier = Modifier.padding(bottom = 15.dp)
                                        )

                                        LaunchedEffect(carnerUser) {
                                            listaHistorial = RepositorioHistorial.obtenerListaHistorial(
                                                carnerUser,
                                                FechaHoraUtil.obtenerFechaActual()
                                            )
                                        }

                                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                            listaHistorial.forEach { registro ->
                                                ItemHistorial(registro = registro)
                                            }
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(30.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    generadorQR(
        mostrar = mostrarModalQr,
        alCerrar = {
            mostrarModalQr = false
            segundoPlano.launch {
                listaHistorial = RepositorioHistorial.obtenerListaHistorial(
                    carnerUser,
                    FechaHoraUtil.obtenerFechaActual()
                )
            }
        }
    )
}

@Composable
fun TarjetaAccion(
    icono: ImageVector,
    fondoIcono: Color,
    tinteIcono: Color,
    titulo: String,
    descripcion: String,
    textoBoton: String,
    gradienteBoton: Brush,
    iconoBoton: ImageVector,
    alHacerClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(25.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color(0xFFF0F0F0))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(fondoIcono),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icono,
                        contentDescription = null,
                        tint = tinteIcono,
                        modifier = Modifier.size(30.dp)
                    )
                }
                Spacer(modifier = Modifier.width(15.dp))
                Column {
                    Text(
                        text = titulo,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF1F2937)
                    )
                    Text(
                        text = descripcion,
                        fontSize = 13.sp,
                        color = Color(0xFF6B7280),
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = alHacerClick,
                modifier = Modifier.fillMaxWidth().height(55.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(15.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize() .background(gradienteBoton),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = iconoBoton,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text( text = textoBoton,fontWeight = FontWeight.Bold,  fontSize = 16.sp,   color = Color.White   )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VistaPreviaPantallaPrincipal() {
    PantallaPrincipal()
}
