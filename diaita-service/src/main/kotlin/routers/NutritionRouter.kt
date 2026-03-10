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
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing

private suspend inline fun <reified T : Any> ApplicationCall.handleSearchRequest(
    searchFailureMessage: String,
    querySelector: (T) -> String,
    search: suspend (T) -> Any?
) {
    val filters = try {
        receive<T>()
    } catch (e: Exception) {
        respondText("Invalid request payload", status = HttpStatusCode.BadRequest)
        return
    }

    if (querySelector(filters).isBlank()) {
        respondText("Invalid request: 'query' field is required", status = HttpStatusCode.BadRequest)
        return
    }

    val result = search(filters)
    if (result != null) {
        respond(HttpStatusCode.OK, result)
    } else {
        respondText(searchFailureMessage, status = HttpStatusCode.InternalServerError)
    }
}

private suspend fun ApplicationCall.requiredQueryParam(name: String): String? {
    val value = request.queryParameters[name]?.trim()
    if (value.isNullOrEmpty()) {
        respondText("Invalid request: '$name' query parameter is required", status = HttpStatusCode.BadRequest)
        return null
    }
    return value
}

private suspend fun ApplicationCall.intQueryParam(
    name: String,
    defaultValue: Int,
    validRange: IntRange
): Int? {
    val rawValue = request.queryParameters[name] ?: return defaultValue
    val parsedValue = rawValue.toIntOrNull()

    if (parsedValue == null || parsedValue !in validRange) {
        respondText(
            "Invalid request: '$name' must be between ${validRange.first} and ${validRange.last}",
            status = HttpStatusCode.BadRequest
        )
        return null
    }

    return parsedValue
}

private suspend fun ApplicationCall.booleanQueryParam(
    name: String,
    defaultValue: Boolean
): Boolean? {
    val rawValue = request.queryParameters[name] ?: return defaultValue

    return when (rawValue.lowercase()) {
        "true" -> true
        "false" -> false
        else -> {
            respondText(
                "Invalid request: '$name' must be 'true' or 'false'",
                status = HttpStatusCode.BadRequest
            )
            null
        }
    }
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

    val food = getById(id)
    if (food != null) {
        respond(HttpStatusCode.OK, food)
    } else {
        respondText(notFoundMessage, status = HttpStatusCode.NotFound)
    }
}

fun Application.configureNutritionRoutes(nutritionController: NutritionController) {
    routing {
        post("/nutrition/meals/upsert") {
            val request = try {
                call.receive<UpsertMealsRequestDto>()
            } catch (e: Exception) {
                call.respondText("Invalid request payload", status = HttpStatusCode.BadRequest)
                return@post
            }

            val validation = request.validate()
            if (!validation.isValid) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ValidationErrorsResponseDto(validation.errors)
                )
                return@post
            }

            val result = nutritionController.upsertMeals(request)
            if (result != null) {
                call.respond(HttpStatusCode.OK, result)
            } else {
                call.respondText("Failed to save meals", status = HttpStatusCode.InternalServerError)
            }
        }

        get("/nutrition/autocomplete/ingredients") {
            val query = call.requiredQueryParam("query") ?: return@get
            val number = call.intQueryParam("number", defaultValue = 10, validRange = 1..100) ?: return@get
            val language = call.request.queryParameters["language"]?.trim().orEmpty().ifEmpty { "en" }
            val metaInformation = call.booleanQueryParam("metaInformation", defaultValue = false) ?: return@get
            val intolerances = call.request.queryParameters["intolerances"]
                ?.split(",")
                ?.map(String::trim)
                ?.filter(String::isNotEmpty)
                ?.takeIf { it.isNotEmpty() }

            if (language !in setOf("en", "de")) {
                call.respondText(
                    "Invalid request: 'language' must be 'en' or 'de'",
                    status = HttpStatusCode.BadRequest
                )
                return@get
            }

            val result = nutritionController.autocompleteIngredients(
                IngredientAutocompleteFiltersDto(
                    query = query,
                    number = number,
                    language = language,
                    metaInformation = metaInformation,
                    intolerances = intolerances
                )
            )

            if (result != null) {
                call.respond(HttpStatusCode.OK, result)
            } else {
                call.respondText("Failed to autocomplete ingredients", status = HttpStatusCode.InternalServerError)
            }
        }

        get("/nutrition/autocomplete/products") {
            val query = call.requiredQueryParam("query") ?: return@get
            val number = call.intQueryParam("number", defaultValue = 10, validRange = 1..25) ?: return@get

            val result = nutritionController.autocompleteProducts(
                ProductSuggestFiltersDto(
                    query = query,
                    number = number
                )
            )

            if (result != null) {
                call.respond(HttpStatusCode.OK, result)
            } else {
                call.respondText("Failed to autocomplete products", status = HttpStatusCode.InternalServerError)
            }
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
