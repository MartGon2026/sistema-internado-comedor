package com.example.qr.conexion_DB.Repositorio


import android.util.Log
import com.example.qr.conexion_DB.Models.codigoQR_Modal
import com.example.qr.conexion_DB.conexioSupabase.conexionBDS

import io.github.jan.supabase.postgrest.from

object codigoQR_Reposi {

    suspend fun ingresarQR(id_carnet: String ,codigoQR: String, tipoComida: String, fechaActual: String, horaActual: String): Boolean {
        return try {
            conexionBDS.supabase.from("codigos_qr").insert(
                codigoQR_Modal.codigoQR_Mo(carnet = id_carnet, codigo = codigoQR, tipo_comida = tipoComida, fecha=fechaActual, hora= horaActual)

            )
            true
        } catch (e: Exception){
            Log.e("QR", "Error al guardar QR", e)
            false
        }

    }

    suspend fun qrCambio(codigoQR: String): Boolean{
        return try {
            val resultado = conexionBDS.supabase.from("codigos_qr").select {
                filter { eq("codigo", codigoQR) }
            }.decodeList<codigoQR_Modal.codigoQR_Mo>().firstOrNull()

            resultado?.utilizado ?: false


        } catch (e: Exception){false}

    }

    suspend fun eliminarQR(codigoQR: String): Boolean{
        return try {
            conexionBDS.supabase.from("codigos_qr").delete {
                filter { eq("codigo",codigoQR) }
            }
            true
        } catch (e: Exception) {false}
    }

    suspend fun verificarYMarcarQR(carnet: String, codigoUnico: String): Boolean {
        return try {
            val carnetLimpio = carnet.trim()
            val codigoLimpio = codigoUnico.trim()


            val resultado = conexionBDS.supabase.from("codigos_qr").select {
                    filter {
                        eq("carnet", carnetLimpio)
                        eq("codigo", codigoLimpio)
                        eq("utilizado", false)
                    }
                }.decodeList<codigoQR_Modal.codigoQR_Mo>()



            if (resultado.isNotEmpty()) {

                conexionBDS.supabase.from("codigos_qr").update(
                        {  set("utilizado", true)  }

                    ) {
                        filter {
                            eq("carnet", carnetLimpio)
                            eq("codigo", codigoLimpio)
                            eq("utilizado", false)
                        }
                    }

                true

            } else {
               false
            }

        } catch (e: Exception) {
            Log.e("QR_DB", "Error en verificarYMarcarQR: ${e.message}", e)
            false
        }
    }




}