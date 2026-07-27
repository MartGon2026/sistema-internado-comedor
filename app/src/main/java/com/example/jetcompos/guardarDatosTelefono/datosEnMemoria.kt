package com.example.jetcompos.guardarDatosTelefono

import android.content.Context
import com.example.jetcompos.conexion_DB.Repositorio.Usuario_R
import com.example.jetcompos.conexion_DB.Repositorio.estudiante_Reposi

object datosEnMemoria {

    private const val nombreArchivo = "datosGuardados"

    data class DatosUsuario(
        val carnet: String,
        val usuario: String,
        val rol: String,
        val nameUser: String
    )

    // GUARDAR DESDE BD (SIEMPRE REEMPLAZA)
    suspend fun guardaDatos(context: Context, user: String): Boolean {
        return try {

            val datos = Usuario_R.obtenerDatosUser(user)
            if (datos == null) return false

            val estudiante = estudiante_Reposi.obtenerDatosEstudiante(datos.carnet)
            if (estudiante == null) return false

            context.getSharedPreferences(nombreArchivo, Context.MODE_PRIVATE)
                .edit()
                .clear()
                .putString("carnet", datos.carnet)
                .putString("usuario", datos.usuario)
                .putString("rol", datos.rol)
                .putString("nameUser", estudiante.nombres)
                .apply()

            true

        } catch (e: Exception) {
            false
        }
    }

    // VER SI HAY SESIÓN
    fun existe(context: Context): Boolean {
        return context.getSharedPreferences(nombreArchivo, Context.MODE_PRIVATE).contains("usuario")
    }

    // OBTENER DATOS GUARDADOS
    fun obtener(context: Context): DatosUsuario? {

        val prefs = context.getSharedPreferences(nombreArchivo, Context.MODE_PRIVATE)

        val carnet = prefs.getString("carnet", null)
        val usuario = prefs.getString("usuario", null)
        val rol = prefs.getString("rol", null)
        val nameUser = prefs.getString("nameUser", null)

        return if (carnet != null && usuario != null && rol != null && nameUser != null) {
            DatosUsuario(carnet, usuario, rol, nameUser)
        } else null
    }

    // GUARDAR SOLO SI CAMBIA USUARIO
    suspend fun guardarSiCambio(context: Context, user: String): Boolean {
        val prefs = context.getSharedPreferences(nombreArchivo, Context.MODE_PRIVATE)
        val actual = prefs.getString("usuario", null)

        if (actual == user) return false

        return guardaDatos(context, user)
    }

    // ELIMINAR SESIÓN
    fun eliminar(context: Context) {
        context.getSharedPreferences(nombreArchivo, Context.MODE_PRIVATE)
            .edit().clear().apply()
    }
}