package com.diaita.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MealRowEntity(
    val id: String,
    @SerialName("user_id")
    val userId: String,
    @SerialName("meal_type")
    val mealType: String,
    @SerialName("eaten_at")
    val eatenAt: String,
    val notes: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null
)

@Serializable
data class MealItemRowEntity(
    val id: String,
    @SerialName("meal_id")
    val mealId: String,
    @SerialName("user_id")
    val userId: String,
    @SerialName("item_type")
    val itemType: String,
    @SerialName("item_id")
    val itemId: String? = null,
    @SerialName("item_name")
    val itemName: String,
    @SerialName("brand_name")
    val brandName: String? = null,
    val quantity: Double,
    val unit: String? = null,
    val calories: Double,
    @SerialName("protein_g")
    val proteinG: Double,
    @SerialName("carbs_g")
    val carbsG: Double,
    @SerialName("fat_g")
    val fatG: Double,
    val position: Int,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
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
    val createdAt: String? = null
)
