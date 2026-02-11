package com.diaita.service

import com.diaita.dto.WorkoutSearchRequestDto
import com.diaita.dto.WorkoutSearchResponseDto
import com.diaita.lib.mappings.toResponseDto
import com.diaita.repo.WorkoutRepo

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
