package com.diaita.routers

import com.diaita.controllers.UserController
import com.diaita.dto.*
import com.diaita.entity.*
import com.diaita.lib.clients.GeminiRestClient
import com.diaita.lib.mappings.toEntity
import com.diaita.repo.UserRepo
import com.diaita.service.UserService
import com.diaita.testdata.UserProfileTestData
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.testing.testApplication
import io.ktor.serialization.kotlinx.json.json
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UserRouterTest {

    private val json = Json

    @AfterTest
    fun tearDown() {
        clearAllMocks()
    }

    private fun Application.testModule(userController: UserController) {
        install(ContentNegotiation) {
            json()
        }
        configureUserRoutes(userController)
    }

    @Test
    fun register_returns_200_and_calls_controller_with_payload_on_success() = testApplication {
        val payload = UserProfileTestData.fullRequest()
        val repo = mockk<UserRepo>()
        val gemini = mockk<GeminiRestClient>()
        coEvery { repo.upsertFullProfile(payload) } returns "Mutation Success"
        coEvery { gemini.askQuestionStream(match { it.isNotBlank() }, any(), any()) } returns "recommendations"

        val service = UserService(repo, gemini)
        val controller = UserController(service)

        application {
            testModule(controller)
        }

        val response = client.post("/register") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(json.encodeToString(payload))
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("\"status\":\"ok\""))
        coVerify(exactly = 1) { repo.upsertFullProfile(payload) }
        coVerify(exactly = 1) { gemini.askQuestionStream(match { it.isNotBlank() }, any(), any()) }
    }

    @Test
    fun register_returns_400_and_does_not_call_controller_for_blank_user_id() = testApplication {
        val invalidPayload = UserProfileTestData.fullRequest(userId = "   ")
        val repo = mockk<UserRepo>()
        val gemini = mockk<GeminiRestClient>()

        val service = UserService(repo, gemini)
        val controller = UserController(service)

        application {
            testModule(controller)
        }

        val response = client.post("/register") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(json.encodeToString(invalidPayload))
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("Invalid request"))
        confirmVerified(repo, gemini)
    }

    @Test
    fun user_profile_returns_400_when_controller_reports_failure() = testApplication {
        val payload = UserProfileTestData.fullRequest()
        val repo = mockk<UserRepo>()
        val gemini = mockk<GeminiRestClient>()
        coEvery { repo.upsertFullProfile(payload) } returns "Mutation Failed"

        val service = UserService(repo, gemini)
        val controller = UserController(service)

        application {
            testModule(controller)
        }

        val response = client.post("/user/profile") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(json.encodeToString(payload))
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("Server Error"))
        coVerify(exactly = 1) { repo.upsertFullProfile(payload) }
        confirmVerified(gemini)
    }

    @Test
    fun user_profile_returns_200_on_success() = testApplication {
        val payload = UserProfileTestData.fullRequest()
        val repo = mockk<UserRepo>()
        val gemini = mockk<GeminiRestClient>()
        coEvery { repo.upsertFullProfile(payload) } returns "Mutation Success"
        coEvery { gemini.askQuestionStream(match { it.isNotBlank() }, any(), any()) } returns "recommendations"

        val service = UserService(repo, gemini)
        val controller = UserController(service)

        application {
            testModule(controller)
        }

        val response = client.post("/user/profile") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(json.encodeToString(payload))
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("\"status\":\"ok\""))
        coVerify(exactly = 1) { repo.upsertFullProfile(payload) }
        coVerify(exactly = 1) { gemini.askQuestionStream(match { it.isNotBlank() }, any(), any()) }
    }

    @Test
    fun register_rejects_malformed_request_payload() = testApplication {
        val repo = mockk<UserRepo>()
        val gemini = mockk<GeminiRestClient>()

        val service = UserService(repo, gemini)
        val controller = UserController(service)

        application {
            testModule(controller)
        }

        val response = client.post("/register") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody("""{"id":"missing-required-fields"}""")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        confirmVerified(repo, gemini)
    }

    @Test
    fun integration_register_calls_repo_and_recommendations_when_upsert_succeeds() = testApplication {
        val repo = mockk<UserRepo>()
        val gemini = mockk<GeminiRestClient>()
        val service = UserService(repo, gemini)
        val controller = UserController(service)
        val payload = UserProfileTestData.fullRequest()

        coEvery { repo.upsertFullProfile(payload) } returns "Mutation Success"
        coEvery { gemini.askQuestionStream(match { it.isNotBlank() }, any(), any()) } returns "recommendations"

        application {
            testModule(controller)
        }

        val response = client.post("/register") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(json.encodeToString(payload))
        }

        assertEquals(HttpStatusCode.OK, response.status)
        coVerify(exactly = 1) { repo.upsertFullProfile(payload) }
        coVerify(exactly = 1) { gemini.askQuestionStream(match { it.isNotBlank() }, any(), any()) }
    }

    @Test
    fun integration_register_skips_recommendations_when_upsert_fails() = testApplication {
        val repo = mockk<UserRepo>()
        val gemini = mockk<GeminiRestClient>()
        val service = UserService(repo, gemini)
        val controller = UserController(service)
        val payload = UserProfileTestData.fullRequest()

        coEvery { repo.upsertFullProfile(payload) } returns "Mutation Failed"
        coEvery { gemini.askQuestionStream(match { it.isNotBlank() }, any(), any()) } returns "recommendations"

        application {
            testModule(controller)
        }

        val response = client.post("/register") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(json.encodeToString(payload))
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        coVerify(exactly = 1) { repo.upsertFullProfile(payload) }
        confirmVerified(gemini)
    }

    @Test
    fun settings_basic_demographics_get_put_delete_follow_expected_behavior() = testApplication {
        val userId = "user-123"
        val repo = mockk<UserRepo>()
        val gemini = mockk<GeminiRestClient>()
        val service = UserService(repo, gemini)
        val controller = UserController(service)
        val dto = UserProfileTestData.fullRequest().basicDemographics
        val row = BasicDemographicsRowEntity(
            userId = userId,
            age = dto.age,
            sex = dto.sex,
            gender = dto.gender,
            height = dto.height,
            weight = dto.weight,
            bodyFatPercentage = dto.bodyFatPercentage,
            leanMass = dto.leanMass,
            biologicalConsiderations = dto.biologicalConsiderations,
            menstrualCycleInfo = dto.menstrualCycleInfo
        )

        coEvery { repo.getBasicDemographics(userId) } returns row
        coEvery { repo.updateBasicDemographics(userId, any()) } returns row
        coEvery { repo.deleteBasicDemographics(userId) } returns true

        application {
            testModule(controller)
        }

        val getResponse = client.get("/user/settings/basic-demographics/$userId")
        assertEquals(HttpStatusCode.OK, getResponse.status)
        val getBody = Json.decodeFromString<BasicDemographicsDto>(getResponse.bodyAsText())
        assertEquals(dto, getBody)

        val putResponse = client.put("/user/settings/basic-demographics/$userId") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(json.encodeToString(dto))
        }
        assertEquals(HttpStatusCode.OK, putResponse.status)
        val putBody = Json.decodeFromString<BasicDemographicsDto>(putResponse.bodyAsText())
        assertEquals(dto, putBody)

        val deleteResponse = client.delete("/user/settings/basic-demographics/$userId")
        assertEquals(HttpStatusCode.OK, deleteResponse.status)
        assertTrue(deleteResponse.bodyAsText().contains("\"status\":\"deleted\""))

        coVerify(exactly = 1) { repo.getBasicDemographics(userId) }
        coVerify(exactly = 1) {
            repo.updateBasicDemographics(
                userId,
                match { it.userId == userId && it.age == dto.age && it.height == dto.height }
            )
        }
        coVerify(exactly = 1) { repo.deleteBasicDemographics(userId) }
        confirmVerified(gemini)
    }

    @Test
    fun settings_basic_demographics_maps_failures_to_expected_status_codes() = testApplication {
        val userId = "user-404"
        val repo = mockk<UserRepo>()
        val gemini = mockk<GeminiRestClient>()
        val service = UserService(repo, gemini)
        val controller = UserController(service)
        val dto = UserProfileTestData.fullRequest().basicDemographics

        coEvery { repo.getBasicDemographics(userId) } returns null
        coEvery { repo.updateBasicDemographics(userId, any()) } returns null
        coEvery { repo.deleteBasicDemographics(userId) } returns false

        application {
            testModule(controller)
        }

        val getResponse = client.get("/user/settings/basic-demographics/$userId")
        assertEquals(HttpStatusCode.NotFound, getResponse.status)

        val putResponse = client.put("/user/settings/basic-demographics/$userId") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(json.encodeToString(dto))
        }
        assertEquals(HttpStatusCode.InternalServerError, putResponse.status)

        val deleteResponse = client.delete("/user/settings/basic-demographics/$userId")
        assertEquals(HttpStatusCode.InternalServerError, deleteResponse.status)

        coVerify(exactly = 1) { repo.getBasicDemographics(userId) }
        coVerify(exactly = 1) { repo.updateBasicDemographics(userId, any()) }
        coVerify(exactly = 1) { repo.deleteBasicDemographics(userId) }
        confirmVerified(gemini)
    }

    @Test
    fun settings_activity_lifestyle_crud_success() = assertSectionCrudSuccess(
        section = "activity-lifestyle",
        body = json.encodeToString(UserProfileTestData.fullRequest().activityLifestyle),
        setup = { repo, userId ->
            val entity = UserProfileTestData.fullRequest().activityLifestyle.toEntity(userId)
            coEvery { repo.getActivityLifestyle(userId) } returns entity
            coEvery { repo.updateActivityLifestyle(userId, any()) } returns entity
            coEvery { repo.deleteActivityLifestyle(userId) } returns true
        }
    )

    @Test
    fun settings_goals_priorities_crud_success() = assertSectionCrudSuccess(
        section = "goals-priorities",
        body = json.encodeToString(UserProfileTestData.fullRequest().goals),
        setup = { repo, userId ->
            val entity = UserProfileTestData.fullRequest().goals.toEntity(userId)
            coEvery { repo.getGoalsPriorities(userId) } returns entity
            coEvery { repo.updateGoalsPriorities(userId, any()) } returns entity
            coEvery { repo.deleteGoalsPriorities(userId) } returns true
        }
    )

    @Test
    fun settings_training_background_crud_success() = assertSectionCrudSuccess(
        section = "training-background",
        body = json.encodeToString(UserProfileTestData.fullRequest().trainingBackground!!),
        setup = { repo, userId ->
            val entity = UserProfileTestData.fullRequest().trainingBackground!!.toEntity(userId)
            coEvery { repo.getTrainingBackground(userId) } returns entity
            coEvery { repo.updateTrainingBackground(userId, any()) } returns entity
            coEvery { repo.deleteTrainingBackground(userId) } returns true
        }
    )

    @Test
    fun settings_medical_history_crud_success() = assertSectionCrudSuccess(
        section = "medical-history",
        body = json.encodeToString(UserProfileTestData.fullRequest().medicalHistory!!),
        setup = { repo, userId ->
            val entity = UserProfileTestData.fullRequest().medicalHistory!!.toEntity(userId)
            coEvery { repo.getMedicalHistory(userId) } returns entity
            coEvery { repo.updateMedicalHistory(userId, any()) } returns entity
            coEvery { repo.deleteMedicalHistory(userId) } returns true
        }
    )

    @Test
    fun settings_nutrition_history_crud_success() = assertSectionCrudSuccess(
        section = "nutrition-history",
        body = json.encodeToString(UserProfileTestData.fullRequest().nutritionHistory!!),
        setup = { repo, userId ->
            val entity = UserProfileTestData.fullRequest().nutritionHistory!!.toEntity(userId)
            coEvery { repo.getNutritionHistory(userId) } returns entity
            coEvery { repo.updateNutritionHistory(userId, any()) } returns entity
            coEvery { repo.deleteNutritionHistory(userId) } returns true
        }
    )

    @Test
    fun settings_behavioral_factors_crud_success() = assertSectionCrudSuccess(
        section = "behavioral-factors",
        body = json.encodeToString(UserProfileTestData.fullRequest().behavioralFactors!!),
        setup = { repo, userId ->
            val entity = UserProfileTestData.fullRequest().behavioralFactors!!.toEntity(userId)
            coEvery { repo.getBehavioralFactors(userId) } returns entity
            coEvery { repo.updateBehavioralFactors(userId, any()) } returns entity
            coEvery { repo.deleteBehavioralFactors(userId) } returns true
        }
    )

    @Test
    fun settings_metrics_tracking_crud_success() = assertSectionCrudSuccess(
        section = "metrics-tracking",
        body = json.encodeToString(UserProfileTestData.fullRequest().metricsTracking!!),
        setup = { repo, userId ->
            val entity = UserProfileTestData.fullRequest().metricsTracking!!.toEntity(userId)
            coEvery { repo.getMetricsTracking(userId) } returns entity
            coEvery { repo.updateMetricsTracking(userId, any()) } returns entity
            coEvery { repo.deleteMetricsTracking(userId) } returns true
        }
    )

    @Test
    fun settings_activity_lifestyle_crud_errors() = assertSectionCrudErrors(
        section = "activity-lifestyle",
        body = json.encodeToString(UserProfileTestData.fullRequest().activityLifestyle),
        setup = { repo, userId ->
            coEvery { repo.getActivityLifestyle(userId) } returns null
            coEvery { repo.updateActivityLifestyle(userId, any()) } returns null
            coEvery { repo.deleteActivityLifestyle(userId) } returns false
        }
    )

    @Test
    fun settings_goals_priorities_crud_errors() = assertSectionCrudErrors(
        section = "goals-priorities",
        body = json.encodeToString(UserProfileTestData.fullRequest().goals),
        setup = { repo, userId ->
            coEvery { repo.getGoalsPriorities(userId) } returns null
            coEvery { repo.updateGoalsPriorities(userId, any()) } returns null
            coEvery { repo.deleteGoalsPriorities(userId) } returns false
        }
    )

    @Test
    fun settings_training_background_crud_errors() = assertSectionCrudErrors(
        section = "training-background",
        body = json.encodeToString(UserProfileTestData.fullRequest().trainingBackground!!),
        setup = { repo, userId ->
            coEvery { repo.getTrainingBackground(userId) } returns null
            coEvery { repo.updateTrainingBackground(userId, any()) } returns null
            coEvery { repo.deleteTrainingBackground(userId) } returns false
        }
    )

    @Test
    fun settings_medical_history_crud_errors() = assertSectionCrudErrors(
        section = "medical-history",
        body = json.encodeToString(UserProfileTestData.fullRequest().medicalHistory!!),
        setup = { repo, userId ->
            coEvery { repo.getMedicalHistory(userId) } returns null
            coEvery { repo.updateMedicalHistory(userId, any()) } returns null
            coEvery { repo.deleteMedicalHistory(userId) } returns false
        }
    )

    @Test
    fun settings_nutrition_history_crud_errors() = assertSectionCrudErrors(
        section = "nutrition-history",
        body = json.encodeToString(UserProfileTestData.fullRequest().nutritionHistory!!),
        setup = { repo, userId ->
            coEvery { repo.getNutritionHistory(userId) } returns null
            coEvery { repo.updateNutritionHistory(userId, any()) } returns null
            coEvery { repo.deleteNutritionHistory(userId) } returns false
        }
    )

    @Test
    fun settings_behavioral_factors_crud_errors() = assertSectionCrudErrors(
        section = "behavioral-factors",
        body = json.encodeToString(UserProfileTestData.fullRequest().behavioralFactors!!),
        setup = { repo, userId ->
            coEvery { repo.getBehavioralFactors(userId) } returns null
            coEvery { repo.updateBehavioralFactors(userId, any()) } returns null
            coEvery { repo.deleteBehavioralFactors(userId) } returns false
        }
    )

    @Test
    fun settings_metrics_tracking_crud_errors() = assertSectionCrudErrors(
        section = "metrics-tracking",
        body = json.encodeToString(UserProfileTestData.fullRequest().metricsTracking!!),
        setup = { repo, userId ->
            coEvery { repo.getMetricsTracking(userId) } returns null
            coEvery { repo.updateMetricsTracking(userId, any()) } returns null
            coEvery { repo.deleteMetricsTracking(userId) } returns false
        }
    )

    @Test
    fun settings_put_returns_400_for_malformed_payload() = testApplication {
        val userId = "bad-json-user"
        val repo = mockk<UserRepo>()
        val gemini = mockk<GeminiRestClient>()
        val service = UserService(repo, gemini)
        val controller = UserController(service)

        application {
            testModule(controller)
        }

        val response = client.put("/user/settings/basic-demographics/$userId") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody("""{"age":"not-a-number"}""")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    private fun assertSectionCrudSuccess(
        section: String,
        body: String,
        setup: (UserRepo, String) -> Unit
    ) = testApplication {
        val userId = "user-$section-success"
        val repo = mockk<UserRepo>()
        val gemini = mockk<GeminiRestClient>()
        val service = UserService(repo, gemini)
        val controller = UserController(service)

        setup(repo, userId)

        application {
            testModule(controller)
        }

        val getResponse = client.get("/user/settings/$section/$userId")
        assertEquals(HttpStatusCode.OK, getResponse.status)

        val putResponse = client.put("/user/settings/$section/$userId") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(body)
        }
        assertEquals(HttpStatusCode.OK, putResponse.status)

        val deleteResponse = client.delete("/user/settings/$section/$userId")
        assertEquals(HttpStatusCode.OK, deleteResponse.status)
    }

    private fun assertSectionCrudErrors(
        section: String,
        body: String,
        setup: (UserRepo, String) -> Unit
    ) = testApplication {
        val userId = "user-$section-error"
        val repo = mockk<UserRepo>()
        val gemini = mockk<GeminiRestClient>()
        val service = UserService(repo, gemini)
        val controller = UserController(service)

        setup(repo, userId)

        application {
            testModule(controller)
        }

        val getResponse = client.get("/user/settings/$section/$userId")
        assertEquals(HttpStatusCode.NotFound, getResponse.status)

        val putResponse = client.put("/user/settings/$section/$userId") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(body)
        }
        assertEquals(HttpStatusCode.InternalServerError, putResponse.status)

        val deleteResponse = client.delete("/user/settings/$section/$userId")
        assertEquals(HttpStatusCode.InternalServerError, deleteResponse.status)
    }
}
