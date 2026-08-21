package com.diaita.service

import com.diaita.dto.CreateWorkoutRequestDto
import com.diaita.dto.PaginationMetadataDto
import com.diaita.dto.UpdateWorkoutRequestDto
import com.diaita.dto.UpsertWorkoutExerciseDto
import com.diaita.dto.WorkoutDailyPointDto
import com.diaita.dto.WorkoutDto
import com.diaita.dto.WorkoutListResponseDto
import com.diaita.dto.WorkoutSearchRequestDto
import com.diaita.dto.WorkoutSearchResponseDto
import com.diaita.dto.WorkoutStatsResponseDto
import com.diaita.dto.WorkoutTotalsDto
import com.diaita.dto.parseWorkoutTimestamp
import com.diaita.entity.WorkoutRowEntity
import com.diaita.entity.WorkoutWriteRowEntity
import com.diaita.lib.mappings.roundVolume
import com.diaita.lib.mappings.toCategoryBreakdown
import com.diaita.lib.mappings.toDto
import com.diaita.lib.mappings.toResponseDto
import com.diaita.lib.mappings.toTotals
import com.diaita.lib.mappings.toWriteRowEntity
import com.diaita.repo.WorkoutRepo
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

class WorkoutService(private val workoutRepo: WorkoutRepo) {

    suspend fun searchWorkouts(request: WorkoutSearchRequestDto): WorkoutSearchResponseDto? {
        val result = workoutRepo.searchExercises(request)

        if (result.error != null || result.body == null) {
            println("Error searching workouts: ${result.error?.message}")
            return null
        }

        return result.body.toResponseDto()
    }

    suspend fun createWorkout(request: CreateWorkoutRequestDto): WorkoutDto? {
        val workoutId = UUID.randomUUID().toString()
        val performedAt = request.performedAt
            ?.let { parseWorkoutTimestamp(it) }
            ?: OffsetDateTime.now(ZoneOffset.UTC)

        val workout = workoutRepo.insertWorkout(
            WorkoutWriteRowEntity(
                id = workoutId,
                userId = request.userId,
                name = request.name.trim(),
                performedAt = performedAt.toString(),
                durationSeconds = request.durationSeconds,
                notes = request.notes?.trim()?.takeIf { it.isNotEmpty() }
            )
        ) ?: return null

        val exercises = if (request.exercises.isEmpty()) {
            emptyList()
        } else {
            workoutRepo.upsertWorkoutExercises(
                buildExerciseRows(
                    exercises = request.exercises,
                    workoutId = workoutId,
                    userId = request.userId,
                    startingPosition = 0
                )
            )
        }

        return workout.toDto(exercises)
    }

    suspend fun getWorkout(workoutId: String, userId: String): WorkoutDto? {
        val workout = workoutRepo.getWorkout(workoutId, userId) ?: return null
        val exercises = workoutRepo.getExercisesForWorkouts(listOf(workoutId))
        return workout.toDto(exercises)
    }

    suspend fun listWorkouts(
        userId: String,
        query: String? = null,
        page: Int = 0,
        pageSize: Int = 20
    ): WorkoutListResponseDto {
        val matching = workoutRepo.getWorkoutsForUser(userId)
            .filter { it.matches(query) }
            .sortedByDescending { it.performedAtOrEpoch() }

        val normalizedPage = page.coerceAtLeast(0)
        val normalizedPageSize = pageSize.coerceAtLeast(1)
        val offset = normalizedPage * normalizedPageSize
        val pageRows = matching.drop(offset).take(normalizedPageSize)

        val exercisesByWorkout = workoutRepo
            .getExercisesForWorkouts(pageRows.map { it.id })
            .groupBy { it.workoutId }

        val total = matching.size

        return WorkoutListResponseDto(
            workouts = pageRows.map { it.toDto(exercisesByWorkout[it.id].orEmpty()) },
            pagination = PaginationMetadataDto(
                total = total,
                page = normalizedPage,
                pageSize = normalizedPageSize,
                totalPages = (total + normalizedPageSize - 1) / normalizedPageSize,
                hasMore = offset + pageRows.size < total,
                hasPrevious = normalizedPage > 0
            )
        )
    }

    suspend fun updateWorkout(
        workoutId: String,
        request: UpdateWorkoutRequestDto
    ): WorkoutDto? {
        val existing = workoutRepo.getWorkout(workoutId, request.userId) ?: return null

        val performedAt = request.performedAt?.let { parseWorkoutTimestamp(it) }?.toString()
            ?: existing.performedAt

        val updated = workoutRepo.updateWorkout(
            workoutId = workoutId,
            userId = request.userId,
            workout = WorkoutWriteRowEntity(
                id = existing.id,
                userId = existing.userId,
                name = request.name?.trim()?.takeIf { it.isNotEmpty() } ?: existing.name,
                performedAt = performedAt,
                durationSeconds = request.durationSeconds ?: existing.durationSeconds,
                notes = request.notes?.trim()?.takeIf { it.isNotEmpty() } ?: existing.notes
            )
        ) ?: return null

        val ops = request.exerciseOps
        if (ops != null) {
            if (ops.deleteIds.isNotEmpty()) {
                workoutRepo.deleteWorkoutExercises(workoutId, ops.deleteIds)
            }

            if (ops.upsert.isNotEmpty()) {
                val remaining = workoutRepo.getExercisesForWorkouts(listOf(workoutId))
                val upsertIds = ops.upsert.mapNotNull { it.id }.toSet()
                val startingPosition = remaining
                    .filterNot { it.id in upsertIds }
                    .maxOfOrNull { it.position + 1 }
                    ?: 0

                workoutRepo.upsertWorkoutExercises(
                    buildExerciseRows(
                        exercises = ops.upsert,
                        workoutId = workoutId,
                        userId = request.userId,
                        startingPosition = startingPosition
                    )
                )
            }
        }

        return updated.toDto(workoutRepo.getExercisesForWorkouts(listOf(workoutId)))
    }

    suspend fun deleteWorkout(workoutId: String, userId: String): Boolean {
        workoutRepo.getWorkout(workoutId, userId) ?: return false
        return workoutRepo.deleteWorkout(workoutId, userId)
    }

    suspend fun getStats(
        userId: String,
        start: LocalDate? = null,
        end: LocalDate? = null
    ): WorkoutStatsResponseDto {
        val workouts = workoutRepo.getWorkoutsForUser(userId)
            .filter { it.isWithin(start, end) }

        if (workouts.isEmpty()) {
            return WorkoutStatsResponseDto()
        }

        val exercisesByWorkout = workoutRepo
            .getExercisesForWorkouts(workouts.map { it.id })
            .groupBy { it.workoutId }

        val totalsByWorkout = workouts.associate { it.id to exercisesByWorkout[it.id].orEmpty().toTotals() }
        val totals = totalsByWorkout.values.toList()
        val totalDurationSeconds = workouts.sumOf { it.durationSeconds ?: 0 }

        return WorkoutStatsResponseDto(
            workoutCount = workouts.size,
            totalDurationSeconds = totalDurationSeconds,
            averageDurationSeconds = totalDurationSeconds / workouts.size,
            totalVolumeKg = roundVolume(totals.sumOf { it.totalVolumeKg }),
            totalSets = totals.sumOf { it.totalSets },
            totalCardioSeconds = totals.sumOf { it.totalCardioSeconds },
            exerciseCount = totals.sumOf { it.exerciseCount },
            byCategory = totals.toCategoryBreakdown(),
            daily = workouts.toDailyPoints(totalsByWorkout)
        )
    }

    private fun buildExerciseRows(
        exercises: List<UpsertWorkoutExerciseDto>,
        workoutId: String,
        userId: String,
        startingPosition: Int
    ) = exercises.mapIndexed { index, exercise ->
        exercise.toWriteRowEntity(
            id = exercise.id ?: UUID.randomUUID().toString(),
            workoutId = workoutId,
            userId = userId,
            position = exercise.position ?: (startingPosition + index)
        )
    }

    private fun List<WorkoutRowEntity>.toDailyPoints(
        totalsByWorkout: Map<String, WorkoutTotalsDto>
    ): List<WorkoutDailyPointDto> = groupBy { it.performedAtOrEpoch().toLocalDate() }
        .toSortedMap()
        .map { (date, workoutsOnDate) ->
            WorkoutDailyPointDto(
                date = date.toString(),
                workoutCount = workoutsOnDate.size,
                durationSeconds = workoutsOnDate.sumOf { it.durationSeconds ?: 0 },
                volumeKg = roundVolume(
                    workoutsOnDate.sumOf { totalsByWorkout[it.id]?.totalVolumeKg ?: 0.0 }
                )
            )
        }
}

private fun WorkoutRowEntity.performedAtOrEpoch(): OffsetDateTime =
    parseWorkoutTimestamp(performedAt) ?: OffsetDateTime.MIN

private fun WorkoutRowEntity.matches(query: String?): Boolean {
    val trimmed = query?.trim()
    if (trimmed.isNullOrEmpty()) {
        return true
    }

    return name.contains(trimmed, ignoreCase = true)
        || notes?.contains(trimmed, ignoreCase = true) == true
}

private fun WorkoutRowEntity.isWithin(start: LocalDate?, end: LocalDate?): Boolean {
    val performedOn = parseWorkoutTimestamp(performedAt)?.toLocalDate() ?: return false
    if (start != null && performedOn.isBefore(start)) {
        return false
    }
    return end == null || !performedOn.isAfter(end)
}
