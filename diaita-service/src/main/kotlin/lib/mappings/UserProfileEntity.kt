package com.diaita.lib.mappings

import com.diaita.dto.ActivityLevelLifestyleDto
import com.diaita.dto.BasicDemographicsDto
import com.diaita.dto.GoalsPrioritiesDto
import com.diaita.dto.NutritionDietHistoryDto
import com.diaita.dto.RegisterUserProfileRequestDto
import com.diaita.dto.RegisteredUserProfileDto
import com.diaita.dto.TrainingBackgroundDto
import com.diaita.entity.ActivityLifestyleRowEntity
import com.diaita.entity.BasicDemographicsRowEntity
import com.diaita.entity.GoalsPrioritiesRowEntity
import com.diaita.entity.NutritionHistoryRowEntity
import com.diaita.entity.TrainingBackgroundRowEntity
import com.diaita.entity.UserProfileRowEntity

fun RegisterUserProfileRequestDto.toUserProfileRowEntity() = UserProfileRowEntity(
    userId = userId,
    age = age,
    height = height,
    weight = weight,
    primaryGoal = primaryGoal,
    timeframe = timeframe,
    activityLevel = activityLevel,
    sleepDuration = sleepDuration,
    stressLevel = stressLevel,
    trainingHistory = trainingHistory,
    trainingAge = trainingAge,
    equipmentAccess = equipmentAccess,
    daysPerWeek = daysPerWeek,
    timePerSession = timePerSession,
    injuries = injuries,
    chronicConditions = chronicConditions,
    mobilityRestrictions = mobilityRestrictions,
    doctorRestrictions = doctorRestrictions,
    dietaryRestrictions = dietaryRestrictions,
    foodAllergies = foodAllergies,
    currentDietPattern = currentDietPattern,
    eatingSchedule = eatingSchedule
)

fun UserProfileRowEntity.toDto() = RegisteredUserProfileDto(
    userId = userId,
    age = age,
    height = height,
    weight = weight,
    primaryGoal = primaryGoal,
    timeframe = timeframe,
    activityLevel = activityLevel,
    sleepDuration = sleepDuration,
    stressLevel = stressLevel,
    trainingHistory = trainingHistory,
    trainingAge = trainingAge,
    equipmentAccess = equipmentAccess,
    daysPerWeek = daysPerWeek,
    timePerSession = timePerSession,
    injuries = injuries,
    chronicConditions = chronicConditions,
    mobilityRestrictions = mobilityRestrictions,
    doctorRestrictions = doctorRestrictions,
    dietaryRestrictions = dietaryRestrictions,
    foodAllergies = foodAllergies,
    currentDietPattern = currentDietPattern,
    eatingSchedule = eatingSchedule
)

fun BasicDemographicsDto.toEntity(userId: String) =
    BasicDemographicsRowEntity(
        userId = userId,
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

fun ActivityLevelLifestyleDto.toEntity(userId: String) =
    ActivityLifestyleRowEntity(
        userId = userId,
        activityLevel = activityLevel,
        dailyStepCount = dailyStepCount,
        jobType = jobType,
        commuteTime = commuteTime,
        sleepDuration = sleepDuration,
        sleepQuality = sleepQuality,
        stressLevel = stressLevel,
        recoveryCapacity = recoveryCapacity
    )

fun GoalsPrioritiesDto.toEntity(userId: String) =
    GoalsPrioritiesRowEntity(
        userId = userId,
        primaryGoal = primaryGoal,
        secondaryGoals = secondaryGoals,
        timeframe = timeframe,
        targetWeight = targetWeight,
        performanceMetric = performanceMetric,
        aestheticGoals = aestheticGoals,
        healthGoals = healthGoals
    )

fun TrainingBackgroundDto.toEntity(userId: String) =
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

fun NutritionDietHistoryDto.toEntity(userId: String) =
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

fun BasicDemographicsRowEntity.toDto() = BasicDemographicsDto(
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

fun ActivityLifestyleRowEntity.toDto() = ActivityLevelLifestyleDto(
    activityLevel = activityLevel,
    dailyStepCount = dailyStepCount,
    jobType = jobType,
    commuteTime = commuteTime,
    sleepDuration = sleepDuration,
    sleepQuality = sleepQuality,
    stressLevel = stressLevel,
    recoveryCapacity = recoveryCapacity
)

fun GoalsPrioritiesRowEntity.toDto() = GoalsPrioritiesDto(
    primaryGoal = primaryGoal,
    secondaryGoals = secondaryGoals,
    timeframe = timeframe,
    targetWeight = targetWeight,
    performanceMetric = performanceMetric,
    aestheticGoals = aestheticGoals,
    healthGoals = healthGoals
)

fun TrainingBackgroundRowEntity.toDto() = TrainingBackgroundDto(
    trainingAge = trainingAge,
    trainingHistory = trainingHistory,
    currentWorkoutRoutine = currentWorkoutRoutine,
    exercisePreferences = exercisePreferences,
    exerciseDislikes = exerciseDislikes,
    equipmentAccess = equipmentAccess,
    timePerSession = timePerSession,
    daysPerWeek = daysPerWeek
)

fun NutritionHistoryRowEntity.toDto() = NutritionDietHistoryDto(
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
