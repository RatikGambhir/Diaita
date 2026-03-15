package com.diaita.repo

import com.diaita.dto.NutritionDaySummaryResponseDto
import com.diaita.dto.UpsertMealsRequestDto
import com.diaita.entity.MealItemRowEntity
import com.diaita.entity.MealRowEntity
import com.diaita.lib.factories.PostgresFactory
import com.diaita.lib.factories.SupabaseManager
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class NutritionRepo(private val supabaseManager: SupabaseManager)  {

    private val json = Json

    suspend fun getMealsForUser(userId: String): List<MealRowEntity> {
        val result = supabaseManager.selectWhere<MealRowEntity>(
            table = PostgresFactory.MEALS_TABLE,
            column = PostgresFactory.USER_ID_COLUMN,
            value = userId
        )

        return result.body ?: emptyList()
    }

    suspend fun getMealItemsForUser(userId: String): List<MealItemRowEntity> {
        val result = supabaseManager.selectWhere<MealItemRowEntity>(
            table = PostgresFactory.MEAL_ITEMS_TABLE,
            column = PostgresFactory.USER_ID_COLUMN,
            value = userId
        )

        return result.body ?: emptyList()
    }

    suspend fun upsertMeals(request: UpsertMealsRequestDto): NutritionDaySummaryResponseDto? {
        val payload = buildJsonObject {
            put("userId", request.userId)
            put("meals", json.parseToJsonElement(json.encodeToString(request.meals)))
        }

        val result = supabaseManager.rpcDecoded<NutritionDaySummaryResponseDto>(
            functionName = PostgresFactory.UPSERT_MEALS_RPC,
            parameters = payload,
            schemaStr = PostgresFactory.NUTRITION_SCHEMA
        )

        return result.body
    }
}
