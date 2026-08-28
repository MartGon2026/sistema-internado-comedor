package com.example.qr.conexion_DB.conexioSupabase

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.serializer.KotlinXSerializer
import kotlinx.serialization.json.Json

object conexionBDS {
    val supabase = createSupabaseClient(
        supabaseUrl = "https://vtryizusiddlydztsgax.supabase.co",
        supabaseKey = "sb_publishable_QYlxUuYrIu5keLsueqjLBg_O34SKhh-" // Pon tu clave de vuelta aquí
    ) {
        // 1. Registrar el serializador global
        defaultSerializer = KotlinXSerializer(Json {
            ignoreUnknownKeys = true
            isLenient = true
        })

        // 2. Instalar Postgrest
        install(Postgrest)
    }
}