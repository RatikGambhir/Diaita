package com.diaita.testdata

import com.diaita.dto.ActivityLevelLifestyleDto
import com.diaita.dto.BasicDemographicsDto
import com.diaita.dto.BehavioralFactorsDto
import com.diaita.dto.GoalsPrioritiesDto
import com.diaita.dto.MedicalHistoryDto
import com.diaita.dto.MetricsTrackingDto
import com.diaita.dto.NutritionDietHistoryDto
import com.diaita.dto.RegisterUserProfileRequestDto
import com.diaita.dto.TrainingBackgroundDto

object UserProfileTestData {
    fun fullRequest(
        userId: String = "11111111-1111-1111-1111-111111111111"
    ): RegisterUserProfileRequestDto {
        return RegisterUserProfileRequestDto(
            id = "22222222-2222-2222-2222-222222222222",
            userId = userId,
            basicDemographics = BasicDemographicsDto(
                age = 28,
                sex = "male",
                gender = "male",
                height = 178,
                weight = 78,
                bodyFatPercentage = 16.2,
                leanMass = 65.4,
                biologicalConsiderations = "none",
                menstrualCycleInfo = "n/a"
            ),
            activityLifestyle = ActivityLevelLifestyleDto(
                activityLevel = "moderately_active",
                dailyStepCount = 8500,
                jobType = "desk",
                commuteTime = "30min",
                sleepDuration = 7.5,
                sleepQuality = "good",
                stressLevel = "moderate",
                recoveryCapacity = "good"
            ),
            goals = GoalsPrioritiesDto(
                primaryGoal = "muscle_gain",
                secondaryGoals = listOf("strength", "fat_loss"),
                timeframe = "12_weeks",
                targetWeight = 82.0,
                performanceMetric = "bench_press",
                aestheticGoals = "lean physique",
                healthGoals = listOf("better_sleep", "lower_stress")
            ),
            trainingBackground = TrainingBackgroundDto(
                trainingAge = "2_years",
                trainingHistory = "regular gym training",
                currentWorkoutRoutine = "push_pull_legs",
                exercisePreferences = listOf("compound_lifts", "cables"),
                exerciseDislikes = listOf("burpees"),
                equipmentAccess = "full_gym",
                timePerSession = 75,
                daysPerWeek = 5
            ),
            medicalHistory = MedicalHistoryDto(
                injuries = listOf("minor_ankle_sprain_2023"),
                chronicConditions = listOf("none"),
                painPatterns = "none",
                mobilityRestrictions = listOf("none"),
                medications = listOf("none"),
                doctorRestrictions = "none"
            ),
            nutritionHistory = NutritionDietHistoryDto(
                currentDietPattern = "high_protein",
                calorieTrackingExperience = true,
                macronutrientPreferences = "high_protein_moderate_carb",
                foodAllergies = listOf("none"),
                dietaryRestrictions = listOf("none"),
                culturalFoodPreferences = "indian_mediterranean",
                cookingSkillLevel = "intermediate",
                foodBudget = "medium",
                eatingSchedule = "3_meals_1_snack",
                snackingHabits = "fruit_nuts",
                alcoholIntake = "occasional",
                supplementUse = listOf("whey", "creatine")
            ),
            behavioralFactors = BehavioralFactorsDto(
                motivationLevel = "high",
                consistencyHistory = "mostly_consistent",
                accountabilityPreference = "app_tracking",
                pastSuccessFailurePatterns = "works_with_structure",
                relationshipWithFood = "balanced",
                disorderedEatingHistory = "none",
                stressEatingTendencies = "low",
                supportSystem = "friends_family"
            ),
            metricsTracking = MetricsTrackingDto(
                preferredProgressMetrics = listOf("weight", "photos", "strength"),
                trackingTools = listOf("apple_watch", "myfitnesspal"),
                checkinFrequency = "weekly"
            ),
            notes = "test profile notes"
        )
    }
}
