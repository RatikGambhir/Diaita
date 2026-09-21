package com.example

import com.diaita.module
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals
import java.nio.file.Files

class Testing {

    @Test
    fun application_starts_with_test_config_and_routes_are_loaded() = testApplication {
        environment {
            val databasePath = Files.createTempFile("diaita-app-test-", ".db").toString()
            config = MapApplicationConfig(
                "database.path" to databasePath,
                "jwt.secret" to "a-test-secret-that-is-long-enough",
                "jwt.issuer" to "diaita-test",
                "jwt.audience" to "diaita-web-test",
                "jwt.realm" to "Diaita test",
                "jwt.lifetimeSeconds" to "3600",
                "foodapi.apiKey" to "test-nutrition-key",
                "foodapi.url" to "https://api.spoonacular.com"
            )
        }

        application {
            module()
        }

        client.get("/auth/me").apply {
            assertEquals(HttpStatusCode.Unauthorized, status)
        }
    }

}
