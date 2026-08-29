package com.example.jetcompos.conexion_DB.Models

import kotlinx.serialization.Serializable

object codigoQR_Modal {
    @Serializable
    data class codigoQR_Mo(
            val carnet: String,
            val codigo: String,
            val tipo_comida: String,
            val fecha: String,
            val hora: String,
            val utilizado: Boolean = false

            )
}