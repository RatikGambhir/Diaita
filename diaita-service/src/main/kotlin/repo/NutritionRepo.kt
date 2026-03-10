package com.diaita.repo

import com.diaita.dto.NutritionDaySummaryResponseDto
import com.diaita.dto.UpsertMealsRequestDto
import com.diaita.lib.factories.SupabaseManager
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class NutritionRepo(private val supabaseManager: SupabaseManager)  {

    private val json = Json

    suspend fun upsertMeals(request: UpsertMealsRequestDto): NutritionDaySummaryResponseDto? {
        val payload = buildJsonObject {
            put("userId", request.userId)
            put("meals", json.parseToJsonElement(json.encodeToString(request.meals)))
        }

        val result = supabaseManager.rpcDecoded<NutritionDaySummaryResponseDto>(
            functionName = UPSERT_MEALS_RPC,
            parameters = mapOf(
                "p_user_id" to request.userId,
                "p_payload" to payload
            )
        )

        return result.body
    }

    companion object {
        private const val UPSERT_MEALS_RPC = "nutrition_upsert_meals"
    }
}
