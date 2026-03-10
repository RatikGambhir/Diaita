package com.diaita.controllers

import com.diaita.dto.FoodAutocompleteResponseDto
import com.diaita.dto.FoodDto
import com.diaita.dto.IngredientAutocompleteFiltersDto
import com.diaita.dto.FoodSearchResponseDto
import com.diaita.dto.IngredientSearchFiltersDto
import com.diaita.dto.MenuItemSearchFiltersDto
import com.diaita.dto.NutritionDaySummaryResponseDto
import com.diaita.dto.ProductSuggestFiltersDto
import com.diaita.dto.ProductSearchFiltersDto
import com.diaita.dto.UpsertMealsRequestDto
import com.diaita.service.NutritionService

class NutritionController(private val nutritionService: NutritionService) {

    suspend fun upsertMeals(request: UpsertMealsRequestDto): NutritionDaySummaryResponseDto? {
        return nutritionService.upsertMeals(request)
    }

    suspend fun autocompleteIngredients(filters: IngredientAutocompleteFiltersDto): FoodAutocompleteResponseDto? {
        return nutritionService.autocompleteIngredients(filters)
    }

    suspend fun autocompleteProducts(filters: ProductSuggestFiltersDto): FoodAutocompleteResponseDto? {
        return nutritionService.autocompleteProducts(filters)
    }

    suspend fun searchIngredients(filters: IngredientSearchFiltersDto): FoodSearchResponseDto? {
        return nutritionService.searchIngredients(filters)
    }

    suspend fun searchProducts(filters: ProductSearchFiltersDto): FoodSearchResponseDto? {
        return nutritionService.searchProducts(filters)
    }

    suspend fun searchMenuItems(filters: MenuItemSearchFiltersDto): FoodSearchResponseDto? {
        return nutritionService.searchMenuItems(filters)
    }

    suspend fun getIngredientById(id: Int): FoodDto? {
        return nutritionService.getIngredientById(id)
    }

    suspend fun getProductById(id: Int): FoodDto? {
        return nutritionService.getProductById(id)
    }

    suspend fun getMenuItemById(id: Int): FoodDto? {
        return nutritionService.getMenuItemById(id)
    }
}
