package com.example.jetcompos.conexion_DB.Models

import kotlinx.serialization.Serializable

object estudiante_Modal {
    @Serializable
    data class estudiante_Mo(
        val carnet: String,
        val nombres: String,
        val apellidos: String,
        val carrera: String,
        val edad_estudi: Int,
        val procedencia: String?,
        val telefono: String?,
        val anoCarrera: String,
        val foto_estudi: String?,
        val estado_internado: String?,
    )

}