package com.diaita.routers

import com.diaita.controllers.WorkoutController
import com.diaita.dto.CreateWorkoutRequestDto
import com.diaita.dto.UpdateWorkoutRequestDto
import com.diaita.dto.WorkoutSearchRequestDto
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.routing
import java.time.LocalDate

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

        // Declared before "/workouts/{id}" so the literal segment wins the route match.
        get("/workouts/stats") {
            val userId = call.workoutUserId() ?: return@get
            val start = (call.optionalDateParam("start") ?: return@get).value
            val end = (call.optionalDateParam("end") ?: return@get).value

            if (start != null && end != null && start.isAfter(end)) {
                call.respondText(
                    "Invalid request: 'start' must not be after 'end'",
                    status = HttpStatusCode.BadRequest
                )
                return@get
            }

            call.respond(HttpStatusCode.OK, workoutController.getWorkoutStats(userId, start, end))
        }

        get("/workouts") {
            val userId = call.workoutUserId() ?: return@get
            val query = call.request.queryParameters["query"]?.trim()?.takeIf { it.isNotEmpty() }
            val page = call.workoutIntParam("page", defaultValue = 0, validRange = 0..10_000) ?: return@get
            val pageSize = call.workoutIntParam("pageSize", defaultValue = 20, validRange = 1..100) ?: return@get

            call.respond(
                HttpStatusCode.OK,
                workoutController.listWorkouts(userId, query, page, pageSize)
            )
        }

        post("/workouts") {
            val request = try {
                call.receive<CreateWorkoutRequestDto>()
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

            val workout = workoutController.createWorkout(request)
            if (workout == null) {
                call.respondText("Failed to create workout", status = HttpStatusCode.InternalServerError)
                return@post
            }

            call.respond(HttpStatusCode.Created, workout)
        }

        get("/workouts/{id}") {
            val workoutId = call.workoutIdParam() ?: return@get
            val userId = call.workoutUserId() ?: return@get

            val workout = workoutController.getWorkout(workoutId, userId)
            if (workout == null) {
                call.respondText("Workout not found", status = HttpStatusCode.NotFound)
                return@get
            }

            call.respond(HttpStatusCode.OK, workout)
        }

        put("/workouts/{id}") {
            val workoutId = call.workoutIdParam() ?: return@put

            val request = try {
                call.receive<UpdateWorkoutRequestDto>()
            } catch (e: Exception) {
                call.respondText("Invalid request payload", status = HttpStatusCode.BadRequest)
                return@put
            }

            val validation = request.validate()
            if (!validation.isValid) {
                call.respondText(
                    validation.errorMessage ?: "Invalid request",
                    status = HttpStatusCode.BadRequest
                )
                return@put
            }

            val workout = workoutController.updateWorkout(workoutId, request)
            if (workout == null) {
                call.respondText("Workout not found", status = HttpStatusCode.NotFound)
                return@put
            }

            call.respond(HttpStatusCode.OK, workout)
        }

        delete("/workouts/{id}") {
            val workoutId = call.workoutIdParam() ?: return@delete
            val userId = call.workoutUserId() ?: return@delete

            if (!workoutController.deleteWorkout(workoutId, userId)) {
                call.respondText("Workout not found", status = HttpStatusCode.NotFound)
                return@delete
            }

            call.respond(HttpStatusCode.OK, mapOf("status" to "deleted"))
        }
    }
}

private suspend fun ApplicationCall.workoutIdParam(): String? {
    val workoutId = parameters["id"]?.trim()
    if (workoutId.isNullOrEmpty()) {
        respondText("Invalid request: workout id is required", status = HttpStatusCode.BadRequest)
        return null
    }
    return workoutId
}

private suspend fun ApplicationCall.workoutUserId(): String? {
    val userId = request.queryParameters["userId"]?.trim()
    if (userId.isNullOrEmpty()) {
        respondText(
            "Invalid request: 'userId' query parameter is required",
            status = HttpStatusCode.BadRequest
        )
        return null
    }
    return userId
}

private suspend fun ApplicationCall.workoutIntParam(
    name: String,
    defaultValue: Int,
    validRange: IntRange
): Int? {
    val rawValue = request.queryParameters[name] ?: return defaultValue
    val parsedValue = rawValue.toIntOrNull()

    if (parsedValue == null || parsedValue !in validRange) {
        respondText(
            "Invalid request: '$name' must be between ${validRange.first} and ${validRange.last}",
            status = HttpStatusCode.BadRequest
        )
        return null
    }

    return parsedValue
}

/**
 * Wraps the parsed value so an absent parameter (`Optional(null)`) stays distinguishable from a
 * malformed one, which returns null after the error response has been written.
 */
@JvmInline
private value class OptionalDate(val value: LocalDate?)

private suspend fun ApplicationCall.optionalDateParam(name: String): OptionalDate? {
    val rawValue = request.queryParameters[name]?.trim()?.takeIf { it.isNotEmpty() }
        ?: return OptionalDate(null)

    val parsed = runCatching { LocalDate.parse(rawValue) }.getOrNull()
    if (parsed == null) {
        respondText(
            "Invalid request: '$name' must be in YYYY-MM-DD format",
            status = HttpStatusCode.BadRequest
        )
        return null
    }

    return OptionalDate(parsed)
}
