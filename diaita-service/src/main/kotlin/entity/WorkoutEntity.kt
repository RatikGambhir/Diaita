package com.diaita.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WorkoutRowEntity(
    val id: String,
    @SerialName("user_id")
    val userId: String,
    val name: String,
    @SerialName("performed_at")
    val performedAt: String,
    @SerialName("duration_seconds")
    val durationSeconds: Int? = null,
    val notes: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null
)

/**
 * Write-side projection of [WorkoutRowEntity]. Database-managed columns are omitted so an insert or
 * upsert never overwrites them with explicit nulls.
 */
@Serializable
data class WorkoutWriteRowEntity(
    val id: String,
    @SerialName("user_id")
    val userId: String,
    val name: String,
    @SerialName("performed_at")
    val performedAt: String,
    @SerialName("duration_seconds")
    val durationSeconds: Int? = null,
    val notes: String? = null
)

@Serializable
data class WorkoutExerciseRowEntity(
    val id: String,
    @SerialName("workout_id")
    val workoutId: String,
    @SerialName("user_id")
    val userId: String,
    @SerialName("exercise_id")
    val exerciseId: Int? = null,
    val name: String,
    val category: String,
    val position: Int,
    val sets: Int? = null,
    val reps: Int? = null,
    @SerialName("weight_kg")
    val weightKg: Double? = null,
    @SerialName("duration_seconds")
    val durationSeconds: Int? = null,
    val intensity: String? = null,
    val target: String? = null,
    val notes: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null
)

/** Write-side projection of [WorkoutExerciseRowEntity]; see [WorkoutWriteRowEntity]. */
@Serializable
data class WorkoutExerciseWriteRowEntity(
    val id: String,
    @SerialName("workout_id")
    val workoutId: String,
    @SerialName("user_id")
    val userId: String,
    @SerialName("exercise_id")
    val exerciseId: Int? = null,
    val name: String,
    val category: String,
    val position: Int,
    val sets: Int? = null,
    val reps: Int? = null,
    @SerialName("weight_kg")
    val weightKg: Double? = null,
    @SerialName("duration_seconds")
    val durationSeconds: Int? = null,
    val intensity: String? = null,
    val target: String? = null,
    val notes: String? = null
)
