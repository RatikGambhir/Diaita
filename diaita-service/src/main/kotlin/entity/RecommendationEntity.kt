package com.diaita.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class RecommendationEntity(
    val id: String,
    @SerialName("user_id")
    val userId: String,
    @SerialName("nutrition_calories")
    val nutritionCalories: JsonElement,
    @SerialName("nutrition_macros")
    val nutritionMacros: JsonElement,
    @SerialName("nutrition_meal_structure")
    val nutritionMealStructure: JsonElement,
    @SerialName("nutrition_foods")
    val nutritionFoods: JsonElement,
    @SerialName("nutrition_checkins")
    val nutritionCheckins: JsonElement,
    @SerialName("nutrition_adjustment_rules")
    val nutritionAdjustmentRules: JsonElement,
    @SerialName("training_focus")
    val trainingFocus: JsonElement,
    @SerialName("training_split")
    val trainingSplit: JsonElement,
    @SerialName("training_exercise_library")
    val trainingExerciseLibrary: JsonElement,
    @SerialName("training_phases")
    val trainingPhases: JsonElement,
    @SerialName("training_day_by_day_plan")
    val trainingDayByDayPlan: JsonElement,
    @SerialName("training_progression_rules")
    val trainingProgressionRules: JsonElement,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null
)
