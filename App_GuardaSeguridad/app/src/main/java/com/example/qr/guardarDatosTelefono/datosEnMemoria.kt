package com.example.qr.guardarDatosTelefono

import android.content.Context
import android.util.Log
import com.example.qr.conexion_DB.Repositorio.GuarSeguri_Reposi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext


object datosEnMemoria {

    private const val nombreArchivo = "datosGuardados"
    private const val CODIGO = "codigoUnico"
    private const val NOMBRE = "Nombre"
    private const val APELLIDO = "Apellido"
    private const val FOTO = "Foto"
    private const val USUARIO = "usuario"
    private const val SEXO = "Sexo"
    private const val TURNO = "Turno"

    data class DatosUsuario(
        val codigoUnico: String,
        val nombre: String,
        val apellido: String,
        val foto: String?,
        val usuario: String?,
        val sexo: String?,
        val turno: String?
    )


    // GUARDAR DESDE BD (SIEMPRE REEMPLAZA)
    suspend fun guardaDatos(context: Context, user: String): Boolean = withContext(Dispatchers.IO + NonCancellable) {
        try {
             val datos = GuarSeguri_Reposi.obtenerDatosUser(user)
            
            if (datos == null) { return@withContext false }

            // ELIMINAR ANTES DE GUARDAR (Para asegurar limpieza total en la prueba)
            eliminar(context)

            // GUARDAR NUEVOS DATOS
            val prefs = context.getSharedPreferences(nombreArchivo, Context.MODE_PRIVATE)
            val exito = prefs.edit()
                .putString(CODIGO, datos.codigoUnico)
                .putString(NOMBRE, datos.Nombre)
                .putString(APELLIDO, datos.Apellido)
                .putString(FOTO, datos.Foto)
                .putString(USUARIO, datos.Usuario)
                .putString(SEXO, datos.Sexo)
                .putString(TURNO, datos.Turno)
                .commit() // commit() asegura que se escriba YA físicamente
            
            exito

        } catch (e: Exception) {  false  }
    }

    fun existe(context: Context): Boolean {
        val prefs = context.getSharedPreferences(nombreArchivo, Context.MODE_PRIVATE)
        val tieneUsuario = prefs.contains(USUARIO)
        val tieneNombre = prefs.contains(NOMBRE)
        val tieneCodigo = prefs.contains(CODIGO)
        
        val esValida = tieneUsuario && tieneNombre && tieneCodigo
        
        if (!esValida && tieneUsuario) {
            Log.w("datosEnMemoria", "Sesión detectada pero incompleta (faltan campos críticos)")
        }
        
        return esValida
    }

    fun obtener(context: Context): DatosUsuario? {
        val prefs = context.getSharedPreferences(nombreArchivo, Context.MODE_PRIVATE)

        val codigoUnico = prefs.getString(CODIGO, null)
        val nombre = prefs.getString(NOMBRE, null)
        val apellido = prefs.getString(APELLIDO, null)
        val foto = prefs.getString(FOTO, null)
        val usuario = prefs.getString(USUARIO, null)
        val sexo = prefs.getString(SEXO, null)
        val turno = prefs.getString(TURNO, null)

        return if (codigoUnico != null && nombre != null) {
            DatosUsuario(codigoUnico,nombre, apellido ?: "", foto, usuario, sexo, turno )
         } else { null }
    }

    fun eliminar(context: Context) {
        context.getSharedPreferences(nombreArchivo, Context.MODE_PRIVATE).edit().clear().commit()
    }
}
