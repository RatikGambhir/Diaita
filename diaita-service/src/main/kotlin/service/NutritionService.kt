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
import com.diaita.lib.logging.logger
import com.diaita.lib.mappings.toFoodAutocompleteSuggestionDto
import com.diaita.lib.mappings.toFoodDto
import com.diaita.lib.mappings.toNormalizedRequest
import com.diaita.lib.mappings.withSortedBuckets
import com.diaita.repo.NutritionRepo
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

class NutritionService(
    private val nutritionRepo: NutritionRepo,
    private val nutritionClient: NutritionRestClient
) {
    private val log = logger()

    enum class FoodSource {
        INGREDIENT,
        PRODUCT,
        MENU_ITEM
    }

    suspend fun getNutritionDaySummary(userId: String, date: String): NutritionDaySummaryResponseDto? {
        val targetDate = runCatching { LocalDate.parse(date.trim()) }.getOrNull() ?: return null
        val normalizedUserId = userId.trim()

        val meals = nutritionRepo.getMealsForUser(normalizedUserId)
        val itemsByMealId = nutritionRepo.getMealItemsForUser(normalizedUserId).groupBy { it.mealId }

        val allMeals = meals.map { MealContext(it, itemsByMealId[it.id].orEmpty()) }
        val dayMeals = allMeals.filter { it.meal.eatenAt.toUtcLocalDate() == targetDate }
        val dayTotals = dayMeals.toMacroTotals()

        return NutritionDaySummaryResponseDto(
            date = targetDate.toString(),
            totalCal = dayTotals.calories,
            totalProtein = dayTotals.protein,
            totalFat = dayTotals.fat,
            totalCarb = dayTotals.carbs,
            analytics = NutritionAnalyticsResponseDto(
                recCal = null,
                recProtein = null,
                recCarb = null,
                recFat = null,
                historical = allMeals.dailyAverages()
            ),
            breakfast = buildMealBucket(dayMeals, allMeals, "breakfast"),
            lunch = buildMealBucket(dayMeals, allMeals, "lunch"),
            dinner = buildMealBucket(dayMeals, allMeals, "dinner"),
            snacks = buildMealBucket(dayMeals, allMeals, "snack")
        ).withSortedBuckets()
    }

    suspend fun upsertMeals(request: UpsertMealsRequestDto): NutritionDaySummaryResponseDto? {
        return nutritionRepo.upsertMeals(request.toNormalizedRequest())?.withSortedBuckets()
    }

    suspend fun autocomplete(
        source: FoodSource,
        ingredientFilters: IngredientAutocompleteFiltersDto? = null,
        productFilters: ProductSuggestFiltersDto? = null
    ): FoodAutocompleteResponseDto? {
        return try {
            val suggestions = when (source) {
                FoodSource.INGREDIENT -> ingredientFilters
                    ?.let { nutritionClient.autocompleteIngredients(it.query, it) }
                    ?.map { it.toFoodAutocompleteSuggestionDto() }

                FoodSource.PRODUCT -> productFilters
                    ?.let { nutritionClient.suggestProducts(it.query, it) }
                    ?.results
                    ?.map { it.toFoodAutocompleteSuggestionDto() }

                FoodSource.MENU_ITEM -> null
            } ?: return null

            FoodAutocompleteResponseDto(suggestions = suggestions, number = suggestions.size)
        } catch (e: Exception) {
            log.error("Autocomplete for {} failed: {}", source, e.message, e)
            null
        }
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
                    val response = nutritionClient.searchIngredients(filters.query, filters) ?: return null
                    buildSearchResponse(
                        items = response.results,
                        totalResults = response.totalResults,
                        offset = response.offset,
                        number = response.number
                    ) { nutritionClient.getIngredientInformation(it.id)?.toFoodDto() }
                }

                FoodSource.PRODUCT -> {
                    val filters = productFilters ?: return null
                    val response = nutritionClient.searchProducts(filters.query, filters) ?: return null
                    buildSearchResponse(
                        items = response.products,
                        totalResults = response.totalProducts,
                        offset = response.offset,
                        number = response.number
                    ) { nutritionClient.getProductInformation(it.id)?.toFoodDto() }
                }

                FoodSource.MENU_ITEM -> {
                    val filters = menuItemFilters ?: return null
                    val response = nutritionClient.searchMenuItems(filters.query, filters) ?: return null
                    buildSearchResponse(
                        items = response.menuItems,
                        totalResults = response.totalMenuItems,
                        offset = response.offset,
                        number = response.number
                    ) { nutritionClient.getMenuItemInformation(it.id)?.toFoodDto() }
                }
            }
        } catch (e: Exception) {
            log.error("Search for {} failed: {}", source, e.message, e)
            null
        }
    }

    suspend fun getFoodById(source: FoodSource, id: Int): FoodDto? = when (source) {
        FoodSource.INGREDIENT -> nutritionClient.getIngredientInformation(id)?.toFoodDto()
        FoodSource.PRODUCT -> nutritionClient.getProductInformation(id)?.toFoodDto()
        FoodSource.MENU_ITEM -> nutritionClient.getMenuItemInformation(id)?.toFoodDto()
    }

    suspend fun autocompleteIngredients(filters: IngredientAutocompleteFiltersDto): FoodAutocompleteResponseDto? =
        autocomplete(source = FoodSource.INGREDIENT, ingredientFilters = filters)

    suspend fun autocompleteProducts(filters: ProductSuggestFiltersDto): FoodAutocompleteResponseDto? =
        autocomplete(source = FoodSource.PRODUCT, productFilters = filters)

    suspend fun searchIngredients(filters: IngredientSearchFiltersDto): FoodSearchResponseDto? =
        search(source = FoodSource.INGREDIENT, ingredientFilters = filters)

    suspend fun searchProducts(filters: ProductSearchFiltersDto): FoodSearchResponseDto? =
        search(source = FoodSource.PRODUCT, productFilters = filters)

    suspend fun searchMenuItems(filters: MenuItemSearchFiltersDto): FoodSearchResponseDto? =
        search(source = FoodSource.MENU_ITEM, menuItemFilters = filters)

    suspend fun getIngredientById(id: Int): FoodDto? = getFoodById(FoodSource.INGREDIENT, id)

    suspend fun getProductById(id: Int): FoodDto? = getFoodById(FoodSource.PRODUCT, id)

    suspend fun getMenuItemById(id: Int): FoodDto? = getFoodById(FoodSource.MENU_ITEM, id)

    /** Resolves full nutrition for each search hit concurrently, dropping any that fail to load. */
    private suspend fun <T> buildSearchResponse(
        items: List<T>,
        totalResults: Int,
        offset: Int,
        number: Int,
        fetchFood: suspend (T) -> FoodDto?
    ): FoodSearchResponseDto {
        val foods = if (items.isEmpty()) {
            emptyList()
        } else {
            coroutineScope {
                items.map { async { fetchFood(it) } }.awaitAll().filterNotNull()
            }
        }

        return FoodSearchResponseDto(
            foods = foods,
            totalResults = totalResults,
            offset = offset,
            number = number
        )
    }

    private fun buildMealBucket(
        dayMeals: List<MealContext>,
        allMeals: List<MealContext>,
        mealType: String
    ): MealBucketResponseDto {
        val items = dayMeals
            .filter { it.meal.mealType == mealType }
            .sortedBy { it.meal.eatenAt }
            .flatMap { context ->
                context.items.sortedBy(MealItemRowEntity::position).map { it.toMealBucketItem() }
            }

        return MealBucketResponseDto(
            items = items,
            historical = allMeals.dailyAverages(mealType)
        )
    }

    /**
     * Averages daily macro totals across every day that has meals, optionally
     * narrowed to a single [mealType]. Meals with an unparseable timestamp are
     * excluded rather than collapsed into one bucket.
     */
    private fun List<MealContext>.dailyAverages(mealType: String? = null): HistoricalMacroAveragesResponseDto {
        val dailyTotals = asSequence()
            .filter { mealType == null || it.meal.mealType == mealType }
            .groupBy { it.meal.eatenAt.toUtcLocalDate() }
            .filterKeys { it != null }
            .map { (_, mealsOnDay) -> mealsOnDay.toMacroTotals() }

        if (dailyTotals.isEmpty()) {
            return HistoricalMacroAveragesResponseDto()
        }

        return HistoricalMacroAveragesResponseDto(
            avgCal = dailyTotals.map(MacroTotals::calories).average(),
            avgProtein = dailyTotals.map(MacroTotals::protein).average(),
            avgFat = dailyTotals.map(MacroTotals::fat).average(),
            avgCarbs = dailyTotals.map(MacroTotals::carbs).average()
        )
    }

    private fun List<MealContext>.toMacroTotals(): MacroTotals = MacroTotals(
        calories = sumOf { it.items.sumOf(MealItemRowEntity::calories) },
        protein = sumOf { it.items.sumOf(MealItemRowEntity::proteinG) },
        fat = sumOf { it.items.sumOf(MealItemRowEntity::fatG) },
        carbs = sumOf { it.items.sumOf(MealItemRowEntity::carbsG) }
    )

    private fun MealItemRowEntity.toMealBucketItem(): MealBucketItemResponseDto = MealBucketItemResponseDto(
        foodName = itemName,
        cal = calories,
        fat = fatG,
        protein = proteinG,
        carb = carbsG,
        servings = quantity,
        servingSize = unit
    )

    private fun String.toUtcLocalDate(): LocalDate? = runCatching {
        Instant.parse(trim()).atZone(ZoneOffset.UTC).toLocalDate()
    }.getOrNull()

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
