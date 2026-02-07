package com.nutrify.routers

import com.nutrify.controllers.WorkoutController
import com.nutrify.dto.WorkoutSearchRequestDto
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.post
import io.ktor.server.routing.routing

fun Application.configureWorkoutRoutes(workoutController: WorkoutController) {
    routing {
        post("/workouts/search") {
            val request = try {
                call.receive<WorkoutSearchRequestDto>()
            } catch (e: Exception) {
                call.respondText("Invalid request payload", status = HttpStatusCode.BadRequest)
                return@post
            }

            val validation = request.validate()
            if (!validation.isValid) {
                call.respondText(
                    validation.errorMessage ?: "Invalid request",
                    status = HttpStatusCode.BadRequest
                )
                return@post
            }

            val response = workoutController.searchWorkouts(request)
            if (response == null) {
                call.respondText("Failed to search workouts", status = HttpStatusCode.InternalServerError)
                return@post
            }

            call.respond(HttpStatusCode.OK, response)
        }
    }
}
