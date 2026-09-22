package com.diaita.service

import com.diaita.testdata.UserProfileTestData
import kotlin.test.Test
import kotlin.test.assertEquals

class RecommendationFactoryTest {
    @Test
    fun activity_levels_from_onboarding_use_the_expected_calorie_multipliers() {
        val baseProfile = UserProfileTestData.fullRequest().copy(weight = 100.0)
        val expectedCalories = mapOf(
            "sedentary" to 2_640,
            "lightly_active" to 2_970,
            "moderately_active" to 3_300,
            "very_active" to 3_740,
            "extremely_active" to 4_180
        )

        expectedCalories.forEach { (activityLevel, expectedBaseline) ->
            val recommendation = RecommendationFactory.create(
                baseProfile.copy(activityLevel = activityLevel)
            )

            assertEquals(
                expectedBaseline,
                recommendation.nutrition.calories.baseline,
                "Unexpected baseline for $activityLevel"
            )
        }
    }

    @Test
    fun missing_optional_training_background_uses_default_schedule() {
        val profile = UserProfileTestData.fullRequest().copy(
            trainingHistory = null,
            trainingAge = null,
            daysPerWeek = null
        )

        val recommendation = RecommendationFactory.create(profile)

        assertEquals(3, recommendation.training.split.daysPerWeek)
        assertEquals(3, recommendation.training.dayByDayPlan.weeks.single().days.size)
    }
}
