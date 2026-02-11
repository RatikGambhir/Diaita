package com.diaita.routers

import com.diaita.controllers.UserController
import com.diaita.dto.ActivityLevelLifestyleDto
import com.diaita.dto.BasicDemographicsDto
import com.diaita.dto.BehavioralFactorsDto
import com.diaita.dto.GoalsPrioritiesDto
import com.diaita.dto.MedicalHistoryDto
import com.diaita.dto.MetricsTrackingDto
import com.diaita.dto.NutritionDietHistoryDto
import com.diaita.dto.RegisterUserProfileRequestDto
import com.diaita.dto.TrainingBackgroundDto
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.configureUserRoutes(userController: UserController) {
    routing {
        post("/register") {
            val user = call.receive<RegisterUserProfileRequestDto>()
            if(user.userId.isBlank()) {
                call.respondText("Invalid request, request body is invalid", status = HttpStatusCode.BadRequest)
                return@post
            }
            val registeredUser = userController.registerUserProfile(user)
            if(registeredUser == "Mutation Success") {
                call.respond(HttpStatusCode.OK, mapOf("status" to "ok"))
                return@post
            }
            call.respondText("Server Error occurred", status = HttpStatusCode.BadRequest)
        }

        post("/user/profile") {
            val user = call.receive<RegisterUserProfileRequestDto>()
            if(user.userId.isBlank()) {
                call.respondText("Invalid request, request body is invalid", status = HttpStatusCode.BadRequest)
                return@post
            }
            val registeredUser = userController.registerUserProfile(user)
            if(registeredUser == "Mutation Success") {
                call.respond(HttpStatusCode.OK, mapOf("status" to "ok"))
                return@post
            }
            call.respondText("Server Error occurred", status = HttpStatusCode.BadRequest)
        }

        route("/user/settings") {
            sectionCrudRoutes(
                section = "basic-demographics",
                getter = { userId -> userController.getBasicDemographics(userId) },
                updater = { userId, body -> userController.updateBasicDemographics(userId, body) },
                deleter = { userId -> userController.deleteBasicDemographics(userId) }
            )

            sectionCrudRoutes(
                section = "activity-lifestyle",
                getter = { userId -> userController.getActivityLifestyle(userId) },
                updater = { userId, body -> userController.updateActivityLifestyle(userId, body) },
                deleter = { userId -> userController.deleteActivityLifestyle(userId) }
            )

            sectionCrudRoutes(
                section = "goals-priorities",
                getter = { userId -> userController.getGoalsPriorities(userId) },
                updater = { userId, body -> userController.updateGoalsPriorities(userId, body) },
                deleter = { userId -> userController.deleteGoalsPriorities(userId) }
            )

            sectionCrudRoutes(
                section = "training-background",
                getter = { userId -> userController.getTrainingBackground(userId) },
                updater = { userId, body -> userController.updateTrainingBackground(userId, body) },
                deleter = { userId -> userController.deleteTrainingBackground(userId) }
            )

            sectionCrudRoutes(
                section = "medical-history",
                getter = { userId -> userController.getMedicalHistory(userId) },
                updater = { userId, body -> userController.updateMedicalHistory(userId, body) },
                deleter = { userId -> userController.deleteMedicalHistory(userId) }
            )

            sectionCrudRoutes(
                section = "nutrition-history",
                getter = { userId -> userController.getNutritionHistory(userId) },
                updater = { userId, body -> userController.updateNutritionHistory(userId, body) },
                deleter = { userId -> userController.deleteNutritionHistory(userId) }
            )

            sectionCrudRoutes(
                section = "behavioral-factors",
                getter = { userId -> userController.getBehavioralFactors(userId) },
                updater = { userId, body -> userController.updateBehavioralFactors(userId, body) },
                deleter = { userId -> userController.deleteBehavioralFactors(userId) }
            )

            sectionCrudRoutes(
                section = "metrics-tracking",
                getter = { userId -> userController.getMetricsTracking(userId) },
                updater = { userId, body -> userController.updateMetricsTracking(userId, body) },
                deleter = { userId -> userController.deleteMetricsTracking(userId) }
            )
        }
    }
}

private inline fun <reified T : Any> Route.sectionCrudRoutes(
    section: String,
    crossinline getter: suspend (String) -> T?,
    crossinline updater: suspend (String, T) -> T?,
    crossinline deleter: suspend (String) -> Boolean
) {
    get("/$section/{userId}") {
        val userId = call.parameters["userId"]
        if (userId.isNullOrBlank()) {
            call.respondText("Missing userId", status = HttpStatusCode.BadRequest)
            return@get
        }

        val result = getter(userId)
        if (result == null) {
            call.respondText("Not found", status = HttpStatusCode.NotFound)
            return@get
        }
        call.respond(HttpStatusCode.OK, result)
    }

    put("/$section/{userId}") {
        val userId = call.parameters["userId"]
        if (userId.isNullOrBlank()) {
            call.respondText("Missing userId", status = HttpStatusCode.BadRequest)
            return@put
        }

        val body = call.receive<T>()
        val result = updater(userId, body)
        if (result == null) {
            call.respondText("Update failed", status = HttpStatusCode.InternalServerError)
            return@put
        }
        call.respond(HttpStatusCode.OK, result)
    }

    delete("/$section/{userId}") {
        val userId = call.parameters["userId"]
        if (userId.isNullOrBlank()) {
            call.respondText("Missing userId", status = HttpStatusCode.BadRequest)
            return@delete
        }

        val success = deleter(userId)
        if (!success) {
            call.respondText("Delete failed", status = HttpStatusCode.InternalServerError)
            return@delete
        }
        call.respond(HttpStatusCode.OK, mapOf("status" to "deleted"))
    }
}
