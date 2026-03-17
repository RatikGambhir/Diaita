package com.diaita.repo

import com.diaita.dto.RegisterUserProfileRequestDto
import com.diaita.dto.RegisteredUserProfileDto
import com.diaita.entity.*
import com.diaita.lib.factories.PostgresFactory
import com.diaita.lib.factories.SupabaseManager
import com.diaita.lib.mappings.*
import kotlinx.serialization.serializer
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive

class UserRepo(private val supabaseManager: SupabaseManager) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun upsertFullProfile(request: RegisterUserProfileRequestDto): RegisteredUserProfileDto? {
        val result = supabaseManager.rpcDecoded<RegisteredUserProfileDto>(
            functionName = PostgresFactory.UPSERT_FULL_PROFILE_RPC,
            parameters = buildJsonObject {
                put("p_user_id", JsonPrimitive(request.userId))
                put("p_payload", request.toUpsertFullProfilePayload())
            }
        )

        return result.body
    }

    private suspend inline fun <reified T : Any> getSection(table: String, userId: String): T? {
        val result = supabaseManager.selectSingle<T>(table, PostgresFactory.USER_ID_COLUMN, userId)
        return result.body
    }

    private suspend inline fun <reified T : Any> updateSection(table: String, data: T, userId: String): T? {
        val result = supabaseManager.update(table, data, PostgresFactory.USER_ID_COLUMN, userId)
        return result.body
    }

    private suspend fun deleteSection(table: String, userId: String): Boolean {
        val result = supabaseManager.delete(table, PostgresFactory.USER_ID_COLUMN, userId)
        return result.error == null
    }

    suspend fun getBasicDemographics(userId: String): BasicDemographicsRowEntity? =
        getSection(PostgresFactory.BASIC_DEMOGRAPHICS_TABLE, userId)

    suspend fun updateBasicDemographics(
        userId: String,
        data: BasicDemographicsRowEntity
    ): BasicDemographicsRowEntity? = updateSection(PostgresFactory.BASIC_DEMOGRAPHICS_TABLE, data, userId)

    suspend fun deleteBasicDemographics(userId: String): Boolean =
        deleteSection(PostgresFactory.BASIC_DEMOGRAPHICS_TABLE, userId)

    suspend fun getActivityLifestyle(userId: String): ActivityLifestyleRowEntity? =
        getSection(PostgresFactory.ACTIVITY_LIFESTYLE_TABLE, userId)

    suspend fun updateActivityLifestyle(
        userId: String,
        data: ActivityLifestyleRowEntity
    ): ActivityLifestyleRowEntity? = updateSection(PostgresFactory.ACTIVITY_LIFESTYLE_TABLE, data, userId)

    suspend fun deleteActivityLifestyle(userId: String): Boolean =
        deleteSection(PostgresFactory.ACTIVITY_LIFESTYLE_TABLE, userId)

    suspend fun getGoalsPriorities(userId: String): GoalsPrioritiesRowEntity? =
        getSection(PostgresFactory.GOALS_PRIORITIES_TABLE, userId)

    suspend fun updateGoalsPriorities(
        userId: String,
        data: GoalsPrioritiesRowEntity
    ): GoalsPrioritiesRowEntity? = updateSection(PostgresFactory.GOALS_PRIORITIES_TABLE, data, userId)

    suspend fun deleteGoalsPriorities(userId: String): Boolean =
        deleteSection(PostgresFactory.GOALS_PRIORITIES_TABLE, userId)

    suspend fun getTrainingBackground(userId: String): TrainingBackgroundRowEntity? =
        getSection(PostgresFactory.TRAINING_BACKGROUND_TABLE, userId)

    suspend fun updateTrainingBackground(
        userId: String,
        data: TrainingBackgroundRowEntity
    ): TrainingBackgroundRowEntity? = updateSection(PostgresFactory.TRAINING_BACKGROUND_TABLE, data, userId)

    suspend fun deleteTrainingBackground(userId: String): Boolean =
        deleteSection(PostgresFactory.TRAINING_BACKGROUND_TABLE, userId)

    suspend fun getNutritionHistory(userId: String): NutritionHistoryRowEntity? =
        getSection(PostgresFactory.NUTRITION_HISTORY_TABLE, userId)

    suspend fun updateNutritionHistory(
        userId: String,
        data: NutritionHistoryRowEntity
    ): NutritionHistoryRowEntity? = updateSection(PostgresFactory.NUTRITION_HISTORY_TABLE, data, userId)

    suspend fun deleteNutritionHistory(userId: String): Boolean =
        deleteSection(PostgresFactory.NUTRITION_HISTORY_TABLE, userId)

    suspend fun getFullProfile(userId: String): RegisteredUserProfileDto? {
        val result = supabaseManager.selectSingle<JsonObject>(
            table = PostgresFactory.USER_PROFILE_TABLE,
            column = PostgresFactory.USER_ID_COLUMN,
            value = userId,
            columns = """
                user_id,
                basic_demographics(*),
                activity_lifestyle(*),
                goals_priorities(*),
                training_background(*),
                nutrition_history(*)
            """.trimIndent()
        )

        val row = result.body ?: return null

        return RegisteredUserProfileDto(
            userId = row["user_id"]?.jsonPrimitive?.content ?: userId,
            basicDemographics = decodeEmbedded<BasicDemographicsRowEntity>(row, PostgresFactory.BASIC_DEMOGRAPHICS_TABLE)?.toDto(),
            activityLifestyle = decodeEmbedded<ActivityLifestyleRowEntity>(row, PostgresFactory.ACTIVITY_LIFESTYLE_TABLE)?.toDto(),
            goals = decodeEmbedded<GoalsPrioritiesRowEntity>(row, PostgresFactory.GOALS_PRIORITIES_TABLE)?.toDto(),
            trainingBackground = decodeEmbedded<TrainingBackgroundRowEntity>(row, PostgresFactory.TRAINING_BACKGROUND_TABLE)?.toDto(),
            nutritionHistory = decodeEmbedded<NutritionHistoryRowEntity>(row, PostgresFactory.NUTRITION_HISTORY_TABLE)?.toDto()
        )
    }

    private inline fun <reified T : Any> decodeEmbedded(row: JsonObject, key: String): T? {
        val element = row[key]?.normalizeEmbedded() ?: return null
        return json.decodeFromJsonElement(serializer<T>(), element)
    }

    private fun JsonElement.normalizeEmbedded(): JsonElement? = when (this) {
        JsonNull -> null
        is JsonArray -> firstOrNull()?.takeUnless { it is JsonNull }
        else -> this
    }
}
