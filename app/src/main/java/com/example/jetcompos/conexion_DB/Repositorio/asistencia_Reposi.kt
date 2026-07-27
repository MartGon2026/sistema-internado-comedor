package com.example.jetcompos.conexion_DB.Repositorio


import com.example.jetcompos.conexion_DB.Models.Usuario_Modal
import com.example.jetcompos.conexion_DB.Models.asistencia_Modal
import com.example.jetcompos.conexion_DB.conexioSupabase.conexionBDS
import io.github.jan.supabase.postgrest.from
import io.ktor.network.sockets.Datagram

object asistencia_Reposi {

    // sera verdadero si no encuentra registro y falso si hya registro
    suspend fun verificarExistencia(Carnet: String, TipoComida: String, Fecha: String): Boolean{
        return try {
            val resultado = conexionBDS.supabase.from("asistencias").select {
                filter { eq("carnet", Carnet); eq("tipo_comida", TipoComida); eq("fecha", Fecha) }

            }.decodeList<asistencia_Modal.asistencia_Mo>()
            resultado.isEmpty()

        } catch (e: Exception){false}

    }
    suspend fun obtenerDatosAsistenciaCarnet(Carnet: String, TipoComida: String, Fecha: String): asistencia_Modal.asistencia_Mo? {
        return try {
            conexionBDS.supabase.from("asistencias").select {
                filter {
                     eq("carnet", Carnet); eq("tipo_comida", TipoComida); eq("fecha", Fecha)


                }
            }.decodeSingleOrNull<asistencia_Modal.asistencia_Mo>()


        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun ingresarDatos(carnet : String, tipoComida: String, codiUsado: String, hora: String, fecha: String, estado: Boolean ): Boolean{
        return try {
            conexionBDS.supabase.from("asistencias").insert(asistencia_Modal.asistencia_Mo(carnet = carnet, tipo_comida = tipoComida, codigo_utilizado = codiUsado, hora = hora, fecha = fecha, estado = estado ) )
            true
        } catch (e: Exception){
            false
        }
    }


}