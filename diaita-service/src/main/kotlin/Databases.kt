package com.diaita

import com.diaita.lib.factories.SupabaseManager
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.ktor.server.application.*


fun Application.configureDatabases(): SupabaseManager {
    val client = connectToSupabase()
    log.info("Connected to Supabase!")
    return SupabaseManager(client)
}

fun Application.connectToSupabase(): SupabaseClient {
    val supabaseUrl = environment.config.property("postgres.url").getString()
    val supabaseKey = environment.config.property("postgres.secret_key").getString()

    return createSupabaseClient(
        supabaseUrl = supabaseUrl,
        supabaseKey = supabaseKey
    ) {
        install(Postgrest)
    }
}
