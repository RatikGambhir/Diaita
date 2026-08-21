package com.diaita.service

import com.diaita.Container
import com.diaita.dto.AdjustmentRulesDto
import com.diaita.dto.CaloriesDto
import com.diaita.dto.CheckinsDto
import com.diaita.dto.FoodsDto
import com.diaita.dto.MacroRatioDto
import com.diaita.dto.MacrosDto
import com.diaita.dto.MealStructureDto
import com.diaita.dto.NutritionRecommendationDto
import com.diaita.entity.MealItemRowEntity
import com.diaita.entity.MealRowEntity
import com.diaita.lib.clients.NutritionRestClient
import com.diaita.repo.NutritionRepo
import com.diaita.repo.RecommendationRepo
import com.diaita.testdata.RecommendationTestData
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class NutritionSeriesServiceTest {

    private val repo = mockk<NutritionRepo>()
    private val client = mockk<NutritionRestClient>(relaxed = true)
    private val recommendationRepo = mockk<RecommendationRepo>()
    private val container = Container().apply {
        bind<NutritionRepo>(repo)
        bind<NutritionRestClient>(client)
        bind<RecommendationRepo>(recommendationRepo)
    }
    private val service = container.get<NutritionService>()

    private val userId = "123e4567-e89b-12d3-a456-426614174000"

    private fun meal(id: String, eatenAt: String, mealType: String = "lunch") = MealRowEntity(
        id = id,
        userId = userId,
        mealType = mealType,
        eatenAt = eatenAt
    )

    private fun item(
        id: String,
        mealId: String,
        calories: Double,
        proteinG: Double = 0.0,
        carbsG: Double = 0.0,
        fatG: Double = 0.0
    ) = MealItemRowEntity(
        id = id,
        mealId = mealId,
        userId = userId,
        itemType = "ingredient",
        itemName = "Food $id",
        quantity = 1.0,
        calories = calories,
        proteinG = proteinG,
        carbsG = carbsG,
        fatG = fatG,
        position = 0
    )

    @Test
    fun dailySeries_emits_one_point_per_day_and_zero_fills_days_without_meals() = runBlocking {
        coEvery { repo.getMealsForUser(userId) } returns listOf(
            meal("m1", "2026-03-01T12:00:00Z"),
            meal("m2", "2026-03-03T12:00:00Z")
        )
        coEvery { repo.getMealItemsForUser(userId) } returns listOf(
            item("i1", "m1", calories = 500.0, proteinG = 40.0),
            item("i2", "m3", calories = 900.0)
        )
        coEvery { recommendationRepo.getRecommendationByUserId(userId) } returns null

        val series = service.getNutritionDailySeries(
            userId,
            LocalDate.parse("2026-03-01"),
            LocalDate.parse("2026-03-04")
        )

        assertEquals(listOf("2026-03-01", "2026-03-02", "2026-03-03", "2026-03-04"), series.days.map { it.date })
        assertEquals(500.0, series.days[0].totalCal)
        assertEquals(40.0, series.days[0].totalProtein)
        assertEquals(0.0, series.days[1].totalCal)
        // "m3" has no matching meal row, so its item contributes to no day.
        assertEquals(0.0, series.days[2].totalCal)
        assertEquals(0.0, series.days[3].totalCal)
    }

    @Test
    fun dailySeries_excludes_meals_outside_the_requested_range() = runBlocking {
        coEvery { repo.getMealsForUser(userId) } returns listOf(
            meal("before", "2026-02-28T12:00:00Z"),
            meal("inside", "2026-03-02T12:00:00Z"),
            meal("after", "2026-03-10T12:00:00Z")
        )
        coEvery { repo.getMealItemsForUser(userId) } returns listOf(
            item("i1", "before", calories = 111.0),
            item("i2", "inside", calories = 222.0),
            item("i3", "after", calories = 333.0)
        )
        coEvery { recommendationRepo.getRecommendationByUserId(userId) } returns null

        val series = service.getNutritionDailySeries(
            userId,
            LocalDate.parse("2026-03-01"),
            LocalDate.parse("2026-03-03")
        )

        assertEquals(222.0, series.days.sumOf { it.totalCal })
        assertEquals(222.0, series.analytics.historical?.avgCal)
    }

    @Test
    fun dailySeries_reports_recommended_targets_from_the_stored_recommendation() = runBlocking {
        coEvery { repo.getMealsForUser(userId) } returns emptyList()
        coEvery { repo.getMealItemsForUser(userId) } returns emptyList()
        coEvery { recommendationRepo.getRecommendationByUserId(userId) } returns recommendationWithTargets()

        val series = service.getNutritionDailySeries(
            userId,
            LocalDate.parse("2026-03-01"),
            LocalDate.parse("2026-03-01")
        )

        assertEquals(2400.0, series.analytics.recCal)
        assertEquals(180.0, series.analytics.recProtein)
        assertEquals(250.0, series.analytics.recCarb)
        assertEquals(70.0, series.analytics.recFat)
    }

    @Test
    fun daySummary_reports_recommended_targets_from_the_stored_recommendation() = runBlocking {
        coEvery { repo.getMealsForUser(userId) } returns emptyList()
        coEvery { repo.getMealItemsForUser(userId) } returns emptyList()
        coEvery { recommendationRepo.getRecommendationByUserId(userId) } returns recommendationWithTargets()

        val summary = service.getNutritionDaySummary(userId, "2026-03-01")

        assertEquals(2400.0, summary?.analytics?.recCal)
        assertEquals(180.0, summary?.analytics?.recProtein)
    }

    @Test
    fun daySummary_leaves_targets_null_when_no_recommendation_exists() = runBlocking {
        coEvery { repo.getMealsForUser(userId) } returns emptyList()
        coEvery { repo.getMealItemsForUser(userId) } returns emptyList()
        coEvery { recommendationRepo.getRecommendationByUserId(userId) } returns null

        val summary = service.getNutritionDaySummary(userId, "2026-03-01")

        assertNull(summary?.analytics?.recCal)
        assertNull(summary?.analytics?.recProtein)
    }

    private fun recommendationWithTargets() = RecommendationTestData.recommendation().let { base ->
        base.copy(
            nutrition = NutritionRecommendationDto(
                calories = CaloriesDto(baseline = 2400, trainingDay = 2600, restDay = 2200),
                macros = MacrosDto(
                    trainingDay = MacroRatioDto(protein = 180, carbs = 250, fat = 70),
                    restDay = MacroRatioDto(protein = 180, carbs = 200, fat = 70)
                ),
                mealStructure = MealStructureDto(meals = emptyList()),
                foods = FoodsDto(
                    proteins = emptyList(),
                    carbs = emptyList(),
                    fats = emptyList(),
                    vegetables = emptyList(),
                    fruits = emptyList()
                ),
                checkins = CheckinsDto(metrics = emptyList(), frequency = "weekly"),
                adjustmentRules = AdjustmentRulesDto(
                    plateauTrigger = "2 weeks",
                    adjustments = emptyList()
                )
            )
        )
    }
}
