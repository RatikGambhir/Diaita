package com.diaita.lib.clients

import com.diaita.lib.logging.loggerFor
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.slf4j.Logger

/**
 * Base for the outbound HTTP clients: shared JSON negotiation, timeouts and a
 * default base [url].
 */
abstract class RestClient(url: String) {

    protected val log: Logger = loggerFor(this::class.java)

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
            url(url)
        }
    }

    /**
     * Runs an upstream call, returning `null` and logging instead of propagating
     * failures — callers surface this to the API as a 5xx.
     */
    protected suspend fun <T> callOrNull(description: String, block: suspend () -> T): T? =
        try {
            block()
        } catch (e: Exception) {
            log.error("{} failed: {}", description, e.message, e)
            null
        }
}
