package com.diaita.lib.mappings

import com.diaita.dto.RegisterUserProfileRequestDto
import com.diaita.dto.ActivityLevelLifestyleDto
import com.diaita.dto.BasicDemographicsDto
import com.diaita.dto.GoalsPrioritiesDto
import com.diaita.dto.NutritionDietHistoryDto
import com.diaita.dto.TrainingBackgroundDto
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

private val rpcPayloadJson = Json {
    explicitNulls = false
}

fun RegisterUserProfileRequestDto.toUpsertFullProfilePayload(): JsonObject = buildJsonObject {
    put("profileId", JsonPrimitive(id))
    put(
        "basicDemographics",
        rpcPayloadJson.encodeToJsonElement(BasicDemographicsDto.serializer(), basicDemographics)
    )
    put(
        "activityLifestyle",
        rpcPayloadJson.encodeToJsonElement(ActivityLevelLifestyleDto.serializer(), activityLifestyle)
    )
    put("goals", rpcPayloadJson.encodeToJsonElement(GoalsPrioritiesDto.serializer(), goals))
    trainingBackground?.let {
        put(
            "trainingBackground",
            rpcPayloadJson.encodeToJsonElement(TrainingBackgroundDto.serializer(), trainingBackground)
        )
    }
    nutritionHistory?.let {
        put(
            "nutritionHistory",
            rpcPayloadJson.encodeToJsonElement(NutritionDietHistoryDto.serializer(), nutritionHistory)
        )
    }
}
