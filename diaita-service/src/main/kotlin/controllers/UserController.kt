package com.diaita.controllers

import com.diaita.dto.RegisterUserProfileRequestDto
import com.diaita.service.UserService

class UserController(private val userService: UserService) {
    suspend fun registerUserProfile(request: RegisterUserProfileRequestDto): String? {
        return userService.registerUserProfile(request)
    }
}
