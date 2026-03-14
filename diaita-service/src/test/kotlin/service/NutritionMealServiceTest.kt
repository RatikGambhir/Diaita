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
    private val container = Container().apply {
        bind<NutritionRepo>(repo)
        bind<NutritionRestClient>(client)
    }
    private val service = container.get<NutritionService>()

    @Test
    fun getNutritionDaySummary_aggregates_totals_and_meal_buckets() = runBlocking {
        coEvery { repo.getMealsForUser("123e4567-e89b-12d3-a456-426614174000") } returns listOf(
            MealRowEntity(
                id = "meal-breakfast",
                userId = "123e4567-e89b-12d3-a456-426614174000",
                mealType = "breakfast",
                eatenAt = "2026-03-09T08:00:00Z"
            ),
            MealRowEntity(
                id = "meal-lunch",
                userId = "123e4567-e89b-12d3-a456-426614174000",
                mealType = "lunch",
                eatenAt = "2026-03-09T12:00:00Z"
            ),
            MealRowEntity(
                id = "meal-old",
                userId = "123e4567-e89b-12d3-a456-426614174000",
                mealType = "breakfast",
                eatenAt = "2026-03-08T08:00:00Z"
            )
        )
        coEvery { repo.getMealItemsForUser("123e4567-e89b-12d3-a456-426614174000") } returns listOf(
            MealItemRowEntity(
                id = "item-1",
                mealId = "meal-breakfast",
                userId = "123e4567-e89b-12d3-a456-426614174000",
                itemType = "ingredient",
                itemName = "apple",
                quantity = 1.0,
                unit = "piece",
                calories = 95.0,
                proteinG = 0.5,
                carbsG = 25.0,
                fatG = 0.3,
                position = 0
            ),
            MealItemRowEntity(
                id = "item-2",
                mealId = "meal-lunch",
                userId = "123e4567-e89b-12d3-a456-426614174000",
                itemType = "ingredient",
                itemName = "burger",
                quantity = 1.0,
                unit = "serving",
                calories = 500.0,
                proteinG = 30.0,
                carbsG = 40.0,
                fatG = 20.0,
                position = 0
            ),
            MealItemRowEntity(
                id = "item-3",
                mealId = "meal-old",
                userId = "123e4567-e89b-12d3-a456-426614174000",
                itemType = "ingredient",
                itemName = "oats",
                quantity = 1.0,
                unit = "bowl",
                calories = 300.0,
                proteinG = 10.0,
                carbsG = 50.0,
                fatG = 5.0,
                position = 0
            )
        )

        val result = service.getNutritionDaySummary(
            "123e4567-e89b-12d3-a456-426614174000",
            "2026-03-09"
        )

        assertEquals("2026-03-09", result?.date)
        assertEquals(595.0, result?.totalCal)
        assertEquals(30.5, result?.totalProtein)
        assertEquals(20.3, result?.totalFat)
        assertEquals(65.0, result?.totalCarb)
        assertEquals(listOf("apple"), result?.breakfast?.items?.map { it.foodName })
        assertEquals(listOf("burger"), result?.lunch?.items?.map { it.foodName })
        assertEquals(447.5, result?.analytics?.historical?.avgCal)
    }

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
