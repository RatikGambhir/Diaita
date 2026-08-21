package com.diaita.service

import com.diaita.dto.FoodAutocompleteResponseDto
import com.diaita.dto.FoodSearchResponseDto
import com.diaita.dto.HistoricalMacroAveragesResponseDto
import com.diaita.dto.IngredientAutocompleteFiltersDto
import com.diaita.dto.IngredientSearchFiltersDto
import com.diaita.dto.MealBucketItemResponseDto
import com.diaita.dto.MealBucketResponseDto
import com.diaita.dto.MenuItemSearchFiltersDto
import com.diaita.dto.NutritionAnalyticsResponseDto
import com.diaita.dto.NutritionDaySummaryResponseDto
import com.diaita.dto.ProductSuggestFiltersDto
import com.diaita.dto.ProductSearchFiltersDto
import com.diaita.dto.FoodDto
import com.diaita.dto.UpsertMealsRequestDto
import com.diaita.entity.MealItemRowEntity
import com.diaita.entity.MealRowEntity
import com.diaita.lib.clients.NutritionRestClient
import com.diaita.lib.mappings.sortedByFoodName
import com.diaita.lib.mappings.toFoodAutocompleteSuggestionDto
import com.diaita.lib.mappings.toFoodDto
import com.diaita.lib.mappings.toNormalizedRequest
import com.diaita.dto.NutritionDailySeriesResponseDto
import com.diaita.dto.NutritionDailyTotalsDto
import com.diaita.dto.RecommendationDto
import com.diaita.repo.NutritionRepo
import com.diaita.repo.RecommendationRepo
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

class NutritionService(
    private val nutritionRepo: NutritionRepo,
    private val nutritionClient: NutritionRestClient,
    private val recommendationRepo: RecommendationRepo
) {
    enum class FoodSource {
        INGREDIENT,
        PRODUCT,
        MENU_ITEM
    }

    suspend fun getNutritionDaySummary(userId: String, date: String): NutritionDaySummaryResponseDto? {
        val normalizedUserId = userId.trim()
        val normalizedDate = date.trim()
        val targetDate = runCatching { LocalDate.parse(normalizedDate) }.getOrNull() ?: return null
        val meals = nutritionRepo.getMealsForUser(normalizedUserId)
        val mealItems = nutritionRepo.getMealItemsForUser(normalizedUserId)
        val itemsByMealId = mealItems.groupBy { it.mealId }
        val dayMeals = meals.filter { it.eatenAt.toUtcLocalDate() == targetDate }
        val dayMealContexts = dayMeals.map { MealContext(it, itemsByMealId[it.id].orEmpty()) }
        val historicalMealContexts = meals.map { MealContext(it, itemsByMealId[it.id].orEmpty()) }

        val summary = NutritionDaySummaryResponseDto(
            date = targetDate.toString(),
            totalCal = dayMealContexts.sumOf { context -> context.items.sumOf(MealItemRowEntity::calories) },
            totalProtein = dayMealContexts.sumOf { context -> context.items.sumOf(MealItemRowEntity::proteinG) },
            totalFat = dayMealContexts.sumOf { context -> context.items.sumOf(MealItemRowEntity::fatG) },
            totalCarb = dayMealContexts.sumOf { context -> context.items.sumOf(MealItemRowEntity::carbsG) },
            analytics = recommendedTargets(normalizedUserId).copy(
                historical = historicalDailyAverages(historicalMealContexts)
            ),
            breakfast = buildMealBucket(dayMealContexts, historicalMealContexts, "breakfast"),
            lunch = buildMealBucket(dayMealContexts, historicalMealContexts, "lunch"),
            dinner = buildMealBucket(dayMealContexts, historicalMealContexts, "dinner"),
            snacks = buildMealBucket(dayMealContexts, historicalMealContexts, "snack")
        )

        return summary.copy(
            breakfast = summary.breakfast.sortedByFoodName(),
            lunch = summary.lunch.sortedByFoodName(),
            dinner = summary.dinner.sortedByFoodName(),
            snacks = summary.snacks.sortedByFoodName()
        )
    }

    suspend fun upsertMeals(request: UpsertMealsRequestDto): NutritionDaySummaryResponseDto? {
        val normalizedRequest = request.toNormalizedRequest()
        val summary = nutritionRepo.upsertMeals(normalizedRequest) ?: return null

        return summary.copy(
            breakfast = summary.breakfast.sortedByFoodName(),
            lunch = summary.lunch.sortedByFoodName(),
            dinner = summary.dinner.sortedByFoodName(),
            snacks = summary.snacks.sortedByFoodName()
        )
    }

    suspend fun autocomplete(
        source: FoodSource,
        ingredientFilters: IngredientAutocompleteFiltersDto? = null,
        productFilters: ProductSuggestFiltersDto? = null
    ): FoodAutocompleteResponseDto? {
        return try {
            when (source) {
                FoodSource.INGREDIENT -> {
                    val filters = ingredientFilters ?: return null
                    val suggestions = nutritionClient.autocompleteIngredients(filters.query, filters) ?: return null
                    FoodAutocompleteResponseDto(
                        suggestions = suggestions.map { it.toFoodAutocompleteSuggestionDto() },
                        number = suggestions.size
                    )
                }

                FoodSource.PRODUCT -> {
                    val filters = productFilters ?: return null
                    val suggestions = nutritionClient.suggestProducts(filters.query, filters)?.results ?: return null
                    FoodAutocompleteResponseDto(
                        suggestions = suggestions.map { it.toFoodAutocompleteSuggestionDto() },
                        number = suggestions.size
                    )
                }

                FoodSource.MENU_ITEM -> null
            }
        } catch (e: Exception) {
            println("Error autocompleting $source in service: ${e.message}")
            null
        }
    }

    private suspend fun <T> buildSearchResponse(
        items: List<T>,
        totalResults: Int,
        offset: Int,
        number: Int,
        fetchFood: suspend (T) -> FoodDto?
    ): FoodSearchResponseDto {
        if (items.isEmpty()) {
            return FoodSearchResponseDto(
                foods = emptyList(),
                totalResults = totalResults,
                offset = offset,
                number = number
            )
        }

        val foods = coroutineScope {
            items.map { item ->
                async {
                    fetchFood(item)
                }
            }.awaitAll().filterNotNull()
        }

        return FoodSearchResponseDto(
            foods = foods,
            totalResults = totalResults,
            offset = offset,
            number = number
        )
    }

    suspend fun search(
        source: FoodSource,
        ingredientFilters: IngredientSearchFiltersDto? = null,
        productFilters: ProductSearchFiltersDto? = null,
        menuItemFilters: MenuItemSearchFiltersDto? = null
    ): FoodSearchResponseDto? {
        return try {
            when (source) {
                FoodSource.INGREDIENT -> {
                    val filters = ingredientFilters ?: return null
                    val searchResponse = nutritionClient.searchIngredients(filters.query, filters) ?: return null
                    buildSearchResponse(
                        items = searchResponse.results,
                        totalResults = searchResponse.totalResults,
                        offset = searchResponse.offset,
                        number = searchResponse.number
                    ) { result ->
                        nutritionClient.getIngredientInformation(result.id)?.toFoodDto()
                    }
                }

                FoodSource.PRODUCT -> {
                    val filters = productFilters ?: return null
                    val searchResponse = nutritionClient.searchProducts(filters.query, filters) ?: return null
                    buildSearchResponse(
                        items = searchResponse.products,
                        totalResults = searchResponse.totalProducts,
                        offset = searchResponse.offset,
                        number = searchResponse.number
                    ) { result ->
                        nutritionClient.getProductInformation(result.id)?.toFoodDto()
                    }
                }

                FoodSource.MENU_ITEM -> {
                    val filters = menuItemFilters ?: return null
                    val searchResponse = nutritionClient.searchMenuItems(filters.query, filters) ?: return null
                    buildSearchResponse(
                        items = searchResponse.menuItems,
                        totalResults = searchResponse.totalMenuItems,
                        offset = searchResponse.offset,
                        number = searchResponse.number
                    ) { result ->
                        nutritionClient.getMenuItemInformation(result.id)?.toFoodDto()
                    }
                }
            }
        } catch (e: Exception) {
            println("Error searching $source in service: ${e.message}")
            null
        }
    }

    suspend fun getFoodById(source: FoodSource, id: Int): FoodDto? {
        return when (source) {
            FoodSource.INGREDIENT -> nutritionClient.getIngredientInformation(id)?.toFoodDto()
            FoodSource.PRODUCT -> nutritionClient.getProductInformation(id)?.toFoodDto()
            FoodSource.MENU_ITEM -> nutritionClient.getMenuItemInformation(id)?.toFoodDto()
        }
    }

    suspend fun autocompleteIngredients(filters: IngredientAutocompleteFiltersDto): FoodAutocompleteResponseDto? {
        return autocomplete(source = FoodSource.INGREDIENT, ingredientFilters = filters)
    }

    suspend fun autocompleteProducts(filters: ProductSuggestFiltersDto): FoodAutocompleteResponseDto? {
        return autocomplete(source = FoodSource.PRODUCT, productFilters = filters)
    }

    suspend fun searchIngredients(filters: IngredientSearchFiltersDto): FoodSearchResponseDto? {
        return search(source = FoodSource.INGREDIENT, ingredientFilters = filters)
    }

    suspend fun searchProducts(filters: ProductSearchFiltersDto): FoodSearchResponseDto? {
        return search(source = FoodSource.PRODUCT, productFilters = filters)
    }

    suspend fun searchMenuItems(filters: MenuItemSearchFiltersDto): FoodSearchResponseDto? {
        return search(source = FoodSource.MENU_ITEM, menuItemFilters = filters)
    }

    suspend fun getIngredientById(id: Int): FoodDto? {
        return getFoodById(FoodSource.INGREDIENT, id)
    }

    suspend fun getProductById(id: Int): FoodDto? {
        return getFoodById(FoodSource.PRODUCT, id)
    }

    suspend fun getMenuItemById(id: Int): FoodDto? {
        return getFoodById(FoodSource.MENU_ITEM, id)
    }

    private fun buildMealBucket(
        dayMealContexts: List<MealContext>,
        historicalMealContexts: List<MealContext>,
        mealType: String
    ): MealBucketResponseDto {
        val dayItems = dayMealContexts
            .filter { it.meal.mealType == mealType }
            .sortedWith(compareBy<MealContext> { it.meal.eatenAt })
            .flatMap { context ->
                context.items.sortedBy(MealItemRowEntity::position).map { item -> item.toMealBucketItem() }
            }

        return MealBucketResponseDto(
            items = dayItems,
            historical = historicalMealBucketAverages(historicalMealContexts, mealType)
        )
    }

    /**
     * Per-day macro totals across a date range, derived from the same two table reads the day
     * summary uses — so a 90-day chart costs one round trip, not ninety.
     */
    suspend fun getNutritionDailySeries(
        userId: String,
        start: LocalDate,
        end: LocalDate
    ): NutritionDailySeriesResponseDto {
        val normalizedUserId = userId.trim()
        val meals = nutritionRepo.getMealsForUser(normalizedUserId)
        val itemsByMealId = nutritionRepo.getMealItemsForUser(normalizedUserId).groupBy { it.mealId }

        val totalsByDate = meals
            .mapNotNull { meal ->
                val eatenOn = meal.eatenAt.toUtcLocalDate() ?: return@mapNotNull null
                if (eatenOn < start || eatenOn > end) return@mapNotNull null
                eatenOn to MealContext(meal, itemsByMealId[meal.id].orEmpty())
            }
            .groupBy({ it.first }, { it.second })
            .mapValues { (_, contexts) -> contexts.toMacroTotals() }

        // Days with nothing logged are emitted as zeroes so the series has no gaps to interpolate.
        val days = generateSequence(start) { current ->
            current.plusDays(1).takeIf { !it.isAfter(end) }
        }.map { date ->
            val totals = totalsByDate[date]
            NutritionDailyTotalsDto(
                date = date.toString(),
                totalCal = totals?.calories ?: 0.0,
                totalProtein = totals?.protein ?: 0.0,
                totalFat = totals?.fat ?: 0.0,
                totalCarb = totals?.carbs ?: 0.0
            )
        }.toList()

        return NutritionDailySeriesResponseDto(
            start = start.toString(),
            end = end.toString(),
            days = days,
            analytics = recommendedTargets(normalizedUserId).copy(
                historical = totalsByDate.values.toList().toHistoricalAverages()
            )
        )
    }

    /**
     * Daily calorie and macro targets from the user's stored recommendation. Returns empty analytics
     * when no recommendation has been generated yet.
     */
    private suspend fun recommendedTargets(userId: String): NutritionAnalyticsResponseDto {
        val recommendation: RecommendationDto = runCatching {
            recommendationRepo.getRecommendationByUserId(userId)
        }.getOrNull() ?: return NutritionAnalyticsResponseDto()

        val macros = recommendation.nutrition.macros.trainingDay

        return NutritionAnalyticsResponseDto(
            recCal = recommendation.nutrition.calories.baseline.toDouble(),
            recProtein = macros.protein.toDouble(),
            recCarb = macros.carbs.toDouble(),
            recFat = macros.fat.toDouble()
        )
    }

    private fun historicalDailyAverages(
        historicalMealContexts: List<MealContext>
    ): HistoricalMacroAveragesResponseDto {
        val totals = historicalMealContexts
            .groupBy { it.meal.eatenAt.toUtcLocalDate() }
            .mapNotNull { (_, mealContexts) ->
                val validMealContexts = mealContexts.filter { it.meal.eatenAt.toUtcLocalDate() != null }
                if (validMealContexts.isEmpty()) {
                    null
                } else {
                    validMealContexts.toMacroTotals()
                }
            }

        return totals.toHistoricalAverages()
    }

    private fun historicalMealBucketAverages(
        historicalMealContexts: List<MealContext>,
        mealType: String
    ): HistoricalMacroAveragesResponseDto {
        val totals = historicalMealContexts
            .filter { it.meal.mealType == mealType }
            .groupBy { it.meal.eatenAt.toUtcLocalDate() }
            .mapNotNull { (_, mealContexts) ->
                val validMealContexts = mealContexts.filter { it.meal.eatenAt.toUtcLocalDate() != null }
                if (validMealContexts.isEmpty()) {
                    null
                } else {
                    validMealContexts.toMacroTotals()
                }
            }

        return totals.toHistoricalAverages()
    }

    private fun List<MealContext>.toMacroTotals(): MacroTotals {
        return MacroTotals(
            calories = sumOf { context -> context.items.sumOf(MealItemRowEntity::calories) },
            protein = sumOf { context -> context.items.sumOf(MealItemRowEntity::proteinG) },
            fat = sumOf { context -> context.items.sumOf(MealItemRowEntity::fatG) },
            carbs = sumOf { context -> context.items.sumOf(MealItemRowEntity::carbsG) }
        )
    }

    private fun List<MacroTotals>.toHistoricalAverages(): HistoricalMacroAveragesResponseDto {
        if (isEmpty()) {
            return HistoricalMacroAveragesResponseDto()
        }

        return HistoricalMacroAveragesResponseDto(
            avgCal = map(MacroTotals::calories).average(),
            avgProtein = map(MacroTotals::protein).average(),
            avgFat = map(MacroTotals::fat).average(),
            avgCarbs = map(MacroTotals::carbs).average()
        )
    }

    private fun MealItemRowEntity.toMealBucketItem(): MealBucketItemResponseDto {
        return MealBucketItemResponseDto(
            foodName = itemName,
            cal = calories,
            fat = fatG,
            protein = proteinG,
            carb = carbsG,
            servings = quantity,
            servingSize = unit
        )
    }

    private fun String.toUtcLocalDate(): LocalDate? {
        return runCatching {
            Instant.parse(trim())
                .atZone(ZoneOffset.UTC)
                .toLocalDate()
        }.getOrNull()
    }

    private data class MealContext(
        val meal: MealRowEntity,
        val items: List<MealItemRowEntity>
    )

    private data class MacroTotals(
        val calories: Double,
        val protein: Double,
        val fat: Double,
        val carbs: Double
    )
}
