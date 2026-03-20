package com.diaita.testdata

import com.diaita.dto.ActivityLevelLifestyleDto
import com.diaita.dto.BasicDemographicsDto
import com.diaita.dto.GoalsPrioritiesDto
import com.diaita.dto.NutritionDietHistoryDto
import com.diaita.dto.RegisterUserProfileRequestDto
import com.diaita.dto.TrainingBackgroundDto

object UserProfileTestData {
    fun fullRequest(
        userId: String = "11111111-1111-1111-1111-111111111111"
    ): RegisterUserProfileRequestDto {
        return RegisterUserProfileRequestDto(
            userId = userId,
            age = 28,
            height = 178.5,
            weight = 78.2,
            primaryGoal = "build_muscle",
            timeframe = "12_weeks",
            activityLevel = "moderately_active",
            sleepDuration = 7.5,
            stressLevel = "moderate",
            trainingHistory = "regular gym training",
            trainingAge = "2_years",
            equipmentAccess = "full_gym",
            daysPerWeek = 5,
            timePerSession = 75,
            injuries = listOf("left_knee_pain"),
            chronicConditions = listOf("asthma"),
            mobilityRestrictions = listOf("limited_ankle_dorsiflexion"),
            doctorRestrictions = "avoid maximal loading",
            dietaryRestrictions = listOf("vegetarian"),
            foodAllergies = listOf("nuts"),
            currentDietPattern = "high_protein",
            eatingSchedule = "3_meals_1_snack"
        )
    }

    fun basicDemographics() = BasicDemographicsDto(
        age = 28,
        sex = "male",
        gender = "male",
        height = 178,
        weight = 78,
        bodyFatPercentage = 16.2,
        leanMass = 65.4,
        biologicalConsiderations = "none",
        menstrualCycleInfo = "n/a"
    )

    fun activityLifestyle() = ActivityLevelLifestyleDto(
        activityLevel = "moderately_active",
        dailyStepCount = 8500,
        jobType = "desk",
        commuteTime = "30min",
        sleepDuration = 7.5,
        sleepQuality = "good",
        stressLevel = "moderate",
        recoveryCapacity = "good"
    )

    fun goals() = GoalsPrioritiesDto(
        primaryGoal = "muscle_gain",
        secondaryGoals = listOf("strength", "fat_loss"),
        timeframe = "12_weeks",
        targetWeight = 82.0,
        performanceMetric = "bench_press",
        aestheticGoals = "lean physique",
        healthGoals = listOf("better_sleep", "lower_stress")
    )

    fun trainingBackground() = TrainingBackgroundDto(
        trainingAge = "2_years",
        trainingHistory = "regular gym training",
        currentWorkoutRoutine = "push_pull_legs",
        exercisePreferences = listOf("compound_lifts", "cables"),
        exerciseDislikes = listOf("burpees"),
        equipmentAccess = "full_gym",
        timePerSession = 75,
        daysPerWeek = 5
    )

    fun nutritionHistory() = NutritionDietHistoryDto(
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
    )
}
