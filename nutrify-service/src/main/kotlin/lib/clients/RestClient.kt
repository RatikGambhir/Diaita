package com.nutrify.lib.clients

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType.Application.Json
import io.ktor.http.buildUrl
import io.ktor.http.parseUrl
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

abstract class RestClient(apiKey: String, url: String) {
    val client: HttpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true; explicitNulls = false })
        }
        install(HttpTimeout) {
            connectTimeoutMillis = 10_000
            socketTimeoutMillis = 90_000        // idle timeout between chunks
            requestTimeoutMillis = 480_000      // 8 minutes overall cap
        }
        defaultRequest {
                headers.append("Content-Type", "application/json")
                headers.append("x-goog-api-key",apiKey)
            url(url)
        }
    }

    fun searchRecipes(query: String): String {
        return ""
    }

    abstract fun getFood(): String
}