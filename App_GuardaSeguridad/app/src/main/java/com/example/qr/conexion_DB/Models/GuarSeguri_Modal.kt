package com.example.qr.conexion_DB.Models

import kotlinx.serialization.Serializable

object GuarSeguri_Modal {

    @Serializable
    data class GS_Mo(
        val codigoUnico: String,
        val Nombre: String,
        val Apellido: String,
        val Sexo: String,
        val Foto: String,
        val Turno: String,
        val Usuario: String?,
        val Contrasena: String?
    )


}