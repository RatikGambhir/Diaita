package com.nutrify.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExerciseEntity(
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
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null
)
