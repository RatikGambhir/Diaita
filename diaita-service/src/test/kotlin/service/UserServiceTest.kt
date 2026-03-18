package com.diaita.service

import com.diaita.Container
import com.diaita.dto.RecommendationDto
import com.diaita.dto.RegisterUserProfileResponseDto
import com.diaita.dto.ServiceResult
import com.diaita.dto.UserSettingsAction
import com.diaita.dto.UserSettingsPage
import com.diaita.lib.clients.GeminiRestClient
import com.diaita.lib.mappings.toEntity
import com.diaita.repo.RecommendationRepo
import com.diaita.repo.UserRepo
import com.diaita.testdata.RecommendationTestData
import com.diaita.testdata.UserProfileTestData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class UserServiceTest {

    private val repo = mockk<UserRepo>()
    private val gemini = mockk<GeminiRestClient>(relaxed = true)
    private val recommendationRepo = mockk<RecommendationRepo>()
    private val json = Json
    private val container = Container().apply {
        bind<UserRepo>(repo)
        bind<GeminiRestClient>(gemini)
        bind<RecommendationRepo>(recommendationRepo)
    }
    private val service = container.get<UserService>()

    @Test
    fun registerUserProfile_returns_success_when_upsert_and_recommendations_succeed() = runBlocking {
        val payload = UserProfileTestData.fullRequest()
        val profile = RecommendationTestData.registeredProfile(payload.userId)
        val recommendation = RecommendationTestData.recommendation()

        coEvery { repo.upsertFullProfile(payload) } returns profile
        coEvery {
            gemini.askQuestionStructured(match { it.isNotBlank() }, any(), RecommendationDto.serializer(), any(), any())
        } returns recommendation
        coEvery { recommendationRepo.saveRecommendation(payload.userId, recommendation) } returns true

        val result = service.registerUserProfile(payload)

        assertIs<ServiceResult.Success<RegisterUserProfileResponseDto>>(result)
        assertEquals(profile, result.data.profile)
        assertEquals(recommendation, result.data.recommendation)
        coVerify(exactly = 1) { repo.upsertFullProfile(payload) }
        coVerify(exactly = 1) {
            gemini.askQuestionStructured(match { it.isNotBlank() }, any(), RecommendationDto.serializer(), any(), any())
        }
        coVerify(exactly = 1) { recommendationRepo.saveRecommendation(payload.userId, recommendation) }
    }

    @Test
    fun registerUserProfile_returns_failure_when_upsert_fails() = runBlocking {
        val payload = UserProfileTestData.fullRequest()
        val recommendation = RecommendationTestData.recommendation()

        coEvery { repo.upsertFullProfile(payload) } returns null
        coEvery {
            gemini.askQuestionStructured(match { it.isNotBlank() }, any(), RecommendationDto.serializer(), any(), any())
        } returns recommendation

        val result = service.registerUserProfile(payload)

        assertIs<ServiceResult.Failure>(result)
        assertTrue(result.error.contains("upsertFullProfile"))
        coVerify(exactly = 1) { repo.upsertFullProfile(payload) }
    }

    @Test
    fun registerUserProfile_returns_failure_when_recommendation_generation_fails() = runBlocking {
        val payload = UserProfileTestData.fullRequest()
        val profile = RecommendationTestData.registeredProfile(payload.userId)

        coEvery { repo.upsertFullProfile(payload) } returns profile
        coEvery {
            gemini.askQuestionStructured(match { it.isNotBlank() }, any(), RecommendationDto.serializer(), any(), any())
        } returns null

        val result = service.registerUserProfile(payload)

        assertIs<ServiceResult.Failure>(result)
        assertTrue(result.error.contains("genRecommendations"))
        coVerify(exactly = 1) { repo.upsertFullProfile(payload) }
        coVerify(exactly = 1) {
            gemini.askQuestionStructured(match { it.isNotBlank() }, any(), RecommendationDto.serializer(), any(), any())
        }
        confirmVerified(recommendationRepo)
    }

    @Test
    fun registerUserProfile_returns_failure_when_recommendation_save_fails() = runBlocking {
        val payload = UserProfileTestData.fullRequest()
        val profile = RecommendationTestData.registeredProfile(payload.userId)
        val recommendation = RecommendationTestData.recommendation()

        coEvery { repo.upsertFullProfile(payload) } returns profile
        coEvery {
            gemini.askQuestionStructured(match { it.isNotBlank() }, any(), RecommendationDto.serializer(), any(), any())
        } returns recommendation
        coEvery { recommendationRepo.saveRecommendation(payload.userId, recommendation) } returns false

        val result = service.registerUserProfile(payload)

        assertIs<ServiceResult.Failure>(result)
        assertTrue(result.error.contains("saveUserRecommendations"))
        coVerify(exactly = 1) { recommendationRepo.saveRecommendation(payload.userId, recommendation) }
    }

    @Test
    fun generateAndSaveRecommendations_returns_structured_recommendation_when_profile_exists() = runBlocking {
        val profile = RecommendationTestData.registeredProfile()
        val recommendation = RecommendationTestData.recommendation()

        coEvery { repo.getFullProfile(profile.userId) } returns profile
        coEvery {
            gemini.askQuestionStructured(match { it.isNotBlank() }, any(), RecommendationDto.serializer(), any(), any())
        } returns recommendation
        coEvery { recommendationRepo.saveRecommendation(profile.userId, recommendation) } returns true

        val result = service.generateAndSaveRecommendations(profile.userId)

        assertIs<ServiceResult.Success<RecommendationDto>>(result)
        assertEquals(recommendation, result.data)
        coVerify(exactly = 1) { repo.getFullProfile(profile.userId) }
        coVerify(exactly = 1) { recommendationRepo.saveRecommendation(profile.userId, recommendation) }
    }

    @Test
    fun getRecommendations_returns_saved_recommendation() = runBlocking {
        val userId = UserProfileTestData.fullRequest().userId
        val recommendation = RecommendationTestData.recommendation()

        coEvery { recommendationRepo.getRecommendationByUserId(userId) } returns recommendation

        assertEquals(recommendation, service.getRecommendations(userId))
    }

    @Test
    fun settings_basic_demographics_crud_success() = runBlocking {
        val payload = UserProfileTestData.fullRequest()
        val userId = payload.userId
        val row = payload.basicDemographics.toEntity(userId)
        val updatePayload = json.encodeToJsonElement(payload.basicDemographics)

        coEvery { repo.getBasicDemographics(userId) } returns row
        coEvery { repo.updateBasicDemographics(userId, any()) } returns row
        coEvery { repo.deleteBasicDemographics(userId) } returns true

        assertEquals(payload.basicDemographics, service.handleUserSettings(userId, UserSettingsPage.BASIC_DEMOGRAPHICS, UserSettingsAction.GET))
        assertEquals(
            payload.basicDemographics,
            service.handleUserSettings(userId, UserSettingsPage.BASIC_DEMOGRAPHICS, UserSettingsAction.UPDATE, updatePayload)
        )
        assertTrue(service.handleUserSettings(userId, UserSettingsPage.BASIC_DEMOGRAPHICS, UserSettingsAction.DELETE) as Boolean)
    }

    @Test
    fun settings_basic_demographics_crud_error() = runBlocking {
        val payload = UserProfileTestData.fullRequest()
        val userId = payload.userId
        val updatePayload = json.encodeToJsonElement(payload.basicDemographics)

        coEvery { repo.getBasicDemographics(userId) } returns null
        coEvery { repo.updateBasicDemographics(userId, any()) } returns null
        coEvery { repo.deleteBasicDemographics(userId) } returns false

        assertNull(service.handleUserSettings(userId, UserSettingsPage.BASIC_DEMOGRAPHICS, UserSettingsAction.GET))
        assertNull(service.handleUserSettings(userId, UserSettingsPage.BASIC_DEMOGRAPHICS, UserSettingsAction.UPDATE, updatePayload))
        assertFalse(service.handleUserSettings(userId, UserSettingsPage.BASIC_DEMOGRAPHICS, UserSettingsAction.DELETE) as Boolean)
    }

    @Test
    fun settings_activity_lifestyle_crud_success() = runBlocking {
        val payload = UserProfileTestData.fullRequest()
        val userId = payload.userId
        val row = payload.activityLifestyle.toEntity(userId)
        val updatePayload = json.encodeToJsonElement(payload.activityLifestyle)

        coEvery { repo.getActivityLifestyle(userId) } returns row
        coEvery { repo.updateActivityLifestyle(userId, any()) } returns row
        coEvery { repo.deleteActivityLifestyle(userId) } returns true

        assertEquals(payload.activityLifestyle, service.handleUserSettings(userId, UserSettingsPage.ACTIVITY_LIFESTYLE, UserSettingsAction.GET))
        assertEquals(
            payload.activityLifestyle,
            service.handleUserSettings(userId, UserSettingsPage.ACTIVITY_LIFESTYLE, UserSettingsAction.UPDATE, updatePayload)
        )
        assertTrue(service.handleUserSettings(userId, UserSettingsPage.ACTIVITY_LIFESTYLE, UserSettingsAction.DELETE) as Boolean)
    }

    @Test
    fun settings_activity_lifestyle_crud_error() = runBlocking {
        val payload = UserProfileTestData.fullRequest()
        val userId = payload.userId
        val updatePayload = json.encodeToJsonElement(payload.activityLifestyle)

        coEvery { repo.getActivityLifestyle(userId) } returns null
        coEvery { repo.updateActivityLifestyle(userId, any()) } returns null
        coEvery { repo.deleteActivityLifestyle(userId) } returns false

        assertNull(service.handleUserSettings(userId, UserSettingsPage.ACTIVITY_LIFESTYLE, UserSettingsAction.GET))
        assertNull(service.handleUserSettings(userId, UserSettingsPage.ACTIVITY_LIFESTYLE, UserSettingsAction.UPDATE, updatePayload))
        assertFalse(service.handleUserSettings(userId, UserSettingsPage.ACTIVITY_LIFESTYLE, UserSettingsAction.DELETE) as Boolean)
    }

    @Test
    fun settings_goals_priorities_crud_success() = runBlocking {
        val payload = UserProfileTestData.fullRequest()
        val userId = payload.userId
        val row = payload.goals.toEntity(userId)
        val updatePayload = json.encodeToJsonElement(payload.goals)

        coEvery { repo.getGoalsPriorities(userId) } returns row
        coEvery { repo.updateGoalsPriorities(userId, any()) } returns row
        coEvery { repo.deleteGoalsPriorities(userId) } returns true

        assertEquals(payload.goals, service.handleUserSettings(userId, UserSettingsPage.GOALS_PRIORITIES, UserSettingsAction.GET))
        assertEquals(
            payload.goals,
            service.handleUserSettings(userId, UserSettingsPage.GOALS_PRIORITIES, UserSettingsAction.UPDATE, updatePayload)
        )
        assertTrue(service.handleUserSettings(userId, UserSettingsPage.GOALS_PRIORITIES, UserSettingsAction.DELETE) as Boolean)
    }

    @Test
    fun settings_goals_priorities_crud_error() = runBlocking {
        val payload = UserProfileTestData.fullRequest()
        val userId = payload.userId
        val updatePayload = json.encodeToJsonElement(payload.goals)

        coEvery { repo.getGoalsPriorities(userId) } returns null
        coEvery { repo.updateGoalsPriorities(userId, any()) } returns null
        coEvery { repo.deleteGoalsPriorities(userId) } returns false

        assertNull(service.handleUserSettings(userId, UserSettingsPage.GOALS_PRIORITIES, UserSettingsAction.GET))
        assertNull(service.handleUserSettings(userId, UserSettingsPage.GOALS_PRIORITIES, UserSettingsAction.UPDATE, updatePayload))
        assertFalse(service.handleUserSettings(userId, UserSettingsPage.GOALS_PRIORITIES, UserSettingsAction.DELETE) as Boolean)
    }

    @Test
    fun settings_training_background_crud_success() = runBlocking {
        val payload = UserProfileTestData.fullRequest()
        val userId = payload.userId
        val row = payload.trainingBackground!!.toEntity(userId)
        val updatePayload = json.encodeToJsonElement(payload.trainingBackground)

        coEvery { repo.getTrainingBackground(userId) } returns row
        coEvery { repo.updateTrainingBackground(userId, any()) } returns row
        coEvery { repo.deleteTrainingBackground(userId) } returns true

        assertEquals(payload.trainingBackground, service.handleUserSettings(userId, UserSettingsPage.TRAINING_BACKGROUND, UserSettingsAction.GET))
        assertEquals(
            payload.trainingBackground,
            service.handleUserSettings(userId, UserSettingsPage.TRAINING_BACKGROUND, UserSettingsAction.UPDATE, updatePayload)
        )
        assertTrue(service.handleUserSettings(userId, UserSettingsPage.TRAINING_BACKGROUND, UserSettingsAction.DELETE) as Boolean)
    }

    @Test
    fun settings_training_background_crud_error() = runBlocking {
        val payload = UserProfileTestData.fullRequest()
        val userId = payload.userId
        val updatePayload = json.encodeToJsonElement(payload.trainingBackground)

        coEvery { repo.getTrainingBackground(userId) } returns null
        coEvery { repo.updateTrainingBackground(userId, any()) } returns null
        coEvery { repo.deleteTrainingBackground(userId) } returns false

        assertNull(service.handleUserSettings(userId, UserSettingsPage.TRAINING_BACKGROUND, UserSettingsAction.GET))
        assertNull(service.handleUserSettings(userId, UserSettingsPage.TRAINING_BACKGROUND, UserSettingsAction.UPDATE, updatePayload))
        assertFalse(service.handleUserSettings(userId, UserSettingsPage.TRAINING_BACKGROUND, UserSettingsAction.DELETE) as Boolean)
    }

    @Test
    fun settings_nutrition_history_crud_success() = runBlocking {
        val payload = UserProfileTestData.fullRequest()
        val userId = payload.userId
        val row = payload.nutritionHistory!!.toEntity(userId)
        val updatePayload = json.encodeToJsonElement(payload.nutritionHistory)

        coEvery { repo.getNutritionHistory(userId) } returns row
        coEvery { repo.updateNutritionHistory(userId, any()) } returns row
        coEvery { repo.deleteNutritionHistory(userId) } returns true

        assertEquals(payload.nutritionHistory, service.handleUserSettings(userId, UserSettingsPage.NUTRITION_HISTORY, UserSettingsAction.GET))
        assertEquals(
            payload.nutritionHistory,
            service.handleUserSettings(userId, UserSettingsPage.NUTRITION_HISTORY, UserSettingsAction.UPDATE, updatePayload)
        )
        assertTrue(service.handleUserSettings(userId, UserSettingsPage.NUTRITION_HISTORY, UserSettingsAction.DELETE) as Boolean)
    }

    @Test
    fun settings_nutrition_history_crud_error() = runBlocking {
        val payload = UserProfileTestData.fullRequest()
        val userId = payload.userId
        val updatePayload = json.encodeToJsonElement(payload.nutritionHistory)

        coEvery { repo.getNutritionHistory(userId) } returns null
        coEvery { repo.updateNutritionHistory(userId, any()) } returns null
        coEvery { repo.deleteNutritionHistory(userId) } returns false

        assertNull(service.handleUserSettings(userId, UserSettingsPage.NUTRITION_HISTORY, UserSettingsAction.GET))
        assertNull(service.handleUserSettings(userId, UserSettingsPage.NUTRITION_HISTORY, UserSettingsAction.UPDATE, updatePayload))
        assertFalse(service.handleUserSettings(userId, UserSettingsPage.NUTRITION_HISTORY, UserSettingsAction.DELETE) as Boolean)
    }
}
