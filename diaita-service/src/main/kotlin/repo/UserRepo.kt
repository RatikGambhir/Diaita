package com.diaita.repo

import com.diaita.dto.RegisterUserProfileRequestDto
import com.diaita.dto.RegisteredUserProfileDto
import com.diaita.dto.UserSettingsPage
import com.diaita.entity.ActivityLifestyleRowEntity
import com.diaita.entity.BasicDemographicsRowEntity
import com.diaita.entity.GoalsPrioritiesRowEntity
import com.diaita.entity.NutritionHistoryRowEntity
import com.diaita.entity.TrainingBackgroundRowEntity
import com.diaita.entity.UserProfileRowEntity
import com.diaita.lib.factories.PostgresFactory
import com.diaita.lib.factories.SupabaseManager
import com.diaita.lib.mappings.toDto
import com.diaita.lib.mappings.toUserProfileRowEntity

class UserRepo(private val supabaseManager: SupabaseManager) {

    suspend fun upsertUserProfile(request: RegisterUserProfileRequestDto): RegisteredUserProfileDto? {
        val result = supabaseManager.upsert(
            table = PostgresFactory.USER_PROFILE_TABLE,
            data = request.toUserProfileRowEntity()
        )

        return result.body?.toDto()
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

    suspend fun getSettingsSection(page: UserSettingsPage, userId: String): Any? {
        val table = page.tableName()
        return when (page) {
            UserSettingsPage.BASIC_DEMOGRAPHICS -> getSection<BasicDemographicsRowEntity>(table, userId)
            UserSettingsPage.ACTIVITY_LIFESTYLE -> getSection<ActivityLifestyleRowEntity>(table, userId)
            UserSettingsPage.GOALS_PRIORITIES -> getSection<GoalsPrioritiesRowEntity>(table, userId)
            UserSettingsPage.TRAINING_BACKGROUND -> getSection<TrainingBackgroundRowEntity>(table, userId)
            UserSettingsPage.NUTRITION_HISTORY -> getSection<NutritionHistoryRowEntity>(table, userId)
        }
    }

    suspend fun updateSettingsSection(
        page: UserSettingsPage,
        userId: String,
        data: Any
    ): Any? = when (page) {
        UserSettingsPage.BASIC_DEMOGRAPHICS -> updateTypedSection<BasicDemographicsRowEntity>(page, userId, data)
        UserSettingsPage.ACTIVITY_LIFESTYLE -> updateTypedSection<ActivityLifestyleRowEntity>(page, userId, data)
        UserSettingsPage.GOALS_PRIORITIES -> updateTypedSection<GoalsPrioritiesRowEntity>(page, userId, data)
        UserSettingsPage.TRAINING_BACKGROUND -> updateTypedSection<TrainingBackgroundRowEntity>(page, userId, data)
        UserSettingsPage.NUTRITION_HISTORY -> updateTypedSection<NutritionHistoryRowEntity>(page, userId, data)
    }

    suspend fun deleteSettingsSection(page: UserSettingsPage, userId: String): Boolean =
        deleteSection(page.tableName(), userId)

    suspend fun getFullProfile(userId: String): RegisteredUserProfileDto? {
        val result = supabaseManager.selectSingle<UserProfileRowEntity>(
            table = PostgresFactory.USER_PROFILE_TABLE,
            column = PostgresFactory.USER_ID_COLUMN,
            value = userId
        )

        return result.body?.toDto()
    }

    private fun UserSettingsPage.tableName(): String = when (this) {
        UserSettingsPage.BASIC_DEMOGRAPHICS -> PostgresFactory.BASIC_DEMOGRAPHICS_TABLE
        UserSettingsPage.ACTIVITY_LIFESTYLE -> PostgresFactory.ACTIVITY_LIFESTYLE_TABLE
        UserSettingsPage.GOALS_PRIORITIES -> PostgresFactory.GOALS_PRIORITIES_TABLE
        UserSettingsPage.TRAINING_BACKGROUND -> PostgresFactory.TRAINING_BACKGROUND_TABLE
        UserSettingsPage.NUTRITION_HISTORY -> PostgresFactory.NUTRITION_HISTORY_TABLE
    }

    /**
     * Updates one settings section, rejecting a payload whose runtime type does
     * not match the row entity the page maps to.
     */
    private suspend inline fun <reified T : Any> updateTypedSection(
        page: UserSettingsPage,
        userId: String,
        data: Any
    ): T? {
        require(data is T) {
            "Invalid payload type for page=$page. Expected ${T::class.simpleName}, got ${data::class.simpleName}"
        }
        return updateSection(page.tableName(), data, userId)
    }
}
