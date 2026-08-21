package com.diaita.routers

import com.diaita.Container
import com.diaita.controllers.WorkoutController
import com.diaita.dto.CreateWorkoutRequestDto
import com.diaita.dto.PaginationMetadataDto
import com.diaita.dto.UpdateWorkoutRequestDto
import com.diaita.dto.UpsertWorkoutExerciseDto
import com.diaita.dto.WorkoutDto
import com.diaita.dto.WorkoutListResponseDto
import com.diaita.dto.WorkoutStatsResponseDto
import com.diaita.service.WorkoutService
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.testing.testApplication
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WorkoutLogRouterTest {

    private val json = Json
    private val service = mockk<WorkoutService>()
    private val container = Container().apply {
        bind<WorkoutService>(service)
    }
    private val controller = container.get<WorkoutController>()

    private val userId = "11111111-1111-1111-1111-111111111111"

    private fun Application.testModule() {
        install(ContentNegotiation) {
            json()
        }
        configureWorkoutRoutes(controller)
    }

    private fun workout(id: String = "workout-1", name: String = "Leg Day") = WorkoutDto(
        id = id,
        userId = userId,
        name = name,
        performedAt = "2026-03-04T00:00Z"
    )

    @Test
    fun create_returns_201_with_the_created_workout() = testApplication {
        val request = CreateWorkoutRequestDto(
            userId = userId,
            name = "Leg Day",
            exercises = listOf(UpsertWorkoutExerciseDto(name = "Squat", category = "lifting"))
        )

        coEvery { service.createWorkout(request) } returns workout()

        application { testModule() }

        val response = client.post("/workouts") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(json.encodeToString(request))
        }

        assertEquals(HttpStatusCode.Created, response.status)
        assertTrue(response.bodyAsText().contains("Leg Day"))
    }

    @Test
    fun create_returns_400_when_the_category_is_unknown() = testApplication {
        application { testModule() }

        val response = client.post("/workouts") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(
                json.encodeToString(
                    CreateWorkoutRequestDto(
                        userId = userId,
                        name = "Leg Day",
                        exercises = listOf(UpsertWorkoutExerciseDto(name = "Squat", category = "yoga"))
                    )
                )
            )
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("Invalid exercise category"))
    }

    @Test
    fun create_returns_400_when_the_name_is_blank() = testApplication {
        application { testModule() }

        val response = client.post("/workouts") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(json.encodeToString(CreateWorkoutRequestDto(userId = userId, name = "   ")))
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("name is required"))
    }

    @Test
    fun create_returns_400_for_a_malformed_performedAt() = testApplication {
        application { testModule() }

        val response = client.post("/workouts") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(
                json.encodeToString(
                    CreateWorkoutRequestDto(userId = userId, name = "Leg Day", performedAt = "04/03/2026")
                )
            )
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("ISO-8601"))
    }

    @Test
    fun create_returns_500_when_the_service_cannot_persist() = testApplication {
        val request = CreateWorkoutRequestDto(userId = userId, name = "Leg Day")

        coEvery { service.createWorkout(request) } returns null

        application { testModule() }

        val response = client.post("/workouts") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(json.encodeToString(request))
        }

        assertEquals(HttpStatusCode.InternalServerError, response.status)
        assertTrue(response.bodyAsText().contains("Failed to create workout"))
    }

    @Test
    fun list_returns_200_and_forwards_query_and_paging() = testApplication {
        coEvery { service.listWorkouts(userId, "leg", 2, 5) } returns WorkoutListResponseDto(
            workouts = listOf(workout()),
            pagination = PaginationMetadataDto(
                total = 11,
                page = 2,
                pageSize = 5,
                totalPages = 3,
                hasMore = false,
                hasPrevious = true
            )
        )

        application { testModule() }

        val response = client.get("/workouts?userId=$userId&query=leg&page=2&pageSize=5")

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("Leg Day"))
        coVerify(exactly = 1) { service.listWorkouts(userId, "leg", 2, 5) }
    }

    @Test
    fun list_returns_400_without_a_userId() = testApplication {
        application { testModule() }

        val response = client.get("/workouts")

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("userId"))
    }

    @Test
    fun list_returns_400_for_an_out_of_range_pageSize() = testApplication {
        application { testModule() }

        val response = client.get("/workouts?userId=$userId&pageSize=500")

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("pageSize"))
    }

    @Test
    fun get_returns_200_for_an_owned_workout() = testApplication {
        coEvery { service.getWorkout("workout-1", userId) } returns workout()

        application { testModule() }

        val response = client.get("/workouts/workout-1?userId=$userId")

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("Leg Day"))
    }

    @Test
    fun get_returns_404_when_the_workout_is_not_found() = testApplication {
        coEvery { service.getWorkout("workout-1", userId) } returns null

        application { testModule() }

        val response = client.get("/workouts/workout-1?userId=$userId")

        assertEquals(HttpStatusCode.NotFound, response.status)
        assertTrue(response.bodyAsText().contains("Workout not found"))
    }

    @Test
    fun stats_route_wins_over_the_id_route() = testApplication {
        coEvery {
            service.getStats(userId, LocalDate.parse("2026-03-01"), LocalDate.parse("2026-03-31"))
        } returns WorkoutStatsResponseDto(workoutCount = 4)

        application { testModule() }

        val response = client.get("/workouts/stats?userId=$userId&start=2026-03-01&end=2026-03-31")

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("\"workoutCount\":4"))
        coVerify(exactly = 0) { service.getWorkout(any(), any()) }
    }

    @Test
    fun stats_returns_400_when_start_is_after_end() = testApplication {
        application { testModule() }

        val response = client.get("/workouts/stats?userId=$userId&start=2026-03-31&end=2026-03-01")

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("must not be after"))
    }

    @Test
    fun stats_returns_400_for_a_malformed_date() = testApplication {
        application { testModule() }

        val response = client.get("/workouts/stats?userId=$userId&start=March")

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("YYYY-MM-DD"))
    }

    @Test
    fun update_returns_200_with_the_updated_workout() = testApplication {
        val request = UpdateWorkoutRequestDto(userId = userId, name = "Leg Day v2")

        coEvery { service.updateWorkout("workout-1", request) } returns workout(name = "Leg Day v2")

        application { testModule() }

        val response = client.put("/workouts/workout-1") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(json.encodeToString(request))
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("Leg Day v2"))
    }

    @Test
    fun update_returns_404_when_the_workout_is_missing() = testApplication {
        val request = UpdateWorkoutRequestDto(userId = userId)

        coEvery { service.updateWorkout("workout-1", request) } returns null

        application { testModule() }

        val response = client.put("/workouts/workout-1") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(json.encodeToString(request))
        }

        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun update_returns_400_for_a_blank_delete_id() = testApplication {
        application { testModule() }

        val response = client.put("/workouts/workout-1") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody("""{"userId":"$userId","exerciseOps":{"deleteIds":[" "]}}""")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("deleteIds"))
    }

    @Test
    fun delete_returns_200_when_the_workout_was_removed() = testApplication {
        coEvery { service.deleteWorkout("workout-1", userId) } returns true

        application { testModule() }

        val response = client.delete("/workouts/workout-1?userId=$userId")

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("deleted"))
    }

    @Test
    fun delete_returns_404_when_the_workout_is_not_owned() = testApplication {
        coEvery { service.deleteWorkout("workout-1", userId) } returns false

        application { testModule() }

        val response = client.delete("/workouts/workout-1?userId=$userId")

        assertEquals(HttpStatusCode.NotFound, response.status)
    }
}
