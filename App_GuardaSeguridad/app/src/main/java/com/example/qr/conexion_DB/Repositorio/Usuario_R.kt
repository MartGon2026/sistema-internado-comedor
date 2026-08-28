package com.example.qr.conexion_DB.Repositorio


import com.example.qr.conexion_DB.Models.Usuario_Modal
import com.example.qr.conexion_DB.conexioSupabase.conexionBDS
import io.github.jan.supabase.postgrest.from
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

object Usuario_R {

    suspend fun ingresarUser(id_carnet: String ,user: String, password: String, role: String): Boolean {
        return try {
            conexionBDS.supabase.from("usuarios").insert( Usuario_Modal.Usuario_Mo(carnet = id_carnet, usuario = user, contrasena = password, rol = role) )
            true
        } catch (e: Exception){
            false
        }

    }

    suspend fun verificarLogin(carnet: String, password: String): Boolean{
        return try {
            val resultado = conexionBDS.supabase.from("usuarios").select {
                filter { eq("carnet", carnet)
                    eq("contrasena", password)}
            }.decodeList<Usuario_Modal.Usuario_Mo>()

            resultado.isNotEmpty()

        } catch (e: Exception){
            false
        }
    }

    suspend fun verificarEntrada(user: String, password: String): Boolean{
        return try {
            val resultado = conexionBDS.supabase.from("usuarios").select {
                filter { eq("usuario", user)
                    eq("contrasena", password)}
            }.decodeList<Usuario_Modal.Usuario_Mo>()

            resultado.isNotEmpty()

        } catch (e: Exception){
            false
        }
    }

    suspend fun obtenerDatosUser(Usuario: String): Usuario_Modal.Usuario_Mo?{
        return try {
            conexionBDS.supabase.from("usuarios").select {
                filter { eq("usuario", Usuario) }
            }.decodeSingle<Usuario_Modal.Usuario_Mo>()

        }catch (e: Exception){
            null
        }
    }





    suspend fun VerificarPassword(Carnet: String, paswordActual: String, nuevoPassword: String, repetirPassword: String): String?{
        return try {

            val DatosUser = obtenerDatosUSerCarnet(Carnet)

            //verificar si existe
            if(DatosUser == null){return "usuario no encontrado"}

            // verificar contraseña
            if (DatosUser.contrasena != paswordActual){ return "La contraseña actual es incorrecta"}

            if (nuevoPassword.length < 4) {
                return "La nueva contraseña es muy corta"
            }

            // Verificar que las nuevas coincidan
            if (nuevoPassword != repetirPassword){ return "Las contraseñas no coinciden"}

            null


        } catch (e: Exception){   "Error: ${e.message}"}


    }
    suspend fun ActualizarPassword(Carnet: String, nuevoPassword: String ): Boolean{
        return try {
            // actualziar contraseña
            val response = conexionBDS.supabase
                .from("usuarios")
                .update(mapOf("contrasena" to nuevoPassword)) {
                    filter { eq("carnet", Carnet) }

            }
            true

        } catch (e: Exception){   false}


    }


}


suspend fun obtenerDatosUSerCarnet(Carnet: String): Usuario_Modal.Usuario_Mo?{
    return try {
        conexionBDS.supabase.from("usuarios").select {
            filter { eq("carnet", Carnet) }
        }.decodeSingle<Usuario_Modal.Usuario_Mo>()

    }catch (e: Exception){
        null
    }
}

suspend fun ActualizarFotoPerfil(Carnet: String, fotoString: String): Boolean {
    return try {
        conexionBDS.supabase.from("usuarios").update(mapOf("fotouser" to fotoString)) {
            filter { eq("carnet", Carnet) }
        }
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

@OptIn(ExperimentalEncodingApi::class)
suspend fun ObtenerFotoPerfil(Carnet: String): ByteArray? {
    return try {
        val usuario = conexionBDS.supabase.from("usuarios").select {
            filter { eq("carnet", Carnet) }
        }.decodeSingle<Usuario_Modal.Usuario_Mo>()


        if (!usuario.fotouser.isNullOrEmpty()) {
            Base64.decode(usuario.fotouser)
        } else {
            null
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }

}