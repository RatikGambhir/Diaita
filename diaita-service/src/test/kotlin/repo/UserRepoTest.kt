package com.diaita.repo

import com.diaita.dto.RegisteredUserProfileDto
import com.diaita.lib.factories.Result
import com.diaita.lib.factories.SupabaseManager
import com.diaita.lib.mappings.toUpsertFullProfilePayload
import com.diaita.lib.mappings.toEntity
import com.diaita.testdata.UserProfileTestData
import io.github.jan.supabase.SupabaseClient
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlin.test.Test
import kotlin.test.assertEquals
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

        assertNull(userRepo.updateBasicDemographics(userId, request.basicDemographics.toEntity(userId)))
        assertNull(userRepo.updateActivityLifestyle(userId, request.activityLifestyle.toEntity(userId)))
        assertNull(userRepo.updateGoalsPriorities(userId, request.goals.toEntity(userId)))
        assertNull(userRepo.updateTrainingBackground(userId, request.trainingBackground!!.toEntity(userId)))
        assertNull(userRepo.updateNutritionHistory(userId, request.nutritionHistory!!.toEntity(userId)))
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
    fun upsertFullProfile_returns_mutation_failed_when_any_required_upsert_fails() = runBlocking {
        val request = UserProfileTestData.fullRequest()
        val manager = mockk<SupabaseManager>()
        val userRepo = repo(manager)

        coEvery {
            manager.rpcDecoded<RegisteredUserProfileDto>(
                "upsert_full_profile",
                buildJsonObject {
                    put("p_user_id", JsonPrimitive(request.userId))
                    put("p_payload", request.toUpsertFullProfilePayload())
                }
            )
        } returns Result<RegisteredUserProfileDto>(null, RuntimeException("boom"))

        val result = userRepo.upsertFullProfile(request)

        assertEquals("Mutation Failed", result)
    }

}
