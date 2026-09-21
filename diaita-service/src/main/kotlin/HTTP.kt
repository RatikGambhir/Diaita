package com.diaita

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.defaultheaders.*

fun Application.configureHTTP() {
    val allowedHosts = System.getenv("DIAITA_CORS_HOSTS")
        ?.takeIf(String::isNotBlank)
        ?: environment.config.propertyOrNull("cors.allowedHosts")?.getString()
        ?: "localhost:3000,127.0.0.1:3000"

    install(DefaultHeaders) {
        header("X-Engine", "Ktor")
    }

    install(CORS) {
        allowedHosts.split(',')
            .map(String::trim)
            .filter(String::isNotEmpty)
            .forEach { allowHost(it, schemes = listOf("http", "https")) }

        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Patch)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Options)

        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)

        allowCredentials = false
    }
}
