package com.diaita.routers

import com.diaita.AUTH_PROVIDER
import com.diaita.authenticatedUserId
import com.diaita.controllers.WorkoutController
import com.diaita.dto.AuthErrorDto
import com.diaita.dto.UpsertWorkoutRequestDto
import com.diaita.dto.WorkoutSearchRequestDto
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.routing

fun Application.configureWorkoutRoutes(workoutController: WorkoutController, requireAuthentication: Boolean = false) {
    routing {
        if (requireAuthentication) {
            authenticate(AUTH_PROVIDER) { workoutRoutes(workoutController) }
        } else {
            workoutRoutes(workoutController)
        }
    }
}

private fun Route.workoutRoutes(controller: WorkoutController) {
    post("/workouts/search") {
        val request = runCatching { call.receive<WorkoutSearchRequestDto>() }.getOrNull()
        if (request == null) {
            call.respondText("Invalid request payload", status = HttpStatusCode.BadRequest)
            return@post
        }
        val validation = request.validate()
        if (!validation.isValid) {
            call.respondText(validation.errorMessage ?: "Invalid request", status = HttpStatusCode.BadRequest)
            return@post
        }
        val response = controller.searchWorkouts(request)
        if (response == null) call.respondText("Failed to search workouts", status = HttpStatusCode.InternalServerError)
        else call.respond(HttpStatusCode.OK, response)
    }

    get("/workouts") {
        val userId = call.authenticatedUserId()
        if (userId == null) {
            call.respond(HttpStatusCode.Unauthorized, AuthErrorDto("Authentication required"))
            return@get
        }
        call.respond(controller.listWorkouts(userId, call.request.queryParameters["query"]))
    }

    get("/workouts/stats") {
        val userId = call.authenticatedUserId()
        if (userId == null) {
            call.respond(HttpStatusCode.Unauthorized, AuthErrorDto("Authentication required"))
            return@get
        }
        call.respond(controller.workoutStats(userId))
    }

    post("/workouts") {
        val userId = call.authenticatedUserId()
        if (userId == null) {
            call.respond(HttpStatusCode.Unauthorized, AuthErrorDto("Authentication required"))
            return@post
        }
        val request = call.receiveWorkoutRequest() ?: return@post
        request.validationError()?.let {
            call.respond(HttpStatusCode.BadRequest, AuthErrorDto(it))
            return@post
        }
        val saved = controller.createWorkout(userId, request)
        if (saved == null) call.respond(HttpStatusCode.InternalServerError, AuthErrorDto("Unable to save workout"))
        else call.respond(HttpStatusCode.Created, saved)
    }

    get("/workouts/{id}") {
        val userId = call.authenticatedUserId()
        val id = call.parameters["id"]
        if (userId == null || id.isNullOrBlank()) {
            call.respond(HttpStatusCode.Unauthorized, AuthErrorDto("Authentication required"))
            return@get
        }
        val workout = controller.getWorkout(userId, id)
        if (workout == null) call.respond(HttpStatusCode.NotFound, AuthErrorDto("Workout not found"))
        else call.respond(workout)
    }

    put("/workouts/{id}") {
        val userId = call.authenticatedUserId()
        val id = call.parameters["id"]
        if (userId == null || id.isNullOrBlank()) {
            call.respond(HttpStatusCode.Unauthorized, AuthErrorDto("Authentication required"))
            return@put
        }
        val request = call.receiveWorkoutRequest() ?: return@put
        request.validationError()?.let {
            call.respond(HttpStatusCode.BadRequest, AuthErrorDto(it))
            return@put
        }
        val saved = controller.updateWorkout(userId, id, request)
        if (saved == null) call.respond(HttpStatusCode.NotFound, AuthErrorDto("Workout not found"))
        else call.respond(saved)
    }

    delete("/workouts/{id}") {
        val userId = call.authenticatedUserId()
        val id = call.parameters["id"]
        if (userId == null || id.isNullOrBlank()) {
            call.respond(HttpStatusCode.Unauthorized, AuthErrorDto("Authentication required"))
            return@delete
        }
        if (controller.deleteWorkout(userId, id)) call.respond(HttpStatusCode.NoContent)
        else call.respond(HttpStatusCode.NotFound, AuthErrorDto("Workout not found"))
    }
}

private suspend fun io.ktor.server.application.ApplicationCall.receiveWorkoutRequest(): UpsertWorkoutRequestDto? {
    return runCatching { receive<UpsertWorkoutRequestDto>() }.getOrElse {
        respond(HttpStatusCode.BadRequest, AuthErrorDto("Invalid workout request"))
        null
    }
}
