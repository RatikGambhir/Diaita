package com.diaita.auth

import com.diaita.dto.AuthSessionDto
import com.diaita.dto.AuthUserDto
import com.diaita.dto.LoginRequestDto
import com.diaita.dto.RegisterRequestDto
import com.diaita.entity.AuthUserEntity
import com.diaita.repo.AuthRepo
import java.time.Instant
import java.util.UUID

class AuthService(
    private val authRepo: AuthRepo,
    private val passwordHasher: PasswordHasher,
    private val tokenService: TokenService
) {
    fun register(request: RegisterRequestDto): AuthResult {
        val email = request.email.trim().lowercase()
        val displayName = request.displayName.trim()
        validationError(email, request.password, displayName)?.let { return AuthResult.Failure(it) }
        if (authRepo.findByEmail(email) != null) {
            return AuthResult.Failure("An account with that email already exists")
        }

        val digest = passwordHasher.hash(request.password)
        val createdAt = Instant.now().toString()
        val entity = AuthUserEntity(
            id = UUID.randomUUID().toString(),
            email = email,
            displayName = displayName,
            passwordHash = digest.hash,
            passwordSalt = digest.salt,
            createdAt = createdAt
        )
        if (!authRepo.createUser(entity)) {
            return AuthResult.Failure("Unable to create account")
        }

        return AuthResult.Success(createSession(entity.toDto()))
    }

    fun login(request: LoginRequestDto): AuthResult {
        val user = authRepo.findByEmail(request.email.trim().lowercase())
            ?: return AuthResult.Failure(INVALID_CREDENTIALS)
        if (!passwordHasher.verify(request.password, user.passwordHash, user.passwordSalt)) {
            return AuthResult.Failure(INVALID_CREDENTIALS)
        }
        return AuthResult.Success(createSession(user.toDto()))
    }

    fun currentUser(userId: String): AuthUserDto? = authRepo.findPublicUser(userId)

    fun isSessionActive(sessionId: String, userId: String): Boolean =
        authRepo.isSessionActive(sessionId, userId)

    fun logout(sessionId: String, userId: String): Boolean = authRepo.revokeSession(sessionId, userId)

    private fun createSession(user: AuthUserDto): AuthSessionDto {
        val sessionId = UUID.randomUUID().toString()
        val expiresAt = Instant.now().plusSeconds(tokenService.lifetimeSeconds)
        authRepo.createSession(sessionId, user.id, expiresAt)
        return AuthSessionDto(
            accessToken = tokenService.create(user, sessionId, expiresAt),
            expiresAt = expiresAt.toString(),
            user = user
        )
    }

    private fun AuthUserEntity.toDto() = AuthUserDto(id, email, displayName, createdAt)

    private fun validationError(email: String, password: String, displayName: String): String? = when {
        !EMAIL_REGEX.matches(email) -> "Enter a valid email address"
        displayName.length !in 2..80 -> "Display name must be between 2 and 80 characters"
        password.length < 8 -> "Password must be at least 8 characters"
        password.length > 200 -> "Password is too long"
        password.none(Char::isLetter) || password.none(Char::isDigit) ->
            "Password must contain at least one letter and one number"
        else -> null
    }

    companion object {
        private const val INVALID_CREDENTIALS = "Invalid email or password"
        private val EMAIL_REGEX = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
    }
}

sealed interface AuthResult {
    data class Success(val session: AuthSessionDto) : AuthResult
    data class Failure(val message: String) : AuthResult
}
