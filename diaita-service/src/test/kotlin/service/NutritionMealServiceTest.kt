package com.diaita.service

import com.diaita.Container
import com.diaita.dto.HistoricalMacroAveragesResponseDto
import com.diaita.dto.MealBucketItemResponseDto
import com.diaita.dto.MealBucketResponseDto
import com.diaita.dto.MealItemOperationsRequestDto
import com.diaita.dto.NutritionAnalyticsResponseDto
import com.diaita.dto.NutritionDaySummaryResponseDto
import com.diaita.dto.UpsertMealItemRequestDto
import com.diaita.dto.UpsertMealRequestDto
import com.diaita.dto.UpsertMealsRequestDto
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
    private val container = Container().apply {
        bind<NutritionRepo>(repo)
        bind<NutritionRestClient>(client)
    }
    private val service = container.get<NutritionService>()

    @Test
    fun upsertMeals_normalizes_request_before_passing_to_repo() = runBlocking {
        val rawRequest = UpsertMealsRequestDto(
            userId = " 123e4567-e89b-12d3-a456-426614174000 ",
            meals = listOf(
                UpsertMealRequestDto(
                    mealType = " Breakfast ",
                    eatenAt = " 2026-03-09T08:00:00Z ",
                    notes = "  morning meal  ",
                    itemOps = MealItemOperationsRequestDto(
                        upsert = listOf(
                            UpsertMealItemRequestDto(
                                itemType = " Ingredient ",
                                itemId = " 9003 ",
                                itemName = " Apple ",
                                brandName = " Orchard ",
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

        coEvery { repo.upsertMeals(any()) } returns summary()

        service.upsertMeals(rawRequest)

        coVerify(exactly = 1) {
            repo.upsertMeals(
                match {
                    it.userId == "123e4567-e89b-12d3-a456-426614174000" &&
                        it.meals.single().mealType == "breakfast" &&
                        it.meals.single().eatenAt == "2026-03-09T08:00:00Z" &&
                        it.meals.single().notes == "morning meal" &&
                        it.meals.single().itemOps.upsert.single().itemType == "ingredient" &&
                        it.meals.single().itemOps.upsert.single().itemName == "Apple" &&
                        it.meals.single().itemOps.upsert.single().itemId == "9003" &&
                        it.meals.single().itemOps.upsert.single().unit == "piece"
                }
            )
        }
    }

    @Test
    fun upsertMeals_sorts_breakfast_items_by_food_name() = runBlocking {
        coEvery { repo.upsertMeals(any()) } returns summary(
            breakfast = MealBucketResponseDto(
                items = listOf(
                    MealBucketItemResponseDto("zucchini", 33.0, 0.1, 2.4, 6.1, 1.0, "cup"),
                    MealBucketItemResponseDto("apple", 95.0, 0.3, 0.5, 25.0, 1.0, "piece")
                )
            )
        )

        val result = service.upsertMeals(validRequest())

        assertEquals(listOf("apple", "zucchini"), result?.breakfast?.items?.map { it.foodName })
    }

    @Test
    fun upsertMeals_sorts_snack_items_by_food_name() = runBlocking {
        coEvery { repo.upsertMeals(any()) } returns summary(
            snacks = MealBucketResponseDto(
                items = listOf(
                    MealBucketItemResponseDto("yogurt", 120.0, 2.0, 12.0, 10.0, 1.0, "cup"),
                    MealBucketItemResponseDto("almonds", 164.0, 14.0, 6.0, 6.0, 1.0, "oz")
                )
            )
        )

        val result = service.upsertMeals(validRequest())

        assertEquals(listOf("almonds", "yogurt"), result?.snacks?.items?.map { it.foodName })
    }

    @Test
    fun upsertMeals_returns_null_when_repo_returns_null() = runBlocking {
        coEvery { repo.upsertMeals(any()) } returns null

        val result = service.upsertMeals(validRequest())

        assertNull(result)
    }

    private fun validRequest(): UpsertMealsRequestDto {
        return UpsertMealsRequestDto(
            userId = "123e4567-e89b-12d3-a456-426614174000",
            meals = listOf(
                UpsertMealRequestDto(
                    mealType = "breakfast",
                    eatenAt = "2026-03-09T08:00:00Z",
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
            )
        )
    }

    private fun summary(
        breakfast: MealBucketResponseDto = MealBucketResponseDto(),
        lunch: MealBucketResponseDto = MealBucketResponseDto(),
        dinner: MealBucketResponseDto = MealBucketResponseDto(),
        snacks: MealBucketResponseDto = MealBucketResponseDto()
    ): NutritionDaySummaryResponseDto {
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
            breakfast = breakfast,
            lunch = lunch,
            dinner = dinner,
            snacks = snacks
        )
    }
}
