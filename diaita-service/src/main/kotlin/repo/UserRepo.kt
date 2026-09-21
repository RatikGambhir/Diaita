package com.diaita.repo

import com.diaita.database.SQLiteDatabase
import com.diaita.dto.RegisterUserProfileRequestDto
import com.diaita.dto.RegisteredUserProfileDto
import com.diaita.dto.UserSettingsPage
import com.diaita.entity.ActivityLifestyleRowEntity
import com.diaita.entity.BasicDemographicsRowEntity
import com.diaita.entity.GoalsPrioritiesRowEntity
import com.diaita.entity.NutritionHistoryRowEntity
import com.diaita.entity.TrainingBackgroundRowEntity
import java.time.Instant
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class UserRepo(private val database: SQLiteDatabase) {
    private val json = Json { ignoreUnknownKeys = true; explicitNulls = false }

    suspend fun upsertUserProfile(request: RegisterUserProfileRequestDto): RegisteredUserProfileDto? =
        runCatching {
            database.connection { connection ->
                connection.prepareStatement(
                    """INSERT INTO user_profiles (user_id, profile_json, updated_at)
                       VALUES (?, ?, ?)
                       ON CONFLICT(user_id) DO UPDATE SET profile_json = excluded.profile_json, updated_at = excluded.updated_at"""
                ).use { statement ->
                    statement.setString(1, request.userId)
                    statement.setString(2, json.encodeToString(request))
                    statement.setString(3, Instant.now().toString())
                    statement.executeUpdate()
                }
            }
            request
        }.getOrNull()

    suspend fun getFullProfile(userId: String): RegisteredUserProfileDto? = database.connection { connection ->
        connection.prepareStatement("SELECT profile_json FROM user_profiles WHERE user_id = ?").use { statement ->
            statement.setString(1, userId)
            statement.executeQuery().use { result ->
                if (result.next()) json.decodeFromString<RegisterUserProfileRequestDto>(result.getString(1)) else null
            }
        }
    }

    suspend fun getSettingsSection(page: UserSettingsPage, userId: String): Any? = database.connection { connection ->
        connection.prepareStatement(
            "SELECT data_json FROM user_settings WHERE user_id = ? AND section = ?"
        ).use { statement ->
            statement.setString(1, userId)
            statement.setString(2, page.storageKey)
            statement.executeQuery().use { result ->
                if (!result.next()) null else decodeSection(page, result.getString(1))
            }
        }
    }

    suspend fun updateSettingsSection(page: UserSettingsPage, userId: String, data: Any): Any? = runCatching {
        val encoded = encodeSection(page, data)
        database.connection { connection ->
            connection.prepareStatement(
                """INSERT INTO user_settings (user_id, section, data_json, updated_at)
                   VALUES (?, ?, ?, ?)
                   ON CONFLICT(user_id, section) DO UPDATE SET data_json = excluded.data_json, updated_at = excluded.updated_at"""
            ).use { statement ->
                statement.setString(1, userId)
                statement.setString(2, page.storageKey)
                statement.setString(3, encoded)
                statement.setString(4, Instant.now().toString())
                statement.executeUpdate()
            }
        }
        data
    }.getOrNull()

    suspend fun deleteSettingsSection(page: UserSettingsPage, userId: String): Boolean = database.connection { connection ->
        connection.prepareStatement("DELETE FROM user_settings WHERE user_id = ? AND section = ?").use { statement ->
            statement.setString(1, userId)
            statement.setString(2, page.storageKey)
            statement.executeUpdate() > 0
        }
    }

    private fun encodeSection(page: UserSettingsPage, data: Any): String = when (page) {
        UserSettingsPage.BASIC_DEMOGRAPHICS -> json.encodeToString(data as BasicDemographicsRowEntity)
        UserSettingsPage.ACTIVITY_LIFESTYLE -> json.encodeToString(data as ActivityLifestyleRowEntity)
        UserSettingsPage.GOALS_PRIORITIES -> json.encodeToString(data as GoalsPrioritiesRowEntity)
        UserSettingsPage.TRAINING_BACKGROUND -> json.encodeToString(data as TrainingBackgroundRowEntity)
        UserSettingsPage.NUTRITION_HISTORY -> json.encodeToString(data as NutritionHistoryRowEntity)
    }

    private fun decodeSection(page: UserSettingsPage, value: String): Any = when (page) {
        UserSettingsPage.BASIC_DEMOGRAPHICS -> json.decodeFromString<BasicDemographicsRowEntity>(value)
        UserSettingsPage.ACTIVITY_LIFESTYLE -> json.decodeFromString<ActivityLifestyleRowEntity>(value)
        UserSettingsPage.GOALS_PRIORITIES -> json.decodeFromString<GoalsPrioritiesRowEntity>(value)
        UserSettingsPage.TRAINING_BACKGROUND -> json.decodeFromString<TrainingBackgroundRowEntity>(value)
        UserSettingsPage.NUTRITION_HISTORY -> json.decodeFromString<NutritionHistoryRowEntity>(value)
    }

    private val UserSettingsPage.storageKey: String
        get() = name.lowercase()
}
