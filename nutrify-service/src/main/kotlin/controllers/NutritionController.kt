package com.nutrify.controllers

import com.nutrify.dto.FoodSearchResponseDto
import com.nutrify.dto.IngredientSearchFiltersDto
import com.nutrify.dto.ProductSearchFiltersDto
import com.nutrify.dto.FoodDto
import com.nutrify.service.NutritionService

class NutritionController(private val nutritionService: NutritionService) {

    suspend fun searchIngredients(filters: IngredientSearchFiltersDto): FoodSearchResponseDto? {
        return nutritionService.searchIngredients(filters)
    }

    suspend fun searchProducts(filters: ProductSearchFiltersDto): FoodSearchResponseDto? {
        return nutritionService.searchProducts(filters)
    }

    suspend fun getIngredientById(id: Int): FoodDto? {
        return nutritionService.getIngredientById(id)
    }

    suspend fun getProductById(id: Int): FoodDto? {
        return nutritionService.getProductById(id)
    }
}
