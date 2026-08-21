package com.diaita.dto

import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneOffset

/**
 * The three buckets a logged exercise can belong to. The wire format is the lowercase
 * [value] so the client and the database agree on a single spelling.
 */
enum class WorkoutExerciseCategory(val value: String) {
    LIFTING("lifting"),
    CARDIO("cardio"),
    MOBILITY("mobility");

    companion object {
        fun fromValue(value: String?): WorkoutExerciseCategory? {
            if (value.isNullOrBlank()) return null
            return entries.firstOrNull { it.value == value.trim().lowercase() }
        }
    }
}

@Serializable
data class WorkoutExerciseLogDto(
    val id: String,
    val exerciseId: Int? = null,
    val name: String,
    val category: String,
    val position: Int,
    val sets: Int? = null,
    val reps: Int? = null,
    val weightKg: Double? = null,
    val durationSeconds: Int? = null,
    val intensity: String? = null,
    val target: String? = null,
    val notes: String? = null
)

@Serializable
data class WorkoutTotalsDto(
    val exerciseCount: Int = 0,
    val liftingCount: Int = 0,
    val cardioCount: Int = 0,
    val mobilityCount: Int = 0,
    val totalSets: Int = 0,
    val totalVolumeKg: Double = 0.0,
    val totalCardioSeconds: Int = 0
)

@Serializable
data class WorkoutDto(
    val id: String,
    val userId: String,
    val name: String,
    val performedAt: String,
    val durationSeconds: Int? = null,
    val notes: String? = null,
    val exercises: List<WorkoutExerciseLogDto> = emptyList(),
    val totals: WorkoutTotalsDto = WorkoutTotalsDto()
)

@Serializable
data class UpsertWorkoutExerciseDto(
    val id: String? = null,
    val exerciseId: Int? = null,
    val name: String,
    val category: String,
    val position: Int? = null,
    val sets: Int? = null,
    val reps: Int? = null,
    val weightKg: Double? = null,
    val durationSeconds: Int? = null,
    val intensity: String? = null,
    val target: String? = null,
    val notes: String? = null
)

@Serializable
data class WorkoutExerciseOpsDto(
    val upsert: List<UpsertWorkoutExerciseDto> = emptyList(),
    val deleteIds: List<String> = emptyList()
)

@Serializable
data class CreateWorkoutRequestDto(
    val userId: String,
    val name: String,
    val performedAt: String? = null,
    val durationSeconds: Int? = null,
    val notes: String? = null,
    val exercises: List<UpsertWorkoutExerciseDto> = emptyList()
) {
    fun validate(): ValidationResultDto {
        if (userId.isBlank()) {
            return ValidationResultDto(false, "userId is required")
        }
        validateName(name)?.let { return it }
        validatePerformedAt(performedAt)?.let { return it }
        validateDurationSeconds(durationSeconds)?.let { return it }
        return validateExercises(exercises)
    }
}

@Serializable
data class UpdateWorkoutRequestDto(
    val userId: String,
    val name: String? = null,
    val performedAt: String? = null,
    val durationSeconds: Int? = null,
    val notes: String? = null,
    val exerciseOps: WorkoutExerciseOpsDto? = null
) {
    fun validate(): ValidationResultDto {
        if (userId.isBlank()) {
            return ValidationResultDto(false, "userId is required")
        }
        name?.let { validateName(it)?.let { error -> return error } }
        validatePerformedAt(performedAt)?.let { return it }
        validateDurationSeconds(durationSeconds)?.let { return it }

        val ops = exerciseOps ?: return ValidationResultDto(true, null)
        if (ops.deleteIds.any { it.isBlank() }) {
            return ValidationResultDto(false, "exerciseOps.deleteIds must not contain blank ids")
        }
        return validateExercises(ops.upsert)
    }
}

@Serializable
data class WorkoutListResponseDto(
    val workouts: List<WorkoutDto>,
    val pagination: PaginationMetadataDto
)

@Serializable
data class WorkoutCategoryBreakdownDto(
    val lifting: Int = 0,
    val cardio: Int = 0,
    val mobility: Int = 0
)

@Serializable
data class WorkoutStatsResponseDto(
    val workoutCount: Int = 0,
    val totalDurationSeconds: Int = 0,
    val averageDurationSeconds: Int = 0,
    val totalVolumeKg: Double = 0.0,
    val totalSets: Int = 0,
    val totalCardioSeconds: Int = 0,
    val exerciseCount: Int = 0,
    val byCategory: WorkoutCategoryBreakdownDto = WorkoutCategoryBreakdownDto(),
    val daily: List<WorkoutDailyPointDto> = emptyList()
)

@Serializable
data class WorkoutDailyPointDto(
    val date: String,
    val workoutCount: Int,
    val durationSeconds: Int,
    val volumeKg: Double
)

private const val MAX_WORKOUT_NAME_LENGTH = 120
private const val MAX_WORKOUT_DURATION_SECONDS = 24 * 60 * 60

private fun validateName(name: String): ValidationResultDto? = when {
    name.isBlank() -> ValidationResultDto(false, "name is required")
    name.length > MAX_WORKOUT_NAME_LENGTH ->
        ValidationResultDto(false, "name must be at most $MAX_WORKOUT_NAME_LENGTH characters")
    else -> null
}

private fun validateDurationSeconds(durationSeconds: Int?): ValidationResultDto? {
    if (durationSeconds == null) return null
    return if (durationSeconds !in 0..MAX_WORKOUT_DURATION_SECONDS) {
        ValidationResultDto(false, "durationSeconds must be between 0 and $MAX_WORKOUT_DURATION_SECONDS")
    } else {
        null
    }
}

private fun validatePerformedAt(performedAt: String?): ValidationResultDto? {
    if (performedAt == null) return null
    return if (parseWorkoutTimestamp(performedAt) == null) {
        ValidationResultDto(false, "performedAt must be an ISO-8601 date or date-time")
    } else {
        null
    }
}

private fun validateExercises(exercises: List<UpsertWorkoutExerciseDto>): ValidationResultDto {
    exercises.forEach { exercise ->
        if (exercise.name.isBlank()) {
            return ValidationResultDto(false, "Each exercise requires a name")
        }
        if (WorkoutExerciseCategory.fromValue(exercise.category) == null) {
            val allowed = WorkoutExerciseCategory.entries.joinToString(", ") { it.value }
            return ValidationResultDto(false, "Invalid exercise category '${exercise.category}'. Allowed: $allowed")
        }
        if (exercise.position != null && exercise.position < 0) {
            return ValidationResultDto(false, "Exercise position must be non-negative")
        }
        if (exercise.sets != null && exercise.sets !in 0..100) {
            return ValidationResultDto(false, "Exercise sets must be between 0 and 100")
        }
        if (exercise.reps != null && exercise.reps !in 0..1000) {
            return ValidationResultDto(false, "Exercise reps must be between 0 and 1000")
        }
        if (exercise.weightKg != null && (exercise.weightKg < 0 || exercise.weightKg > 2000)) {
            return ValidationResultDto(false, "Exercise weightKg must be between 0 and 2000")
        }
        if (exercise.durationSeconds != null && exercise.durationSeconds !in 0..MAX_WORKOUT_DURATION_SECONDS) {
            return ValidationResultDto(
                false,
                "Exercise durationSeconds must be between 0 and $MAX_WORKOUT_DURATION_SECONDS"
            )
        }
    }
    return ValidationResultDto(true, null)
}

/**
 * Accepts either a bare `YYYY-MM-DD` day or a full ISO-8601 offset date-time and normalizes both to
 * an instant so clients can post whichever they hold.
 */
fun parseWorkoutTimestamp(value: String): OffsetDateTime? {
    val trimmed = value.trim()
    if (trimmed.isEmpty()) return null

    runCatching { OffsetDateTime.parse(trimmed) }.getOrNull()?.let { return it }
    return runCatching { LocalDate.parse(trimmed).atStartOfDay().atOffset(ZoneOffset.UTC) }.getOrNull()
}
