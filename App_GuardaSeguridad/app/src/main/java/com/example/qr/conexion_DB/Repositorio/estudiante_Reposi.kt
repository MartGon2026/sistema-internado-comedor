package com.example.qr.conexion_DB.Repositorio

import android.util.Log
import com.example.qr.conexion_DB.Models.estudiante_Modal
import com.example.qr.conexion_DB.conexioSupabase.conexionBDS
import io.github.jan.supabase.postgrest.from

import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

object estudiante_Reposi {


    suspend fun actualizarEstudiante(Carnet: String, Estado: String): Boolean{
        return try {
            conexionBDS.supabase.from("estudiantes").update({
                set("estado_internado", Estado)
            }) {  filter { eq("carnet", Carnet) }  }
            true

        } catch (e: Error){false}
    }

    suspend fun obtenerTodosEstudiantes(): List<estudiante_Modal.estudiante_Mo> {
        return try {
            val resultado = conexionBDS.supabase.from("estudiantes").select().decodeList<estudiante_Modal.estudiante_Mo>()

            Log.d("SUPABASE_OK", "✅ Trayendo: ${resultado.size} estudiantes")
            resultado
        } catch (e: Exception) {
             Log.e("SUPABASE_OK", "❌ Error: ${e.message}")
            emptyList()
        }
    }

    suspend fun datoEstudiante(CarnerBuscado: String): estudiante_Modal.estudiante_Mo? {
        return try {
            val estudiante = conexionBDS.supabase.from("estudiantes").select {
                    filter {    eq("carnet", CarnerBuscado)  }

                    limit(1)
                }.decodeList<estudiante_Modal.estudiante_Mo>().firstOrNull()

            estudiante

        } catch (e: Exception) {     null   }
    }

    suspend fun obtenerEstudiantesPorAsistencia( tipoComida: String, fecha: String ): List<estudiante_Modal.estudiante_Mo> {

        return try {

            // Obtener los carnets que cumplen las condiciones
            val carnets = asistencia_Reposi.obtenerCarnetsPorAsistencia(tipoComida, fecha)

            if (carnets.isEmpty()) {
                return emptyList()
            }

            // Buscar todos los estudiantes cuyos carnets estén en la lista
            val resultado = conexionBDS.supabase.from("estudiantes").select {
                    filter {
                        isIn("carnet", carnets)
                    }
                }
                .decodeList<estudiante_Modal.estudiante_Mo>()

            Log.d("SUPABAS", "✅ Estudiantes encontrados: ${resultado.size}")

            resultado

        } catch (e: Exception) {
            Log.e("SUPABAS", "❌ Error: ${e.message}")
            emptyList()
        }
    }


    @Serializable
    data class FotoEstudiante(
        @SerialName("foto_estudi")
        val fotoEstudi: String? = null
    )

    suspend fun obtenerFotoEstudiante(carnet: String): String? {
        return try {
            val resultado = conexionBDS.supabase .from("estudiantes")
                .select(columns = Columns.raw("foto_estudi")) {
                    filter {
                        eq("carnet", carnet.trim())
                    }
                }
                .decodeList<FotoEstudiante>().firstOrNull()

            resultado?.fotoEstudi

        } catch (e: Exception) {
            Log.e("FOTO_ESTUDIANTE", "Error obteniendo foto: ${e.message}", e)
            null
        }
    }

    suspend fun obtenerEstudiantesFueraInternado( estadoestudi: String ): List<estudiante_Modal.estudiante_Mo> {

        return try {
            // Buscar todos los estudiantes cuyos estaado Internado  esten en fuera
            val resultado = conexionBDS.supabase.from("estudiantes").select {
                filter {
                    eq("estado_internado", estadoestudi)
                }
            }
                .decodeList<estudiante_Modal.estudiante_Mo>()

            Log.d("SUPABAS", "✅ Estudiantes encontrados: ${resultado.size}")

            resultado

        } catch (e: Exception) {
            Log.e("SUPABAS", "❌ Error: ${e.message}")
            emptyList()
        }
    }



    suspend fun obtenerUnTotalEstudiantes(): estudiante_Modal.ResumenEstudiantes {

        return try {

            val todosLosEstudiantes = conexionBDS.supabase.from("estudiantes").select().decodeList<estudiante_Modal.estudiante_Mo>()
            
            // Filtramos para ignorar a los estudiantes externos
            val estudiantesInternos = todosLosEstudiantes.filter { 
                !it.estado_internado.equals("EXTERNO", ignoreCase = true) 
            }
            
            val total = estudiantesInternos.size

            val dentro = estudiantesInternos.count {
                it.estado_internado.equals( "DENTRO", ignoreCase = true  )
            }

            val fuera = estudiantesInternos.count {
                it.estado_internado.equals(  "FUERA", ignoreCase = true   )
            }

            estudiante_Modal.ResumenEstudiantes( total, dentro, fuera )

        } catch (e: Exception) {

            Log.e( "RESUMEN_ESTUDIANTES","Error: ${e.message}")

            estudiante_Modal.ResumenEstudiantes( 0,0,0  )
        }
    }



}