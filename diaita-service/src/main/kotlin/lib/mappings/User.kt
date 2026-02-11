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

fun MedicalHistoryDto.toEntity() = MedicalHistoryEntity(
    injuries = injuries,
    chronicConditions = chronicConditions,
    painPatterns = painPatterns,
    mobilityRestrictions = mobilityRestrictions,
    medications = medications,
    doctorRestrictions = doctorRestrictions
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

fun BehavioralFactorsDto.toEntity() = BehavioralFactorsEntity(
    motivationLevel = motivationLevel,
    consistencyHistory = consistencyHistory,
    accountabilityPreference = accountabilityPreference,
    pastSuccessFailurePatterns = pastSuccessFailurePatterns,
    relationshipWithFood = relationshipWithFood,
    disorderedEatingHistory = disorderedEatingHistory,
    stressEatingTendencies = stressEatingTendencies,
    supportSystem = supportSystem
)

fun MetricsTrackingDto.toEntity() = MetricsTrackingEntity(
    preferredProgressMetrics = preferredProgressMetrics,
    trackingTools = trackingTools,
    checkinFrequency = checkinFrequency
)

fun RegisterUserProfileRequestDto.toEntity() = UserProfileEntity(
    id = id,
    userId = userId,
    basicDemographics = basicDemographics.toEntity(),
    activityLifestyle = activityLifestyle.toEntity(),
    goals = goals.toEntity(),
    trainingBackground = trainingBackground?.toEntity(),
    medicalHistory = medicalHistory?.toEntity(),
    nutritionHistory = nutritionHistory?.toEntity(),
    behavioralFactors = behavioralFactors?.toEntity(),
    metricsTracking = metricsTracking?.toEntity(),
    notes = notes
)
