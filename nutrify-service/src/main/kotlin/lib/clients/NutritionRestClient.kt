package com.nutrify.lib.clients


class NutritionRestClient(
    apiKey: String,
    url: String
) : RestClient(apiKey, url)

{
    override fun getFood(): String {
        return searchRecipes("hello")
    }
}