package com.diaita.controllers

import com.diaita.dto.RecommendationDto
import com.diaita.dto.RegisterUserProfileRequestDto
import com.diaita.dto.RegisterUserProfileResponseDto
import com.diaita.dto.RegisteredUserProfileDto
import com.diaita.dto.ServiceResult
import com.diaita.dto.UserSettingsAction
import com.diaita.dto.UserSettingsPage
import com.diaita.service.UserService
import kotlinx.serialization.json.JsonElement

class UserController(private val userService: UserService) {
    suspend fun registerUserProfile(request: RegisterUserProfileRequestDto): ServiceResult<RegisterUserProfileResponseDto> {
        return userService.registerUserProfile(request)
    }

    suspend fun generateAndSaveRecommendations(userId: String): ServiceResult<RecommendationDto> {
        return userService.generateAndSaveRecommendations(userId)
    }

    suspend fun getUserProfile(userId: String): RegisteredUserProfileDto? {
        return userService.getUserProfile(userId)
    }

    suspend fun getRecommendations(userId: String): RecommendationDto? {
        return userService.getRecommendations(userId)
    }

    suspend fun handleUserSettings(
        userId: String,
        page: UserSettingsPage,
        action: UserSettingsAction,
        payload: JsonElement? = null
    ): Any? {
        return userService.handleUserSettings(userId, page, action, payload)
    }
}
