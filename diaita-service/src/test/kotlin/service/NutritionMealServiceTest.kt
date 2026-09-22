package com.diaita.service

import com.diaita.dto.MealItemOperationsRequestDto
import com.diaita.dto.UpsertMealItemRequestDto
import com.diaita.dto.UpsertMealRequestDto
import com.diaita.dto.UpsertMealsRequestDto
import com.diaita.entity.MealItemRowEntity
import com.diaita.entity.MealRowEntity
import com.diaita.lib.clients.NutritionRestClient
import com.diaita.repo.NutritionRepo
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class NutritionMealServiceTest {
    private val repo = mockk<NutritionRepo>()
    private val client = mockk<NutritionRestClient>(relaxed = true)
    private val service = NutritionService(repo, client)
    private val userId = "123e4567-e89b-12d3-a456-426614174000"

    @Test
    fun getNutritionDaySummary_aggregates_and_returns_persisted_ids() = runBlocking {
        coEvery { repo.getMealsForUser(userId) } returns listOf(
            MealRowEntity("meal-breakfast", userId, "breakfast", "2026-03-09T08:00:00Z"),
            MealRowEntity("meal-old", userId, "breakfast", "2026-03-08T08:00:00Z")
        )
        coEvery { repo.getMealItemsForUser(userId) } returns listOf(
            item("item-1", "meal-breakfast", "apple", 95.0, 0.5, 25.0, 0.3),
            item("item-2", "meal-old", "oats", 300.0, 10.0, 50.0, 5.0)
        )

        val result = service.getNutritionDaySummary(userId, "2026-03-09")

        assertEquals(95.0, result?.totalCal)
        assertEquals("meal-breakfast", result?.breakfast?.mealId)
        assertEquals("item-1", result?.breakfast?.items?.single()?.id)
        assertEquals(197.5, result?.analytics?.historical?.avgCal)
    }

    @Test
    fun upsertMeals_normalizes_then_reads_the_updated_day() = runBlocking {
        coEvery { repo.upsertMeals(any()) } returns true
        coEvery { repo.getMealsForUser(userId) } returns emptyList()
        coEvery { repo.getMealItemsForUser(userId) } returns emptyList()

        val result = service.upsertMeals(rawRequest())

        assertEquals("2026-03-09", result?.date)
        coVerify(exactly = 1) {
            repo.upsertMeals(match {
                it.userId == userId &&
                    it.meals.single().mealType == "breakfast" &&
                    it.meals.single().itemOps.upsert.single().itemName == "Apple"
            })
        }
    }

    @Test
    fun upsertMeals_returns_null_when_transaction_fails() = runBlocking {
        coEvery { repo.upsertMeals(any()) } returns false
        assertNull(service.upsertMeals(rawRequest()))
    }

    private fun rawRequest() = UpsertMealsRequestDto(
        userId = " $userId ",
        meals = listOf(
            UpsertMealRequestDto(
                mealType = " Breakfast ",
                eatenAt = " 2026-03-09T08:00:00Z ",
                itemOps = MealItemOperationsRequestDto(
                    upsert = listOf(
                        UpsertMealItemRequestDto(
                            itemType = " Custom ",
                            itemName = " Apple ",
                            quantity = 1.0,
                            unit = " piece ",
                            calories = 95.0,
                            proteinG = 0.5,
                            carbsG = 25.0,
                            fatG = 0.3,
                            position = 0
                        )
                    )
                )
            )
        )
    )

    private fun item(
        id: String,
        mealId: String,
        name: String,
        calories: Double,
        protein: Double,
        carbs: Double,
        fat: Double
    ) = MealItemRowEntity(
        id = id,
        mealId = mealId,
        userId = userId,
        itemType = "custom",
        itemName = name,
        quantity = 1.0,
        unit = "serving",
        calories = calories,
        proteinG = protein,
        carbsG = carbs,
        fatG = fat,
        position = 0
    )
}
