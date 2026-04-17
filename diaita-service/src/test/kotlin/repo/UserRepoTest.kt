package com.diaita.repo

import com.diaita.dto.UserSettingsPage
import com.diaita.lib.factories.SupabaseManager
import com.diaita.lib.mappings.toEntity
import com.diaita.testdata.UserProfileTestData
import io.github.jan.supabase.SupabaseClient
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNull

class UserRepoTest {

    private fun repo(): UserRepo {
        val supabaseClient = mockk<SupabaseClient>(relaxed = true)
        return UserRepo(SupabaseManager(supabaseClient))
    }

    private fun repo(manager: SupabaseManager): UserRepo = UserRepo(manager)

    @Test
    fun section_getters_return_null_when_data_source_fails() = runBlocking {
        val userRepo = repo()
        val userId = "user-get-fail"

        assertNull(userRepo.getSettingsSection(UserSettingsPage.BASIC_DEMOGRAPHICS, userId))
        assertNull(userRepo.getSettingsSection(UserSettingsPage.ACTIVITY_LIFESTYLE, userId))
        assertNull(userRepo.getSettingsSection(UserSettingsPage.GOALS_PRIORITIES, userId))
        assertNull(userRepo.getSettingsSection(UserSettingsPage.TRAINING_BACKGROUND, userId))
        assertNull(userRepo.getSettingsSection(UserSettingsPage.NUTRITION_HISTORY, userId))
    }

    @Test
    fun section_updaters_return_null_when_data_source_fails() = runBlocking {
        val request = UserProfileTestData.fullRequest(userId = "user-update-fail")
        val userRepo = repo()
        val userId = request.userId

        assertNull(userRepo.updateSettingsSection(UserSettingsPage.BASIC_DEMOGRAPHICS, userId, UserProfileTestData.basicDemographics().toEntity(userId)))
        assertNull(userRepo.updateSettingsSection(UserSettingsPage.ACTIVITY_LIFESTYLE, userId, UserProfileTestData.activityLifestyle().toEntity(userId)))
        assertNull(userRepo.updateSettingsSection(UserSettingsPage.GOALS_PRIORITIES, userId, UserProfileTestData.goals().toEntity(userId)))
        assertNull(userRepo.updateSettingsSection(UserSettingsPage.TRAINING_BACKGROUND, userId, UserProfileTestData.trainingBackground().toEntity(userId)))
        assertNull(userRepo.updateSettingsSection(UserSettingsPage.NUTRITION_HISTORY, userId, UserProfileTestData.nutritionHistory().toEntity(userId)))
    }

    @Test
    fun section_deletes_return_false_when_data_source_fails() = runBlocking {
        val userRepo = repo()
        val userId = "user-delete-fail"

        assertFalse(userRepo.deleteSettingsSection(UserSettingsPage.BASIC_DEMOGRAPHICS, userId))
        assertFalse(userRepo.deleteSettingsSection(UserSettingsPage.ACTIVITY_LIFESTYLE, userId))
        assertFalse(userRepo.deleteSettingsSection(UserSettingsPage.GOALS_PRIORITIES, userId))
        assertFalse(userRepo.deleteSettingsSection(UserSettingsPage.TRAINING_BACKGROUND, userId))
        assertFalse(userRepo.deleteSettingsSection(UserSettingsPage.NUTRITION_HISTORY, userId))
    }

    @Test
    fun upsertUserProfile_returns_null_when_upsert_fails() = runBlocking {
        val request = UserProfileTestData.fullRequest()
        val userRepo = repo()

        val result = userRepo.upsertUserProfile(request)

        assertNull(result)
    }
}
