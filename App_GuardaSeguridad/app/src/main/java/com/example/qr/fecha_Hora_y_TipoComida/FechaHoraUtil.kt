package com.example.qr.fecha_Hora_y_TipoComida


import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


object FechaHoraUtil {

    fun obtenerHoraActual(): String{
        return SimpleDateFormat( "HH:mm:ss", Locale.getDefault() ).format(Date())

    }

    fun obtenerFechaActual(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault() ).format(Date())
    }
    fun fechaTelefono(): String {
        val formato = SimpleDateFormat("d 'de' MMMM", Locale("es", "ES"))
        return formato.format(Date())
    }



}