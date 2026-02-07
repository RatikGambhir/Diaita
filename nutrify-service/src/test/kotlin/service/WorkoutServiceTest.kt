package com.nutrify.service

import com.nutrify.dto.WorkoutSearchRequestDto
import com.nutrify.entity.ExerciseEntity
import com.nutrify.lib.factories.PaginatedResult
import com.nutrify.lib.factories.Result
import com.nutrify.repo.WorkoutRepo
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class WorkoutServiceTest {

    @Test
    fun searchWorkouts_maps_paginated_result_to_response() = runBlocking {
        val repo = mockk<WorkoutRepo>()
        val service = WorkoutService(repo)
        val request = WorkoutSearchRequestDto(exerciseType = "Cardio", page = 1, pageSize = 2)

        coEvery { repo.searchExercises(request) } returns Result(
            body = PaginatedResult(
                data = listOf(
                    ExerciseEntity(
                        id = 1,
                        exercise = "Running",
                        exerciseType = "Cardio",
                        equipment = "None",
                        mechanics = "Isolation"
                    ),
                    ExerciseEntity(id = 2, exercise = "Rowing", exerciseType = "Cardio")
                ),
                total = 5,
                page = 1,
                pageSize = 2,
                hasMore = true
            ),
            error = null
        )

        val response = service.searchWorkouts(request)

        assertNotNull(response)
        assertEquals(2, response.exercises.size)
        assertEquals("Running", response.exercises.first().exercise)
        assertEquals(
            "Equipment: None | Mechanics: Isolation",
            response.exercises.first().description
        )
        assertEquals(5, response.pagination.total)
        assertEquals(3, response.pagination.totalPages)
        assertTrue(response.pagination.hasMore)
        assertTrue(response.pagination.hasPrevious)
    }

    @Test
    fun searchWorkouts_returns_null_when_repo_fails() = runBlocking {
        val repo = mockk<WorkoutRepo>()
        val service = WorkoutService(repo)
        val request = WorkoutSearchRequestDto(exerciseType = "Cardio")

        coEvery { repo.searchExercises(request) } returns Result(
            body = null,
            error = IllegalStateException("db unavailable")
        )

        val response = service.searchWorkouts(request)

        assertNull(response)
    }
}
