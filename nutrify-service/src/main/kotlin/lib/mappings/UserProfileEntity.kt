package com.nutrify.lib.mappings

import com.nutrify.dto.*
import com.nutrify.entity.*

fun RegisterUserProfileRequest.toUserProfileRow(userId: String) = UserProfileRow(
    userId = userId,
    profileId = id,
    notes = notes
)

fun RegisterUserProfileRequest.toBasicDemographicsRow(userId: String) =
    BasicDemographicsRow(
        userId = userId,
        age = basicDemographics.age,
        sex = basicDemographics.sex,
        gender = basicDemographics.gender,
        height = basicDemographics.height,
        weight = basicDemographics.weight,
        bodyFatPercentage = basicDemographics.bodyFatPercentage,
        leanMass = basicDemographics.leanMass,
        biologicalConsiderations = basicDemographics.biologicalConsiderations,
        menstrualCycleInfo = basicDemographics.menstrualCycleInfo
    )

fun RegisterUserProfileRequest.toActivityLifestyleRow(userId: String) =
    ActivityLifestyleRow(
        userId = userId,
        activityLevel = activityLifestyle.activityLevel,
        dailyStepCount = activityLifestyle.dailyStepCount,
        jobType = activityLifestyle.jobType,
        commuteTime = activityLifestyle.commuteTime,
        sleepDuration = activityLifestyle.sleepDuration,
        sleepQuality = activityLifestyle.sleepQuality,
        stressLevel = activityLifestyle.stressLevel,
        recoveryCapacity = activityLifestyle.recoveryCapacity
    )

fun RegisterUserProfileRequest.toGoalsPrioritiesRow(userId: String) =
    GoalsPrioritiesRow(
        userId = userId,
        primaryGoal = goals.primaryGoal,
        secondaryGoals = goals.secondaryGoals,
        timeframe = goals.timeframe,
        targetWeight = goals.targetWeight,
        performanceMetric = goals.performanceMetric,
        aestheticGoals = goals.aestheticGoals,
        healthGoals = goals.healthGoals
    )

fun TrainingBackground.toTrainingBackgroundRow(userId: String) =
    TrainingBackgroundRow(
        userId = userId,
        trainingAge = trainingAge,
        trainingHistory = trainingHistory,
        currentWorkoutRoutine = currentWorkoutRoutine,
        exercisePreferences = exercisePreferences,
        exerciseDislikes = exerciseDislikes,
        equipmentAccess = equipmentAccess,
        timePerSession = timePerSession,
        daysPerWeek = daysPerWeek
    )

fun MedicalHistory.toMedicalHistoryRow(userId: String) =
    MedicalHistoryRow(
        userId = userId,
        injuries = injuries,
        chronicConditions = chronicConditions,
        painPatterns = painPatterns,
        mobilityRestrictions = mobilityRestrictions,
        medications = medications,
        doctorRestrictions = doctorRestrictions
    )

fun NutritionDietHistory.toNutritionHistoryRow(userId: String) =
    NutritionHistoryRow(
        userId = userId,
        currentDietPattern = currentDietPattern,
        calorieTrackingExperience = calorieTrackingExperience,
        macronutrientPreferences = macronutrientPreferences,
        foodAllergies = foodAllergies,
        dietaryRestrictions = dietaryRestrictions,
        culturalFoodPreferences = culturalFoodPreferences,
        cookingSkillLevel = cookingSkillLevel,
        foodBudget = foodBudget,
        eatingSchedule = eatingSchedule,
        snackingHabits = snackingHabits,
        alcoholIntake = alcoholIntake,
        supplementUse = supplementUse
    )

fun BehavioralFactors.toBehavioralFactorsRow(userId: String) =
    BehavioralFactorsRow(
        userId = userId,
        motivationLevel = motivationLevel,
        consistencyHistory = consistencyHistory,
        accountabilityPreference = accountabilityPreference,
        pastSuccessFailurePatterns = pastSuccessFailurePatterns,
        relationshipWithFood = relationshipWithFood,
        disorderedEatingHistory = disorderedEatingHistory,
        stressEatingTendencies = stressEatingTendencies,
        supportSystem = supportSystem
    )

fun MetricsTracking.toMetricsTrackingRow(userId: String) =
    MetricsTrackingRow(
        userId = userId,
        preferredProgressMetrics = preferredProgressMetrics,
        trackingTools = trackingTools,
        checkinFrequency = checkinFrequency
    )
