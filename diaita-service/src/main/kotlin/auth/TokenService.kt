package com.diaita.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.diaita.dto.AuthUserDto
import java.time.Instant
import java.util.Date

class TokenService(
    secret: String,
    val issuer: String,
    val audience: String,
    val lifetimeSeconds: Long
) {
    private val algorithm = Algorithm.HMAC256(secret)
    val verifier: JWTVerifier = JWT.require(algorithm)
        .withIssuer(issuer)
        .withAudience(audience)
        .build()

    fun create(user: AuthUserDto, sessionId: String, expiresAt: Instant): String =
        JWT.create()
            .withIssuer(issuer)
            .withAudience(audience)
            .withSubject(user.id)
            .withClaim("sid", sessionId)
            .withClaim("email", user.email)
            .withIssuedAt(Date.from(Instant.now()))
            .withExpiresAt(Date.from(expiresAt))
            .sign(algorithm)
}
