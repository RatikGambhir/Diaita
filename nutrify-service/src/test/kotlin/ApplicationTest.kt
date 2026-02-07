package com.example

import com.nutrify.module
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class Testing {

    @Test
    fun application_starts_with_test_config_and_routes_are_loaded() = testApplication {
        environment {
            config = MapApplicationConfig(
                "postgres.url" to "https://example.supabase.co",
                "postgres.secret_key" to "test-publishable-key",
                "api.gemini" to "test-gemini-key",
                "api.nutrition" to "test-nutrition-key"
            )
        }

        application {
            module()
        }

        client.get("/register").apply {
            assertEquals(HttpStatusCode.MethodNotAllowed, status)
        }
    }

}
