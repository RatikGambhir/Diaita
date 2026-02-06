package com.nutrify.controllers

import com.nutrify.dto.RegisterUserProfileRequest
import com.nutrify.service.UserService

class UserController(private val userService: UserService) {
    suspend fun registerUserProfile(request: RegisterUserProfileRequest): String? {
        return userService.registerUserProfile(request)
    }
}
