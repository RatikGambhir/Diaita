package com.nutrify.routers

import com.nutrify.controllers.WorkoutController
import com.nutrify.dto.ExerciseDto
import com.nutrify.dto.PaginationMetadataDto
import com.nutrify.dto.WorkoutSearchRequestDto
import com.nutrify.dto.WorkoutSearchResponseDto
import com.nutrify.service.WorkoutService
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.testing.testApplication
import io.ktor.serialization.kotlinx.json.json
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WorkoutRouterTest {

    private val json = Json

    private fun Application.testModule(controller: WorkoutController) {
        install(ContentNegotiation) {
            json()
        }
        configureWorkoutRoutes(controller)
    }

    @Test
    fun search_returns_200_and_response_body_on_success() = testApplication {
        val service = mockk<WorkoutService>()
        val controller = WorkoutController(service)
        val request = WorkoutSearchRequestDto(exerciseType = "Cardio")

        coEvery { service.searchWorkouts(request) } returns WorkoutSearchResponseDto(
            exercises = listOf(
                ExerciseDto(
                    id = 1,
                    exercise = "Running",
                    exerciseType = "Cardio",
                    description = "Equipment: None | Mechanics: Isolation"
                )
            ),
            pagination = PaginationMetadataDto(
                total = 1,
                page = 0,
                pageSize = 20,
                totalPages = 1,
                hasMore = false,
                hasPrevious = false
            )
        )

        application { testModule(controller) }

        val response = client.post("/workouts/search") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(json.encodeToString(request))
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("Running"))
    }

    @Test
    fun search_returns_400_for_invalid_payload() = testApplication {
        val service = mockk<WorkoutService>()
        val controller = WorkoutController(service)
        application { testModule(controller) }

        val response = client.post("/workouts/search") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody("""{"foo":"bar"}""")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("Invalid request payload"))
    }

    @Test
    fun search_returns_400_for_validation_error() = testApplication {
        val service = mockk<WorkoutService>()
        val controller = WorkoutController(service)
        application { testModule(controller) }

        val response = client.post("/workouts/search") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(json.encodeToString(WorkoutSearchRequestDto(page = -1, exerciseType = "Cardio")))
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("non-negative"))
    }

    @Test
    fun search_returns_500_when_service_returns_null() = testApplication {
        val service = mockk<WorkoutService>()
        val controller = WorkoutController(service)
        val request = WorkoutSearchRequestDto(exerciseType = "Cardio")

        coEvery { service.searchWorkouts(request) } returns null

        application { testModule(controller) }

        val response = client.post("/workouts/search") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(json.encodeToString(request))
        }

        assertEquals(HttpStatusCode.InternalServerError, response.status)
        assertTrue(response.bodyAsText().contains("Failed to search workouts"))
    }
}
