package com.diaita

import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.defaultheaders.*
import kotlinx.serialization.json.Json

fun Application.configureHTTP() {
    install(DefaultHeaders) {
        header("X-Engine", "Ktor")
    }


    install(CORS) {
        allowHost("localhost:3000", schemes = listOf("http"))

        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Patch)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Options)

        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)

        allowCredentials = true // only if using cookies
    }
}
