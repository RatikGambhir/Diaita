package com.diaita.service

import com.diaita.Container
import com.diaita.dto.CreateWorkoutRequestDto
import com.diaita.dto.UpdateWorkoutRequestDto
import com.diaita.dto.UpsertWorkoutExerciseDto
import com.diaita.dto.WorkoutExerciseOpsDto
import com.diaita.entity.WorkoutExerciseRowEntity
import com.diaita.entity.WorkoutExerciseWriteRowEntity
import com.diaita.entity.WorkoutRowEntity
import com.diaita.entity.WorkoutWriteRowEntity
import com.diaita.repo.WorkoutRepo
import io.mockk.CapturingSlot
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class WorkoutLogServiceTest {

    private val repo = mockk<WorkoutRepo>()
    private val container = Container().apply {
        bind<WorkoutRepo>(repo)
    }
    private val service = container.get<WorkoutService>()

    private val userId = "11111111-1111-1111-1111-111111111111"

    private fun workoutRow(
        id: String = "workout-1",
        name: String = "Leg Day",
        performedAt: String = "2026-03-04T00:00Z",
        durationSeconds: Int? = 3600,
        notes: String? = null
    ) = WorkoutRowEntity(
        id = id,
        userId = userId,
        name = name,
        performedAt = performedAt,
        durationSeconds = durationSeconds,
        notes = notes
    )

    private fun exerciseRow(
        id: String,
        workoutId: String = "workout-1",
        name: String = "Squat",
        category: String = "lifting",
        position: Int = 0,
        sets: Int? = null,
        reps: Int? = null,
        weightKg: Double? = null,
        durationSeconds: Int? = null
    ) = WorkoutExerciseRowEntity(
        id = id,
        workoutId = workoutId,
        userId = userId,
        name = name,
        category = category,
        position = position,
        sets = sets,
        reps = reps,
        weightKg = weightKg,
        durationSeconds = durationSeconds
    )

    @Test
    fun createWorkout_persists_workout_and_exercises_with_sequential_positions() = runBlocking {
        val workoutSlot = slot<WorkoutWriteRowEntity>()
        val exercisesSlot = slot<List<WorkoutExerciseWriteRowEntity>>()

        coEvery { repo.insertWorkout(capture(workoutSlot)) } answers { workoutRow(id = workoutSlot.captured.id) }
        coEvery { repo.upsertWorkoutExercises(capture(exercisesSlot)) } answers {
            exercisesSlot.captured.map {
                exerciseRow(
                    id = it.id,
                    workoutId = it.workoutId,
                    name = it.name,
                    category = it.category,
                    position = it.position,
                    sets = it.sets,
                    reps = it.reps,
                    weightKg = it.weightKg,
                    durationSeconds = it.durationSeconds
                )
            }
        }

        val result = service.createWorkout(
            CreateWorkoutRequestDto(
                userId = userId,
                name = "  Leg Day  ",
                performedAt = "2026-03-04",
                durationSeconds = 3600,
                exercises = listOf(
                    UpsertWorkoutExerciseDto(name = "Squat", category = "lifting", sets = 5, reps = 5, weightKg = 100.0),
                    UpsertWorkoutExerciseDto(name = "Row Erg", category = "cardio", durationSeconds = 600)
                )
            )
        )

        assertNotNull(result)
        assertEquals("Leg Day", workoutSlot.captured.name)
        assertEquals("2026-03-04T00:00Z", workoutSlot.captured.performedAt)
        assertEquals(listOf(0, 1), exercisesSlot.captured.map { it.position })
        assertEquals(2, result.exercises.size)
        assertEquals(2500.0, result.totals.totalVolumeKg)
        assertEquals(5, result.totals.totalSets)
        assertEquals(600, result.totals.totalCardioSeconds)
        assertEquals(1, result.totals.liftingCount)
        assertEquals(1, result.totals.cardioCount)
    }

    @Test
    fun createWorkout_returns_null_when_insert_fails() = runBlocking {
        coEvery { repo.insertWorkout(any()) } returns null

        val result = service.createWorkout(
            CreateWorkoutRequestDto(userId = userId, name = "Leg Day")
        )

        assertNull(result)
        coVerify(exactly = 0) { repo.upsertWorkoutExercises(any()) }
    }

    @Test
    fun getWorkout_orders_exercises_by_position() = runBlocking {
        coEvery { repo.getWorkout("workout-1", userId) } returns workoutRow()
        coEvery { repo.getExercisesForWorkouts(listOf("workout-1")) } returns listOf(
            exerciseRow(id = "b", name = "Second", position = 1),
            exerciseRow(id = "a", name = "First", position = 0)
        )

        val result = service.getWorkout("workout-1", userId)

        assertNotNull(result)
        assertEquals(listOf("First", "Second"), result.exercises.map { it.name })
    }

    @Test
    fun getWorkout_returns_null_when_workout_is_not_owned_by_user() = runBlocking {
        coEvery { repo.getWorkout("workout-1", userId) } returns null

        assertNull(service.getWorkout("workout-1", userId))
        coVerify(exactly = 0) { repo.getExercisesForWorkouts(any()) }
    }

    @Test
    fun listWorkouts_filters_by_query_sorts_newest_first_and_paginates() = runBlocking {
        coEvery { repo.getWorkoutsForUser(userId) } returns listOf(
            workoutRow(id = "old", name = "Leg Day", performedAt = "2026-03-01T00:00Z"),
            workoutRow(id = "new", name = "Leg Day Redux", performedAt = "2026-03-10T00:00Z"),
            workoutRow(id = "other", name = "Swim", performedAt = "2026-03-05T00:00Z")
        )
        coEvery { repo.getExercisesForWorkouts(listOf("new")) } returns emptyList()

        val result = service.listWorkouts(userId, query = "leg", page = 0, pageSize = 1)

        assertEquals(listOf("new"), result.workouts.map { it.id })
        assertEquals(2, result.pagination.total)
        assertEquals(2, result.pagination.totalPages)
        assertTrue(result.pagination.hasMore)
        assertFalse(result.pagination.hasPrevious)
    }

    @Test
    fun listWorkouts_returns_empty_page_past_the_end() = runBlocking {
        coEvery { repo.getWorkoutsForUser(userId) } returns listOf(workoutRow())
        coEvery { repo.getExercisesForWorkouts(emptyList()) } returns emptyList()

        val result = service.listWorkouts(userId, page = 5, pageSize = 20)

        assertTrue(result.workouts.isEmpty())
        assertEquals(1, result.pagination.total)
        assertFalse(result.pagination.hasMore)
        assertTrue(result.pagination.hasPrevious)
    }

    @Test
    fun updateWorkout_keeps_existing_fields_when_request_omits_them() = runBlocking {
        val existing = workoutRow(name = "Leg Day", durationSeconds = 3600, notes = "felt good")
        val updateSlot = slot<WorkoutWriteRowEntity>()

        coEvery { repo.getWorkout("workout-1", userId) } returns existing
        coEvery { repo.updateWorkout("workout-1", userId, capture(updateSlot)) } answers { existing }
        coEvery { repo.getExercisesForWorkouts(listOf("workout-1")) } returns emptyList()

        val result = service.updateWorkout("workout-1", UpdateWorkoutRequestDto(userId = userId))

        assertNotNull(result)
        assertEquals("Leg Day", updateSlot.captured.name)
        assertEquals(3600, updateSlot.captured.durationSeconds)
        assertEquals("felt good", updateSlot.captured.notes)
        assertEquals(existing.performedAt, updateSlot.captured.performedAt)
    }

    @Test
    fun updateWorkout_deletes_then_appends_new_exercises_after_the_surviving_ones() = runBlocking {
        val existing = workoutRow()
        val upsertSlot: CapturingSlot<List<WorkoutExerciseWriteRowEntity>> = slot()

        coEvery { repo.getWorkout("workout-1", userId) } returns existing
        coEvery { repo.updateWorkout("workout-1", userId, any()) } returns existing
        coEvery { repo.deleteWorkoutExercises("workout-1", listOf("gone")) } returns true
        coEvery { repo.getExercisesForWorkouts(listOf("workout-1")) } returns listOf(
            exerciseRow(id = "kept", position = 3)
        )
        coEvery { repo.upsertWorkoutExercises(capture(upsertSlot)) } returns emptyList()

        service.updateWorkout(
            "workout-1",
            UpdateWorkoutRequestDto(
                userId = userId,
                exerciseOps = WorkoutExerciseOpsDto(
                    upsert = listOf(UpsertWorkoutExerciseDto(name = "Lunge", category = "lifting")),
                    deleteIds = listOf("gone")
                )
            )
        )

        coVerify(exactly = 1) { repo.deleteWorkoutExercises("workout-1", listOf("gone")) }
        assertEquals(listOf(4), upsertSlot.captured.map { it.position })
        assertEquals("Lunge", upsertSlot.captured.single().name)
    }

    @Test
    fun updateWorkout_honours_explicit_positions_for_reordering() = runBlocking {
        val existing = workoutRow()
        val upsertSlot: CapturingSlot<List<WorkoutExerciseWriteRowEntity>> = slot()

        coEvery { repo.getWorkout("workout-1", userId) } returns existing
        coEvery { repo.updateWorkout("workout-1", userId, any()) } returns existing
        coEvery { repo.getExercisesForWorkouts(listOf("workout-1")) } returns listOf(
            exerciseRow(id = "a", position = 0),
            exerciseRow(id = "b", position = 1)
        )
        coEvery { repo.upsertWorkoutExercises(capture(upsertSlot)) } returns emptyList()

        service.updateWorkout(
            "workout-1",
            UpdateWorkoutRequestDto(
                userId = userId,
                exerciseOps = WorkoutExerciseOpsDto(
                    upsert = listOf(
                        UpsertWorkoutExerciseDto(id = "b", name = "Squat", category = "lifting", position = 0),
                        UpsertWorkoutExerciseDto(id = "a", name = "Bench", category = "lifting", position = 1)
                    )
                )
            )
        )

        assertEquals(listOf("b" to 0, "a" to 1), upsertSlot.captured.map { it.id to it.position })
    }

    @Test
    fun updateWorkout_returns_null_when_workout_is_missing() = runBlocking {
        coEvery { repo.getWorkout("workout-1", userId) } returns null

        assertNull(service.updateWorkout("workout-1", UpdateWorkoutRequestDto(userId = userId)))
        coVerify(exactly = 0) { repo.updateWorkout(any(), any(), any()) }
    }

    @Test
    fun deleteWorkout_refuses_when_the_workout_belongs_to_another_user() = runBlocking {
        coEvery { repo.getWorkout("workout-1", userId) } returns null

        assertFalse(service.deleteWorkout("workout-1", userId))
        coVerify(exactly = 0) { repo.deleteWorkout(any(), any()) }
    }

    @Test
    fun deleteWorkout_removes_an_owned_workout() = runBlocking {
        coEvery { repo.getWorkout("workout-1", userId) } returns workoutRow()
        coEvery { repo.deleteWorkout("workout-1", userId) } returns true

        assertTrue(service.deleteWorkout("workout-1", userId))
    }

    @Test
    fun getStats_aggregates_volume_duration_and_daily_points_within_the_range() = runBlocking {
        coEvery { repo.getWorkoutsForUser(userId) } returns listOf(
            workoutRow(id = "w1", performedAt = "2026-03-01T00:00Z", durationSeconds = 3000),
            workoutRow(id = "w2", performedAt = "2026-03-02T00:00Z", durationSeconds = 1800),
            workoutRow(id = "outside", performedAt = "2026-02-01T00:00Z", durationSeconds = 9999)
        )
        coEvery { repo.getExercisesForWorkouts(listOf("w1", "w2")) } returns listOf(
            exerciseRow(id = "e1", workoutId = "w1", sets = 3, reps = 10, weightKg = 50.0),
            exerciseRow(id = "e2", workoutId = "w1", category = "cardio", durationSeconds = 900, position = 1),
            exerciseRow(id = "e3", workoutId = "w2", category = "mobility", position = 0)
        )

        val stats = service.getStats(
            userId,
            start = LocalDate.parse("2026-03-01"),
            end = LocalDate.parse("2026-03-31")
        )

        assertEquals(2, stats.workoutCount)
        assertEquals(4800, stats.totalDurationSeconds)
        assertEquals(2400, stats.averageDurationSeconds)
        assertEquals(1500.0, stats.totalVolumeKg)
        assertEquals(3, stats.totalSets)
        assertEquals(900, stats.totalCardioSeconds)
        assertEquals(3, stats.exerciseCount)
        assertEquals(1, stats.byCategory.lifting)
        assertEquals(1, stats.byCategory.cardio)
        assertEquals(1, stats.byCategory.mobility)
        assertEquals(listOf("2026-03-01", "2026-03-02"), stats.daily.map { it.date })
        assertEquals(1500.0, stats.daily.first().volumeKg)
    }

    @Test
    fun getStats_returns_zeroed_stats_when_no_workouts_match() = runBlocking {
        coEvery { repo.getWorkoutsForUser(userId) } returns emptyList()

        val stats = service.getStats(userId)

        assertEquals(0, stats.workoutCount)
        assertEquals(0, stats.averageDurationSeconds)
        assertTrue(stats.daily.isEmpty())
        coVerify(exactly = 0) { repo.getExercisesForWorkouts(any()) }
    }
}
