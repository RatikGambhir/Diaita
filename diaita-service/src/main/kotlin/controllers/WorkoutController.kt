package com.diaita.controllers

import com.diaita.dto.WorkoutSearchRequestDto
import com.diaita.dto.WorkoutSearchResponseDto
import com.diaita.service.WorkoutService

class WorkoutController(private val workoutService: WorkoutService) {

    suspend fun searchWorkouts(request: WorkoutSearchRequestDto): WorkoutSearchResponseDto? {
        return workoutService.searchWorkouts(request)
    }
}
