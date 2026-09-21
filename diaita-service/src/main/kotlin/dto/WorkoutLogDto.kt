package com.diaita.dto

import java.time.Instant
import kotlinx.serialization.Serializable

@Serializable
data class UpsertWorkoutRequestDto(
    val name: String,
    val performedAt: String,
    val durationMinutes: Int = 0,
    val notes: String? = null,
    val exercises: List<UpsertWorkoutExerciseDto> = emptyList()
) {
    fun validationError(): String? = when {
        name.isBlank() -> "Workout name is required"
        name.trim().length > 120 -> "Workout name must be at most 120 characters"
        runCatching { Instant.parse(performedAt.trim()) }.isFailure -> "performedAt must be an ISO-8601 timestamp"
        durationMinutes !in 0..1_440 -> "Duration must be between 0 and 1440 minutes"
        notes != null && notes.length > 2_000 -> "Notes must be at most 2000 characters"
        exercises.size > 100 -> "A workout can contain at most 100 exercises"
        else -> exercises.mapIndexedNotNull { index, exercise ->
            exercise.validationError()?.let { "exercises[$index]: $it" }
        }.firstOrNull()
    }
}

@Serializable
data class UpsertWorkoutExerciseDto(
    val id: String? = null,
    val exerciseId: Int? = null,
    val exerciseName: String,
    val category: String = "strength",
    val sets: Int = 0,
    val reps: Int? = null,
    val weightKg: Double? = null,
    val durationMinutes: Int? = null,
    val distanceKm: Double? = null,
    val notes: String? = null
) {
    fun validationError(): String? = when {
        exerciseName.isBlank() -> "Exercise name is required"
        exerciseName.trim().length > 120 -> "Exercise name must be at most 120 characters"
        sets !in 0..100 -> "Sets must be between 0 and 100"
        reps != null && reps !in 0..10_000 -> "Reps must be between 0 and 10000"
        weightKg != null && weightKg !in 0.0..5_000.0 -> "Weight must be between 0 and 5000 kg"
        durationMinutes != null && durationMinutes !in 0..1_440 -> "Exercise duration must be between 0 and 1440 minutes"
        distanceKm != null && distanceKm !in 0.0..10_000.0 -> "Distance must be between 0 and 10000 km"
        else -> null
    }
}

@Serializable
data class WorkoutExerciseLogDto(
    val id: String,
    val exerciseId: Int? = null,
    val exerciseName: String,
    val category: String,
    val sets: Int,
    val reps: Int? = null,
    val weightKg: Double? = null,
    val durationMinutes: Int? = null,
    val distanceKm: Double? = null,
    val notes: String? = null
)

@Serializable
data class WorkoutLogDto(
    val id: String,
    val name: String,
    val performedAt: String,
    val durationMinutes: Int,
    val notes: String? = null,
    val exercises: List<WorkoutExerciseLogDto>,
    val totalVolumeKg: Double
)

@Serializable
data class WorkoutStatsDto(
    val workoutsLast30Days: Int,
    val minutesLast30Days: Int,
    val totalVolumeKg: Double
)
