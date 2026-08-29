package com.example.jetcompos.conexion_DB.Repositorio

import com.example.jetcompos.conexion_DB.Models.Usuario_Modal
import com.example.jetcompos.conexion_DB.Models.estudiante_Modal
import com.example.jetcompos.conexion_DB.conexioSupabase.conexionBDS
import io.github.jan.supabase.postgrest.from

object estudiante_Reposi {

    suspend fun obtenerDatosEstudiante(carnet: String): estudiante_Modal.estudiante_Mo? {
        return try {

            val resultado = conexionBDS.supabase.from("estudiantes").select {
                    filter { eq("carnet", carnet) }

            }.decodeList<estudiante_Modal.estudiante_Mo>()

            resultado.firstOrNull()

        } catch (e: Exception) {
            null
        }
    }
}