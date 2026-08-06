package com.diaita.routers

import com.diaita.controllers.UserController
import com.diaita.dto.RegisterUserProfileRequestDto
import com.diaita.dto.ServiceResult
import com.diaita.dto.UserSettingsAction
import com.diaita.dto.UserSettingsPage
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.plugins.ContentTransformationException
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonElement

fun Application.configureUserRoutes(userController: UserController) {
    routing {
        post("/register") {
            call.handleProfileUpsert(userController)
        }

        post("/user/profile") {
            call.handleProfileUpsert(userController)
        }

        get("/user/profile/{userId}") {
            val userId = call.requiredPathParam("userId") ?: return@get

            call.respondOrFail(
                userController.getUserProfile(userId),
                "Profile not found",
                HttpStatusCode.NotFound
            )
        }

        post("/users/{userId}/recommendations/generate") {
            val userId = call.requiredPathParam("userId") ?: return@post

            call.respondToServiceResult(userController.generateAndSaveRecommendations(userId))
        }

        get("/users/{userId}/recommendations") {
            val userId = call.requiredPathParam("userId") ?: return@get

            call.respondOrFail(
                userController.getRecommendations(userId),
                "No recommendations found",
                HttpStatusCode.NotFound
            )
        }

        route("/user/settings/{userId}") {
            get { call.handleUserSettingsRequest(userController) }
            put { call.handleUserSettingsRequest(userController) }
            delete { call.handleUserSettingsRequest(userController) }
            post { call.handleUserSettingsRequest(userController) }
        }
    }
}

private suspend fun ApplicationCall.respondToServiceResult(result: ServiceResult<Any>) {
    when (result) {
        is ServiceResult.Success -> respond(HttpStatusCode.OK, result.data)
        is ServiceResult.Failure -> respondText(result.error, status = HttpStatusCode.InternalServerError)
    }
}

private suspend fun ApplicationCall.handleProfileUpsert(userController: UserController) {
    val user = try {
        receive<RegisterUserProfileRequestDto>()
    } catch (_: ContentTransformationException) {
        respondText("Invalid request, request body is invalid", status = HttpStatusCode.BadRequest)
        return
    }

    val validationError = user.validationError()
    if (validationError != null) {
        respondText(validationError, status = HttpStatusCode.BadRequest)
        return
    }

    respondToServiceResult(userController.registerUserProfile(user))
}

private fun RegisterUserProfileRequestDto.validationError(): String? = when {
    userId.isBlank() ->
        "Invalid request, request body is invalid"

    age !in 13..120 ->
        "Invalid request: age must be between 13 and 120"

    height <= 0.0 || weight <= 0.0 ->
        "Invalid request: height and weight must be greater than 0"

    primaryGoal.isBlank() || activityLevel.isBlank() ->
        "Invalid request: primaryGoal and activityLevel are required"

    sleepDuration != null && sleepDuration !in 0.0..24.0 ->
        "Invalid request: sleepDuration must be between 0 and 24"

    daysPerWeek != null && daysPerWeek !in 0..14 ->
        "Invalid request: daysPerWeek must be between 0 and 14"

    timePerSession != null && timePerSession !in 0..1440 ->
        "Invalid request: timePerSession must be between 0 and 1440"

    trainingHistory.isNullOrBlank() && trainingAge.isNullOrBlank() ->
        "Invalid request: at least one of trainingHistory or trainingAge is required"

    else -> null
}

private suspend fun ApplicationCall.handleUserSettingsRequest(userController: UserController) {
    val userId = requiredPathParam("userId", missingMessage = "Missing userId") ?: return

    val page = UserSettingsPage.fromQuery(request.queryParameters["page"])
    if (page == null) {
        respondText("Missing or invalid page query parameter", status = HttpStatusCode.BadRequest)
        return
    }

    val action = UserSettingsAction.fromQuery(request.queryParameters["action"])
    if (action == null) {
        respondText("Missing or invalid action query parameter", status = HttpStatusCode.BadRequest)
        return
    }

    val payload = if (action == UserSettingsAction.UPDATE) {
        try {
            receive<JsonElement>()
        } catch (_: ContentTransformationException) {
            respondText("Invalid request body", status = HttpStatusCode.BadRequest)
            return
        }
    } else {
        null
    }

    val result = try {
        userController.handleUserSettings(
            userId = userId,
            page = page,
            action = action,
            payload = payload
        )
    } catch (_: SerializationException) {
        respondText("Invalid request body for selected page", status = HttpStatusCode.BadRequest)
        return
    }

    when (action) {
        UserSettingsAction.GET ->
            respondOrFail(result, "Not found", HttpStatusCode.NotFound)

        UserSettingsAction.UPDATE ->
            respondOrFail(result, "Update failed")

        UserSettingsAction.DELETE ->
            if (result as? Boolean == true) {
                respond(HttpStatusCode.OK, mapOf("status" to "deleted"))
            } else {
                respondText("Delete failed", status = HttpStatusCode.InternalServerError)
            }
    }
}
