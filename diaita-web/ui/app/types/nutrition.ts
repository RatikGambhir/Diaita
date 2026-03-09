export type NutritionSearchFilter = "ingredient" | "product";

export type NutritionFood = {
  id: string;
  name: string;
  brand: string | null;
  category: string | null;
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
