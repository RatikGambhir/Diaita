export type NutritionSearchFilter = "ingredient" | "product";

export type NutritionMealType = "breakfast" | "lunch" | "dinner" | "snack";

export type NutritionItemType =
  | "ingredient"
  | "product"
  | "menu_item"
  | "custom";

export type NutritionFood = {
  id: string;
  name: string;
  brand: string | null;
  category: NutritionItemType | null;
  servingSize: string;
  calories: number;
  carbs: number;
  protein: number;
  fat: number;
};

export type NutritionAutocompleteSuggestion = {
  id: string;
  name: string;
  category: NutritionSearchFilter;
  image: string | null;
  brand: string | null;
  aisle: string | null;
  possibleUnits: string[];
};

export type NutritionUpsertMealItem = {
  id?: string;
  itemType: NutritionItemType;
  itemId?: string;
  itemName: string;
  brandName?: string;
  quantity: number;
  unit?: string;
  calories: number;
  proteinG: number;
  carbsG: number;
  fatG: number;
  position: number;
};

export type NutritionUpsertMeal = {
  id?: string;
  mealType: NutritionMealType;
  eatenAt: string;
  notes?: string;
  itemOps?: {
    upsert?: NutritionUpsertMealItem[];
    deleteIds?: string[];
  };
};

export type NutritionUpsertMealsRequest = {
  userId: string;
  meals: NutritionUpsertMeal[];
};

export type NutritionHistoricalAverages = {
  avgCal: number | null;
  avgProtein: number | null;
  avgFat: number | null;
  avgCarbs: number | null;
};

export type NutritionMealBucketItem = {
  foodName: string;
  cal: number;
  fat: number;
  protein: number;
  carb: number;
  servings: number;
  servingSize: string | null;
};

export type NutritionMealBucket = {
  items: NutritionMealBucketItem[];
  historical: NutritionHistoricalAverages | null;
};

export type NutritionDaySummary = {
  date: string;
  totalCal: number;
  totalProtein: number;
  totalFat: number;
  totalCarb: number;
  analytics: {
    recCal: number | null;
    recProtein: number | null;
    recCarb: number | null;
    recFat: number | null;
    historical: NutritionHistoricalAverages | null;
  };
  breakfast: NutritionMealBucket;
  lunch: NutritionMealBucket;
  dinner: NutritionMealBucket;
  snacks: NutritionMealBucket;
};
