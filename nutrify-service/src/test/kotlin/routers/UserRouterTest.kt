package com.nutrify.routers

import com.nutrify.controllers.UserController
import com.nutrify.lib.clients.GeminiRestClient
import com.nutrify.repo.UserRepo
import com.nutrify.service.UserService
import com.nutrify.testdata.UserProfileTestData
import io.ktor.client.request.header
import io.ktor.client.request.post
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
        coEvery { gemini.askQuestion(match { it.isNotBlank() }, any(), any()) } returns "recommendations"

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
        coVerify(exactly = 1) { gemini.askQuestion(match { it.isNotBlank() }, any(), any()) }
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
        coEvery { gemini.askQuestion(match { it.isNotBlank() }, any(), any()) } returns "recommendations"

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
        coVerify(exactly = 1) { gemini.askQuestion(match { it.isNotBlank() }, any(), any()) }
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
        coEvery { gemini.askQuestion(match { it.isNotBlank() }, any(), any()) } returns "recommendations"

        application {
            testModule(controller)
        }

        val response = client.post("/register") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(json.encodeToString(payload))
        }

        assertEquals(HttpStatusCode.OK, response.status)
        coVerify(exactly = 1) { repo.upsertFullProfile(payload) }
        coVerify(exactly = 1) { gemini.askQuestion(match { it.isNotBlank() }, any(), any()) }
    }

    @Test
    fun integration_register_skips_recommendations_when_upsert_fails() = testApplication {
        val repo = mockk<UserRepo>()
        val gemini = mockk<GeminiRestClient>()
        val service = UserService(repo, gemini)
        val controller = UserController(service)
        val payload = UserProfileTestData.fullRequest()

        coEvery { repo.upsertFullProfile(payload) } returns "Mutation Failed"
        coEvery { gemini.askQuestion(match { it.isNotBlank() }, any(), any()) } returns "recommendations"

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
}
