package com.example.jetcompos.conexion_DB.Repositorio

import android.util.Log
import com.example.jetcompos.conexion_DB.Models.qrComedor
import com.example.jetcompos.conexion_DB.conexioSupabase.conexionBDS
import io.github.jan.supabase.postgrest.from

object qrComedor_Reposi {
    suspend fun verificarExistencia(datoQR: String): Boolean{
        return try {
            Log.d("entra","resultado : $datoQR")
            val resultado = conexionBDS.supabase.from("codigoQrComedor").select {
                filter { eq("CodQR", datoQR) }

            }.decodeList<qrComedor.qrComedor_Mo>()
            Log.d("hhh","resultado : $resultado")

            resultado.isNotEmpty()

        } catch (e: Exception){false}

    }


    suspend fun obtenerTodosQR(): List<qrComedor.qrComedor_Mo> {

        return try {

            val resultado = conexionBDS.supabase.from("codigoQrComedor").select()
                .decodeList<qrComedor.qrComedor_Mo>()

            Log.d("TODOS_QR", "Datos tabla: $resultado")

            resultado

        } catch (e: Exception) {

            Log.e("TODOS_QR", "Error: ${e.message}")

            emptyList()
        }
    }


}