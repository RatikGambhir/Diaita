package com.diaita.repo

import com.diaita.database.SQLiteDatabase
import com.diaita.dto.RecommendationDto
import java.time.Instant
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class RecommendationRepo(private val database: SQLiteDatabase) {
    private val json = Json { ignoreUnknownKeys = true; explicitNulls = false }

    suspend fun saveRecommendation(userId: String, recommendation: RecommendationDto): Boolean = runCatching {
        database.connection { connection ->
            connection.prepareStatement(
                """INSERT INTO recommendations (user_id, data_json, updated_at)
                   VALUES (?, ?, ?)
                   ON CONFLICT(user_id) DO UPDATE SET data_json = excluded.data_json, updated_at = excluded.updated_at"""
            ).use { statement ->
                statement.setString(1, userId)
                statement.setString(2, json.encodeToString(recommendation))
                statement.setString(3, Instant.now().toString())
                statement.executeUpdate() == 1
            }
        }
    }.getOrDefault(false)

    suspend fun getRecommendationByUserId(userId: String): RecommendationDto? = database.connection { connection ->
        connection.prepareStatement("SELECT data_json FROM recommendations WHERE user_id = ?").use { statement ->
            statement.setString(1, userId)
            statement.executeQuery().use { result ->
                if (result.next()) json.decodeFromString<RecommendationDto>(result.getString(1)) else null
            }
        }
    }
}
