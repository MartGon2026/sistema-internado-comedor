package com.example.qr.conexion_DB.Models

import kotlinx.serialization.Serializable

object obtenerListaCarnet {

    @Serializable
    data class obtnerCarnet (
        val carnet: String
    )

}