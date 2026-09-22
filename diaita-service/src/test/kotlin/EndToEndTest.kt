package com.diaita

import com.diaita.dto.AuthSessionDto
import com.diaita.dto.LoginRequestDto
import com.diaita.dto.MealItemOperationsRequestDto
import com.diaita.dto.NutritionDaySummaryResponseDto
import com.diaita.dto.RegisterRequestDto
import com.diaita.dto.UpsertMealItemRequestDto
import com.diaita.dto.UpsertMealRequestDto
import com.diaita.dto.UpsertMealsRequestDto
import com.diaita.dto.UpsertWorkoutExerciseDto
import com.diaita.dto.UpsertWorkoutRequestDto
import com.diaita.dto.WorkoutLogDto
import com.diaita.testdata.UserProfileTestData
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.testApplication
import java.nio.file.Files
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EndToEndTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun authenticated_user_can_save_profile_food_and_workout_then_logout() = testApplication {
        environment {
            config = MapApplicationConfig(
                "database.path" to Files.createTempFile("diaita-e2e-", ".db").toString(),
                "jwt.secret" to "a-test-secret-that-is-long-enough",
                "jwt.issuer" to "diaita-test",
                "jwt.audience" to "diaita-web-test",
                "jwt.realm" to "Diaita test",
                "jwt.lifetimeSeconds" to "3600",
                "foodapi.apiKey" to "",
                "foodapi.url" to "https://api.spoonacular.com"
            )
        }
        application { module() }

        val registerResponse = client.post("/auth/register") {
            jsonBody(RegisterRequestDto("e2e@example.com", "password1", "End To End"))
        }
        assertEquals(HttpStatusCode.Created, registerResponse.status)
        val session = json.decodeFromString<AuthSessionDto>(registerResponse.bodyAsText())

        val profile = UserProfileTestData.fullRequest(userId = session.user.id)
        val profileResponse = client.post("/user/profile") {
            authorizedJson(session.accessToken, profile)
        }
        assertEquals(HttpStatusCode.OK, profileResponse.status)

        val nutritionResponse = client.post("/nutrition/meals/upsert") {
            authorizedJson(
                session.accessToken,
                UpsertMealsRequestDto(
                    userId = session.user.id,
                    meals = listOf(
                        UpsertMealRequestDto(
                            mealType = "breakfast",
                            eatenAt = "2026-08-06T08:00:00Z",
                            itemOps = MealItemOperationsRequestDto(
                                upsert = listOf(
                                    UpsertMealItemRequestDto(
                                        itemType = "custom",
                                        itemName = "Greek yogurt",
                                        quantity = 1.0,
                                        unit = "cup",
                                        calories = 150.0,
                                        proteinG = 20.0,
                                        carbsG = 8.0,
                                        fatG = 3.0,
                                        position = 0
                                    )
                                )
                            )
                        )
                    )
                )
            )
        }
        assertEquals(HttpStatusCode.OK, nutritionResponse.status)
        val summary = json.decodeFromString<NutritionDaySummaryResponseDto>(nutritionResponse.bodyAsText())
        assertEquals(150.0, summary.totalCal)
        assertEquals("Greek yogurt", summary.breakfast.items.single().foodName)

        val workoutResponse = client.post("/workouts") {
            authorizedJson(
                session.accessToken,
                UpsertWorkoutRequestDto(
                    name = "Full Body",
                    performedAt = "2026-08-06T18:00:00Z",
                    durationMinutes = 45,
                    exercises = listOf(
                        UpsertWorkoutExerciseDto(
                            exerciseId = 2,
                            exerciseName = "Bench Press",
                            sets = 3,
                            reps = 8,
                            weightKg = 60.0
                        )
                    )
                )
            )
        }
        assertEquals(HttpStatusCode.Created, workoutResponse.status)
        val workout = json.decodeFromString<WorkoutLogDto>(workoutResponse.bodyAsText())
        assertEquals(1440.0, workout.totalVolumeKg)

        val workoutList = client.get("/workouts") { bearer(session.accessToken) }
        assertEquals(HttpStatusCode.OK, workoutList.status)
        assertTrue(workoutList.bodyAsText().contains("Full Body"))

        val logout = client.post("/auth/logout") { bearer(session.accessToken) }
        assertEquals(HttpStatusCode.OK, logout.status)
        assertEquals(HttpStatusCode.Unauthorized, client.get("/workouts") { bearer(session.accessToken) }.status)
    }

    private inline fun <reified T> io.ktor.client.request.HttpRequestBuilder.jsonBody(value: T) {
        header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        setBody(json.encodeToString(value))
    }

    private inline fun <reified T> io.ktor.client.request.HttpRequestBuilder.authorizedJson(token: String, value: T) {
        bearer(token)
        jsonBody(value)
    }

    private fun io.ktor.client.request.HttpRequestBuilder.bearer(token: String) {
        header(HttpHeaders.Authorization, "Bearer $token")
    }
}
