package com.diaita.service

import com.diaita.Container
import com.diaita.lib.clients.GeminiRestClient
import com.diaita.lib.mappings.toEntity
import com.diaita.repo.UserRepo
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
    private val container = Container().apply {
        bind<UserRepo>(repo)
        bind<GeminiRestClient>(gemini)
    }
    private val service = container.get<UserService>()

    @Test
    fun registerUserProfile_returns_success_when_upsert_and_recommendations_succeed() = runBlocking {
        val payload = UserProfileTestData.fullRequest()

        coEvery { repo.upsertFullProfile(payload) } returns "Mutation Success"
        coEvery {
            gemini.askQuestionStream(match { it.isNotBlank() }, any(), any())
        } returns "recommendation"

        val result = service.registerUserProfile(payload)

        assertEquals("Mutation Success", result)
        coVerify(exactly = 1) { repo.upsertFullProfile(payload) }
        coVerify(exactly = 1) {
            gemini.askQuestionStream(match { it.isNotBlank() }, any(), any())
        }
    }

    @Test
    fun registerUserProfile_returns_null_and_skips_recommendations_when_upsert_fails() = runBlocking {
        val payload = UserProfileTestData.fullRequest()

        coEvery { repo.upsertFullProfile(payload) } returns "Mutation Failed"

        val result = service.registerUserProfile(payload)

        assertNull(result)
        coVerify(exactly = 1) { repo.upsertFullProfile(payload) }
        confirmVerified(gemini)
    }

    @Test
    fun registerUserProfile_returns_null_when_recommendation_generation_fails() = runBlocking {
        val payload = UserProfileTestData.fullRequest()

        coEvery { repo.upsertFullProfile(payload) } returns "Mutation Success"
        coEvery { gemini.askQuestionStream(match { it.isNotBlank() }, any(), any()) } returns null

        val result = service.registerUserProfile(payload)

        assertNull(result)
        coVerify(exactly = 1) { repo.upsertFullProfile(payload) }
        coVerify(exactly = 1) { gemini.askQuestionStream(match { it.isNotBlank() }, any(), any()) }
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
    fun settings_medical_history_crud_success() = runBlocking {
        val payload = UserProfileTestData.fullRequest()
        val userId = payload.userId
        val row = payload.medicalHistory!!.toEntity(userId)

        coEvery { repo.getMedicalHistory(userId) } returns row
        coEvery { repo.updateMedicalHistory(userId, any()) } returns row
        coEvery { repo.deleteMedicalHistory(userId) } returns true

        assertEquals(payload.medicalHistory, service.getMedicalHistory(userId))
        assertEquals(payload.medicalHistory, service.updateMedicalHistory(userId, payload.medicalHistory))
        assertTrue(service.deleteMedicalHistory(userId))
    }

    @Test
    fun settings_medical_history_crud_error() = runBlocking {
        val payload = UserProfileTestData.fullRequest()
        val userId = payload.userId

        coEvery { repo.getMedicalHistory(userId) } returns null
        coEvery { repo.updateMedicalHistory(userId, any()) } returns null
        coEvery { repo.deleteMedicalHistory(userId) } returns false

        assertNull(service.getMedicalHistory(userId))
        assertNull(service.updateMedicalHistory(userId, payload.medicalHistory!!))
        assertFalse(service.deleteMedicalHistory(userId))
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

    @Test
    fun settings_behavioral_factors_crud_success() = runBlocking {
        val payload = UserProfileTestData.fullRequest()
        val userId = payload.userId
        val row = payload.behavioralFactors!!.toEntity(userId)

        coEvery { repo.getBehavioralFactors(userId) } returns row
        coEvery { repo.updateBehavioralFactors(userId, any()) } returns row
        coEvery { repo.deleteBehavioralFactors(userId) } returns true

        assertEquals(payload.behavioralFactors, service.getBehavioralFactors(userId))
        assertEquals(payload.behavioralFactors, service.updateBehavioralFactors(userId, payload.behavioralFactors))
        assertTrue(service.deleteBehavioralFactors(userId))
    }

    @Test
    fun settings_behavioral_factors_crud_error() = runBlocking {
        val payload = UserProfileTestData.fullRequest()
        val userId = payload.userId

        coEvery { repo.getBehavioralFactors(userId) } returns null
        coEvery { repo.updateBehavioralFactors(userId, any()) } returns null
        coEvery { repo.deleteBehavioralFactors(userId) } returns false

        assertNull(service.getBehavioralFactors(userId))
        assertNull(service.updateBehavioralFactors(userId, payload.behavioralFactors!!))
        assertFalse(service.deleteBehavioralFactors(userId))
    }

    @Test
    fun settings_metrics_tracking_crud_success() = runBlocking {
        val payload = UserProfileTestData.fullRequest()
        val userId = payload.userId
        val row = payload.metricsTracking!!.toEntity(userId)

        coEvery { repo.getMetricsTracking(userId) } returns row
        coEvery { repo.updateMetricsTracking(userId, any()) } returns row
        coEvery { repo.deleteMetricsTracking(userId) } returns true

        assertEquals(payload.metricsTracking, service.getMetricsTracking(userId))
        assertEquals(payload.metricsTracking, service.updateMetricsTracking(userId, payload.metricsTracking))
        assertTrue(service.deleteMetricsTracking(userId))
    }

    @Test
    fun settings_metrics_tracking_crud_error() = runBlocking {
        val payload = UserProfileTestData.fullRequest()
        val userId = payload.userId

        coEvery { repo.getMetricsTracking(userId) } returns null
        coEvery { repo.updateMetricsTracking(userId, any()) } returns null
        coEvery { repo.deleteMetricsTracking(userId) } returns false

        assertNull(service.getMetricsTracking(userId))
        assertNull(service.updateMetricsTracking(userId, payload.metricsTracking!!))
        assertFalse(service.deleteMetricsTracking(userId))
    }
}
