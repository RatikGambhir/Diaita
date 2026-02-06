package com.nutrify.entity

import kotlinx.serialization.Serializable

@Serializable
data class BasicDemographicsEntity(
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
data class ActivityLevelLifestyleEntity(
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
data class GoalsPrioritiesEntity(
    val primaryGoal: String,
    val secondaryGoals: List<String>? = null,
    val timeframe: String? = null,
    val targetWeight: Double? = null,
    val performanceMetric: String? = null,
    val aestheticGoals: String? = null,
    val healthGoals: List<String>? = null
)

@Serializable
data class TrainingBackgroundEntity(
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
data class MedicalHistoryEntity(
    val injuries: List<String>? = null,
    val chronicConditions: List<String>? = null,
    val painPatterns: String? = null,
    val mobilityRestrictions: List<String>? = null,
    val medications: List<String>? = null,
    val doctorRestrictions: String? = null
)

@Serializable
data class NutritionDietHistoryEntity(
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
data class BehavioralFactorsEntity(
    val motivationLevel: String? = null,
    val consistencyHistory: String? = null,
    val accountabilityPreference: String? = null,
    val pastSuccessFailurePatterns: String? = null,
    val relationshipWithFood: String? = null,
    val disorderedEatingHistory: String? = null,
    val stressEatingTendencies: String? = null,
    val supportSystem: String? = null
)

@Serializable
data class MetricsTrackingEntity(
    val preferredProgressMetrics: List<String>? = null,
    val trackingTools: List<String>? = null,
    val checkinFrequency: String? = null
)

@Serializable
data class UserProfileEntity(
    val id: String,
    val userId: String,
    val basicDemographics: BasicDemographicsEntity,
    val activityLifestyle: ActivityLevelLifestyleEntity,
    val goals: GoalsPrioritiesEntity,
    val trainingBackground: TrainingBackgroundEntity? = null,
    val medicalHistory: MedicalHistoryEntity? = null,
    val nutritionHistory: NutritionDietHistoryEntity? = null,
    val behavioralFactors: BehavioralFactorsEntity? = null,
    val metricsTracking: MetricsTrackingEntity? = null,
    val notes: String? = null
)
