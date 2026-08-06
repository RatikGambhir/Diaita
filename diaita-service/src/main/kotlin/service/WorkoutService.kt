package com.diaita.service

import com.diaita.dto.WorkoutSearchRequestDto
import com.diaita.dto.WorkoutSearchResponseDto
import com.diaita.lib.logging.logger
import com.diaita.lib.mappings.toResponseDto
import com.diaita.repo.WorkoutRepo

class WorkoutService(private val workoutRepo: WorkoutRepo) {

    private val log = logger()

    suspend fun searchWorkouts(request: WorkoutSearchRequestDto): WorkoutSearchResponseDto? {
        val result = workoutRepo.searchExercises(request)
        val exercises = result.body

        if (result.error != null || exercises == null) {
            log.error("Exercise search failed: {}", result.error?.message, result.error)
            return null
        }

        return exercises.toResponseDto()
    }
}
