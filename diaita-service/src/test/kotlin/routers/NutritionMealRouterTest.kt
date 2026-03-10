package com.diaita.routers

import com.diaita.Container
import com.diaita.controllers.NutritionController
import com.diaita.dto.HistoricalMacroAveragesResponseDto
import com.diaita.dto.MealBucketItemResponseDto
import com.diaita.dto.MealBucketResponseDto
import com.diaita.dto.MealItemOperationsRequestDto
import com.diaita.dto.NutritionAnalyticsResponseDto
import com.diaita.dto.NutritionDaySummaryResponseDto
import com.diaita.dto.UpsertMealItemRequestDto
import com.diaita.dto.UpsertMealRequestDto
import com.diaita.dto.UpsertMealsRequestDto
import com.diaita.service.NutritionService
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

class NutritionMealRouterTest {

    private val json = Json
    private val service = mockk<NutritionService>()
    private val container = Container().apply {
        bind<NutritionService>(service)
    }
    private val controller = container.get<NutritionController>()

    private fun Application.testModule() {
        install(ContentNegotiation) {
            json()
        }
        configureNutritionRoutes(controller)
    }

    @Test
    fun upsertMeals_returns_200_for_single_meal_request() = testApplication {
        val request = validRequest()
        coEvery { service.upsertMeals(request) } returns summary()

        application { testModule() }

        val response = client.post("/nutrition/meals/upsert") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(json.encodeToString(request))
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("\"date\":\"2026-03-09\""))
    }

    @Test
    fun upsertMeals_returns_200_for_batch_request() = testApplication {
        val request = validRequest(
            meals = listOf(
                validMeal(mealType = "breakfast"),
                validMeal(
                    id = "11111111-1111-1111-1111-111111111111",
                    mealType = "lunch",
                    eatenAt = "2026-03-09T12:00:00Z"
                )
            )
        )
        coEvery { service.upsertMeals(request) } returns summary()

        application { testModule() }

        val response = client.post("/nutrition/meals/upsert") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(json.encodeToString(request))
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("\"totalCal\":95.0"))
    }

    @Test
    fun upsertMeals_returns_400_for_malformed_payload() = testApplication {
        application { testModule() }

        val response = client.post("/nutrition/meals/upsert") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody("""{"userId":"123"}""")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("Invalid request payload"))
    }

    @Test
    fun upsertMeals_returns_400_with_structured_validation_errors() = testApplication {
        val request = validRequest(userId = "bad-uuid")

        application { testModule() }

        val response = client.post("/nutrition/meals/upsert") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(json.encodeToString(request))
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("\"path\":\"userId\""))
    }

    @Test
    fun upsertMeals_returns_500_when_service_returns_null() = testApplication {
        val request = validRequest()
        coEvery { service.upsertMeals(request) } returns null

        application { testModule() }

        val response = client.post("/nutrition/meals/upsert") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(json.encodeToString(request))
        }

        assertEquals(HttpStatusCode.InternalServerError, response.status)
        assertTrue(response.bodyAsText().contains("Failed to save meals"))
    }

    private fun validRequest(
        userId: String = "123e4567-e89b-12d3-a456-426614174000",
        meals: List<UpsertMealRequestDto> = listOf(validMeal())
    ): UpsertMealsRequestDto {
        return UpsertMealsRequestDto(
            userId = userId,
            meals = meals
        )
    }

    private fun validMeal(
        id: String? = null,
        mealType: String = "breakfast",
        eatenAt: String = "2026-03-09T08:00:00Z"
    ): UpsertMealRequestDto {
        return UpsertMealRequestDto(
            id = id,
            mealType = mealType,
            eatenAt = eatenAt,
            itemOps = MealItemOperationsRequestDto(
                upsert = listOf(
                    UpsertMealItemRequestDto(
                        itemType = "ingredient",
                        itemId = "9003",
                        itemName = "apple",
                        quantity = 1.0,
                        unit = "piece",
                        calories = 95.0,
                        proteinG = 0.5,
                        carbsG = 25.0,
                        fatG = 0.3,
                        position = 0
                    )
                )
            )
        )
    }

    private fun summary(): NutritionDaySummaryResponseDto {
        return NutritionDaySummaryResponseDto(
            date = "2026-03-09",
            totalCal = 95.0,
            totalProtein = 0.5,
            totalFat = 0.3,
            totalCarb = 25.0,
            analytics = NutritionAnalyticsResponseDto(
                recCal = 2000.0,
                recProtein = 150.0,
                recCarb = 225.0,
                recFat = 70.0,
                historical = HistoricalMacroAveragesResponseDto(
                    avgCal = 1800.0,
                    avgProtein = 130.0,
                    avgFat = 60.0,
                    avgCarbs = 190.0
                )
            ),
            breakfast = MealBucketResponseDto(
                items = listOf(
                    MealBucketItemResponseDto(
                        foodName = "apple",
                        cal = 95.0,
                        fat = 0.3,
                        protein = 0.5,
                        carb = 25.0,
                        servings = 1.0,
                        servingSize = "piece"
                    )
                )
            ),
            lunch = MealBucketResponseDto(),
            dinner = MealBucketResponseDto(),
            snacks = MealBucketResponseDto()
        )
    }
}
