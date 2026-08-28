package com.example.qr.fecha_Hora_y_TipoComida

import kotlinx.datetime.LocalTime
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TipoComidaActual {
    fun obtenerTipoComida(): String {
        val horaActual = SimpleDateFormat( "HH:mm", Locale.getDefault() ).format(Date())

        return when{
            horaActual in "07:00".."11:15" -> "DESAYUNO"
            horaActual in "12:00".."15:15" -> "ALMUERZO"
            horaActual in "04:30".."07:30" -> "CENA"
            else -> "FUERA_DE_HORARIO"

        }
    }

    fun validarHorario(): Boolean {
        return obtenerTipoComida() != "FUERA_DE_HORARIO"
    }

}