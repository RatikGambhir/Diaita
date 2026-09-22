package com.diaita.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExerciseSearchRowEntity(
    val id: Int? = null,
    val exercise: String,
    @SerialName("exercise_type")
    val exerciseType: String? = null,
    @SerialName("exercise_variation")
    val exerciseVariation: String? = null,
    @SerialName("primary_fitness_focus")
    val primaryFitnessFocus: String? = null,
    @SerialName("secondary_fitness_focus")
    val secondaryFitnessFocus: String? = null,
    val equipment: String? = null,
    val mechanics: String? = null,
    val utility: String? = null,
    val force: String? = null,
    @SerialName("total_count")
    val totalCount: Int = 0
) {
    fun toExerciseEntity(): ExerciseEntity = ExerciseEntity(
        id = id,
        exercise = exercise,
        exerciseType = exerciseType,
        exerciseVariation = exerciseVariation,
        primaryFitnessFocus = primaryFitnessFocus,
        secondaryFitnessFocus = secondaryFitnessFocus,
        equipment = equipment,
        mechanics = mechanics,
        utility = utility,
        force = force
    )
}
