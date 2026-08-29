package com.example.jetcompos.mensajeria


import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

sealed class ResultadoMensaje {
    data class Exito(val mensaje: String) : ResultadoMensaje()
    data class Error(val mensaje: String) : ResultadoMensaje()
    data class Advertencia(val mensaje: String) : ResultadoMensaje()

    data class Confirmacion(val mensaje: String, val onCancel: () -> Unit, val onConfirm: () -> Unit) : ResultadoMensaje()
}

object Mensajeria {

    var mensajeActual by mutableStateOf<ResultadoMensaje?>(null)
        private set

    fun exito(texto: String) {
        mensajeActual = ResultadoMensaje.Exito(texto)
    }

    fun advertencia(texto: String) {
        mensajeActual = ResultadoMensaje.Advertencia(texto)
    }

    fun error(texto: String) {
        mensajeActual = ResultadoMensaje.Error(texto)
    }
    fun confirmar(texto: String, cancelar: () -> Unit, confi: () -> Unit){
        mensajeActual = ResultadoMensaje.Confirmacion(texto, cancelar, confi)
    }
    fun limpiar() {
        mensajeActual = null
    }
}