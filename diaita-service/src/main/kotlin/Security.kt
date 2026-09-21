package com.diaita

import com.diaita.auth.AuthService
import com.diaita.auth.TokenService
import com.diaita.dto.AuthErrorDto
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.authentication
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.response.respond

const val AUTH_PROVIDER = "auth-jwt"

data class DiaitaPrincipal(val userId: String, val sessionId: String)

fun Application.configureSecurity(authService: AuthService, tokenService: TokenService) {
    val configuredRealm = environment.config.property("jwt.realm").getString()
    install(Authentication) {
        jwt(AUTH_PROVIDER) {
            realm = configuredRealm
            verifier(tokenService.verifier)
            validate { credential ->
                val userId = credential.payload.subject?.takeIf(String::isNotBlank)
                val sessionId = credential.payload.getClaim("sid").asString()?.takeIf(String::isNotBlank)
                if (userId != null && sessionId != null && authService.isSessionActive(sessionId, userId)) {
                    DiaitaPrincipal(userId, sessionId)
                } else {
                    null
                }
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, AuthErrorDto("Authentication required"))
            }
        }
    }
}

fun ApplicationCall.authenticatedPrincipal(): DiaitaPrincipal? = authentication.principal()

fun ApplicationCall.authenticatedUserId(): String? = authenticatedPrincipal()?.userId
