package com.diaita.entity

data class AuthUserEntity(
    val id: String,
    val email: String,
    val displayName: String,
    val passwordHash: String,
    val passwordSalt: String,
    val createdAt: String
)
