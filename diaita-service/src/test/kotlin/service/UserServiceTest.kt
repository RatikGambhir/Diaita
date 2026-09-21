package com.diaita.service

import com.diaita.dto.RegisterUserProfileResponseDto
import com.diaita.dto.ServiceResult
import com.diaita.dto.UserSettingsAction
import com.diaita.dto.UserSettingsPage
import com.diaita.lib.mappings.toEntity
import com.diaita.repo.RecommendationRepo
import com.diaita.repo.UserRepo
import com.diaita.testdata.UserProfileTestData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class UserServiceTest {
    private val repo = mockk<UserRepo>()
    private val recommendationRepo = mockk<RecommendationRepo>()
    private val service = UserService(repo, recommendationRepo)

    @Test
    fun registerUserProfile_saves_profile_and_local_recommendation() = runBlocking {
        val profile = UserProfileTestData.fullRequest()
        coEvery { repo.upsertUserProfile(profile) } returns profile
        coEvery { recommendationRepo.saveRecommendation(profile.userId, any()) } returns true

        val result = assertIs<ServiceResult.Success<RegisterUserProfileResponseDto>>(
            service.registerUserProfile(profile)
        )

        assertEquals(profile, result.data.profile)
        assertEquals(profile.primaryGoal, result.data.recommendation.training.focus.primary)
        coVerify(exactly = 1) { recommendationRepo.saveRecommendation(profile.userId, result.data.recommendation) }
    }

    @Test
    fun registerUserProfile_returns_failure_when_profile_cannot_be_saved() = runBlocking {
        val profile = UserProfileTestData.fullRequest()
        coEvery { repo.upsertUserProfile(profile) } returns null

        val result = assertIs<ServiceResult.Failure>(service.registerUserProfile(profile))
        assertTrue(result.error.contains("upsertUserProfile"))
    }

    @Test
    fun generateAndSaveRecommendations_uses_saved_profile() = runBlocking {
        val profile = UserProfileTestData.fullRequest()
        coEvery { repo.getFullProfile(profile.userId) } returns profile
        coEvery { recommendationRepo.saveRecommendation(profile.userId, any()) } returns true

        val result = assertIs<ServiceResult.Success<*>>(service.generateAndSaveRecommendations(profile.userId))
        assertTrue(result.data != null)
    }

    @Test
    fun settings_basic_demographics_crud_maps_dto_and_entity() = runBlocking {
        val userId = UserProfileTestData.fullRequest().userId
        val dto = UserProfileTestData.basicDemographics()
        val entity = dto.toEntity(userId)
        val payload = Json.encodeToJsonElement(dto)
        coEvery { repo.getSettingsSection(UserSettingsPage.BASIC_DEMOGRAPHICS, userId) } returns entity
        coEvery { repo.updateSettingsSection(UserSettingsPage.BASIC_DEMOGRAPHICS, userId, any()) } returns entity
        coEvery { repo.deleteSettingsSection(UserSettingsPage.BASIC_DEMOGRAPHICS, userId) } returns true

        assertEquals(dto, service.handleUserSettings(userId, UserSettingsPage.BASIC_DEMOGRAPHICS, UserSettingsAction.GET))
        assertEquals(dto, service.handleUserSettings(userId, UserSettingsPage.BASIC_DEMOGRAPHICS, UserSettingsAction.UPDATE, payload))
        assertTrue(service.handleUserSettings(userId, UserSettingsPage.BASIC_DEMOGRAPHICS, UserSettingsAction.DELETE) as Boolean)
    }
}
