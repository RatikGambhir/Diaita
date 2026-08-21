package com.diaita.lib.mappings

import com.diaita.dto.UpsertWorkoutExerciseDto
import com.diaita.dto.WorkoutCategoryBreakdownDto
import com.diaita.dto.WorkoutDto
import com.diaita.dto.WorkoutExerciseCategory
import com.diaita.dto.WorkoutExerciseLogDto
import com.diaita.dto.WorkoutTotalsDto
import com.diaita.entity.WorkoutExerciseRowEntity
import com.diaita.entity.WorkoutExerciseWriteRowEntity
import com.diaita.entity.WorkoutRowEntity

fun WorkoutExerciseRowEntity.toDto() = WorkoutExerciseLogDto(
    id = id,
    exerciseId = exerciseId,
    name = name,
    category = category,
    position = position,
    sets = sets,
    reps = reps,
    weightKg = weightKg,
    durationSeconds = durationSeconds,
    intensity = intensity,
    target = target,
    notes = notes
)

fun UpsertWorkoutExerciseDto.toWriteRowEntity(
    id: String,
    workoutId: String,
    userId: String,
    position: Int
) = WorkoutExerciseWriteRowEntity(
    id = id,
    workoutId = workoutId,
    userId = userId,
    exerciseId = exerciseId,
    name = name.trim(),
    category = (WorkoutExerciseCategory.fromValue(category) ?: WorkoutExerciseCategory.LIFTING).value,
    position = position,
    sets = sets,
    reps = reps,
    weightKg = weightKg,
    durationSeconds = durationSeconds,
    intensity = intensity?.trim()?.takeIf { it.isNotEmpty() },
    target = target?.trim()?.takeIf { it.isNotEmpty() },
    notes = notes?.trim()?.takeIf { it.isNotEmpty() }
)

fun WorkoutRowEntity.toDto(exercises: List<WorkoutExerciseRowEntity>): WorkoutDto {
    val ordered = exercises.sortedBy { it.position }

    return WorkoutDto(
        id = id,
        userId = userId,
        name = name,
        performedAt = performedAt,
        durationSeconds = durationSeconds,
        notes = notes,
        exercises = ordered.map { it.toDto() },
        totals = ordered.toTotals()
    )
}

/**
 * Volume is only meaningful for lifting work, so cardio and mobility rows contribute to their counts
 * and to cardio seconds but never to `totalVolumeKg`.
 */
fun List<WorkoutExerciseRowEntity>.toTotals(): WorkoutTotalsDto {
    var liftingCount = 0
    var cardioCount = 0
    var mobilityCount = 0
    var totalSets = 0
    var totalVolumeKg = 0.0
    var totalCardioSeconds = 0

    forEach { exercise ->
        when (WorkoutExerciseCategory.fromValue(exercise.category)) {
            WorkoutExerciseCategory.LIFTING -> {
                liftingCount += 1
                val sets = exercise.sets ?: 0
                totalSets += sets
                totalVolumeKg += sets * (exercise.reps ?: 0) * (exercise.weightKg ?: 0.0)
            }
            WorkoutExerciseCategory.CARDIO -> {
                cardioCount += 1
                totalCardioSeconds += exercise.durationSeconds ?: 0
            }
            WorkoutExerciseCategory.MOBILITY -> mobilityCount += 1
            null -> Unit
        }
    }

    return WorkoutTotalsDto(
        exerciseCount = size,
        liftingCount = liftingCount,
        cardioCount = cardioCount,
        mobilityCount = mobilityCount,
        totalSets = totalSets,
        totalVolumeKg = roundVolume(totalVolumeKg),
        totalCardioSeconds = totalCardioSeconds
    )
}

fun List<WorkoutTotalsDto>.toCategoryBreakdown() = WorkoutCategoryBreakdownDto(
    lifting = sumOf { it.liftingCount },
    cardio = sumOf { it.cardioCount },
    mobility = sumOf { it.mobilityCount }
)

fun roundVolume(value: Double): Double = Math.round(value * 100.0) / 100.0
