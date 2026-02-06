package com.nutrify.repo

import com.nutrify.dto.RegisterUserProfileRequest
import com.nutrify.lib.factories.SupabaseManager
import com.nutrify.lib.mappings.*

class UserRepo(private val supabaseManager: SupabaseManager) {

    suspend fun upsertFullProfile(request: RegisterUserProfileRequest): String {
        val userId = request.userId

        if (upsertOrFail("user_profile", request.toUserProfileRow(userId)) == null) {
            return "Mutation Failed"
        }
        if (upsertOrFail("basic_demographics", request.toBasicDemographicsRow(userId)) == null) {
            return "Mutation Failed"
        }
        if (upsertOrFail("activity_lifestyle", request.toActivityLifestyleRow(userId)) == null) {
            return "Mutation Failed"
        }
        if (upsertOrFail("goals_priorities", request.toGoalsPrioritiesRow(userId)) == null) {
            return "Mutation Failed"
        }

        request.trainingBackground?.let { trainingBackground ->
            if (upsertOrFail("training_background", trainingBackground.toTrainingBackgroundRow(userId)) == null) {
                return "Mutation Failed"
            }
        }

        request.medicalHistory?.let { medicalHistory ->
            if (upsertOrFail("medical_history", medicalHistory.toMedicalHistoryRow(userId)) == null) {
                return "Mutation Failed"
            }
        }

        request.nutritionHistory?.let { nutritionHistory ->
            if (upsertOrFail("nutrition_history", nutritionHistory.toNutritionHistoryRow(userId)) == null) {
                return "Mutation Failed"
            }
        }

        request.behavioralFactors?.let { behavioralFactors ->
            if (upsertOrFail("behavioral_factors", behavioralFactors.toBehavioralFactorsRow(userId)) == null) {
                return "Mutation Failed"
            }
        }

        request.metricsTracking?.let { metricsTracking ->
            if (upsertOrFail("metrics_tracking", metricsTracking.toMetricsTrackingRow(userId)) == null) {
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
}
