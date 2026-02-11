package com.diaita.dto

import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.UUID





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
data class MedicalHistoryDto(
    val injuries: List<String>? = null,
    val chronicConditions: List<String>? = null,
    val painPatterns: String? = null,
    val mobilityRestrictions: List<String>? = null,
    val medications: List<String>? = null,
    val doctorRestrictions: String? = null
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
data class BehavioralFactorsDto(
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
data class MetricsTrackingDto(
    val preferredProgressMetrics: List<String>? = null, // scale weight, measurements, photos, performance
    val trackingTools: List<String>? = null, // Apple Watch, Fitbit, MyFitnessPal, Cronometer
    val checkinFrequency: String? = null
)

@Serializable
data class RegisterUserProfileRequestDto(
    val id: String,
    val userId: String,
    val basicDemographics: BasicDemographicsDto,
    val activityLifestyle: ActivityLevelLifestyleDto,
    val goals: GoalsPrioritiesDto,
    val trainingBackground: TrainingBackgroundDto? = null,
    val medicalHistory: MedicalHistoryDto? = null,
    val nutritionHistory: NutritionDietHistoryDto? = null,
    val behavioralFactors: BehavioralFactorsDto? = null,
    val metricsTracking: MetricsTrackingDto? = null,
    val notes: String? = null
)


