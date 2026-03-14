package com.diaita
import com.diaita.lib.clients.GeminiRestClient
import com.diaita.lib.clients.NutritionRestClient
import com.diaita.lib.clients.RestClient

import io.ktor.server.application.*



fun Application.configureRestClient(): List<RestClient> {
        val geminiKey = environment.config.property("api.gemini").getString()
        val nutritionAPIKey = environment.config.property("foodapi.apiKey").getString()
        val nutritionAPIUrl = environment.config.property("foodapi.url").getString()
        val geminiClient = GeminiRestClient(geminiKey)
        val nutritionAPIClient = NutritionRestClient(nutritionAPIKey, nutritionAPIUrl)
        return listOf(geminiClient, nutritionAPIClient)
}