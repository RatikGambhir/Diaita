package com.diaita.lib.mappings

import com.diaita.dto.FoodAutocompleteSuggestionDto
import com.diaita.dto.IngredientInformationDto
import com.diaita.dto.IngredientAutocompleteItemDto
import com.diaita.dto.FoodDto
import com.diaita.dto.MenuItemInformationDto
import com.diaita.dto.ProductInformationDto
import com.diaita.dto.ProductSuggestItemDto
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
        servingSize = amount ?: 1.00,
        servingUnit = unit ?: "not found",
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

fun IngredientAutocompleteItemDto.toFoodAutocompleteSuggestionDto(): FoodAutocompleteSuggestionDto {
    return FoodAutocompleteSuggestionDto(
        id = id.toString(),
        name = name,
        category = "ingredient",
        image = image,
        brand = null,
        aisle = aisle,
        possibleUnits = possibleUnits
    )
}

fun ProductSuggestItemDto.toFoodAutocompleteSuggestionDto(): FoodAutocompleteSuggestionDto {
    return FoodAutocompleteSuggestionDto(
        id = id.toString(),
        name = title,
        category = "product",
        image = null,
        brand = null,
        aisle = null,
        possibleUnits = emptyList()
    )
}
