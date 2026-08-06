package com.diaita.lib.clients

import com.diaita.dto.IngredientAutocompleteFiltersDto
import com.diaita.dto.IngredientAutocompleteItemDto
import com.diaita.dto.IngredientInformationDto
import com.diaita.dto.IngredientSearchFiltersDto
import com.diaita.dto.IngredientSearchResponseDto
import com.diaita.dto.MenuItemInformationDto
import com.diaita.dto.MenuItemSearchFiltersDto
import com.diaita.dto.MenuItemSearchResponseDto
import com.diaita.dto.ProductInformationDto
import com.diaita.dto.ProductSearchFiltersDto
import com.diaita.dto.ProductSearchResponseDto
import com.diaita.dto.ProductSuggestFiltersDto
import com.diaita.dto.ProductSuggestResponseDto
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.parameter

/** Client for the Spoonacular food API. */
open class NutritionRestClient(
    private val apiKey: String,
    url: String
) : RestClient(url) {

    open suspend fun searchIngredients(
        query: String,
        filters: IngredientSearchFiltersDto
    ): IngredientSearchResponseDto? = callOrNull("Ingredient search") {
        client.get("/food/ingredients/search") {
            authorizedQuery(query)
            optionalParameter("minProteinPercent", filters.minProteinPercent)
            optionalParameter("maxProteinPercent", filters.maxProteinPercent)
            optionalParameter("minFatPercent", filters.minFatPercent)
            optionalParameter("maxFatPercent", filters.maxFatPercent)
            optionalParameter("minCarbsPercent", filters.minCarbsPercent)
            optionalParameter("maxCarbsPercent", filters.maxCarbsPercent)
            csvParameter("intolerances", filters.intolerances)
            parameter("offset", filters.offset)
            parameter("number", filters.number)
        }.body<IngredientSearchResponseDto>()
    }

    open suspend fun autocompleteIngredients(
        query: String,
        filters: IngredientAutocompleteFiltersDto
    ): List<IngredientAutocompleteItemDto>? = callOrNull("Ingredient autocomplete") {
        client.get("/food/ingredients/autocomplete") {
            authorizedQuery(query)
            parameter("number", filters.number)
            parameter("language", filters.language)
            parameter("metaInformation", filters.metaInformation)
            csvParameter("intolerances", filters.intolerances)
        }.body<List<IngredientAutocompleteItemDto>>()
    }

    open suspend fun getIngredientInformation(
        id: Int,
        amount: Double? = 100.0,
        unit: String? = "grams"
    ): IngredientInformationDto? = callOrNull("Ingredient information lookup for id=$id") {
        client.get("/food/ingredients/$id/information") {
            parameter("apiKey", apiKey)
            optionalParameter("amount", amount)
            optionalParameter("unit", unit)
        }.body<IngredientInformationDto>()
    }

    open suspend fun searchProducts(
        query: String,
        filters: ProductSearchFiltersDto
    ): ProductSearchResponseDto? = callOrNull("Product search") {
        client.get("/food/products/search") {
            authorizedQuery(query)
            optionalParameter("minCalories", filters.minCalories)
            optionalParameter("maxCalories", filters.maxCalories)
            optionalParameter("minCarbs", filters.minCarbs)
            optionalParameter("maxCarbs", filters.maxCarbs)
            optionalParameter("minProtein", filters.minProtein)
            optionalParameter("maxProtein", filters.maxProtein)
            optionalParameter("minFat", filters.minFat)
            optionalParameter("maxFat", filters.maxFat)
            parameter("offset", filters.offset)
            parameter("number", filters.number)
        }.body<ProductSearchResponseDto>()
    }

    open suspend fun getProductInformation(id: Int): ProductInformationDto? =
        callOrNull("Product information lookup for id=$id") {
            client.get("/food/products/$id") {
                parameter("apiKey", apiKey)
            }.body<ProductInformationDto>()
        }

    open suspend fun suggestProducts(
        query: String,
        filters: ProductSuggestFiltersDto
    ): ProductSuggestResponseDto? = callOrNull("Product suggest") {
        client.get("/food/products/suggest") {
            authorizedQuery(query)
            parameter("number", filters.number)
        }.body<ProductSuggestResponseDto>()
    }

    open suspend fun searchMenuItems(
        query: String,
        filters: MenuItemSearchFiltersDto
    ): MenuItemSearchResponseDto? = callOrNull("Menu item search") {
        client.get("/food/menuItems/search") {
            authorizedQuery(query)
            parameter("offset", filters.offset)
            parameter("number", filters.number)
        }.body<MenuItemSearchResponseDto>()
    }

    open suspend fun getMenuItemInformation(id: Int): MenuItemInformationDto? =
        callOrNull("Menu item information lookup for id=$id") {
            client.get("/food/menuItems/$id") {
                parameter("apiKey", apiKey)
            }.body<MenuItemInformationDto>()
        }

    private fun HttpRequestBuilder.authorizedQuery(query: String) {
        parameter("apiKey", apiKey)
        parameter("query", query)
    }

    private fun HttpRequestBuilder.optionalParameter(name: String, value: Any?) {
        value?.let { parameter(name, it) }
    }

    private fun HttpRequestBuilder.csvParameter(name: String, values: List<String>?) {
        values?.takeIf { it.isNotEmpty() }?.let { parameter(name, it.joinToString(",")) }
    }
}
