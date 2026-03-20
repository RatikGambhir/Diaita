package com.diaita.routers

import com.diaita.Container
import com.diaita.controllers.UserController
import com.diaita.dto.*
import com.diaita.entity.*
import com.diaita.lib.clients.GeminiRestClient
import com.diaita.lib.mappings.toEntity
import com.diaita.repo.RecommendationRepo
import com.diaita.repo.UserRepo
import com.diaita.testdata.RecommendationTestData
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
    private val repo = mockk<UserRepo>()
    private val gemini = mockk<GeminiRestClient>(relaxed = true)
    private val recommendationRepo = mockk<RecommendationRepo>()
    private val container = Container().apply {
        bind<UserRepo>(repo)
        bind<GeminiRestClient>(gemini)
        bind<RecommendationRepo>(recommendationRepo)
    }
    private val controller = container.get<UserController>()

    @AfterTest
    fun tearDown() {
        clearAllMocks()
    }

    private fun Application.testModule() {
        install(ContentNegotiation) {
            json()
        }
        configureUserRoutes(controller)
    }

    @Test
    fun register_returns_200_and_calls_controller_with_payload_on_success() = testApplication {
        val payload = UserProfileTestData.fullRequest()
        val profile = RecommendationTestData.registeredProfile(payload.userId)
        val recommendation = RecommendationTestData.recommendation()
        coEvery { repo.upsertUserProfile(payload) } returns profile
        coEvery {
            gemini.askQuestionStructured(match { it.isNotBlank() }, any(), RecommendationDto.serializer(), any(), any())
        } returns recommendation
        coEvery { recommendationRepo.saveRecommendation(payload.userId, recommendation) } returns true

        application {
            testModule()
        }

        val response = client.post("/register") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(json.encodeToString(payload))
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val body = Json.decodeFromString<RegisterUserProfileResponseDto>(response.bodyAsText())
        assertEquals(profile, body.profile)
        assertEquals(recommendation, body.recommendation)
        coVerify(exactly = 1) { repo.upsertUserProfile(payload) }
        coVerify(exactly = 1) {
            gemini.askQuestionStructured(match { it.isNotBlank() }, any(), RecommendationDto.serializer(), any(), any())
        }
        coVerify(exactly = 1) { recommendationRepo.saveRecommendation(payload.userId, recommendation) }
    }

    @Test
    fun register_returns_400_and_does_not_call_controller_for_blank_user_id() = testApplication {
        val invalidPayload = UserProfileTestData.fullRequest(userId = "   ")

        application {
            testModule()
        }

        val response = client.post("/register") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(json.encodeToString(invalidPayload))
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("Invalid request"))
        confirmVerified(repo, gemini, recommendationRepo)
    }

    @Test
    fun user_profile_returns_400_for_invalid_age_range() = testApplication {
        val invalidPayload = UserProfileTestData.fullRequest().copy(age = 10)

        application {
            testModule()
        }

        val response = client.post("/user/profile") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(json.encodeToString(invalidPayload))
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("age"))
        confirmVerified(repo, gemini, recommendationRepo)
    }

    @Test
    fun user_profile_returns_400_for_missing_primary_goal_or_activity_level() = testApplication {
        val invalidPayload = UserProfileTestData.fullRequest().copy(primaryGoal = "", activityLevel = "")

        application {
            testModule()
        }

        val response = client.post("/user/profile") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(json.encodeToString(invalidPayload))
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("primaryGoal"))
        confirmVerified(repo, gemini, recommendationRepo)
    }

    @Test
    fun user_profile_returns_400_when_training_background_missing() = testApplication {
        val invalidPayload = UserProfileTestData.fullRequest().copy(trainingHistory = null, trainingAge = null)

        application {
            testModule()
        }

        val response = client.post("/user/profile") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(json.encodeToString(invalidPayload))
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("trainingHistory"))
        confirmVerified(repo, gemini, recommendationRepo)
    }

    @Test
    fun user_profile_returns_500_when_controller_reports_failure() = testApplication {
        val payload = UserProfileTestData.fullRequest()
        val recommendation = RecommendationTestData.recommendation()
        coEvery { repo.upsertUserProfile(payload) } returns null
        coEvery {
            gemini.askQuestionStructured(match { it.isNotBlank() }, any(), RecommendationDto.serializer(), any(), any())
        } returns recommendation

        application {
            testModule()
        }

        val response = client.post("/user/profile") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(json.encodeToString(payload))
        }

        assertEquals(HttpStatusCode.InternalServerError, response.status)
        assertTrue(response.bodyAsText().contains("upsertUserProfile failed"))
        coVerify(exactly = 1) { repo.upsertUserProfile(payload) }
    }

    @Test
    fun user_profile_returns_200_on_success() = testApplication {
        val payload = UserProfileTestData.fullRequest()
        val profile = RecommendationTestData.registeredProfile(payload.userId)
        val recommendation = RecommendationTestData.recommendation()
        coEvery { repo.upsertUserProfile(payload) } returns profile
        coEvery {
            gemini.askQuestionStructured(match { it.isNotBlank() }, any(), RecommendationDto.serializer(), any(), any())
        } returns recommendation
        coEvery { recommendationRepo.saveRecommendation(payload.userId, recommendation) } returns true

        application {
            testModule()
        }

        val response = client.post("/user/profile") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(json.encodeToString(payload))
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val body = Json.decodeFromString<RegisterUserProfileResponseDto>(response.bodyAsText())
        assertEquals(profile, body.profile)
        assertEquals(recommendation, body.recommendation)
        coVerify(exactly = 1) { repo.upsertUserProfile(payload) }
        coVerify(exactly = 1) {
            gemini.askQuestionStructured(match { it.isNotBlank() }, any(), RecommendationDto.serializer(), any(), any())
        }
        coVerify(exactly = 1) { recommendationRepo.saveRecommendation(payload.userId, recommendation) }
    }

    @Test
    fun register_rejects_malformed_request_payload() = testApplication {
        application {
            testModule()
        }

        val response = client.post("/register") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody("""{"id":"missing-required-fields"}""")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        confirmVerified(repo, gemini, recommendationRepo)
    }

    @Test
    fun integration_register_calls_repo_and_recommendations_when_upsert_succeeds() = testApplication {
        val payload = UserProfileTestData.fullRequest()
        val profile = RecommendationTestData.registeredProfile(payload.userId)
        val recommendation = RecommendationTestData.recommendation()

        coEvery { repo.upsertUserProfile(payload) } returns profile
        coEvery {
            gemini.askQuestionStructured(match { it.isNotBlank() }, any(), RecommendationDto.serializer(), any(), any())
        } returns recommendation
        coEvery { recommendationRepo.saveRecommendation(payload.userId, recommendation) } returns true

        application {
            testModule()
        }

        val response = client.post("/register") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(json.encodeToString(payload))
        }

        assertEquals(HttpStatusCode.OK, response.status)
        coVerify(exactly = 1) { repo.upsertUserProfile(payload) }
        coVerify(exactly = 1) {
            gemini.askQuestionStructured(match { it.isNotBlank() }, any(), RecommendationDto.serializer(), any(), any())
        }
        coVerify(exactly = 1) { recommendationRepo.saveRecommendation(payload.userId, recommendation) }
    }

    @Test
    fun integration_register_returns_500_when_upsert_fails() = testApplication {
        val payload = UserProfileTestData.fullRequest()
        val recommendation = RecommendationTestData.recommendation()

        coEvery { repo.upsertUserProfile(payload) } returns null
        coEvery {
            gemini.askQuestionStructured(match { it.isNotBlank() }, any(), RecommendationDto.serializer(), any(), any())
        } returns recommendation

        application {
            testModule()
        }

        val response = client.post("/register") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(json.encodeToString(payload))
        }

        assertEquals(HttpStatusCode.InternalServerError, response.status)
        assertTrue(response.bodyAsText().contains("upsertUserProfile failed"))
        coVerify(exactly = 1) { repo.upsertUserProfile(payload) }
    }

    @Test
    fun generate_recommendations_returns_structured_payload() = testApplication {
        val profile = RecommendationTestData.registeredProfile()
        val recommendation = RecommendationTestData.recommendation()

        coEvery { repo.getFullProfile(profile.userId) } returns profile
        coEvery {
            gemini.askQuestionStructured(match { it.isNotBlank() }, any(), RecommendationDto.serializer(), any(), any())
        } returns recommendation
        coEvery { recommendationRepo.saveRecommendation(profile.userId, recommendation) } returns true

        application {
            testModule()
        }

        val response = client.post("/users/${profile.userId}/recommendations/generate")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(recommendation, Json.decodeFromString<RecommendationDto>(response.bodyAsText()))
    }

    @Test
    fun get_recommendations_returns_saved_structured_payload() = testApplication {
        val userId = UserProfileTestData.fullRequest().userId
        val recommendation = RecommendationTestData.recommendation()

        coEvery { recommendationRepo.getRecommendationByUserId(userId) } returns recommendation

        application {
            testModule()
        }

        val response = client.get("/users/$userId/recommendations")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(recommendation, Json.decodeFromString<RecommendationDto>(response.bodyAsText()))
    }

    @Test
    fun settings_basic_demographics_get_put_delete_follow_expected_behavior() = testApplication {
        val userId = "user-123"
        val dto = UserProfileTestData.basicDemographics()
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
            testModule()
        }

        val getResponse = client.get("/user/settings/$userId?page=basic-demographics&action=get")
        assertEquals(HttpStatusCode.OK, getResponse.status)
        val getBody = Json.decodeFromString<BasicDemographicsDto>(getResponse.bodyAsText())
        assertEquals(dto, getBody)

        val putResponse = client.put("/user/settings/$userId?page=basic-demographics&action=update") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(json.encodeToString(dto))
        }
        assertEquals(HttpStatusCode.OK, putResponse.status)
        val putBody = Json.decodeFromString<BasicDemographicsDto>(putResponse.bodyAsText())
        assertEquals(dto, putBody)

        val deleteResponse = client.delete("/user/settings/$userId?page=basic-demographics&action=delete")
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
        confirmVerified(gemini, recommendationRepo)
    }

    @Test
    fun settings_basic_demographics_maps_failures_to_expected_status_codes() = testApplication {
        val userId = "user-404"
        val dto = UserProfileTestData.basicDemographics()

        coEvery { repo.getBasicDemographics(userId) } returns null
        coEvery { repo.updateBasicDemographics(userId, any()) } returns null
        coEvery { repo.deleteBasicDemographics(userId) } returns false

        application {
            testModule()
        }

        val getResponse = client.get("/user/settings/$userId?page=basic-demographics&action=get")
        assertEquals(HttpStatusCode.NotFound, getResponse.status)

        val putResponse = client.put("/user/settings/$userId?page=basic-demographics&action=update") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(json.encodeToString(dto))
        }
        assertEquals(HttpStatusCode.InternalServerError, putResponse.status)

        val deleteResponse = client.delete("/user/settings/$userId?page=basic-demographics&action=delete")
        assertEquals(HttpStatusCode.InternalServerError, deleteResponse.status)

        coVerify(exactly = 1) { repo.getBasicDemographics(userId) }
        coVerify(exactly = 1) { repo.updateBasicDemographics(userId, any()) }
        coVerify(exactly = 1) { repo.deleteBasicDemographics(userId) }
        confirmVerified(gemini, recommendationRepo)
    }

    @Test
    fun settings_activity_lifestyle_crud_success() = assertSectionCrudSuccess(
        section = "activity-lifestyle",
        body = json.encodeToString(UserProfileTestData.activityLifestyle()),
        setup = { userId ->
            val entity = UserProfileTestData.activityLifestyle().toEntity(userId)
            coEvery { repo.getActivityLifestyle(userId) } returns entity
            coEvery { repo.updateActivityLifestyle(userId, any()) } returns entity
            coEvery { repo.deleteActivityLifestyle(userId) } returns true
        }
    )

    @Test
    fun settings_goals_priorities_crud_success() = assertSectionCrudSuccess(
        section = "goals-priorities",
        body = json.encodeToString(UserProfileTestData.goals()),
        setup = { userId ->
            val entity = UserProfileTestData.goals().toEntity(userId)
            coEvery { repo.getGoalsPriorities(userId) } returns entity
            coEvery { repo.updateGoalsPriorities(userId, any()) } returns entity
            coEvery { repo.deleteGoalsPriorities(userId) } returns true
        }
    )

    @Test
    fun settings_training_background_crud_success() = assertSectionCrudSuccess(
        section = "training-background",
        body = json.encodeToString(UserProfileTestData.trainingBackground()),
        setup = { userId ->
            val entity = UserProfileTestData.trainingBackground().toEntity(userId)
            coEvery { repo.getTrainingBackground(userId) } returns entity
            coEvery { repo.updateTrainingBackground(userId, any()) } returns entity
            coEvery { repo.deleteTrainingBackground(userId) } returns true
        }
    )

    @Test
    fun settings_nutrition_history_crud_success() = assertSectionCrudSuccess(
        section = "nutrition-history",
        body = json.encodeToString(UserProfileTestData.nutritionHistory()),
        setup = { userId ->
            val entity = UserProfileTestData.nutritionHistory().toEntity(userId)
            coEvery { repo.getNutritionHistory(userId) } returns entity
            coEvery { repo.updateNutritionHistory(userId, any()) } returns entity
            coEvery { repo.deleteNutritionHistory(userId) } returns true
        }
    )

    @Test
    fun settings_activity_lifestyle_crud_errors() = assertSectionCrudErrors(
        section = "activity-lifestyle",
        body = json.encodeToString(UserProfileTestData.activityLifestyle()),
        setup = { userId ->
            coEvery { repo.getActivityLifestyle(userId) } returns null
            coEvery { repo.updateActivityLifestyle(userId, any()) } returns null
            coEvery { repo.deleteActivityLifestyle(userId) } returns false
        }
    )

    @Test
    fun settings_goals_priorities_crud_errors() = assertSectionCrudErrors(
        section = "goals-priorities",
        body = json.encodeToString(UserProfileTestData.goals()),
        setup = { userId ->
            coEvery { repo.getGoalsPriorities(userId) } returns null
            coEvery { repo.updateGoalsPriorities(userId, any()) } returns null
            coEvery { repo.deleteGoalsPriorities(userId) } returns false
        }
    )

    @Test
    fun settings_training_background_crud_errors() = assertSectionCrudErrors(
        section = "training-background",
        body = json.encodeToString(UserProfileTestData.trainingBackground()),
        setup = { userId ->
            coEvery { repo.getTrainingBackground(userId) } returns null
            coEvery { repo.updateTrainingBackground(userId, any()) } returns null
            coEvery { repo.deleteTrainingBackground(userId) } returns false
        }
    )

    @Test
    fun settings_nutrition_history_crud_errors() = assertSectionCrudErrors(
        section = "nutrition-history",
        body = json.encodeToString(UserProfileTestData.nutritionHistory()),
        setup = { userId ->
            coEvery { repo.getNutritionHistory(userId) } returns null
            coEvery { repo.updateNutritionHistory(userId, any()) } returns null
            coEvery { repo.deleteNutritionHistory(userId) } returns false
        }
    )

    @Test
    fun settings_put_returns_400_for_malformed_payload() = testApplication {
        val userId = "bad-json-user"

        application {
            testModule()
        }

        val response = client.put("/user/settings/$userId?page=basic-demographics&action=update") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody("""{"age":"not-a-number"}""")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun settings_returns_400_for_missing_page_query_param() = testApplication {
        val userId = "missing-page-user"

        application {
            testModule()
        }

        val response = client.get("/user/settings/$userId?action=get")

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("page query parameter"))
        confirmVerified(repo, gemini, recommendationRepo)
    }

    @Test
    fun settings_returns_400_for_missing_action_query_param() = testApplication {
        val userId = "missing-action-user"

        application {
            testModule()
        }

        val response = client.get("/user/settings/$userId?page=basic-demographics")

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("action query parameter"))
        confirmVerified(repo, gemini, recommendationRepo)
    }

    private fun assertSectionCrudSuccess(
        section: String,
        body: String,
        setup: (String) -> Unit
    ) = testApplication {
        val userId = "user-$section-success"
        setup(userId)

        application {
            testModule()
        }

        val getResponse = client.get("/user/settings/$userId?page=$section&action=get")
        assertEquals(HttpStatusCode.OK, getResponse.status)

        val putResponse = client.put("/user/settings/$userId?page=$section&action=update") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(body)
        }
        assertEquals(HttpStatusCode.OK, putResponse.status)

        val deleteResponse = client.delete("/user/settings/$userId?page=$section&action=delete")
        assertEquals(HttpStatusCode.OK, deleteResponse.status)
    }

    private fun assertSectionCrudErrors(
        section: String,
        body: String,
        setup: (String) -> Unit
    ) = testApplication {
        val userId = "user-$section-error"
        setup(userId)

        application {
            testModule()
        }

        val getResponse = client.get("/user/settings/$userId?page=$section&action=get")
        assertEquals(HttpStatusCode.NotFound, getResponse.status)

        val putResponse = client.put("/user/settings/$userId?page=$section&action=update") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(body)
        }
        assertEquals(HttpStatusCode.InternalServerError, putResponse.status)

        val deleteResponse = client.delete("/user/settings/$userId?page=$section&action=delete")
        assertEquals(HttpStatusCode.InternalServerError, deleteResponse.status)
    }
}
