package com.diaita.repo

import com.diaita.dto.RegisterUserProfileRequestDto
import com.diaita.dto.RegisteredUserProfileDto
import com.diaita.entity.*
import com.diaita.lib.factories.SupabaseManager
import com.diaita.lib.mappings.*
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

class UserRepo(private val supabaseManager: SupabaseManager) {

    suspend fun upsertFullProfile(request: RegisterUserProfileRequestDto): String {
        val result = supabaseManager.rpcDecoded<RegisteredUserProfileDto>(
            functionName = UPSERT_FULL_PROFILE_RPC,
            parameters = buildJsonObject {
                put("p_user_id", JsonPrimitive(request.userId))
                put("p_payload", request.toUpsertFullProfilePayload())
            }
        )

        return if (result.body != null) "Mutation Success" else "Mutation Failed"
    }

    private suspend inline fun <reified T : Any> getSection(table: String, userId: String): T? {
        val result = supabaseManager.selectSingle<T>(table, "user_id", userId)
        return result.body
    }

    private suspend inline fun <reified T : Any> updateSection(table: String, data: T, userId: String): T? {
        val result = supabaseManager.update(table, data, "user_id", userId)
        return result.body
    }

    private suspend fun deleteSection(table: String, userId: String): Boolean {
        val result = supabaseManager.delete(table, "user_id", userId)
        return result.error == null
    }

    suspend fun getBasicDemographics(userId: String): BasicDemographicsRowEntity? =
        getSection("basic_demographics", userId)

    suspend fun updateBasicDemographics(
        userId: String,
        data: BasicDemographicsRowEntity
    ): BasicDemographicsRowEntity? = updateSection("basic_demographics", data, userId)

    suspend fun deleteBasicDemographics(userId: String): Boolean =
        deleteSection("basic_demographics", userId)

    suspend fun getActivityLifestyle(userId: String): ActivityLifestyleRowEntity? =
        getSection("activity_lifestyle", userId)

    suspend fun updateActivityLifestyle(
        userId: String,
        data: ActivityLifestyleRowEntity
    ): ActivityLifestyleRowEntity? = updateSection("activity_lifestyle", data, userId)

    suspend fun deleteActivityLifestyle(userId: String): Boolean =
        deleteSection("activity_lifestyle", userId)

    suspend fun getGoalsPriorities(userId: String): GoalsPrioritiesRowEntity? =
        getSection("goals_priorities", userId)

    suspend fun updateGoalsPriorities(
        userId: String,
        data: GoalsPrioritiesRowEntity
    ): GoalsPrioritiesRowEntity? = updateSection("goals_priorities", data, userId)

    suspend fun deleteGoalsPriorities(userId: String): Boolean =
        deleteSection("goals_priorities", userId)

    suspend fun getTrainingBackground(userId: String): TrainingBackgroundRowEntity? =
        getSection("training_background", userId)

    suspend fun updateTrainingBackground(
        userId: String,
        data: TrainingBackgroundRowEntity
    ): TrainingBackgroundRowEntity? = updateSection("training_background", data, userId)

    suspend fun deleteTrainingBackground(userId: String): Boolean =
        deleteSection("training_background", userId)

    suspend fun getNutritionHistory(userId: String): NutritionHistoryRowEntity? =
        getSection("nutrition_history", userId)

    suspend fun updateNutritionHistory(
        userId: String,
        data: NutritionHistoryRowEntity
    ): NutritionHistoryRowEntity? = updateSection("nutrition_history", data, userId)

    suspend fun deleteNutritionHistory(userId: String): Boolean =
        deleteSection("nutrition_history", userId)

    suspend fun getFullProfile(userId: String): RegisteredUserProfileDto? {
        val result = supabaseManager.rpcDecoded<RegisteredUserProfileDto>(
            functionName = GET_FULL_PROFILE_RPC,
            parameters = buildJsonObject {
                put("p_user_id", JsonPrimitive(userId))
            }
        )
        return result.body
    }

    private companion object {
        const val UPSERT_FULL_PROFILE_RPC = "upsert_full_profile"
        const val GET_FULL_PROFILE_RPC = "get_full_profile"
    }
}
