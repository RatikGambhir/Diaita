package com.diaita.repo

import com.diaita.dto.RegisterUserProfileRequestDto
import com.diaita.dto.RegisteredUserProfileDto
import com.diaita.entity.*
import com.diaita.lib.factories.PostgresFactory
import com.diaita.lib.factories.SupabaseManager
import com.diaita.lib.mappings.*
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

class UserRepo(private val supabaseManager: SupabaseManager) {

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
        val result = supabaseManager.rpcDecoded<RegisteredUserProfileDto>(
            functionName = PostgresFactory.GET_FULL_PROFILE_RPC,
            parameters = buildJsonObject {
                put("p_user_id", JsonPrimitive(userId))
            }
        )
        return result.body
    }
}
