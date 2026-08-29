package com.example.jetcompos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.jetcompos.mensajeria.PantallaMensajeGlobal
import com.example.jetcompos.mensajeria.PantallaRegistroRepetido
import com.example.jetcompos.ui.theme.JetcomposTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            JetcomposTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 1. Creamos el controlador de navegación
                    val navController = rememberNavController()





                    // 3. Definimos las rutas de la app
                    NavHost(
                        navController = navController,
                        startDestination = "login" // Empezamos en el Login
                    ) {

                        // --- RUTA: LOGIN ---
                        composable("login") {
                            LoginScreen(
                                onLoginSuccess = {
                                    // Al loguearse, navegamos al Dashboard
                                    navController.navigate("pantalla_principal") {
                                        // Esto limpia el historial para que no pueda volver al login con el botón atrás
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }

                        // --- RUTA: PANTALLA PRINCIPAL (Dashboard) ---
                        composable("pantalla_principal") {
                            PantallaPrincipal(
                                alCerrarSesion = {
                                    // Si cierran sesión, regresar al login
                                    navController.navigate("login") {
                                        popUpTo("pantalla_principal") { inclusive = true }
                                    }
                                }
                            )
                        }

                        // --- RUTA: CONFIGURACIÓN ---
                        composable("configuracion") {
                            // Cambiado a 'ConfiguracionModerna' para enlazar con el nuevo diseño
                            Configuracion(
                                usuario = DatosUsuarios(
                                    nombre= "",
                                    Apellido = "",
                                    carnet = "",
                                    carrera = "",
                                    edad = 0,
                                    usuario = "",
                                    carreraAno = ""
                                ),
                                alVolver = {
                                    // Vuelve a la pantalla anterior
                                    navController.popBackStack()
                                }
                            )
                        }

                    }
                    // 👇 ESTO VA AQUÍ (GLOBAL)
                    PantallaMensajeGlobal()
                    PantallaRegistroRepetido()
                }
            }
        }
    }
}