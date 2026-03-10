package com.diaita.repo

import com.diaita.dto.RecommendationDto
import com.diaita.entity.RecommendationEntity
import com.diaita.lib.factories.SupabaseManager
import com.diaita.lib.mappings.toDto
import com.diaita.lib.mappings.toStoragePayload

class RecommendationRepo(private val supabaseManager: SupabaseManager) {

    suspend fun saveRecommendation(userId: String, recommendation: RecommendationDto): Boolean {
        val payload = recommendation.toStoragePayload(userId)
        val existing = supabaseManager
            .selectWhere<RecommendationEntity>("recommendations", "user_id", userId)
            .body
            ?.firstOrNull()

        val result = if (existing == null) {
            supabaseManager.insert("recommendations", payload)
        } else {
            supabaseManager.update("recommendations", payload, "user_id", userId)
        }

        return result.body != null
    }

    suspend fun getRecommendationByUserId(userId: String): RecommendationDto? {
        val result = supabaseManager
            .selectWhere<RecommendationEntity>("recommendations", "user_id", userId)

        return result.body?.firstOrNull()?.toDto()
    }
}
