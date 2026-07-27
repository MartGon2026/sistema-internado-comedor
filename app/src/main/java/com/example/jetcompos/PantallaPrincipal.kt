package com.example.jetcompos

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.jetcompos.conexion_DB.Repositorio.asistencia_Reposi
import com.example.jetcompos.fecha_Hora_y_TipoComida.FechaHoraUtil
import com.example.jetcompos.fecha_Hora_y_TipoComida.TipoComidaActual
import com.example.jetcompos.guardarDatosTelefono.datosEnMemoria
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import com.example.jetcompos.Escanner.PermiCamara
import android.content.Intent
import com.example.jetcompos.Escanner.EscanerQRActivity


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPrincipal(
    navController: NavController? = null,
    alCerrarSesion: () -> Unit = {}
) {
    var mostrarModalQr by remember { mutableStateOf(false) }
    var mostrarMenuUsuario by remember { mutableStateOf(false) }
    val SegundoPlano = rememberCoroutineScope()
    var  nombreUsuario by remember { mutableStateOf("") }
    var  CarnerUser by remember { mutableStateOf("") }
    var listaHistorial by remember {
        mutableStateOf<List<ModeloHistorial>>(emptyList())
    }

    val context = LocalContext.current
    val datos = datosEnMemoria.obtener(context)
     nombreUsuario = datos?.nameUser ?: "Usuario"
     CarnerUser = datos?.carnet ?:""

    val pedirPermisoCamara = PermiCamara(
        context = context,
        onPermissionGranted = {
            // Aquí abres el escáner QR
            val intent = Intent(context,EscanerQRActivity::class.java )
            context.startActivity(intent)

        },
        onPermissionDenied = {
            // Mostrar mensaje de que el usuario no aceptó el permiso

        }
    )

    // URL de la imagen de fondo (Comida)
    val painter = rememberAsyncImagePainter(
        model = "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&q=80"
    )

    Scaffold(

            floatingActionButton = {

                Row( modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween ) {
                // btono de generar qr
                FloatingActionButton(
                    onClick = {
                        mostrarModalQr = true
                     /*   SegundoPlano.launch{
                          if (!TipoComidaActual.validarHorario()){ println("fuera de horario"); return@launch}

                           val existe = asistencia_Reposi.verificarExistencia("1601050103", TipoComidaActual.obtenerTipoComida(), FechaHoraUtil.obtenerFechaActual() )

                            if (existe){
                                mostrarModalQr = true
                            } else { println("Ya consumió esta comida") }

                        }


                    */
                     },
                    containerColor = coloress.PrimaryGreen,
                    contentColor = coloress.White,
                    shape = RoundedCornerShape(50.dp),
                    modifier = Modifier.height(50.dp).padding(start = 30.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCode2,
                            contentDescription = null
                        )
                        Text(
                            text = "Generar",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )

                    }


                }
   // Segundo botón
                    FloatingActionButton(
                        onClick = {
                            // Acción del segundo botón
                            pedirPermisoCamara()
                        },
                        containerColor = coloress.rojo,
                        contentColor = coloress.White,
                        shape = RoundedCornerShape(50.dp),
                        modifier = Modifier.height(50.dp)
                    ) {Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = null
                        )
                        Text( text = "Scanner", fontWeight = FontWeight.SemiBold, fontSize = 14.sp )

                    }
                    }


                }

            }


        ) { padding ->
            Box(
                modifier = Modifier.fillMaxSize() .background(coloress.FondoGris).padding(padding)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        mostrarMenuUsuario = false
                    }
            ) {

                // HEADER
                Box( modifier = Modifier.fillMaxWidth().height(220.dp)  ) {

                    Image( painter = painter, contentDescription = "Fondo",
                        contentScale = ContentScale.Crop,    modifier = Modifier.fillMaxSize()
                    )

                    Box(
                        modifier = Modifier.fillMaxSize().background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        coloress.PrimaryGreen.copy(alpha = 0.85f),
                                        coloress.PrimaryGreen.copy(alpha = 0.95f)
                                    )
                                )
                            )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(top = 60.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {

                        Column {
                            Text(text = "¡Hola, $nombreUsuario!",color = coloress.White, fontSize = 22.sp,  fontWeight = FontWeight.Bold )

                            Text(text = "Bienvenido de nuevo",color = coloress.White.copy(alpha = 0.9f), fontSize = 14.sp )
                        }

                        Box {
                            Box( modifier = Modifier.size(45.dp).background(
                                coloress.White.copy(alpha = 0.2f), CircleShape
                                ).clickable { mostrarMenuUsuario = true }, // Abre el menú al tocar
                                contentAlignment = Alignment.Center

                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = coloress.White
                                )
                            }

                            // El menú desplegable en sí
                            DropdownMenu(
                                expanded = mostrarMenuUsuario,
                                onDismissRequest = { mostrarMenuUsuario = false }, // Cierra si tocas fuera
                                modifier = Modifier.background(Color.White)
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Configuración", color= coloress.PrimaryGreen) },
                                    onClick = {
                                        mostrarMenuUsuario = false

                                        // Aquí se "nexa": Navega a la pantalla de configuración
                                        navController?.navigate("configuracion")
                                    },
                                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
                                )
                                DropdownMenuItem(
                                    text = { Text("Cerrar Sesión", color = Color.Red) },
                                    onClick = {
                                        mostrarMenuUsuario = false
                                        alCerrarSesion()
                                    }
                                )
                            }
                        }}}

                // CONTENIDO
                Column( modifier = Modifier.fillMaxSize().padding(top = 170.dp).padding(horizontal = 20.dp) ) {

                    // TARJETA ESTADO ACTUAL
                    Card(
                        colors = CardDefaults.cardColors( containerColor = coloress.White ),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(  defaultElevation = 8.dp ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Column( modifier = Modifier.weight(1f) ) {

                                Text( text = "Estado Actual",  fontWeight = FontWeight.Bold,fontSize = 18.sp, color = coloress.TextoPrincipal  )

                                Spacer( modifier = Modifier.height(6.dp) )

                                Text( text = "Tu alimentación está activa para el día de hoy.", fontSize = 13.sp, color = coloress.TextoSecundario )
                            }

                            Box( modifier = Modifier.background( coloress.VerdeClaro,RoundedCornerShape(20.dp) ).padding(horizontal = 14.dp, vertical = 8.dp )  )
                            {
                                Text(  text = "ACTIVO", color = coloress.PrimaryGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(25.dp))

                    Text( text = "Historial",   color = coloress.TextoPrincipal,   fontWeight = FontWeight.Bold,
                        fontSize = 18.sp, modifier = Modifier.padding(bottom = 15.dp) )
/// mostra el historial
                    LaunchedEffect(CarnerUser) {

                        listaHistorial = RepositorioHistorial.obtenerListaHistorial(   CarnerUser,  FechaHoraUtil.obtenerFechaActual() )
                    }
                    Column( verticalArrangement = Arrangement.spacedBy(0.dp) ) {
                        listaHistorial.forEach { registro ->
                            ItemHistorial(registro = registro)
                        }
                    }

                    Spacer(modifier = Modifier.height(80.dp))
                }
            }


    }

    generadorQR(
        mostrar = mostrarModalQr,
        alCerrar = {
            println("ANTES: $mostrarModalQr")
            mostrarModalQr = false
            println("DESPUES: $mostrarModalQr")

            SegundoPlano.launch {
                listaHistorial = RepositorioHistorial.obtenerListaHistorial(CarnerUser, FechaHoraUtil.obtenerFechaActual()  )
            }
        }
    )
}


@Preview(showBackground = true)
@Composable
fun VistaPreviaPantallaPrincipal() {
    PantallaPrincipal()
}