package com.diaita.lib.mappings

import com.diaita.dto.*
import com.diaita.entity.*

fun RegisterUserProfileRequestDto.toUserProfileEntity(userId: String) = UserProfileRowEntity(
    userId = userId,
    profileId = id,
    notes = notes
)

fun RegisterUserProfileRequestDto.toBasicDemographicsEntity(userId: String) =
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

fun RegisterUserProfileRequestDto.toActivityLifestyleEntity(userId: String) =
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

fun RegisterUserProfileRequestDto.toGoalsPrioritiesEntity(userId: String) =
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

fun MedicalHistoryDto.toEntity(userId: String) =
    MedicalHistoryRowEntity(
        userId = userId,
        injuries = injuries,
        chronicConditions = chronicConditions,
        painPatterns = painPatterns,
        mobilityRestrictions = mobilityRestrictions,
        medications = medications,
        doctorRestrictions = doctorRestrictions
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

fun BehavioralFactorsDto.toEntity(userId: String) =
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

fun MetricsTrackingDto.toEntity(userId: String) =
    MetricsTrackingRowEntity(
        userId = userId,
        preferredProgressMetrics = preferredProgressMetrics,
        trackingTools = trackingTools,
        checkinFrequency = checkinFrequency
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

fun MedicalHistoryRowEntity.toDto() = MedicalHistoryDto(
    injuries = injuries,
    chronicConditions = chronicConditions,
    painPatterns = painPatterns,
    mobilityRestrictions = mobilityRestrictions,
    medications = medications,
    doctorRestrictions = doctorRestrictions
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

fun BehavioralFactorsRowEntity.toDto() = BehavioralFactorsDto(
    motivationLevel = motivationLevel,
    consistencyHistory = consistencyHistory,
    accountabilityPreference = accountabilityPreference,
    pastSuccessFailurePatterns = pastSuccessFailurePatterns,
    relationshipWithFood = relationshipWithFood,
    disorderedEatingHistory = disorderedEatingHistory,
    stressEatingTendencies = stressEatingTendencies,
    supportSystem = supportSystem
)

fun MetricsTrackingRowEntity.toDto() = MetricsTrackingDto(
    preferredProgressMetrics = preferredProgressMetrics,
    trackingTools = trackingTools,
    checkinFrequency = checkinFrequency
)
