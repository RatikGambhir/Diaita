package com.nutrify.lib.clients

import com.nutrify.dto.IngredientInformationDto
import com.nutrify.dto.IngredientSearchFiltersDto
import com.nutrify.dto.IngredientSearchResponseDto
import com.nutrify.dto.ProductInformationDto
import com.nutrify.dto.ProductSearchFiltersDto
import com.nutrify.dto.ProductSearchResponseDto
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

open class NutritionRestClient(
    private val apiKey: String,
    url: String
) : RestClient(apiKey, url) {

    override fun getFood(): String {
        return searchRecipes("hello")
    }

    open suspend fun searchIngredients(
        query: String,
        filters: IngredientSearchFiltersDto
    ): IngredientSearchResponseDto? {
        return try {
            client.get("/food/ingredients/search") {
                parameter("apiKey", apiKey)
                parameter("query", query)
                filters.minProteinPercent?.let { parameter("minProteinPercent", it) }
                filters.maxProteinPercent?.let { parameter("maxProteinPercent", it) }
                filters.minFatPercent?.let { parameter("minFatPercent", it) }
                filters.maxFatPercent?.let { parameter("maxFatPercent", it) }
                filters.minCarbsPercent?.let { parameter("minCarbsPercent", it) }
                filters.maxCarbsPercent?.let { parameter("maxCarbsPercent", it) }
                filters.intolerances?.takeIf { it.isNotEmpty() }
                    ?.let { parameter("intolerances", it.joinToString(",")) }
                parameter("offset", filters.offset)
                parameter("number", filters.number)
            }.body<IngredientSearchResponseDto>()
        } catch (e: Exception) {
            println("Error searching ingredients: ${e.message}")
            null
        }
    }

    open suspend fun getIngredientInformation(
        id: Int,
        amount: Double? = 100.0,
        unit: String? = "grams"
    ): IngredientInformationDto? {
        return try {
            client.get("/food/ingredients/$id/information") {
                parameter("apiKey", apiKey)
                amount?.let { parameter("amount", it) }
                unit?.let { parameter("unit", it) }
            }.body<IngredientInformationDto>()
        } catch (e: Exception) {
            println("Error getting ingredient information: ${e.message}")
            null
        }
    }

    open suspend fun searchProducts(
        query: String,
        filters: ProductSearchFiltersDto
    ): ProductSearchResponseDto? {
        return try {
            client.get("/food/products/search") {
                parameter("apiKey", apiKey)
                parameter("query", query)
                filters.minCalories?.let { parameter("minCalories", it) }
                filters.maxCalories?.let { parameter("maxCalories", it) }
                filters.minCarbs?.let { parameter("minCarbs", it) }
                filters.maxCarbs?.let { parameter("maxCarbs", it) }
                filters.minProtein?.let { parameter("minProtein", it) }
                filters.maxProtein?.let { parameter("maxProtein", it) }
                filters.minFat?.let { parameter("minFat", it) }
                filters.maxFat?.let { parameter("maxFat", it) }
                parameter("offset", filters.offset)
                parameter("number", filters.number)
            }.body<ProductSearchResponseDto>()
        } catch (e: Exception) {
            println("Error searching products: ${e.message}")
            null
        }
    }

    open suspend fun getProductInformation(id: Int): ProductInformationDto? {
        return try {
            client.get("/food/products/$id") {
                parameter("apiKey", apiKey)
            }.body<ProductInformationDto>()
        } catch (e: Exception) {
            println("Error getting product information: ${e.message}")
            null
        }
    }
}
