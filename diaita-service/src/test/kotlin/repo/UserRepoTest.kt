package com.diaita.repo

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

        assertNull(userRepo.getBasicDemographics(userId))
        assertNull(userRepo.getActivityLifestyle(userId))
        assertNull(userRepo.getGoalsPriorities(userId))
        assertNull(userRepo.getTrainingBackground(userId))
        assertNull(userRepo.getNutritionHistory(userId))
    }

    @Test
    fun section_updaters_return_null_when_data_source_fails() = runBlocking {
        val request = UserProfileTestData.fullRequest(userId = "user-update-fail")
        val userRepo = repo()
        val userId = request.userId

        assertNull(userRepo.updateBasicDemographics(userId, UserProfileTestData.basicDemographics().toEntity(userId)))
        assertNull(userRepo.updateActivityLifestyle(userId, UserProfileTestData.activityLifestyle().toEntity(userId)))
        assertNull(userRepo.updateGoalsPriorities(userId, UserProfileTestData.goals().toEntity(userId)))
        assertNull(userRepo.updateTrainingBackground(userId, UserProfileTestData.trainingBackground().toEntity(userId)))
        assertNull(userRepo.updateNutritionHistory(userId, UserProfileTestData.nutritionHistory().toEntity(userId)))
    }

    @Test
    fun section_deletes_return_false_when_data_source_fails() = runBlocking {
        val userRepo = repo()
        val userId = "user-delete-fail"

        assertFalse(userRepo.deleteBasicDemographics(userId))
        assertFalse(userRepo.deleteActivityLifestyle(userId))
        assertFalse(userRepo.deleteGoalsPriorities(userId))
        assertFalse(userRepo.deleteTrainingBackground(userId))
        assertFalse(userRepo.deleteNutritionHistory(userId))
    }

    @Test
    fun upsertUserProfile_returns_null_when_upsert_fails() = runBlocking {
        val request = UserProfileTestData.fullRequest()
        val userRepo = repo()

        val result = userRepo.upsertUserProfile(request)

        assertNull(result)
    }
}
