package com.diaita.dto

import kotlinx.serialization.Serializable

@Serializable
data class BasicDemographicsDto(
    val age: Int,
    val sex: String? = null,
    val gender: String? = null,
    val height: Int,
    val weight: Int,
    val bodyFatPercentage: Double? = null,
    val leanMass: Double? = null,
    val biologicalConsiderations: String? = null,
    val menstrualCycleInfo: String? = null
)

@Serializable
data class ActivityLevelLifestyleDto(
    val activityLevel: String,
    val dailyStepCount: Int? = null,
    val jobType: String? = null,
    val commuteTime: String? = null,
    val sleepDuration: Double? = null,
    val sleepQuality: String? = null,
    val stressLevel: String? = null,
    val recoveryCapacity: String? = null
)

@Serializable
data class GoalsPrioritiesDto(
    val primaryGoal: String,
    val secondaryGoals: List<String>? = null,
    val timeframe: String? = null,
    val targetWeight: Double? = null,
    val performanceMetric: String? = null,
    val aestheticGoals: String? = null,
    val healthGoals: List<String>? = null
)

@Serializable
data class TrainingBackgroundDto(
    val trainingAge: String? = null,
    val trainingHistory: String? = null,
    val currentWorkoutRoutine: String? = null,
    val exercisePreferences: List<String>? = null,
    val exerciseDislikes: List<String>? = null,
    val equipmentAccess: String? = null,
    val timePerSession: Int? = null,
    val daysPerWeek: Int? = null
)

@Serializable
data class NutritionDietHistoryDto(
    val currentDietPattern: String? = null,
    val calorieTrackingExperience: Boolean? = null,
    val macronutrientPreferences: String? = null,
    val foodAllergies: List<String>? = null,
    val dietaryRestrictions: List<String>? = null,
    val culturalFoodPreferences: String? = null,
    val cookingSkillLevel: String? = null,
    val foodBudget: String? = null,
    val eatingSchedule: String? = null,
    val snackingHabits: String? = null,
    val alcoholIntake: String? = null,
    val supplementUse: List<String>? = null
)

@Serializable
data class RegisterUserProfileRequestDto(
    val id: String,
    val userId: String,
    val basicDemographics: BasicDemographicsDto,
    val activityLifestyle: ActivityLevelLifestyleDto,
    val goals: GoalsPrioritiesDto,
    val trainingBackground: TrainingBackgroundDto? = null,
    val nutritionHistory: NutritionDietHistoryDto? = null
)
