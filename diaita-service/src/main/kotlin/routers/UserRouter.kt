package com.diaita.routers

import com.diaita.controllers.UserController
import com.diaita.dto.RegisterUserProfileRequestDto
import com.diaita.dto.ServiceResult
import com.diaita.dto.UserSettingsAction
import com.diaita.dto.UserSettingsPage
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
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
            val user = call.receive<RegisterUserProfileRequestDto>()
            if(user.userId.isBlank()) {
                call.respondText("Invalid request, request body is invalid", status = HttpStatusCode.BadRequest)
                return@post
            }
            when (val result = userController.registerUserProfile(user)) {
                is ServiceResult.Success -> call.respond(HttpStatusCode.OK, result.data)
                is ServiceResult.Failure -> call.respondText(result.error, status = HttpStatusCode.InternalServerError)
            }
        }

        post("/user/profile") {
            val user = call.receive<RegisterUserProfileRequestDto>()
            if(user.userId.isBlank()) {
                call.respondText("Invalid request, request body is invalid", status = HttpStatusCode.BadRequest)
                return@post
            }
            when (val result = userController.registerUserProfile(user)) {
                is ServiceResult.Success -> call.respond(HttpStatusCode.OK, result.data)
                is ServiceResult.Failure -> call.respondText(result.error, status = HttpStatusCode.InternalServerError)
            }
        }

        get("/user/profile/{userId}") {
            val userId = call.parameters["userId"]
            if (userId.isNullOrBlank()) {
                call.respondText("Missing userId parameter", status = HttpStatusCode.BadRequest)
                return@get
            }

            val profile = userController.getUserProfile(userId)
            if (profile != null) {
                call.respond(HttpStatusCode.OK, profile)
                return@get
            }

            call.respondText("Profile not found", status = HttpStatusCode.NotFound)
        }

        post("/users/{userId}/recommendations/generate") {
            val userId = call.parameters["userId"]
            if (userId.isNullOrBlank()) {
                call.respondText("Missing userId parameter", status = HttpStatusCode.BadRequest)
                return@post
            }

            when (val result = userController.generateAndSaveRecommendations(userId)) {
                is ServiceResult.Success -> call.respond(HttpStatusCode.OK, result.data)
                is ServiceResult.Failure -> call.respondText(result.error, status = HttpStatusCode.InternalServerError)
            }
        }

        get("/users/{userId}/recommendations") {
            val userId = call.parameters["userId"]
            if (userId.isNullOrBlank()) {
                call.respondText("Missing userId parameter", status = HttpStatusCode.BadRequest)
                return@get
            }

            val recommendation = userController.getRecommendations(userId)
            if (recommendation != null) {
                call.respond(HttpStatusCode.OK, recommendation)
                return@get
            }

            call.respondText("No recommendations found", status = HttpStatusCode.NotFound)
        }

        route("/user/settings/{userId}") {
            get {
                call.handleUserSettingsRequest(userController)
            }
            put {
                call.handleUserSettingsRequest(userController)
            }
            delete {
                call.handleUserSettingsRequest(userController)
            }
            post {
                call.handleUserSettingsRequest(userController)
            }
        }
    }
}

private suspend fun ApplicationCall.handleUserSettingsRequest(userController: UserController) {
    val userId = parameters["userId"]
    if (userId.isNullOrBlank()) {
        respondText("Missing userId", status = HttpStatusCode.BadRequest)
        return
    }

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
        } catch (_: Exception) {
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
        UserSettingsAction.GET -> {
            if (result == null) {
                respondText("Not found", status = HttpStatusCode.NotFound)
                return
            }
            respond(HttpStatusCode.OK, result)
        }
        UserSettingsAction.UPDATE -> {
            if (result == null) {
                respondText("Update failed", status = HttpStatusCode.InternalServerError)
                return
            }
            respond(HttpStatusCode.OK, result)
        }
        UserSettingsAction.DELETE -> {
            val success = result as? Boolean ?: false
            if (!success) {
                respondText("Delete failed", status = HttpStatusCode.InternalServerError)
                return
            }
            respond(HttpStatusCode.OK, mapOf("status" to "deleted"))
        }
    }
}
