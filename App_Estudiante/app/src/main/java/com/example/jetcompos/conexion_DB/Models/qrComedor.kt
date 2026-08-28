package com.example.jetcompos.conexion_DB.Models

import kotlinx.serialization.Serializable

object qrComedor {
    @Serializable
    data class qrComedor_Mo(
        val CodQR: String,
        val fechaCreacion: String,

    )
}