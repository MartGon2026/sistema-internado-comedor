package com.example.jetcompos.conexion_DB.Models

import kotlinx.serialization.Serializable

object Usuario_Modal {
    @Serializable
    data class Usuario_Mo(
        val carnet: String,
        val usuario: String,
        val contrasena: String,
        val rol: String,
        val estado: Boolean = true,
        val sesion_actica: Boolean = false,
        val fotouser: String? = null
    )
}