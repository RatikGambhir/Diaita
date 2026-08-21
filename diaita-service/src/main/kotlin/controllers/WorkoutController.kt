package com.diaita.controllers

import com.diaita.dto.CreateWorkoutRequestDto
import com.diaita.dto.UpdateWorkoutRequestDto
import com.diaita.dto.WorkoutDto
import com.diaita.dto.WorkoutListResponseDto
import com.diaita.dto.WorkoutSearchRequestDto
import com.diaita.dto.WorkoutSearchResponseDto
import com.diaita.dto.WorkoutStatsResponseDto
import com.diaita.service.WorkoutService
import java.time.LocalDate

class WorkoutController(private val workoutService: WorkoutService) {

    suspend fun searchWorkouts(request: WorkoutSearchRequestDto): WorkoutSearchResponseDto? {
        return workoutService.searchWorkouts(request)
    }

    suspend fun createWorkout(request: CreateWorkoutRequestDto): WorkoutDto? {
        return workoutService.createWorkout(request)
    }

    suspend fun listWorkouts(
        userId: String,
        query: String?,
        page: Int,
        pageSize: Int
    ): WorkoutListResponseDto {
        return workoutService.listWorkouts(userId, query, page, pageSize)
    }

    suspend fun getWorkout(workoutId: String, userId: String): WorkoutDto? {
        return workoutService.getWorkout(workoutId, userId)
    }

    suspend fun updateWorkout(workoutId: String, request: UpdateWorkoutRequestDto): WorkoutDto? {
        return workoutService.updateWorkout(workoutId, request)
    }

    suspend fun deleteWorkout(workoutId: String, userId: String): Boolean {
        return workoutService.deleteWorkout(workoutId, userId)
    }

    suspend fun getWorkoutStats(
        userId: String,
        start: LocalDate?,
        end: LocalDate?
    ): WorkoutStatsResponseDto {
        return workoutService.getStats(userId, start, end)
    }
}
