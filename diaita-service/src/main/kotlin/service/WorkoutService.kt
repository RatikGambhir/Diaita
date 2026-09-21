package com.diaita.service

import com.diaita.dto.WorkoutSearchRequestDto
import com.diaita.dto.WorkoutSearchResponseDto
import com.diaita.dto.UpsertWorkoutRequestDto
import com.diaita.dto.WorkoutLogDto
import com.diaita.dto.WorkoutStatsDto
import com.diaita.lib.mappings.toResponseDto
import com.diaita.repo.WorkoutRepo

class WorkoutService(private val workoutRepo: WorkoutRepo) {

    suspend fun listWorkouts(userId: String, query: String?): List<WorkoutLogDto> =
        workoutRepo.listWorkouts(userId, query)

    suspend fun getWorkout(userId: String, workoutId: String): WorkoutLogDto? =
        workoutRepo.getWorkout(userId, workoutId)

    suspend fun createWorkout(userId: String, request: UpsertWorkoutRequestDto): WorkoutLogDto? =
        workoutRepo.createWorkout(userId, request)

    suspend fun updateWorkout(userId: String, workoutId: String, request: UpsertWorkoutRequestDto): WorkoutLogDto? =
        workoutRepo.updateWorkout(userId, workoutId, request)

    suspend fun deleteWorkout(userId: String, workoutId: String): Boolean =
        workoutRepo.deleteWorkout(userId, workoutId)

    suspend fun workoutStats(userId: String): WorkoutStatsDto = workoutRepo.workoutStats(userId)

    suspend fun searchWorkouts(request: WorkoutSearchRequestDto): WorkoutSearchResponseDto? {
        val result = workoutRepo.searchExercises(request)

        if (result.error != null || result.body == null) {
            println("Error searching workouts: ${result.error?.message}")
            return null
        }

        return result.body.toResponseDto()
    }
}
