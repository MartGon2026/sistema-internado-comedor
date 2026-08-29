package com.example.jetcompos

import android.R
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetcompos.conexion_DB.Repositorio.ActualizarFotoPerfil
//import com.example.jetcompos.conexion_DB.Repositorio.ActualizarFotoPerfil
import com.example.jetcompos.conexion_DB.Repositorio.Usuario_R
import com.example.jetcompos.conexion_DB.Repositorio.estudiante_Reposi
import com.example.jetcompos.guardarDatosTelefono.datosEnMemoria
import com.example.jetcompos.mensajeria.Mensajeria
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Modelo de datos global estructurado
data class DatosUsuarios(

    val nombre: String,
    val Apellido: String,
    val carnet: String,
    val carrera: String,
    val edad: Int,
    var usuario: String,
    val carreraAno: String,


)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Configuracion( usuario: DatosUsuarios, alVolver: () -> Unit ) {
    // Estado reflejado en la pantalla principal
    var usuarioVisual by remember { mutableStateOf(usuario.usuario) }

    // Estados para el nuevo modal de cambiar usuario
    var mostrarModalUsuario by remember { mutableStateOf(false) }
    var usuarioTemporalBorrador by remember { mutableStateOf("") }

    // Estados para el modal de contraseña
    var mostrarModalPass by remember { mutableStateOf(false) }
    var passActual by remember { mutableStateOf("") }
    var passNueva by remember { mutableStateOf("") }
    var passConfirmar by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf("") }
    var CartnetUsuario by remember { mutableStateOf("") }
    val segundoPlano = rememberCoroutineScope()



    var UserDatosobtenidos by remember { mutableStateOf<DatosUsuarios?>(null) }

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        val datos = datosEnMemoria.obtener(context)
        CartnetUsuario = datos?.carnet ?: "carnet del estudiante"


        val estudiante = estudiante_Reposi.obtenerDatosEstudiante(CartnetUsuario)

        UserDatosobtenidos = estudiante?.let{
            DatosUsuarios(
                nombre = it.nombres,
                Apellido = it.apellidos,
                carnet = it.carnet,
                carrera = it.carrera,
                edad = it.edad_estudi,
                usuario = datos?.usuario ?: "",
                carreraAno = it.anoCarrera,


            )
        }

    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text( text = "Configuración",  fontWeight = FontWeight.SemiBold, letterSpacing = 0.5.sp )
                },
                navigationIcon = {
                    IconButton(onClick = alVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = coloress.PrimaryGreen,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(coloress.FondoGris)
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

// llamamos al selector de imagnes
         SeleccionarImagen(CartnetUsuario, onFotoConvertida = { bytesObtenidos ->

                segundoPlano.launch {
                    println("este es el bittes $bytesObtenidos")
                    val exito = ActualizarFotoPerfil(CartnetUsuario, bytesObtenidos)

                    if (exito) {
                        println("¡Foto guardada en la base de datos de forma veloz!")
                    } else {
                        println("Hubo un error al subir la foto.")
                    }
                }
            })
/// informacion del perfil
            Spacer(modifier = Modifier.height(16.dp))

            val primerNombre = sacarDatoNombre(UserDatosobtenidos?.nombre ?: "")
            val primerApellido = sacarDatoNombre(UserDatosobtenidos?.Apellido ?: "")
            val nombreApellido = (UserDatosobtenidos?.nombre ?: "") + " " + (UserDatosobtenidos?.Apellido ?: "")

            Text(text = "$primerNombre $primerApellido",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = coloress.TextoPrincipal
            )

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = coloress.PrimaryGreen.copy(alpha = 0.08f),
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Text( text = "Estudiante", color = coloress.PrimaryGreen, fontWeight = FontWeight.Medium,
                    fontSize = 13.sp, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // SECCIÓN: Información Académica (Campos de Lectura Plana)
            Text( text = "Información Académica", color = coloress.TextoSecundario, fontSize = 13.sp,
                fontWeight = FontWeight.Bold, modifier = Modifier
                    .align(Alignment.Start)
                    .padding(start = 4.dp, bottom = 8.dp)
            )

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ItemFilaConfiguracion(label = "Nombre Completo", valor = nombreApellido)
                    HorizontalDivider(color = coloress.FondoGris, modifier = Modifier.padding(vertical = 10.dp))
                    ItemFilaConfiguracion(label = "Carnet Universitario", valor = UserDatosobtenidos?.carnet ?:"")
                    HorizontalDivider(color = coloress.FondoGris, modifier = Modifier.padding(vertical = 10.dp))
                    ItemFilaConfiguracion(label = "Carrera", valor = UserDatosobtenidos?.carrera ?:"")
                    HorizontalDivider(color = coloress.FondoGris, modifier = Modifier.padding(vertical = 10.dp))
                    ItemFilaConfiguracion(label = "Año Académico", valor = UserDatosobtenidos?.carreraAno ?:"")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // SECCIÓN: Credenciales de Acceso Modificables por Diálogo
            Text(text = "Credenciales de Acceso",  color = coloress.TextoSecundario, fontSize = 13.sp,
                fontWeight = FontWeight.Bold, modifier = Modifier
                    .align(Alignment.Start)
                    .padding(start = 4.dp, bottom = 8.dp)
            )

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

             // Fila de Nombre de Usuario (Bloqueado con botón para accionar emergencia)
                    Text("Nombre de Usuario", color = coloress.TextoSecundario, fontWeight = FontWeight.Medium, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = UserDatosobtenidos?.usuario ?: "",
                            onValueChange = {},
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            enabled = false,
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = coloress.TextoSecundario) },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = coloress.TextoPrincipal,
                                disabledContainerColor = coloress.FondoGris.copy(alpha = 0.4f),
                                disabledBorderColor = Color.Transparent
                            )
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        IconButton(
                            onClick = {
                                // Cargamos el valor vigente actual en el borrador temporal antes de abrir
                                usuarioTemporalBorrador = usuarioVisual
                                mostrarModalUsuario = true
                            },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(coloress.VerdeClaro)
                                .size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Cambiar Usuario",
                                tint = coloress.PrimaryGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Fila de Contraseña
                    Text("Contraseña", color = coloress.TextoSecundario, fontWeight = FontWeight.Medium, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = "••••••••",
                            onValueChange = {},
                            modifier = Modifier.weight(1f),
                            enabled = false,
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = coloress.TextoSecundario) },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = coloress.TextoPrincipal,
                                disabledContainerColor = coloress.FondoGris.copy(alpha = 0.4f),
                                disabledBorderColor = Color.Transparent
                            ),
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        IconButton(
                            onClick = {
                                mostrarModalPass = true
                                passActual = ""
                                passNueva = ""
                                passConfirmar = ""
                                mensajeError = ""
                            },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(coloress.VerdeClaro)
                                .size(48.dp)
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = "Cambiar Contraseña", tint = coloress.PrimaryGreen, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botón de guardado definitivo
            Button(
                onClick = {
                    usuario.usuario = usuarioVisual
                    alVolver()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = coloress.PrimaryGreen)
            ) {
                Text( text = "Guardar Cambios", color = coloress.White, fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold, letterSpacing = 0.5.sp
                )
            }
        }
    }

    // NOU MODAL: DIÁLOGO EMERGENTE PARA EDITAR NOMBRE DE USUARIO
    if (mostrarModalUsuario) {
        AlertDialog(
            onDismissRequest = { mostrarModalUsuario = false },
            title = { Text("Modificar Usuario", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White,
            text = {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Text(
                        text = "Ingresa tu nuevo nombre de acceso institucional.",  color = coloress.TextoSecundario,
                        fontSize = 14.sp, modifier = Modifier.padding(bottom = 16.dp)
                    )
                    OutlinedTextField(
                        value = usuarioTemporalBorrador,
                        onValueChange = { usuarioTemporalBorrador = it },
                        label = { Text("Nombre de Usuario") },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = coloress.PrimaryGreen) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors =  misColoresCampos()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Aplica el cambio y lo refleja directamente en el campo de texto de atrás
                        usuarioVisual = usuarioTemporalBorrador.trim()
                        mostrarModalUsuario = false
                    },
                    // Deshabilitar botón si el input queda completamente vacío
                    enabled = usuarioTemporalBorrador.isNotBlank(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = coloress.PrimaryGreen)
                ) {
                    Text("Cambiar", color = coloress.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarModalUsuario = false }) {
                    Text("Cancelar", color = coloress.rojo)
                }
            }
        )
    }

    // DIÁLOGO DE CONTRASEÑA ESTILIZADO
    if (mostrarModalPass) {
        AlertDialog(
            onDismissRequest = { mostrarModalPass = false },
            title = { Text("Cambiar Contraseña", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = coloress.PrimaryGreen) },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White,
            text = {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    if (mensajeError.isNotEmpty()) {
                        Surface(
                            color = Color.Red.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        ) {
                            Text(
                                text = mensajeError,  color = Color.Red,
                                fontSize = 13.sp, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }

                    OutlinedTextField(
                        value = passActual,
                        onValueChange = { passActual = it },
                        label = { Text("Contraseña Actual") },
                       visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = misColoresCampos()



                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = passNueva,
                        onValueChange = { passNueva = it },
                        label = { Text("Nueva Contraseña") },
                       // visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),

                        colors = misColoresCampos()


                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = passConfirmar,
                        onValueChange = { passConfirmar = it },
                        label = { Text("Confirmar Nueva Contraseña") },
                       // visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = misColoresCampos()
                    )
                }
            },

            // validar contraseña nuevas es

            confirmButton = {

                Button(
                    onClick = {

                            segundoPlano.launch {

                                validarCambioPassword( CartnetUsuario.trim(), passActual.trim(), passNueva.trim(), passConfirmar.trim(),
                                    onError = { mensajeError = it },
                                    onSuccess = {
                                        mostrarModalPass = false
                                        Mensajeria.confirmar( "¿Deseas actualizar la contraseña?",
                                            confi = {
                                                segundoPlano.launch {
                                                    val actualizado = Usuario_R.ActualizarPassword(CartnetUsuario.trim(), passNueva.trim())
                                                    if (actualizado) {
                                                        mostrarModalPass = false
                                                        Mensajeria.exito("Contraseña actualizada")
                                                    } else {
                                                        mostrarModalPass = false
                                                        Mensajeria.error("No se pudo actualizar la conreaseña\nVuelva intentarlo")
                                                    }

                                                }

                                            },
                                            cancelar = {       }
                                        )





                                    }
                                )


                        }


                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = coloress.PrimaryGreen)
                ) { Text("Confirmar", color= coloress.White) }
            },
            dismissButton = {
                TextButton(onClick = { mostrarModalPass = false }) {
                    Text("Cancelar", color = coloress.rojo)
                }
            }
        )
    }



}

// Al final de tu archivo, totalmente fuera de la función Configuracion:
fun sacarDatoNombre(texto: String): String {
    if (texto.isBlank()) return ""
    val partes = texto.trim().split("\\s+".toRegex())
    return partes.getOrElse(0) { "" }
}

@Composable
fun misColoresCampos() = OutlinedTextFieldDefaults.colors(
    // Color de las letras que el usuario escribe cuando está posicionado en el campo
    focusedTextColor = coloress.TextoPrincipal,

    // Color del texto escrito cuando el usuario pasa a otra casilla (pierde el foco)
    unfocusedTextColor = coloress.TextoPrincipal,

    // Color del texto de la etiqueta (Label) cuando sube porque la casilla está seleccionada
    focusedLabelColor = coloress.PrimaryGreen,

    // Color de la etiqueta cuando vuelve abajo a su estado normal (casilla vacía y sin seleccionar)
    unfocusedLabelColor = coloress.GrisClaro,

    // Color de la línea del borde del cuadro cuando el usuario está escribiendo en él
    focusedBorderColor = coloress.PrimaryGreen,

    // Color de la línea del borde del cuadro cuando la casilla no está seleccionada
    unfocusedBorderColor = coloress.TextoSecundario
)

suspend fun validarCambioPassword(Carnet: String, passActual: String, passNueva: String,  passConfirmar: String,
    onError: (String) -> Unit,  onSuccess: (String) -> Unit ) {

    val passwordBD  = Usuario_R.VerificarPassword(Carnet,passActual,passNueva, passConfirmar)

    if (passwordBD == null){
        onSuccess("Contraseña actualizada correctamente")

    } else{
        onError(passwordBD)
    }


}

@Composable
fun ItemFilaConfiguracion(label: String, valor: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label, color = coloress.TextoSecundario,fontSize = 11.sp,
            fontWeight = FontWeight.Medium,letterSpacing = 0.3.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = valor, color = coloress.TextoPrincipal,
            fontSize = 15.sp, fontWeight = FontWeight.Normal
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ConfiguracionModernaPreview() {
    Configuracion(
        usuario = DatosUsuarios(
            nombre= "martin ismael",
            Apellido = "Gonzalez Orozco",
            carnet = "2021001",
            carrera = "Ingeniería",
            edad = 20,
            usuario = "martin123",
            carreraAno = "3er Año"
        ),
        alVolver = {}
    )
}