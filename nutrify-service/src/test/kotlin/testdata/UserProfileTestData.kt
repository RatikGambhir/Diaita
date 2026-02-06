package com.nutrify.testdata

import com.nutrify.dto.ActivityLevelLifestyle
import com.nutrify.dto.BasicDemographics
import com.nutrify.dto.BehavioralFactors
import com.nutrify.dto.GoalsPriorities
import com.nutrify.dto.MedicalHistory
import com.nutrify.dto.MetricsTracking
import com.nutrify.dto.NutritionDietHistory
import com.nutrify.dto.RegisterUserProfileRequest
import com.nutrify.dto.TrainingBackground

object UserProfileTestData {
    fun fullRequest(
        userId: String = "11111111-1111-1111-1111-111111111111"
    ): RegisterUserProfileRequest {
        return RegisterUserProfileRequest(
            id = "22222222-2222-2222-2222-222222222222",
            userId = userId,
            basicDemographics = BasicDemographics(
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
            activityLifestyle = ActivityLevelLifestyle(
                activityLevel = "moderately_active",
                dailyStepCount = 8500,
                jobType = "desk",
                commuteTime = "30min",
                sleepDuration = 7.5,
                sleepQuality = "good",
                stressLevel = "moderate",
                recoveryCapacity = "good"
            ),
            goals = GoalsPriorities(
                primaryGoal = "muscle_gain",
                secondaryGoals = listOf("strength", "fat_loss"),
                timeframe = "12_weeks",
                targetWeight = 82.0,
                performanceMetric = "bench_press",
                aestheticGoals = "lean physique",
                healthGoals = listOf("better_sleep", "lower_stress")
            ),
            trainingBackground = TrainingBackground(
                trainingAge = "2_years",
                trainingHistory = "regular gym training",
                currentWorkoutRoutine = "push_pull_legs",
                exercisePreferences = listOf("compound_lifts", "cables"),
                exerciseDislikes = listOf("burpees"),
                equipmentAccess = "full_gym",
                timePerSession = 75,
                daysPerWeek = 5
            ),
            medicalHistory = MedicalHistory(
                injuries = listOf("minor_ankle_sprain_2023"),
                chronicConditions = listOf("none"),
                painPatterns = "none",
                mobilityRestrictions = listOf("none"),
                medications = listOf("none"),
                doctorRestrictions = "none"
            ),
            nutritionHistory = NutritionDietHistory(
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
            behavioralFactors = BehavioralFactors(
                motivationLevel = "high",
                consistencyHistory = "mostly_consistent",
                accountabilityPreference = "app_tracking",
                pastSuccessFailurePatterns = "works_with_structure",
                relationshipWithFood = "balanced",
                disorderedEatingHistory = "none",
                stressEatingTendencies = "low",
                supportSystem = "friends_family"
            ),
            metricsTracking = MetricsTracking(
                preferredProgressMetrics = listOf("weight", "photos", "strength"),
                trackingTools = listOf("apple_watch", "myfitnesspal"),
                checkinFrequency = "weekly"
            ),
            notes = "test profile notes"
        )
    }
}
