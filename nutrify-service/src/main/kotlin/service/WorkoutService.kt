package com.nutrify.service

import com.nutrify.dto.WorkoutSearchRequestDto
import com.nutrify.dto.WorkoutSearchResponseDto
import com.nutrify.lib.mappings.toResponseDto
import com.nutrify.repo.WorkoutRepo

class WorkoutService(private val workoutRepo: WorkoutRepo) {

    suspend fun searchWorkouts(request: WorkoutSearchRequestDto): WorkoutSearchResponseDto? {
        val result = workoutRepo.searchExercises(request)

        if (result.error != null || result.body == null) {
            println("Error searching workouts: ${result.error?.message}")
            return null
        }

        return result.body.toResponseDto()
    }
}
