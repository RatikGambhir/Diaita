package com.diaita

import com.diaita.lib.clients.NutritionRestClient
import com.diaita.lib.clients.RestClient
import io.ktor.server.application.Application

fun Application.configureRestClient(): List<RestClient> {
    val nutritionAPIKey = System.getenv("SPOONACULAR_API_KEY")
        ?.trim()
        ?.takeIf(String::isNotEmpty)
        ?: environment.config.propertyOrNull("foodapi.apiKey")?.getString().orEmpty()
    val nutritionAPIUrl = environment.config.property("foodapi.url").getString()
    return listOf(NutritionRestClient(nutritionAPIKey, nutritionAPIUrl))
}
