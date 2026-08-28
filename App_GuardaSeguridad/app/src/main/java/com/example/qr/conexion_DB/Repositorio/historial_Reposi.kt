package com.example.qr.conexion_DB.Repositorio

import android.util.Log
import com.example.qr.conexion_DB.Models.historial_Modal
import com.example.qr.conexion_DB.conexioSupabase.conexionBDS
import com.example.qr.fecha_Hora_y_TipoComida.FechaHoraUtil
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns

object historial_Reposi {

    suspend fun nuevoHistorial(Carnet: String, FechaSalida: String, HoraSalida: String): Boolean{
        return try {
            conexionBDS.supabase.from("historial_internado").insert(historial_Modal.historial_Mo(Carnet,FechaSalida,HoraSalida, Estado = "FUERA" ) )
            true
        }
        catch (e: Exception){ false}
    }

    suspend fun actualizarHistorial(Carnet: String, FechaEntrada: String, HoraEntrada: String): Boolean{
        return try {
            conexionBDS.supabase.from("historial_internado").update({
                set("fecha_entrada", FechaEntrada); set("hora_entrada", HoraEntrada); set("Estado", "DENTRO" )
            }) {  filter { eq("carnet", Carnet);  eq ("Estado", "FUERA") } }
             true
        } catch (e: Exception){false}
    }

    suspend fun obtenerHistorialCompleto(fechaHoy: String): List<historial_Modal.historial_Mo> {
        return try {
            Log.d("HISTORIAL_DEBUG", "Consultando base de datos para la fecha: $fechaHoy")
            
            // Traemos todos los registros sin filtro inicial para diagnosticar
            val todos = conexionBDS.supabase.from("historial_internado").select().decodeList<historial_Modal.historial_Mo>()
            
            Log.d("HISTORIAL_DEBUG", "Total registros recuperados: ${todos.size}")

            val filtrados = todos.filter { registro ->
                // Normalizamos la fecha (quitamos espacios por si acaso)
                val fSalida = registro.fecha_salida.trim()
                val fEntrada = registro.fecha_entrada?.trim() ?: ""
                val estado = registro.Estado?.uppercase()?.trim() ?: ""

                val actividadHoy = fSalida == fechaHoy || fEntrada == fechaHoy
                val pendiente = estado == "FUERA" || (fEntrada.isEmpty() && estado != "DENTRO")

                if (actividadHoy || pendiente) {
                    Log.d("HISTORIAL_DEBUG", "Registro aceptado: Carnet ${registro.carnet}, Estado $estado")
                    true
                } else false
            }.sortedWith(
                compareByDescending<historial_Modal.historial_Mo> { it.Estado == "FUERA" }
                .thenByDescending { it.fecha_salida }
                .thenByDescending { it.hora_salida }
            )

            Log.d("HISTORIAL_DEBUG", "Registros después de filtrar: ${filtrados.size}")
            filtrados

        } catch (e: Exception) {
            Log.e("HISTORIAL_REPO", "ERROR CRÍTICO: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun obtenerHistorialConfirmado(): List<historial_Modal.historial_Mo> {
        return try {
            conexionBDS.supabase.from("historial_internado").select {
                filter {  eq("Estado", "DENTRO")  }
            }  .decodeList<historial_Modal.historial_Mo>()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun obtenerUltimos3Movimientos(): List<historial_Modal.historial_Mo> {
        return try {
            conexionBDS.supabase.from("historial_internado").select().decodeList<historial_Modal.historial_Mo>()
                .sortedWith(compareByDescending<historial_Modal.historial_Mo> { it.fecha_salida }
                .thenByDescending { it.hora_salida })
                .distinctBy { it.carnet } // Filtramos para que cada persona aparezca solo una vez
                .take(3)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun obtenerUnTotalSalidaEntrada(): historial_Modal.totalSalidasEntradas {
        return try {
            val registrosHoy = conexionBDS.supabase.from("historial_internado").select ( columns = Columns.list("hora_salida",  "fecha_entrada", "hora_entrada") ){
                filter { eq("fecha_salida", FechaHoraUtil.obtenerFechaActual()) }
            } .decodeList<historial_Modal.MovimientoHoy>()

            val salidas = registrosHoy.size
            val entradas = registrosHoy.count { it.fecha_entrada != null }

            var ultimaHora = "";  var ultimoTipo = ""

            registrosHoy.forEach { registro ->
                if (registro.hora_salida > ultimaHora) {
                    ultimaHora = registro.hora_salida
                    ultimoTipo = "Salida"
                }
                val horaEntrada = registro.hora_entrada
                if (  registro.fecha_entrada != null &&  horaEntrada != null &&      horaEntrada > ultimaHora ) {
                    ultimaHora = horaEntrada
                    ultimoTipo = "Entrada"
                }
            }
            val ultimoMovimiento = if (ultimaHora.isNotEmpty()) { "$ultimoTipo - $ultimaHora"   } else {  "Sin registros"   }
            historial_Modal.totalSalidasEntradas( salidas, entradas, ultimoMovimiento )
        } catch (e: Exception) {
            Log.e( "RESUMEN_ESTUDIANTES","Error: ${e.message}")
            historial_Modal.totalSalidasEntradas( 0,0, "Sin registros" )
        }
    }
}