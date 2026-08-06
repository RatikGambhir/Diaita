package com.diaita.routers

import com.diaita.controllers.NutritionController
import com.diaita.dto.IngredientAutocompleteFiltersDto
import com.diaita.dto.IngredientSearchFiltersDto
import com.diaita.dto.MenuItemSearchFiltersDto
import com.diaita.dto.ProductSuggestFiltersDto
import com.diaita.dto.ProductSearchFiltersDto
import com.diaita.dto.UpsertMealsRequestDto
import com.diaita.dto.ValidationErrorsResponseDto
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import java.time.LocalDate
import java.util.UUID

private val SUPPORTED_AUTOCOMPLETE_LANGUAGES = setOf("en", "de")

fun Application.configureNutritionRoutes(nutritionController: NutritionController) {
    routing {
        get("/nutrition/day-summary") {
            val userId = call.requiredQueryParam("userId") ?: return@get
            val date = call.requiredQueryParam("date") ?: return@get

            if (!isValidUuid(userId)) {
                call.respondText(
                    "Invalid request: 'userId' must be a valid UUID",
                    status = HttpStatusCode.BadRequest
                )
                return@get
            }

            if (!isValidIsoDate(date)) {
                call.respondText(
                    "Invalid request: 'date' must be in YYYY-MM-DD format",
                    status = HttpStatusCode.BadRequest
                )
                return@get
            }

            call.respondOrFail(
                nutritionController.getNutritionDaySummary(userId, date),
                "Failed to fetch nutrition day summary"
            )
        }

        post("/nutrition/meals/upsert") {
            val request = call.receiveOrBadRequest<UpsertMealsRequestDto>() ?: return@post

            val validation = request.validate()
            if (!validation.isValid) {
                call.respond(HttpStatusCode.BadRequest, ValidationErrorsResponseDto(validation.errors))
                return@post
            }

            call.respondOrFail(nutritionController.upsertMeals(request), "Failed to save meals")
        }

        get("/nutrition/autocomplete/ingredients") {
            val query = call.requiredQueryParam("query") ?: return@get
            val number = call.intQueryParam("number", defaultValue = 10, validRange = 1..100) ?: return@get
            val metaInformation = call.booleanQueryParam("metaInformation", defaultValue = false) ?: return@get
            val language = call.request.queryParameters["language"]?.trim().orEmpty().ifEmpty { "en" }

            if (language !in SUPPORTED_AUTOCOMPLETE_LANGUAGES) {
                call.respondText(
                    "Invalid request: 'language' must be 'en' or 'de'",
                    status = HttpStatusCode.BadRequest
                )
                return@get
            }

            call.respondOrFail(
                nutritionController.autocompleteIngredients(
                    IngredientAutocompleteFiltersDto(
                        query = query,
                        number = number,
                        language = language,
                        metaInformation = metaInformation,
                        intolerances = call.csvQueryParam("intolerances")
                    )
                ),
                "Failed to autocomplete ingredients"
            )
        }

        get("/nutrition/autocomplete/products") {
            val query = call.requiredQueryParam("query") ?: return@get
            val number = call.intQueryParam("number", defaultValue = 10, validRange = 1..25) ?: return@get

            call.respondOrFail(
                nutritionController.autocompleteProducts(
                    ProductSuggestFiltersDto(query = query, number = number)
                ),
                "Failed to autocomplete products"
            )
        }

        post("/nutrition/search/ingredients") {
            call.handleSearchRequest<IngredientSearchFiltersDto>(
                searchFailureMessage = "Failed to search ingredients",
                querySelector = { it.query },
                search = { nutritionController.searchIngredients(it) }
            )
        }

        post("/nutrition/search/products") {
            call.handleSearchRequest<ProductSearchFiltersDto>(
                searchFailureMessage = "Failed to search products",
                querySelector = { it.query },
                search = { nutritionController.searchProducts(it) }
            )
        }

        post("/nutrition/search/menuItems") {
            call.handleSearchRequest<MenuItemSearchFiltersDto>(
                searchFailureMessage = "Failed to search menu items",
                querySelector = { it.query },
                search = { nutritionController.searchMenuItems(it) }
            )
        }

        get("/nutrition/ingredient/{id}") {
            call.handleGetByIdRequest(
                invalidIdMessage = "Invalid ingredient id",
                notFoundMessage = "Ingredient not found",
                getById = { nutritionController.getIngredientById(it) }
            )
        }

        get("/nutrition/product/{id}") {
            call.handleGetByIdRequest(
                invalidIdMessage = "Invalid product id",
                notFoundMessage = "Product not found",
                getById = { nutritionController.getProductById(it) }
            )
        }

        get("/nutrition/menuItem/{id}") {
            call.handleGetByIdRequest(
                invalidIdMessage = "Invalid menu item id",
                notFoundMessage = "Menu item not found",
                getById = { nutritionController.getMenuItemById(it) }
            )
        }
    }
}

/** Receives a filter body, rejects a blank `query`, then runs [search]. */
private suspend inline fun <reified T : Any> ApplicationCall.handleSearchRequest(
    searchFailureMessage: String,
    querySelector: (T) -> String,
    search: suspend (T) -> Any?
) {
    val filters = receiveOrBadRequest<T>() ?: return

    if (querySelector(filters).isBlank()) {
        respondText("Invalid request: 'query' field is required", status = HttpStatusCode.BadRequest)
        return
    }

    respondOrFail(search(filters), searchFailureMessage)
}

private suspend fun ApplicationCall.handleGetByIdRequest(
    invalidIdMessage: String,
    notFoundMessage: String,
    getById: suspend (Int) -> Any?
) {
    val id = parameters["id"]?.toIntOrNull()
    if (id == null) {
        respondText(invalidIdMessage, status = HttpStatusCode.BadRequest)
        return
    }

    respondOrFail(getById(id), notFoundMessage, HttpStatusCode.NotFound)
}

private fun isValidUuid(value: String): Boolean =
    runCatching { UUID.fromString(value.trim()) }.isSuccess

private fun isValidIsoDate(value: String): Boolean =
    runCatching { LocalDate.parse(value.trim()) }.isSuccess
