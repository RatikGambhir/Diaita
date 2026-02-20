package com.diaita.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IngredientSearchFiltersDto(
    val query: String,
    val minProteinPercent: Double? = null,
    val maxProteinPercent: Double? = null,
    val minFatPercent: Double? = null,
    val maxFatPercent: Double? = null,
    val minCarbsPercent: Double? = null,
    val maxCarbsPercent: Double? = null,
    val intolerances: List<String>? = null,
    val offset: Int = 0,
    val number: Int = 10
)

@Serializable
data class ProductSearchFiltersDto(
    val query: String,
    val minCalories: Double? = null,
    val maxCalories: Double? = null,
    val minCarbs: Double? = null,
    val maxCarbs: Double? = null,
    val minProtein: Double? = null,
    val maxProtein: Double? = null,
    val minFat: Double? = null,
    val maxFat: Double? = null,
    val offset: Int = 0,
    val number: Int = 10
)

@Serializable
data class MenuItemSearchFiltersDto(
    val query: String,
    val offset: Int = 0,
    val number: Int = 10
)

@Serializable
data class FoodSearchResponseDto(
    val foods: List<FoodDto>,
    val totalResults: Int,
    val offset: Int,
    val number: Int
)

@Serializable
data class IngredientSearchResponseDto(
    val results: List<IngredientSearchResultDto> = emptyList(),
    val offset: Int = 0,
    val number: Int = 0,
    val totalResults: Int = 0
)

@Serializable
data class IngredientSearchResultDto(
    val id: Int,
    val name: String,
    val image: String? = null
)

@Serializable
data class ProductSearchResponseDto(
    @SerialName("products")
    val products: List<ProductSearchResultDto> = emptyList(),
    val offset: Int = 0,
    val number: Int = 0,
    @SerialName("totalProducts")
    val totalProducts: Int = 0
)

@Serializable
data class ProductSearchResultDto(
    val id: Int,
    val title: String,
    val image: String? = null,
    val brand: String? = null
)

@Serializable
data class MenuItemSearchResponseDto(
    val menuItems: List<MenuItemSearchResultDto> = emptyList(),
    val totalMenuItems: Int = 0,
    val offset: Int = 0,
    val number: Int = 0
)

@Serializable
data class MenuItemSearchResultDto(
    val id: Int,
    val title: String,
    val restaurantChain: String? = null,
    val image: String? = null
)

@Serializable
data class IngredientInformationDto(
    val id: Int,
    val name: String,
    val amount: Double? = null,
    val unit: String? = null,
    val nutrition: SpoonacularNutritionDto? = null
)

@Serializable
data class ProductInformationDto(
    val id: Int,
    val title: String,
    val brand: String? = null,
    val brands: String? = null,
    @SerialName("serving_size")
    val servingSize: Double? = null,
    @SerialName("serving_unit")
    val servingUnit: String? = null,
    val nutrition: SpoonacularNutritionDto? = null
)

@Serializable
data class MenuItemInformationDto(
    val id: Int,
    val title: String,
    val restaurantChain: String? = null,
    val nutrition: SpoonacularNutritionDto? = null
)

@Serializable
data class SpoonacularNutritionDto(
    val nutrients: List<SpoonacularNutrientDto> = emptyList()
)

@Serializable
data class SpoonacularNutrientDto(
    val name: String,
    val amount: Double? = null,
    val unit: String? = null
)
