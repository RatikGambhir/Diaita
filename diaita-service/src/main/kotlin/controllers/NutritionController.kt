package com.diaita.controllers

import com.diaita.dto.FoodSearchResponseDto
import com.diaita.dto.IngredientSearchFiltersDto
import com.diaita.dto.ProductSearchFiltersDto
import com.diaita.dto.FoodDto
import com.diaita.service.NutritionService

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
