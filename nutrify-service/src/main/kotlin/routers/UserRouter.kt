package com.nutrify.routers

import com.nutrify.controllers.UserController
import com.nutrify.dto.RegisterUserProfileRequest
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.post
import io.ktor.server.routing.routing




fun Application.configureUserRoutes(userController: UserController) {
    routing {
        post("/register") {
            val user = call.receive<RegisterUserProfileRequest>()
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
            val user = call.receive<RegisterUserProfileRequest>()
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
    }
}
