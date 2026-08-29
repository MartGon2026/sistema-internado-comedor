package com.example.qr.conexion_DB.Repositorio

import com.example.qr.conexion_DB.Models.GuarSeguri_Modal
import com.example.qr.conexion_DB.conexioSupabase.conexionBDS
import io.github.jan.supabase.postgrest.from

object GuarSeguri_Reposi {

    suspend fun verificarLogin(user: String, password: String): Boolean{
        return try {
            val resultado = conexionBDS.supabase.from("GuardaSeguridad").select {
                filter { eq("Usuario", user)
                    eq("Contrasena", password)}
            }.decodeList<GuarSeguri_Modal.GS_Mo>()

            resultado.isNotEmpty()

        } catch (e: Exception){
            false
        }
    }

    suspend fun obtenerDatosUser(user: String): GuarSeguri_Modal.GS_Mo?{
        return try {
            conexionBDS.supabase.from("GuardaSeguridad").select {
                filter { eq("Usuario", user) }
            }.decodeSingle<GuarSeguri_Modal.GS_Mo>()

        }catch (e: Exception){
            null
        }
    }

}