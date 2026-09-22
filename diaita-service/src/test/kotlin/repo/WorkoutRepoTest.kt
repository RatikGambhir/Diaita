package com.diaita.repo

import com.diaita.dto.UpsertWorkoutExerciseDto
import com.diaita.dto.UpsertWorkoutRequestDto
import com.diaita.dto.WorkoutSearchRequestDto
import com.diaita.insertTestUser
import com.diaita.testDatabase
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class WorkoutRepoTest {
    @Test
    fun searchExercises_uses_normalized_filters_and_pagination() = runBlocking {
        val repo = WorkoutRepo(testDatabase())
        val result = repo.searchExercises(
            WorkoutSearchRequestDto(query = "row", exerciseTypes = listOf("Cardio Conditioning"), pageSize = 10)
        )

        assertEquals(null, result.error)
        assertTrue(result.body?.data?.any { it.exercise == "Rowing" } == true)
        assertEquals(0, result.body?.page)
    }

    @Test
    fun workout_crud_and_stats_persist_in_sqlite() = runBlocking {
        val database = testDatabase()
        val userId = "123e4567-e89b-12d3-a456-426614174000"
        database.insertTestUser(userId)
        val repo = WorkoutRepo(database)
        val request = UpsertWorkoutRequestDto(
            name = "Strength Day",
            performedAt = "2026-08-06T18:00:00Z",
            durationMinutes = 60,
            exercises = listOf(
                UpsertWorkoutExerciseDto(
                    exerciseId = 2,
                    exerciseName = "Bench Press",
                    sets = 3,
                    reps = 10,
                    weightKg = 50.0
                )
            )
        )

        val created = assertNotNull(repo.createWorkout(userId, request))
        assertEquals(1500.0, created.totalVolumeKg)
        assertEquals(created, repo.getWorkout(userId, created.id))
        assertEquals(1, repo.listWorkouts(userId, null).size)
        assertTrue(repo.workoutStats(userId).totalVolumeKg == 1500.0)
        assertTrue(repo.deleteWorkout(userId, created.id))
        assertTrue(repo.listWorkouts(userId, null).isEmpty())
    }
}
