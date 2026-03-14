import type {
  NutritionAutocompleteSuggestion,
  NutritionDaySummary,
  NutritionFood,
  NutritionItemType,
  NutritionSearchFilter,
  NutritionUpsertMealsRequest,
} from "~/types/nutrition";
import { apiClient } from "./client";


type FoodDto = {
  id: string;
  name: string;
  brand: string | null;
  category: string | null;
  servingSize: number | null;
  servingUnit: string | null;
  caloriesPerServingSize: number | null;
  proteinGPerServingSize: number | null;
  carbGPerServingSize: number | null;
  fatGPerServingSize: number | null;
  createdAt: string | null;
};

type FoodSearchResponseDto = {
  foods: FoodDto[];
  totalResults: number;
  offset: number;
  number: number;
};

type FoodAutocompleteResponseDto = {
  suggestions: FoodAutocompleteSuggestionDto[];
  number: number;
};

type FoodAutocompleteSuggestionDto = {
  id: string;
  name: string;
  category: NutritionSearchFilter;
  image: string | null;
  brand: string | null;
  aisle: string | null;
  possibleUnits: string[];
};

type SearchFoodsParams = {
  query: string;
  filter: NutritionSearchFilter;
  offset?: number;
  number?: number;
};

type AutocompleteFoodsParams = {
  query: string;
  filter: NutritionSearchFilter;
  number?: number;
};

const roundNutritionValue = (value: number) => Math.round(value * 10) / 10;

const formatServingAmount = (value: number) => {
  const roundedValue = roundNutritionValue(value);
  return Number.isInteger(roundedValue) ? `${roundedValue}` : `${roundedValue}`;
};

const formatServingSize = (food: FoodDto) => {
  const unit = food.servingUnit?.trim();
  const normalizedUnit = unit && unit.toLowerCase() !== "not found" ? unit : null;

  if (food.servingSize == null && !normalizedUnit) {
    return "1 serving";
  }

  if (food.servingSize == null) {
    return normalizedUnit!;
  }

  if (!normalizedUnit) {
    return formatServingAmount(food.servingSize);
  }

  return `${formatServingAmount(food.servingSize)} ${normalizedUnit}`;
};

const normalizeFoodCategory = (
  category: string | null,
): NutritionItemType | null => {
  if (
    category === "ingredient"
    || category === "product"
    || category === "menu_item"
    || category === "custom"
  ) {
    return category;
  }

  return null;
};

const mapFoodDto = (food: FoodDto): NutritionFood => ({
  id: food.id,
  name: food.name,
  brand: food.brand,
  category: normalizeFoodCategory(food.category),
  servingSize: formatServingSize(food),
  calories: roundNutritionValue(food.caloriesPerServingSize ?? 0),
  carbs: roundNutritionValue(food.carbGPerServingSize ?? 0),
  protein: roundNutritionValue(food.proteinGPerServingSize ?? 0),
  fat: roundNutritionValue(food.fatGPerServingSize ?? 0),
});

const mapAutocompleteSuggestionDto = (
  suggestion: FoodAutocompleteSuggestionDto,
): NutritionAutocompleteSuggestion => ({
  id: suggestion.id,
  name: suggestion.name,
  category: suggestion.category,
  image: suggestion.image,
  brand: suggestion.brand,
  aisle: suggestion.aisle,
  possibleUnits: suggestion.possibleUnits,
});

export const nutritionApi = {
  async getDaySummary(
    userId: string,
    date: string,
  ): Promise<NutritionDaySummary> {
    const response = await apiClient.get<NutritionDaySummary>(
      "/nutrition/day-summary",
      {
        params: { userId, date },
      },
    );

    return response.data;
  },

  async upsertMeals(
    payload: NutritionUpsertMealsRequest,
  ): Promise<NutritionDaySummary> {
    const response = await apiClient.post<NutritionDaySummary>(
      "/nutrition/meals/upsert",
      payload,
    );

    return response.data;
  },

  async autocompleteFoods({
    query,
    filter,
    number = 8,
  }: AutocompleteFoodsParams): Promise<NutritionAutocompleteSuggestion[]> {
    const trimmedQuery = query.trim();

    if (!trimmedQuery) {
      return [];
    }

    const endpoint =
      filter === "product"
        ? "/nutrition/autocomplete/products"
        : "/nutrition/autocomplete/ingredients";

    const params =
      filter === "product"
        ? { query: trimmedQuery, number }
        : {
            query: trimmedQuery,
            number,
            language: "en",
            metaInformation: true,
          };

    const response = await apiClient.get<FoodAutocompleteResponseDto>(
      endpoint,
      {
        params,
      },
    );

    return response.data.suggestions.map(mapAutocompleteSuggestionDto);
  },

  async searchFoods({
    query,
    filter,
    offset = 0,
    number = 10,
  }: SearchFoodsParams): Promise<NutritionFood[]> {
    const trimmedQuery = query.trim();

    if (!trimmedQuery) {
      return [];
    }

    const endpoint =
      filter === "product"
        ? "/nutrition/search/products"
        : "/nutrition/search/ingredients";

    const response = await apiClient.post<FoodSearchResponseDto>(
      endpoint,
      {
        query: trimmedQuery,
        offset,
        number,
      },
    );

    return response.data.foods.map(mapFoodDto);
  },
};
