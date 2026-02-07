package com.nutrify.controllers

import com.nutrify.dto.RegisterUserProfileRequestDto
import com.nutrify.service.UserService

class UserController(private val userService: UserService) {
    suspend fun registerUserProfile(request: RegisterUserProfileRequestDto): String? {
        return userService.registerUserProfile(request)
    }
}
