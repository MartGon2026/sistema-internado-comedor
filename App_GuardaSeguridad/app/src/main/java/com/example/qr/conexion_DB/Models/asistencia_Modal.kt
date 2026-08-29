package com.example.qr.conexion_DB.Models

import kotlinx.serialization.Serializable

object asistencia_Modal {

    @Serializable
    data class asistencia_Mo (
        val carnet: String,
        val codigo_utilizado: String,
        val tipo_comida: String,
        val fecha: String,
        val hora: String,
        val estado: Boolean
    )
}