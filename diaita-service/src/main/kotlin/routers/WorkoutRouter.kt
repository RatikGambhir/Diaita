package com.diaita.routers

import com.diaita.controllers.WorkoutController
import com.diaita.dto.WorkoutSearchRequestDto
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.response.respondText
import io.ktor.server.routing.post
import io.ktor.server.routing.routing

fun Application.configureWorkoutRoutes(workoutController: WorkoutController) {
    routing {
        post("/workouts/search") {
            val request = call.receiveOrBadRequest<WorkoutSearchRequestDto>() ?: return@post

            val validation = request.validate()
            if (!validation.isValid) {
                call.respondText(
                    validation.errorMessage ?: "Invalid request",
                    status = HttpStatusCode.BadRequest
                )
                return@post
            }

            call.respondOrFail(
                workoutController.searchWorkouts(request),
                "Failed to search workouts"
            )
        }
    }
}
