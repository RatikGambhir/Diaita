package com.diaita.lib.prompt_extensions

import com.diaita.dto.RegisterUserProfileRequestDto

fun RegisterUserProfileRequestDto.toPromptVariables(): Map<String, Any> {
    return mapOf(
        // Basic Demographics
        "userId" to userId,
        "age" to basicDemographics.age,
        "sex" to (basicDemographics.sex ?: ""),
        "gender" to (basicDemographics.gender ?: ""),
        "height" to basicDemographics.height,
        "weight" to basicDemographics.weight,
        "bodyFatPercentage" to (basicDemographics.bodyFatPercentage?.toString() ?: ""),
        "leanMass" to (basicDemographics.leanMass?.toString() ?: ""),
        "biologicalConsiderations" to (basicDemographics.biologicalConsiderations ?: ""),
        "menstrualCycleInfo" to (basicDemographics.menstrualCycleInfo ?: ""),

        // Activity & Lifestyle
        "activityLevel" to activityLifestyle.activityLevel,
        "dailyStepCount" to (activityLifestyle.dailyStepCount?.toString() ?: ""),
        "jobType" to (activityLifestyle.jobType ?: ""),
        "commuteTime" to (activityLifestyle.commuteTime ?: ""),
        "sleepDuration" to (activityLifestyle.sleepDuration?.toString() ?: ""),
        "sleepQuality" to (activityLifestyle.sleepQuality ?: ""),
        "stressLevel" to (activityLifestyle.stressLevel ?: ""),
        "recoveryCapacity" to (activityLifestyle.recoveryCapacity ?: ""),

        // Goals & Priorities
        "primaryGoal" to goals.primaryGoal,
        "secondaryGoals" to (goals.secondaryGoals?.joinToString(", ") ?: ""),
        "timeframe" to (goals.timeframe ?: ""),
        "targetWeight" to (goals.targetWeight?.toString() ?: ""),
        "performanceMetric" to (goals.performanceMetric ?: ""),
        "aestheticGoals" to (goals.aestheticGoals ?: ""),
        "healthGoals" to (goals.healthGoals?.joinToString(", ") ?: ""),

        // Training Background
        "trainingAge" to (trainingBackground?.trainingAge ?: ""),
        "trainingHistory" to (trainingBackground?.trainingHistory ?: ""),
        "currentWorkoutRoutine" to (trainingBackground?.currentWorkoutRoutine ?: ""),
        "exercisePreferences" to (trainingBackground?.exercisePreferences?.joinToString(", ") ?: ""),
        "exerciseDislikes" to (trainingBackground?.exerciseDislikes?.joinToString(", ") ?: ""),
        "equipmentAccess" to (trainingBackground?.equipmentAccess ?: ""),
        "timePerSession" to (trainingBackground?.timePerSession?.toString() ?: ""),
        "daysPerWeek" to (trainingBackground?.daysPerWeek?.toString() ?: ""),

        // Medical History
        "injuries" to (medicalHistory?.injuries?.joinToString(", ") ?: ""),
        "chronicConditions" to (medicalHistory?.chronicConditions?.joinToString(", ") ?: ""),
        "painPatterns" to (medicalHistory?.painPatterns ?: ""),
        "mobilityRestrictions" to (medicalHistory?.mobilityRestrictions?.joinToString(", ") ?: ""),
        "medications" to (medicalHistory?.medications?.joinToString(", ") ?: ""),
        "doctorRestrictions" to (medicalHistory?.doctorRestrictions ?: ""),

        // Nutrition & Diet History
        "currentDietPattern" to (nutritionHistory?.currentDietPattern ?: ""),
        "calorieTrackingExperience" to (nutritionHistory?.calorieTrackingExperience?.toString() ?: ""),
        "macronutrientPreferences" to (nutritionHistory?.macronutrientPreferences ?: ""),
        "foodAllergies" to (nutritionHistory?.foodAllergies?.joinToString(", ") ?: ""),
        "dietaryRestrictions" to (nutritionHistory?.dietaryRestrictions?.joinToString(", ") ?: ""),
        "culturalFoodPreferences" to (nutritionHistory?.culturalFoodPreferences ?: ""),
        "cookingSkillLevel" to (nutritionHistory?.cookingSkillLevel ?: ""),
        "foodBudget" to (nutritionHistory?.foodBudget ?: ""),
        "eatingSchedule" to (nutritionHistory?.eatingSchedule ?: ""),
        "snackingHabits" to (nutritionHistory?.snackingHabits ?: ""),
        "alcoholIntake" to (nutritionHistory?.alcoholIntake ?: ""),
        "supplementUse" to (nutritionHistory?.supplementUse?.joinToString(", ") ?: ""),

        // Behavioral Factors
        "motivationLevel" to (behavioralFactors?.motivationLevel ?: ""),
        "consistencyHistory" to (behavioralFactors?.consistencyHistory ?: ""),
        "accountabilityPreference" to (behavioralFactors?.accountabilityPreference ?: ""),
        "pastSuccessFailurePatterns" to (behavioralFactors?.pastSuccessFailurePatterns ?: ""),
        "relationshipWithFood" to (behavioralFactors?.relationshipWithFood ?: ""),
        "disorderedEatingHistory" to (behavioralFactors?.disorderedEatingHistory ?: ""),
        "stressEatingTendencies" to (behavioralFactors?.stressEatingTendencies ?: ""),
        "supportSystem" to (behavioralFactors?.supportSystem ?: ""),

        // Metrics & Tracking
        "preferredProgressMetrics" to (metricsTracking?.preferredProgressMetrics?.joinToString(", ") ?: ""),
        "trackingTools" to (metricsTracking?.trackingTools?.joinToString(", ") ?: ""),
        "checkinFrequency" to (metricsTracking?.checkinFrequency ?: ""),

        // Notes
        "notes" to (notes ?: "")
    )
}
