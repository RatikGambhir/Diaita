package com.diaita.lib.prompt_extensions

import com.diaita.dto.ActivityLevelLifestyleDto
import com.diaita.dto.BasicDemographicsDto
import com.diaita.dto.GoalsPrioritiesDto
import com.diaita.dto.NutritionDietHistoryDto
import com.diaita.dto.RegisterUserProfileRequestDto
import com.diaita.dto.RegisteredUserProfileDto
import com.diaita.dto.TrainingBackgroundDto

fun RegisterUserProfileRequestDto.toPromptVariables(): Map<String, Any> = buildPromptVariables(
    userId = userId,
    basicDemographics = basicDemographics,
    activityLifestyle = activityLifestyle,
    goals = goals,
    trainingBackground = trainingBackground,
    nutritionHistory = nutritionHistory,
    notes = ""
)

fun RegisteredUserProfileDto.toPromptVariables(): Map<String, Any> = buildPromptVariables(
    userId = userId,
    basicDemographics = basicDemographics,
    activityLifestyle = activityLifestyle,
    goals = goals,
    trainingBackground = trainingBackground,
    nutritionHistory = nutritionHistory,
    notes = notes ?: ""
)

private fun buildPromptVariables(
    userId: String,
    basicDemographics: BasicDemographicsDto?,
    activityLifestyle: ActivityLevelLifestyleDto?,
    goals: GoalsPrioritiesDto?,
    trainingBackground: TrainingBackgroundDto?,
    nutritionHistory: NutritionDietHistoryDto?,
    notes: String
): Map<String, Any> {
    return mapOf(
        "userId" to userId,
        "age" to (basicDemographics?.age?.toString() ?: ""),
        "sex" to (basicDemographics?.sex ?: ""),
        "gender" to (basicDemographics?.gender ?: ""),
        "height" to (basicDemographics?.height?.toString() ?: ""),
        "weight" to (basicDemographics?.weight?.toString() ?: ""),
        "bodyFatPercentage" to (basicDemographics?.bodyFatPercentage?.toString() ?: ""),
        "leanMass" to (basicDemographics?.leanMass?.toString() ?: ""),
        "biologicalConsiderations" to (basicDemographics?.biologicalConsiderations ?: ""),
        "menstrualCycleInfo" to (basicDemographics?.menstrualCycleInfo ?: ""),
        "activityLevel" to (activityLifestyle?.activityLevel ?: ""),
        "dailyStepCount" to (activityLifestyle?.dailyStepCount?.toString() ?: ""),
        "jobType" to (activityLifestyle?.jobType ?: ""),
        "commuteTime" to (activityLifestyle?.commuteTime ?: ""),
        "sleepDuration" to (activityLifestyle?.sleepDuration?.toString() ?: ""),
        "sleepQuality" to (activityLifestyle?.sleepQuality ?: ""),
        "stressLevel" to (activityLifestyle?.stressLevel ?: ""),
        "recoveryCapacity" to (activityLifestyle?.recoveryCapacity ?: ""),
        "primaryGoal" to (goals?.primaryGoal ?: ""),
        "secondaryGoals" to (goals?.secondaryGoals?.joinToString(", ") ?: ""),
        "timeframe" to (goals?.timeframe ?: ""),
        "targetWeight" to (goals?.targetWeight?.toString() ?: ""),
        "performanceMetric" to (goals?.performanceMetric ?: ""),
        "aestheticGoals" to (goals?.aestheticGoals ?: ""),
        "healthGoals" to (goals?.healthGoals?.joinToString(", ") ?: ""),
        "trainingAge" to (trainingBackground?.trainingAge ?: ""),
        "trainingHistory" to (trainingBackground?.trainingHistory ?: ""),
        "currentWorkoutRoutine" to (trainingBackground?.currentWorkoutRoutine ?: ""),
        "exercisePreferences" to (trainingBackground?.exercisePreferences?.joinToString(", ") ?: ""),
        "exerciseDislikes" to (trainingBackground?.exerciseDislikes?.joinToString(", ") ?: ""),
        "equipmentAccess" to (trainingBackground?.equipmentAccess ?: ""),
        "timePerSession" to (trainingBackground?.timePerSession?.toString() ?: ""),
        "daysPerWeek" to (trainingBackground?.daysPerWeek?.toString() ?: ""),
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
        "notes" to notes
    )
}
