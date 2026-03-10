package com.diaita.service

import com.diaita.Container
import com.diaita.dto.RecommendationDto
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
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UserServiceTest {

    private val repo = mockk<UserRepo>()
    private val gemini = mockk<GeminiRestClient>(relaxed = true)
    private val recommendationRepo = mockk<RecommendationRepo>()
    private val container = Container().apply {
        bind<UserRepo>(repo)
        bind<GeminiRestClient>(gemini)
        bind<RecommendationRepo>(recommendationRepo)
    }
    private val service = container.get<UserService>()

    @Test
    fun registerUserProfile_returns_success_when_upsert_and_recommendations_succeed() = runBlocking {
        val payload = UserProfileTestData.fullRequest()
        val recommendation = RecommendationTestData.recommendation()

        coEvery { repo.upsertFullProfile(payload) } returns "Mutation Success"
        coEvery {
            gemini.askQuestionStructured(match { it.isNotBlank() }, any(), RecommendationDto.serializer(), any(), any())
        } returns recommendation
        coEvery { recommendationRepo.saveRecommendation(payload.userId, recommendation) } returns true

        val result = service.registerUserProfile(payload)

        assertEquals("Mutation Success", result)
        coVerify(exactly = 1) { repo.upsertFullProfile(payload) }
        coVerify(exactly = 1) {
            gemini.askQuestionStructured(match { it.isNotBlank() }, any(), RecommendationDto.serializer(), any(), any())
        }
        coVerify(exactly = 1) { recommendationRepo.saveRecommendation(payload.userId, recommendation) }
    }

    @Test
    fun registerUserProfile_returns_null_and_skips_recommendations_when_upsert_fails() = runBlocking {
        val payload = UserProfileTestData.fullRequest()

        coEvery { repo.upsertFullProfile(payload) } returns "Mutation Failed"

        val result = service.registerUserProfile(payload)

        assertNull(result)
        coVerify(exactly = 1) { repo.upsertFullProfile(payload) }
        confirmVerified(gemini, recommendationRepo)
    }

    @Test
    fun registerUserProfile_returns_null_when_recommendation_generation_fails() = runBlocking {
        val payload = UserProfileTestData.fullRequest()

        coEvery { repo.upsertFullProfile(payload) } returns "Mutation Success"
        coEvery {
            gemini.askQuestionStructured(match { it.isNotBlank() }, any(), RecommendationDto.serializer(), any(), any())
        } returns null

        val result = service.registerUserProfile(payload)

        assertNull(result)
        coVerify(exactly = 1) { repo.upsertFullProfile(payload) }
        coVerify(exactly = 1) {
            gemini.askQuestionStructured(match { it.isNotBlank() }, any(), RecommendationDto.serializer(), any(), any())
        }
        confirmVerified(recommendationRepo)
    }

    @Test
    fun registerUserProfile_returns_null_when_recommendation_save_fails() = runBlocking {
        val payload = UserProfileTestData.fullRequest()
        val recommendation = RecommendationTestData.recommendation()

        coEvery { repo.upsertFullProfile(payload) } returns "Mutation Success"
        coEvery {
            gemini.askQuestionStructured(match { it.isNotBlank() }, any(), RecommendationDto.serializer(), any(), any())
        } returns recommendation
        coEvery { recommendationRepo.saveRecommendation(payload.userId, recommendation) } returns false

        val result = service.registerUserProfile(payload)

        assertNull(result)
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

        assertEquals(recommendation, result)
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

        coEvery { repo.getBasicDemographics(userId) } returns row
        coEvery { repo.updateBasicDemographics(userId, any()) } returns row
        coEvery { repo.deleteBasicDemographics(userId) } returns true

        assertEquals(payload.basicDemographics, service.getBasicDemographics(userId))
        assertEquals(payload.basicDemographics, service.updateBasicDemographics(userId, payload.basicDemographics))
        assertTrue(service.deleteBasicDemographics(userId))
    }

    @Test
    fun settings_basic_demographics_crud_error() = runBlocking {
        val payload = UserProfileTestData.fullRequest()
        val userId = payload.userId

        coEvery { repo.getBasicDemographics(userId) } returns null
        coEvery { repo.updateBasicDemographics(userId, any()) } returns null
        coEvery { repo.deleteBasicDemographics(userId) } returns false

        assertNull(service.getBasicDemographics(userId))
        assertNull(service.updateBasicDemographics(userId, payload.basicDemographics))
        assertFalse(service.deleteBasicDemographics(userId))
    }

    @Test
    fun settings_activity_lifestyle_crud_success() = runBlocking {
        val payload = UserProfileTestData.fullRequest()
        val userId = payload.userId
        val row = payload.activityLifestyle.toEntity(userId)

        coEvery { repo.getActivityLifestyle(userId) } returns row
        coEvery { repo.updateActivityLifestyle(userId, any()) } returns row
        coEvery { repo.deleteActivityLifestyle(userId) } returns true

        assertEquals(payload.activityLifestyle, service.getActivityLifestyle(userId))
        assertEquals(payload.activityLifestyle, service.updateActivityLifestyle(userId, payload.activityLifestyle))
        assertTrue(service.deleteActivityLifestyle(userId))
    }

    @Test
    fun settings_activity_lifestyle_crud_error() = runBlocking {
        val payload = UserProfileTestData.fullRequest()
        val userId = payload.userId

        coEvery { repo.getActivityLifestyle(userId) } returns null
        coEvery { repo.updateActivityLifestyle(userId, any()) } returns null
        coEvery { repo.deleteActivityLifestyle(userId) } returns false

        assertNull(service.getActivityLifestyle(userId))
        assertNull(service.updateActivityLifestyle(userId, payload.activityLifestyle))
        assertFalse(service.deleteActivityLifestyle(userId))
    }

    @Test
    fun settings_goals_priorities_crud_success() = runBlocking {
        val payload = UserProfileTestData.fullRequest()
        val userId = payload.userId
        val row = payload.goals.toEntity(userId)

        coEvery { repo.getGoalsPriorities(userId) } returns row
        coEvery { repo.updateGoalsPriorities(userId, any()) } returns row
        coEvery { repo.deleteGoalsPriorities(userId) } returns true

        assertEquals(payload.goals, service.getGoalsPriorities(userId))
        assertEquals(payload.goals, service.updateGoalsPriorities(userId, payload.goals))
        assertTrue(service.deleteGoalsPriorities(userId))
    }

    @Test
    fun settings_goals_priorities_crud_error() = runBlocking {
        val payload = UserProfileTestData.fullRequest()
        val userId = payload.userId

        coEvery { repo.getGoalsPriorities(userId) } returns null
        coEvery { repo.updateGoalsPriorities(userId, any()) } returns null
        coEvery { repo.deleteGoalsPriorities(userId) } returns false

        assertNull(service.getGoalsPriorities(userId))
        assertNull(service.updateGoalsPriorities(userId, payload.goals))
        assertFalse(service.deleteGoalsPriorities(userId))
    }

    @Test
    fun settings_training_background_crud_success() = runBlocking {
        val payload = UserProfileTestData.fullRequest()
        val userId = payload.userId
        val row = payload.trainingBackground!!.toEntity(userId)

        coEvery { repo.getTrainingBackground(userId) } returns row
        coEvery { repo.updateTrainingBackground(userId, any()) } returns row
        coEvery { repo.deleteTrainingBackground(userId) } returns true

        assertEquals(payload.trainingBackground, service.getTrainingBackground(userId))
        assertEquals(payload.trainingBackground, service.updateTrainingBackground(userId, payload.trainingBackground))
        assertTrue(service.deleteTrainingBackground(userId))
    }

    @Test
    fun settings_training_background_crud_error() = runBlocking {
        val payload = UserProfileTestData.fullRequest()
        val userId = payload.userId

        coEvery { repo.getTrainingBackground(userId) } returns null
        coEvery { repo.updateTrainingBackground(userId, any()) } returns null
        coEvery { repo.deleteTrainingBackground(userId) } returns false

        assertNull(service.getTrainingBackground(userId))
        assertNull(service.updateTrainingBackground(userId, payload.trainingBackground!!))
        assertFalse(service.deleteTrainingBackground(userId))
    }

    @Test
    fun settings_nutrition_history_crud_success() = runBlocking {
        val payload = UserProfileTestData.fullRequest()
        val userId = payload.userId
        val row = payload.nutritionHistory!!.toEntity(userId)

        coEvery { repo.getNutritionHistory(userId) } returns row
        coEvery { repo.updateNutritionHistory(userId, any()) } returns row
        coEvery { repo.deleteNutritionHistory(userId) } returns true

        assertEquals(payload.nutritionHistory, service.getNutritionHistory(userId))
        assertEquals(payload.nutritionHistory, service.updateNutritionHistory(userId, payload.nutritionHistory))
        assertTrue(service.deleteNutritionHistory(userId))
    }

    @Test
    fun settings_nutrition_history_crud_error() = runBlocking {
        val payload = UserProfileTestData.fullRequest()
        val userId = payload.userId

        coEvery { repo.getNutritionHistory(userId) } returns null
        coEvery { repo.updateNutritionHistory(userId, any()) } returns null
        coEvery { repo.deleteNutritionHistory(userId) } returns false

        assertNull(service.getNutritionHistory(userId))
        assertNull(service.updateNutritionHistory(userId, payload.nutritionHistory!!))
        assertFalse(service.deleteNutritionHistory(userId))
    }
}
