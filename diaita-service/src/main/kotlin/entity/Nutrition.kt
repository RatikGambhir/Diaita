package com.diaita.entity

import kotlinx.serialization.Serializable
import java.time.Instant

enum class MealTypeEntity {
    BREAKFAST,
    LUNCH,
    DINNER,
    SNACK
}

@Serializable
data class MealEntity(
    val id: String,
    val userId: String,
    val mealItemId: List<String>? = null,
    val mealType: String? = null,
    val notes: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

@Serializable
data class FoodEntity(
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
    val createdAt: String? = null,

)

@Serializable
data class MealItemEntity(
    val id: String,
    val mealId: String,
    val foodId: String? = null,
    val customName: String? = null,
    val quantity: Double,
    val calories: Double,
    val proteinG: Double,
    val carbG: Double,
    val fatG: Double,
    val consumedAt: String,
)
