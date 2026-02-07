package com.nutrify.controllers

import com.nutrify.dto.WorkoutSearchRequestDto
import com.nutrify.dto.WorkoutSearchResponseDto
import com.nutrify.service.WorkoutService

class WorkoutController(private val workoutService: WorkoutService) {

    suspend fun searchWorkouts(request: WorkoutSearchRequestDto): WorkoutSearchResponseDto? {
        return workoutService.searchWorkouts(request)
    }
}
