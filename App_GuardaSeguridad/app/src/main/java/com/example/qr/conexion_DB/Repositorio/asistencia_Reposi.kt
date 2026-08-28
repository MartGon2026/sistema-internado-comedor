package com.example.qr.conexion_DB.Repositorio

import android.util.Log
import com.example.qr.conexion_DB.Models.asistencia_Modal
import com.example.qr.conexion_DB.Models.obtenerListaCarnet
import io.github.jan.supabase.postgrest.query.Columns
import com.example.qr.conexion_DB.conexioSupabase.conexionBDS
import io.github.jan.supabase.postgrest.from

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


    // estraer solo los carnet
    suspend fun obtenerCarnetsPorAsistencia( tipoComida: String, fecha: String ): List<String> {
        Log.e("entro a la funcion asistencia", "asdsa")
        return try {

            val resultado = conexionBDS.supabase.from("asistencias").select(
                    columns = Columns.raw("carnet")
                ) {
                    filter {
                        eq("tipo_comida", tipoComida)
                        eq("fecha", fecha)
                    }
                Log.e("entro de ella", "dentro")
                }
                .decodeList<obtenerListaCarnet.obtnerCarnet>() // usamos el modelo pero solo viene carnet
            Log.d("SUPabase", "verificar : ${resultado.size} estudiantes")

            val carnets = resultado.map { it.carnet }

            Log.e("salir", "consulta")

            carnets

        } catch (e: Exception) {
            Log.e("SUPABASE_OK", "❌ Error: ${e.message}")
            emptyList()
        }
    }



}