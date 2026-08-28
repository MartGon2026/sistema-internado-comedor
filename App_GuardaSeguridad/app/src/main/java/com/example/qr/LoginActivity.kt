package com.example.qr

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.qr.conexion_DB.Models.GuarSeguri_Modal
import com.example.qr.conexion_DB.Repositorio.GuarSeguri_Reposi
import com.example.qr.guardarDatosTelefono.datosEnMemoria
import com.example.qr.mensajes.mensajes
import kotlinx.coroutines.launch

class    LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etUsuario = findViewById<EditText>(R.id.etUsuario)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnIngresar = findViewById<Button>(R.id.btnIngresar)
        val loading = findViewById<ProgressBar>(R.id.loading)

// autocompletamos al usuario
        if (datosEnMemoria.existe(this)) {
            val datos = datosEnMemoria.obtener(this)
            etUsuario.setText(datos?.usuario)
            etPassword.requestFocus()
            etPassword.resources.displayMetrics
        }

        btnIngresar.setOnClickListener {
            val usuario = etUsuario.text.toString()
            val password = etPassword.text.toString()

            if (usuario.isNotEmpty() && password.isNotEmpty()) {
                // Desaparecemos el botón y mostramos la rueda de carga
                btnIngresar.visibility = View.INVISIBLE
                loading.visibility = View.VISIBLE

                lifecycleScope.launch {
                    val loginExitoso = GuarSeguri_Reposi.verificarLogin(usuario, password)

                    if (loginExitoso) {
                        // Intentamos guardar datos
                        val exitoGuardado = datosEnMemoria.guardaDatos(this@LoginActivity, usuario)

                        if (exitoGuardado) {
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            // Si falla el guardado, regresamos al estado normal
                            loading.visibility = View.GONE
                            btnIngresar.visibility = View.VISIBLE
                            mensajes.mostrar(this@LoginActivity, layoutInflater, "Error de datos",
                                "Se validó el usuario pero no se pudieron descargar sus datos. Intente de nuevo.", 
                                mensajes.TipoMensaje.ERROR
                            )
                        }
                    } else {
                        // Si falla el login, regresamos al estado normal
                        loading.visibility = View.GONE
                        btnIngresar.visibility = View.VISIBLE
                        mensajes.mostrar(this@LoginActivity, layoutInflater, "Acceso Denegado",
                            "El usuario o la contraseña son incorrectos.", mensajes.TipoMensaje.ADVERTENCIA
                        )
                    }
                }
            } else {
                lifecycleScope.launch {
                    mensajes.mostrar(this@LoginActivity, layoutInflater, "Campos Vacíos",
                        "Por favor complete todos los campos para ingresar.",
                        mensajes.TipoMensaje.ADVERTENCIA
                    )
                }
            }
        }
    }
}