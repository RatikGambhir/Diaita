package com.diaita.repo

import com.diaita.dto.RegisterUserProfileRequestDto
import com.diaita.entity.*
import com.diaita.lib.factories.SupabaseManager
import com.diaita.lib.mappings.*

class UserRepo(private val supabaseManager: SupabaseManager) {

    suspend fun upsertFullProfile(request: RegisterUserProfileRequestDto): String {
        val userId = request.userId

        if (upsertOrFail("user_profile", request.toUserProfileEntity(userId)) == null) {
            return "Mutation Failed"
        }
        if (upsertOrFail("basic_demographics", request.toBasicDemographicsEntity(userId)) == null) {
            return "Mutation Failed"
        }
        if (upsertOrFail("activity_lifestyle", request.toActivityLifestyleEntity(userId)) == null) {
            return "Mutation Failed"
        }
        if (upsertOrFail("goals_priorities", request.toGoalsPrioritiesEntity(userId)) == null) {
            return "Mutation Failed"
        }

        request.trainingBackground?.let { trainingBackground ->
            if (upsertOrFail("training_background", trainingBackground.toEntity(userId)) == null) {
                return "Mutation Failed"
            }
        }

        request.medicalHistory?.let { medicalHistory ->
            if (upsertOrFail("medical_history", medicalHistory.toEntity(userId)) == null) {
                return "Mutation Failed"
            }
        }

        request.nutritionHistory?.let { nutritionHistory ->
            if (upsertOrFail("nutrition_history", nutritionHistory.toEntity(userId)) == null) {
                return "Mutation Failed"
            }
        }

        request.behavioralFactors?.let { behavioralFactors ->
            if (upsertOrFail("behavioral_factors", behavioralFactors.toEntity(userId)) == null) {
                return "Mutation Failed"
            }
        }

        request.metricsTracking?.let { metricsTracking ->
            if (upsertOrFail("metrics_tracking", metricsTracking.toEntity(userId)) == null) {
                return "Mutation Failed"
            }
        }

        return "Mutation Success"
    }

    private suspend inline fun <reified T : Any> upsertOrFail(table: String, data: T): T? {
        val result = supabaseManager.upsert(table, data)
        if (result.body == null) {
            println("Error upserting $table: ${result.error?.message}")
        }
        return result.body
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

    suspend fun getMedicalHistory(userId: String): MedicalHistoryRowEntity? =
        getSection("medical_history", userId)

    suspend fun updateMedicalHistory(
        userId: String,
        data: MedicalHistoryRowEntity
    ): MedicalHistoryRowEntity? = updateSection("medical_history", data, userId)

    suspend fun deleteMedicalHistory(userId: String): Boolean =
        deleteSection("medical_history", userId)

    suspend fun getNutritionHistory(userId: String): NutritionHistoryRowEntity? =
        getSection("nutrition_history", userId)

    suspend fun updateNutritionHistory(
        userId: String,
        data: NutritionHistoryRowEntity
    ): NutritionHistoryRowEntity? = updateSection("nutrition_history", data, userId)

    suspend fun deleteNutritionHistory(userId: String): Boolean =
        deleteSection("nutrition_history", userId)

    suspend fun getBehavioralFactors(userId: String): BehavioralFactorsRowEntity? =
        getSection("behavioral_factors", userId)

    suspend fun updateBehavioralFactors(
        userId: String,
        data: BehavioralFactorsRowEntity
    ): BehavioralFactorsRowEntity? = updateSection("behavioral_factors", data, userId)

    suspend fun deleteBehavioralFactors(userId: String): Boolean =
        deleteSection("behavioral_factors", userId)

    suspend fun getMetricsTracking(userId: String): MetricsTrackingRowEntity? =
        getSection("metrics_tracking", userId)

    suspend fun updateMetricsTracking(
        userId: String,
        data: MetricsTrackingRowEntity
    ): MetricsTrackingRowEntity? = updateSection("metrics_tracking", data, userId)

    suspend fun deleteMetricsTracking(userId: String): Boolean =
        deleteSection("metrics_tracking", userId)
}
