package com.diaita.routers

import com.diaita.AUTH_PROVIDER
import com.diaita.auth.AuthResult
import com.diaita.auth.AuthService
import com.diaita.authenticatedPrincipal
import com.diaita.dto.AuthErrorDto
import com.diaita.dto.LoginRequestDto
import com.diaita.dto.RegisterRequestDto
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.configureAuthRoutes(authService: AuthService) {
    routing {
        route("/auth") {
            post("/register") {
                val request = runCatching { call.receive<RegisterRequestDto>() }.getOrNull()
                if (request == null) {
                    call.respond(HttpStatusCode.BadRequest, AuthErrorDto("Invalid registration request"))
                    return@post
                }
                when (val result = authService.register(request)) {
                    is AuthResult.Success -> call.respond(HttpStatusCode.Created, result.session)
                    is AuthResult.Failure -> call.respond(HttpStatusCode.BadRequest, AuthErrorDto(result.message))
                }
            }

            post("/login") {
                val request = runCatching { call.receive<LoginRequestDto>() }.getOrNull()
                if (request == null) {
                    call.respond(HttpStatusCode.BadRequest, AuthErrorDto("Invalid login request"))
                    return@post
                }
                when (val result = authService.login(request)) {
                    is AuthResult.Success -> call.respond(HttpStatusCode.OK, result.session)
                    is AuthResult.Failure -> call.respond(HttpStatusCode.Unauthorized, AuthErrorDto(result.message))
                }
            }

            authenticate(AUTH_PROVIDER) {
                get("/me") {
                    val principal = call.authenticatedPrincipal()!!
                    val user = authService.currentUser(principal.userId)
                    if (user == null) {
                        call.respond(HttpStatusCode.NotFound, AuthErrorDto("User not found"))
                    } else {
                        call.respond(HttpStatusCode.OK, user)
                    }
                }

                post("/logout") {
                    val principal = call.authenticatedPrincipal()!!
                    authService.logout(principal.sessionId, principal.userId)
                    call.respond(HttpStatusCode.OK, mapOf("status" to "signed_out"))
                }
            }
        }
    }
}
