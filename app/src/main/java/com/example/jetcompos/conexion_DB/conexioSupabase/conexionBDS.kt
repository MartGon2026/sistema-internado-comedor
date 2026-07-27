package com.example.jetcompos.conexion_DB.conexioSupabase

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

object conexionBDS {
    val supabase = createSupabaseClient(
        supabaseUrl = "https://vtryizusiddlydztsgax.supabase.co",
        supabaseKey = "sb_publishable_QYlxUuYrIu5keLsueqjLBg_O34SKhh-"
    ) {
        install(Postgrest)
    }
}