package com.diaita.dto

import kotlinx.serialization.Serializable

@Serializable
data class FoodDto(
    val id: String,
    val name: String,
    val brand: String? = null,
    val category: String? = null,
    val servingSize: Double? = null,
    val servingUnit: String? = null,
    val caloriesPerServingSize: Double? = null,
    val proteinGPerServingSize: Double? = null,
    val carbGPerServingSize: Double? = null,
    val fatGPerServingSize: Double? = null,
    val createdAt: String? = null
)
