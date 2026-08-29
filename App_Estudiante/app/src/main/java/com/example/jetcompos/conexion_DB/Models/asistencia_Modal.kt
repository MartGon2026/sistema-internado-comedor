package com.example.jetcompos.conexion_DB.Models

import kotlinx.serialization.Serializable

object asistencia_Modal {

    @Serializable
    data class asistencia_Mo (
        val carnet: String,
        val tipo_comida: String,
        val codigo_utilizado: String,
        val hora: String,
        val fecha: String,
        val estado: Boolean
    )
}