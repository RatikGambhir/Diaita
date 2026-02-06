package com.nutrify.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserProfileRow(
    @SerialName("user_id")
    val userId: String,
    @SerialName("profile_id")
    val profileId: String,
    val notes: String? = null
)

@Serializable
data class BasicDemographicsRow(
    @SerialName("user_id")
    val userId: String,
    val age: Int,
    val sex: String? = null,
    val gender: String? = null,
    val height: Int,
    val weight: Int,
    @SerialName("body_fat_percentage")
    val bodyFatPercentage: Double? = null,
    @SerialName("lean_mass")
    val leanMass: Double? = null,
    @SerialName("biological_considerations")
    val biologicalConsiderations: String? = null,
    @SerialName("menstrual_cycle_info")
    val menstrualCycleInfo: String? = null
)

@Serializable
data class ActivityLifestyleRow(
    @SerialName("user_id")
    val userId: String,
    @SerialName("activity_level")
    val activityLevel: String,
    @SerialName("daily_step_count")
    val dailyStepCount: Int? = null,
    @SerialName("job_type")
    val jobType: String? = null,
    @SerialName("commute_time")
    val commuteTime: String? = null,
    @SerialName("sleep_duration")
    val sleepDuration: Double? = null,
    @SerialName("sleep_quality")
    val sleepQuality: String? = null,
    @SerialName("stress_level")
    val stressLevel: String? = null,
    @SerialName("recovery_capacity")
    val recoveryCapacity: String? = null
)

@Serializable
data class GoalsPrioritiesRow(
    @SerialName("user_id")
    val userId: String,
    @SerialName("primary_goal")
    val primaryGoal: String,
    @SerialName("secondary_goals")
    val secondaryGoals: List<String>? = null,
    val timeframe: String? = null,
    @SerialName("target_weight")
    val targetWeight: Double? = null,
    @SerialName("performance_metric")
    val performanceMetric: String? = null,
    @SerialName("aesthetic_goals")
    val aestheticGoals: String? = null,
    @SerialName("health_goals")
    val healthGoals: List<String>? = null
)

@Serializable
data class TrainingBackgroundRow(
    @SerialName("user_id")
    val userId: String,
    @SerialName("training_age")
    val trainingAge: String? = null,
    @SerialName("training_history")
    val trainingHistory: String? = null,
    @SerialName("current_workout_routine")
    val currentWorkoutRoutine: String? = null,
    @SerialName("exercise_preferences")
    val exercisePreferences: List<String>? = null,
    @SerialName("exercise_dislikes")
    val exerciseDislikes: List<String>? = null,
    @SerialName("equipment_access")
    val equipmentAccess: String? = null,
    @SerialName("time_per_session")
    val timePerSession: Int? = null,
    @SerialName("days_per_week")
    val daysPerWeek: Int? = null
)

@Serializable
data class MedicalHistoryRow(
    @SerialName("user_id")
    val userId: String,
    val injuries: List<String>? = null,
    @SerialName("chronic_conditions")
    val chronicConditions: List<String>? = null,
    @SerialName("pain_patterns")
    val painPatterns: String? = null,
    @SerialName("mobility_restrictions")
    val mobilityRestrictions: List<String>? = null,
    val medications: List<String>? = null,
    @SerialName("doctor_restrictions")
    val doctorRestrictions: String? = null
)

@Serializable
data class NutritionHistoryRow(
    @SerialName("user_id")
    val userId: String,
    @SerialName("current_diet_pattern")
    val currentDietPattern: String? = null,
    @SerialName("calorie_tracking_experience")
    val calorieTrackingExperience: Boolean? = null,
    @SerialName("macronutrient_preferences")
    val macronutrientPreferences: String? = null,
    @SerialName("food_allergies")
    val foodAllergies: List<String>? = null,
    @SerialName("dietary_restrictions")
    val dietaryRestrictions: List<String>? = null,
    @SerialName("cultural_food_preferences")
    val culturalFoodPreferences: String? = null,
    @SerialName("cooking_skill_level")
    val cookingSkillLevel: String? = null,
    @SerialName("food_budget")
    val foodBudget: String? = null,
    @SerialName("eating_schedule")
    val eatingSchedule: String? = null,
    @SerialName("snacking_habits")
    val snackingHabits: String? = null,
    @SerialName("alcohol_intake")
    val alcoholIntake: String? = null,
    @SerialName("supplement_use")
    val supplementUse: List<String>? = null
)

@Serializable
data class BehavioralFactorsRow(
    @SerialName("user_id")
    val userId: String,
    @SerialName("motivation_level")
    val motivationLevel: String? = null,
    @SerialName("consistency_history")
    val consistencyHistory: String? = null,
    @SerialName("accountability_preference")
    val accountabilityPreference: String? = null,
    @SerialName("past_success_failure_patterns")
    val pastSuccessFailurePatterns: String? = null,
    @SerialName("relationship_with_food")
    val relationshipWithFood: String? = null,
    @SerialName("disordered_eating_history")
    val disorderedEatingHistory: String? = null,
    @SerialName("stress_eating_tendencies")
    val stressEatingTendencies: String? = null,
    @SerialName("support_system")
    val supportSystem: String? = null
)

@Serializable
data class MetricsTrackingRow(
    @SerialName("user_id")
    val userId: String,
    @SerialName("preferred_progress_metrics")
    val preferredProgressMetrics: List<String>? = null,
    @SerialName("tracking_tools")
    val trackingTools: List<String>? = null,
    @SerialName("checkin_frequency")
    val checkinFrequency: String? = null
)
