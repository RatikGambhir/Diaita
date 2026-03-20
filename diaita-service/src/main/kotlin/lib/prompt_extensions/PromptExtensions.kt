package com.diaita.lib.prompt_extensions

import com.diaita.dto.RegisterUserProfileRequestDto

fun RegisterUserProfileRequestDto.toPromptVariables(): Map<String, Any> = mapOf(
    "userId" to userId,
    "age" to age.toString(),
    "height" to height.toString(),
    "weight" to weight.toString(),
    "activityLevel" to activityLevel,
    "sleepDuration" to (sleepDuration?.toString() ?: ""),
    "stressLevel" to (stressLevel ?: ""),
    "primaryGoal" to primaryGoal,
    "timeframe" to (timeframe ?: ""),
    "trainingAge" to (trainingAge ?: ""),
    "trainingHistory" to (trainingHistory ?: ""),
    "equipmentAccess" to (equipmentAccess ?: ""),
    "timePerSession" to (timePerSession?.toString() ?: ""),
    "daysPerWeek" to (daysPerWeek?.toString() ?: ""),
    "injuries" to (injuries?.joinToString(", ") ?: ""),
    "chronicConditions" to (chronicConditions?.joinToString(", ") ?: ""),
    "mobilityRestrictions" to (mobilityRestrictions?.joinToString(", ") ?: ""),
    "doctorRestrictions" to (doctorRestrictions ?: ""),
    "currentDietPattern" to (currentDietPattern ?: ""),
    "foodAllergies" to (foodAllergies?.joinToString(", ") ?: ""),
    "dietaryRestrictions" to (dietaryRestrictions?.joinToString(", ") ?: ""),
    "eatingSchedule" to (eatingSchedule ?: "")
)
