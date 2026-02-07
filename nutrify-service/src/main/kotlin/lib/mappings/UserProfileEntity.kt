package com.nutrify.lib.mappings

import com.nutrify.dto.*
import com.nutrify.entity.*

fun RegisterUserProfileRequestDto.toUserProfileRow(userId: String) = UserProfileRowEntity(
    userId = userId,
    profileId = id,
    notes = notes
)

fun RegisterUserProfileRequestDto.toBasicDemographicsRow(userId: String) =
    BasicDemographicsRowEntity(
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

fun RegisterUserProfileRequestDto.toActivityLifestyleRow(userId: String) =
    ActivityLifestyleRowEntity(
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

fun RegisterUserProfileRequestDto.toGoalsPrioritiesRow(userId: String) =
    GoalsPrioritiesRowEntity(
        userId = userId,
        primaryGoal = goals.primaryGoal,
        secondaryGoals = goals.secondaryGoals,
        timeframe = goals.timeframe,
        targetWeight = goals.targetWeight,
        performanceMetric = goals.performanceMetric,
        aestheticGoals = goals.aestheticGoals,
        healthGoals = goals.healthGoals
    )

fun TrainingBackgroundDto.toTrainingBackgroundRow(userId: String) =
    TrainingBackgroundRowEntity(
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

fun MedicalHistoryDto.toMedicalHistoryRow(userId: String) =
    MedicalHistoryRowEntity(
        userId = userId,
        injuries = injuries,
        chronicConditions = chronicConditions,
        painPatterns = painPatterns,
        mobilityRestrictions = mobilityRestrictions,
        medications = medications,
        doctorRestrictions = doctorRestrictions
    )

fun NutritionDietHistoryDto.toNutritionHistoryRow(userId: String) =
    NutritionHistoryRowEntity(
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

fun BehavioralFactorsDto.toBehavioralFactorsRow(userId: String) =
    BehavioralFactorsRowEntity(
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

fun MetricsTrackingDto.toMetricsTrackingRow(userId: String) =
    MetricsTrackingRowEntity(
        userId = userId,
        preferredProgressMetrics = preferredProgressMetrics,
        trackingTools = trackingTools,
        checkinFrequency = checkinFrequency
    )
