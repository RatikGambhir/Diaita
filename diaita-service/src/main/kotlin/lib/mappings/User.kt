package com.diaita.lib.mappings

import com.diaita.dto.*
import com.diaita.entity.*

fun BasicDemographicsDto.toEntity() = BasicDemographicsEntity(
    age = age,
    sex = sex,
    gender = gender,
    height = height,
    weight = weight,
    bodyFatPercentage = bodyFatPercentage,
    leanMass = leanMass,
    biologicalConsiderations = biologicalConsiderations,
    menstrualCycleInfo = menstrualCycleInfo
)

fun ActivityLevelLifestyleDto.toEntity() = ActivityLevelLifestyleEntity(
    activityLevel = activityLevel,
    dailyStepCount = dailyStepCount,
    jobType = jobType,
    commuteTime = commuteTime,
    sleepDuration = sleepDuration,
    sleepQuality = sleepQuality,
    stressLevel = stressLevel,
    recoveryCapacity = recoveryCapacity
)

fun GoalsPrioritiesDto.toEntity() = GoalsPrioritiesEntity(
    primaryGoal = primaryGoal,
    secondaryGoals = secondaryGoals,
    timeframe = timeframe,
    targetWeight = targetWeight,
    performanceMetric = performanceMetric,
    aestheticGoals = aestheticGoals,
    healthGoals = healthGoals
)

fun TrainingBackgroundDto.toEntity() = TrainingBackgroundEntity(
    trainingAge = trainingAge,
    trainingHistory = trainingHistory,
    currentWorkoutRoutine = currentWorkoutRoutine,
    exercisePreferences = exercisePreferences,
    exerciseDislikes = exerciseDislikes,
    equipmentAccess = equipmentAccess,
    timePerSession = timePerSession,
    daysPerWeek = daysPerWeek
)

fun NutritionDietHistoryDto.toEntity() = NutritionDietHistoryEntity(
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

fun RegisterUserProfileRequestDto.toEntity() = UserProfileEntity(
    id = id,
    userId = userId,
    basicDemographics = basicDemographics.toEntity(),
    activityLifestyle = activityLifestyle.toEntity(),
    goals = goals.toEntity(),
    trainingBackground = trainingBackground?.toEntity(),
    nutritionHistory = nutritionHistory?.toEntity(),
    // TODO: Re-add medicalHistory, behavioralFactors, metricsTracking, and notes mappings when those setup features return.
)
