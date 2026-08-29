package com.example.qr.conexion_DB.Models

import kotlinx.serialization.Serializable

object estudiante_Modal {
    @Serializable
    data class estudiante_Mo(
        val carnet: String,
        val nombres: String,
        val apellidos: String,
        val carrera: String,
        val edad_estudi: Int,
        val anoCarrera: String,
        val procedencia: String?,
        val telefono: String?,
        val foto_estudi: String?,
        val estado_internado: String
    )
    data class ResumenEstudiantes(
        val total: Int,
        val dentro: Int,
        val fuera: Int
    )

}