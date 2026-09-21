package com.diaita.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequestDto(
    val email: String,
    val password: String,
    val displayName: String
)

@Serializable
data class LoginRequestDto(
    val email: String,
    val password: String
)

@Serializable
data class AuthUserDto(
    val id: String,
    val email: String,
    val displayName: String,
    val createdAt: String
)

@Serializable
data class AuthSessionDto(
    val accessToken: String,
    val expiresAt: String,
    val user: AuthUserDto
)

@Serializable
data class AuthErrorDto(val message: String)
