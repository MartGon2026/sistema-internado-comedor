package com.example.jetcompos


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource

import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import com.example.jetcompos.conexion_DB.Repositorio.Usuario_R
import com.example.jetcompos.guardarDatosTelefono.datosEnMemoria
import com.example.jetcompos.mensajeria.Mensajeria

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {

    var user by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var hayDatosGuardados by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()



    Column(
        modifier = Modifier.fillMaxSize().background(coloress.White).padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // Logo
        Box(
            modifier = Modifier.size(80.dp).background(coloress.White, RoundedCornerShape(15.dp)),
            contentAlignment = Alignment.Center
        ) {
           /* Icon(
                imageVector = Icons.Default.School,
                contentDescription = "Logo",
                tint = coloress.PrimaryGreen,
                modifier = Modifier.size(85.dp)
            )*/
          Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.size(75.dp)
          )

        }

        Spacer(modifier = Modifier.height(15.dp))

        Text(  text = "Bienvenido",  color = coloress.PrimaryGreen,  fontSize = 24.sp, fontWeight = FontWeight.Bold )
        Text(  text = "Al comedor-internado INATEC", color = coloress.TextSub, fontSize = 14.sp,  textAlign = TextAlign.Center,       modifier = Modifier.fillMaxWidth() )

        Spacer(modifier = Modifier.height(40.dp))

        // Tarjeta del Formulario
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = coloress.GrayBg), 
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(25.dp)) {

                // dodne escribe el Carnet
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text( text = " Usuario", color = coloress.TextMain, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)  )
                    OutlinedTextField(
                        value = user,
                        onValueChange = { user = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Ingrese su usuario") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = coloress.PrimaryGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        },

                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = coloress.PrimaryGreen,
                            unfocusedIndicatorColor = Color.Transparent,
                            unfocusedContainerColor = coloress.White,
                            focusedContainerColor = coloress.White,

                            focusedTextColor = coloress.PrimaryGreen,
                            unfocusedTextColor = coloress.TextMain

                        ),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                    )
                }

                Spacer(modifier = Modifier.height(15.dp))

                // Input Password
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text( text = "Contraseña", color = coloress.TextMain, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(start = 4.dp, bottom = 6.dp) )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("••••••") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = coloress.PrimaryGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        },

                        // icono de mostra contraseña
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    passwordVisible = !passwordVisible
                                }
                            ) {
                                Icon(
                                    imageVector =
                                        if (passwordVisible)
                                            Icons.Default.Visibility
                                        else
                                            Icons.Default.VisibilityOff,
                                    contentDescription = "Mostrar contraseña"
                                )
                            }
                        },


                        visualTransformation =
                            if (passwordVisible)
                                VisualTransformation.None
                            else
                                PasswordVisualTransformation(),
                        // Y AQUÍ TAMBIÉN:
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            errorIndicatorColor = Color.Transparent,
                            unfocusedContainerColor = coloress.White,
                            focusedContainerColor = coloress.White,

                            focusedTextColor = coloress.PrimaryGreen,
                            unfocusedTextColor = coloress.TextMain

                        ),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {

                        isLoading = true
                        scope.launch {
                            val acceso = Usuario_R.verificarEntrada(user,password) // llamamos a la clase y despues a la funcion que hay dentro de la calse y le pasamos los parametros

                            delay(500)
                            isLoading = false
                            println("algo pasa con la base de dato login$acceso")
                            if (acceso) {
                              datosEnMemoria.guardaDatos(context, user)
                              onLoginSuccess()


                            } else {
                                // Aquí luego puedes mostrar un mensaje
                                Mensajeria.error("Usuario o contraseña incorrectos")
                                println("Usuario o contraseña incorrectos")
                                println("user: $user" )

                            }



                        }
                    },
                    modifier = Modifier .fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = coloress.PrimaryGreen),
                    shape = RoundedCornerShape(10.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = coloress.White, modifier = Modifier.size(20.dp))
                    } else {
                        Text("INGRESAR", fontWeight = FontWeight.SemiBold, color = coloress.White)
                    }
                }
            }
        }
    }
}




@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    LoginScreen(
        onLoginSuccess = {}
    )
}