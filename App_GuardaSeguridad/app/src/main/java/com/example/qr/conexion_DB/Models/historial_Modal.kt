package com.example.qr.conexion_DB.Models

import kotlinx.serialization.Serializable

object historial_Modal {
    @Serializable
    data class historial_Mo(
        val carnet: String,
        val fecha_salida: String,
        val hora_salida: String,
        val fecha_entrada: String? = null,
        val hora_entrada: String? = null,
        val Estado: String? = null
    )

    @Serializable
    data class MovimientoHoy(
        val hora_salida: String,
        val fecha_entrada: String? = null,
        val hora_entrada: String? = null
    )

    data class totalSalidasEntradas(
        val totalSalida: Int,
        val totalEntrada: Int,
        val ultimoMovimiento: String
    )
}