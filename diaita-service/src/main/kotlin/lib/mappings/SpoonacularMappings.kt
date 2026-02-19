package com.diaita.lib.mappings

import com.diaita.dto.IngredientInformationDto
import com.diaita.dto.FoodDto
import com.diaita.dto.MenuItemInformationDto
import com.diaita.dto.ProductInformationDto
import com.diaita.dto.SpoonacularNutrientDto

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

fun MenuItemInformationDto.toFoodDto(): FoodDto {
    val nutrients = nutrition?.nutrients ?: emptyList()

    return FoodDto(
        id = id.toString(),
        name = title,
        brand = restaurantChain,
        category = "menuItem",
        servingSize = null,
        servingUnit = null,
        caloriesPerServingSize = nutrients.valueOf("Calories"),
        proteinGPerServingSize = nutrients.valueOf("Protein"),
        carbGPerServingSize = nutrients.valueOf("Carbohydrates", "Carbs"),
        fatGPerServingSize = nutrients.valueOf("Fat"),
        createdAt = null
    )
}
