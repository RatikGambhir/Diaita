package com.diaita.lib.mappings

import com.diaita.dto.AdjustmentRulesDto
import com.diaita.dto.CaloriesDto
import com.diaita.dto.CheckinsDto
import com.diaita.dto.DayByDayPlanDto
import com.diaita.dto.ExerciseLibraryDto
import com.diaita.dto.FoodsDto
import com.diaita.dto.FocusDto
import com.diaita.dto.MacrosDto
import com.diaita.dto.MealStructureDto
import com.diaita.dto.NutritionRecommendationDto
import com.diaita.dto.PhaseDto
import com.diaita.dto.ProgressionRulesDto
import com.diaita.dto.RecommendationDto
import com.diaita.dto.SplitDto
import com.diaita.dto.TrainingRecommendationDto
import com.diaita.entity.RecommendationEntity
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

private val recommendationJson = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
}

fun RecommendationDto.toStoragePayload(userId: String) = buildJsonObject {
    put("user_id", userId)
    put("nutrition_calories", recommendationJson.encodeToJsonElement(CaloriesDto.serializer(), nutrition.calories))
    put("nutrition_macros", recommendationJson.encodeToJsonElement(MacrosDto.serializer(), nutrition.macros))
    put(
        "nutrition_meal_structure",
        recommendationJson.encodeToJsonElement(MealStructureDto.serializer(), nutrition.mealStructure)
    )
    put("nutrition_foods", recommendationJson.encodeToJsonElement(FoodsDto.serializer(), nutrition.foods))
    put("nutrition_checkins", recommendationJson.encodeToJsonElement(CheckinsDto.serializer(), nutrition.checkins))
    put(
        "nutrition_adjustment_rules",
        recommendationJson.encodeToJsonElement(AdjustmentRulesDto.serializer(), nutrition.adjustmentRules)
    )
    put("training_focus", recommendationJson.encodeToJsonElement(FocusDto.serializer(), training.focus))
    put("training_split", recommendationJson.encodeToJsonElement(SplitDto.serializer(), training.split))
    put(
        "training_exercise_library",
        recommendationJson.encodeToJsonElement(ExerciseLibraryDto.serializer(), training.exerciseLibrary)
    )
    put(
        "training_phases",
        recommendationJson.encodeToJsonElement(ListSerializer(PhaseDto.serializer()), training.phases)
    )
    put(
        "training_day_by_day_plan",
        recommendationJson.encodeToJsonElement(DayByDayPlanDto.serializer(), training.dayByDayPlan)
    )
    put(
        "training_progression_rules",
        recommendationJson.encodeToJsonElement(ProgressionRulesDto.serializer(), training.progressionRules)
    )
}

fun RecommendationEntity.toDto(): RecommendationDto = RecommendationDto(
    nutrition = NutritionRecommendationDto(
        calories = recommendationJson.decodeFromJsonElement(CaloriesDto.serializer(), nutritionCalories),
        macros = recommendationJson.decodeFromJsonElement(MacrosDto.serializer(), nutritionMacros),
        mealStructure = recommendationJson.decodeFromJsonElement(MealStructureDto.serializer(), nutritionMealStructure),
        foods = recommendationJson.decodeFromJsonElement(FoodsDto.serializer(), nutritionFoods),
        checkins = recommendationJson.decodeFromJsonElement(CheckinsDto.serializer(), nutritionCheckins),
        adjustmentRules = recommendationJson.decodeFromJsonElement(
            AdjustmentRulesDto.serializer(),
            nutritionAdjustmentRules
        )
    ),
    training = TrainingRecommendationDto(
        focus = recommendationJson.decodeFromJsonElement(FocusDto.serializer(), trainingFocus),
        split = recommendationJson.decodeFromJsonElement(SplitDto.serializer(), trainingSplit),
        exerciseLibrary = recommendationJson.decodeFromJsonElement(
            ExerciseLibraryDto.serializer(),
            trainingExerciseLibrary
        ),
        phases = recommendationJson.decodeFromJsonElement(ListSerializer(PhaseDto.serializer()), trainingPhases),
        dayByDayPlan = recommendationJson.decodeFromJsonElement(DayByDayPlanDto.serializer(), trainingDayByDayPlan),
        progressionRules = recommendationJson.decodeFromJsonElement(
            ProgressionRulesDto.serializer(),
            trainingProgressionRules
        )
    )
)
