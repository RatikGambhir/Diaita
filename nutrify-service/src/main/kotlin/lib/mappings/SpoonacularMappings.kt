package com.nutrify.lib.mappings

import com.nutrify.dto.IngredientInformationDto
import com.nutrify.dto.FoodDto
import com.nutrify.dto.ProductInformationDto
import com.nutrify.dto.SpoonacularNutrientDto

private fun List<SpoonacularNutrientDto>.valueOf(vararg nutrientNames: String): Double? {
    return firstOrNull { nutrient ->
        nutrientNames.any { name -> nutrient.name.equals(name, ignoreCase = true) }
    }?.amount
}

fun IngredientInformationDto.toFoodDto(): FoodDto {
    val nutrients = nutrition?.nutrients ?: emptyList()

    return FoodDto(
        id = id.toString(),
        name = name,
        brand = null,
        category = "ingredient",
        servingSize = amount ?: 100.0,
        servingUnit = unit ?: "grams",
        caloriesPerServingSize = nutrients.valueOf("Calories"),
        proteinGPerServingSize = nutrients.valueOf("Protein"),
        carbGPerServingSize = nutrients.valueOf("Carbohydrates", "Carbs"),
        fatGPerServingSize = nutrients.valueOf("Fat"),
        createdAt = null
    )
}

fun ProductInformationDto.toFoodDto(): FoodDto {
    val nutrients = nutrition?.nutrients ?: emptyList()

    return FoodDto(
        id = id.toString(),
        name = title,
        brand = brand ?: brands,
        category = "product",
        servingSize = servingSize,
        servingUnit = servingUnit,
        caloriesPerServingSize = nutrients.valueOf("Calories"),
        proteinGPerServingSize = nutrients.valueOf("Protein"),
        carbGPerServingSize = nutrients.valueOf("Carbohydrates", "Carbs"),
        fatGPerServingSize = nutrients.valueOf("Fat"),
        createdAt = null
    )
}
