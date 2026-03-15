package com.diaita.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegisterUserProfileResponseDto(
    val profile: RegisteredUserProfileDto,
    val recommendation: RecommendationDto
)
