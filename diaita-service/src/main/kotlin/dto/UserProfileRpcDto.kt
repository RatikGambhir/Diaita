package com.diaita.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegisteredUserProfileDto(
    val userId: String,
    val profileId: String? = null,
    val notes: String? = null,
    val basicDemographics: BasicDemographicsDto? = null,
    val activityLifestyle: ActivityLevelLifestyleDto? = null,
    val goals: GoalsPrioritiesDto? = null,
    val trainingBackground: TrainingBackgroundDto? = null,
    val nutritionHistory: NutritionDietHistoryDto? = null
)
