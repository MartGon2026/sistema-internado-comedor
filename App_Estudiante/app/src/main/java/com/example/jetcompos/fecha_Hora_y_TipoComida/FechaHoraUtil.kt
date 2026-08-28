package com.example.jetcompos.fecha_Hora_y_TipoComida


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

    fun obtenerFechaCorta(): String {
        val idiomaEspanol = Locale("es", "NI")

        return SimpleDateFormat( "d MMM",    idiomaEspanol ).format(Date()).lowercase(idiomaEspanol)
    }


}