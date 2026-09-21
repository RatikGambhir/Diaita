package com.diaita.controllers

import com.diaita.dto.WorkoutSearchRequestDto
import com.diaita.dto.WorkoutSearchResponseDto
import com.diaita.dto.UpsertWorkoutRequestDto
import com.diaita.dto.WorkoutLogDto
import com.diaita.dto.WorkoutStatsDto
import com.diaita.service.WorkoutService

class WorkoutController(private val workoutService: WorkoutService) {

    suspend fun listWorkouts(userId: String, query: String?): List<WorkoutLogDto> = workoutService.listWorkouts(userId, query)
    suspend fun getWorkout(userId: String, workoutId: String): WorkoutLogDto? = workoutService.getWorkout(userId, workoutId)
    suspend fun createWorkout(userId: String, request: UpsertWorkoutRequestDto): WorkoutLogDto? = workoutService.createWorkout(userId, request)
    suspend fun updateWorkout(userId: String, workoutId: String, request: UpsertWorkoutRequestDto): WorkoutLogDto? = workoutService.updateWorkout(userId, workoutId, request)
    suspend fun deleteWorkout(userId: String, workoutId: String): Boolean = workoutService.deleteWorkout(userId, workoutId)
    suspend fun workoutStats(userId: String): WorkoutStatsDto = workoutService.workoutStats(userId)

    suspend fun searchWorkouts(request: WorkoutSearchRequestDto): WorkoutSearchResponseDto? {
        return workoutService.searchWorkouts(request)
    }
}
