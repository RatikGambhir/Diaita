package com.diaita.routers

import com.diaita.Container
import com.diaita.controllers.UserController
import com.diaita.dto.ServiceResult
import com.diaita.service.UserService
import com.diaita.testdata.RecommendationTestData
import io.ktor.client.request.post
import io.ktor.http.contentType
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.testing.testApplication
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RecommendationGenerateRouterTest {

    private val service = mockk<UserService>()
    private val container = Container().apply {
        bind<UserService>(service)
    }
    private val controller = container.get<UserController>()

    private val userId = "123e4567-e89b-12d3-a456-426614174000"

    private fun Application.testModule() {
        install(ContentNegotiation) {
            json()
        }
        configureUserRoutes(controller)
    }

    @Test
    fun generate_forwards_supplied_preferences() = testApplication {
        val preferences = mapOf("workoutType" to "strength", "duration" to "45")

        coEvery {
            service.generateAndSaveRecommendations(userId, preferences)
        } returns ServiceResult.Success(RecommendationTestData.recommendation())

        application { testModule() }

        val response = client.post("/users/$userId/recommendations/generate") {
            contentType(ContentType.Application.Json)
            setBody("""{"preferences":{"workoutType":"strength","duration":"45"}}""")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        coVerify(exactly = 1) { service.generateAndSaveRecommendations(userId, preferences) }
    }

    @Test
    fun generate_treats_a_missing_body_as_no_preferences() = testApplication {
        coEvery {
            service.generateAndSaveRecommendations(userId, emptyMap())
        } returns ServiceResult.Success(RecommendationTestData.recommendation())

        application { testModule() }

        val response = client.post("/users/$userId/recommendations/generate")

        assertEquals(HttpStatusCode.OK, response.status)
        coVerify(exactly = 1) { service.generateAndSaveRecommendations(userId, emptyMap()) }
    }

    @Test
    fun generate_rejects_an_overlong_preference_value() = testApplication {
        application { testModule() }

        val response = client.post("/users/$userId/recommendations/generate") {
            contentType(ContentType.Application.Json)
            setBody("""{"preferences":{"goal":"${"x".repeat(201)}"}}""")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("at most"))
        coVerify(exactly = 0) { service.generateAndSaveRecommendations(any(), any()) }
    }

    @Test
    fun generate_rejects_a_malformed_body_instead_of_silently_ignoring_it() = testApplication {
        application { testModule() }

        val response = client.post("/users/$userId/recommendations/generate") {
            contentType(ContentType.Application.Json)
            setBody("""{"preferences":"not-an-object"}""")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        coVerify(exactly = 0) { service.generateAndSaveRecommendations(any(), any()) }
    }

    @Test
    fun generate_returns_500_when_generation_fails() = testApplication {
        coEvery {
            service.generateAndSaveRecommendations(userId, emptyMap())
        } returns ServiceResult.Failure("genRecommendations failed: returned null")

        application { testModule() }

        val response = client.post("/users/$userId/recommendations/generate")

        assertEquals(HttpStatusCode.InternalServerError, response.status)
        assertTrue(response.bodyAsText().contains("genRecommendations"))
    }
}
