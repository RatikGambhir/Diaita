<script setup lang="ts">
import type { FunctionalComponent } from "vue";
import type { LucideProps } from "lucide-vue-next";
import { computed, ref, watch } from "vue";
import { nutritionApi } from "~/api/nutrition";
import { useToast } from "~/composables/useToast";
import { useUserStore } from "~/stores/useUserStore";
import type {
  NutritionDaySummary,
  NutritionFood,
  NutritionItemType,
  NutritionMealBucket,
  NutritionMealType,
  NutritionUpsertMealItem,
} from "~/types/NutritionTypes";
import Button from "~/components/ui/button/Button.vue"
import NutritionSummaryCard from "~/components/nutrition/NutritionSummaryCard.vue"
import MacroDistributionCard from "~/components/nutrition/MacroDistributionCard.vue"
import MealCard from "~/components/nutrition/MealCard.vue"
import { Apple, CalendarDays, Sun, Moon } from "lucide-vue-next"
import { Popover, PopoverTrigger, PopoverContent } from "~/components/ui/popover"
import { Calendar } from "~/components/ui/calendar"

type MealItem = {
  id: string | null;
  name: string;
  calories: number;
  carbs: number;
  protein: number;
  fat: number;
};

type NutritionTotals = {
  calories: number;
  carbs: number;
  protein: number;
  fat: number;
};

type Meal = {
  mealType: NutritionMealType;
  mealId: string | null;
  name: string;
  icon: FunctionalComponent<LucideProps>;
  totals: NutritionTotals;
  items: MealItem[];
};

type MealConfig = {
  mealType: NutritionMealType;
  name: string;
  icon: FunctionalComponent<LucideProps>;
  hour: number;
};

type Macro = {
  name: string;
  percent: number;
  color: string;
  labelColor: string;
  labelClass: string;
};

const MEAL_CONFIGS: MealConfig[] = [
  { mealType: "breakfast", name: "Breakfast", icon: Sun, hour: 8 },
  { mealType: "lunch", name: "Lunch", icon: Sun, hour: 12 },
  { mealType: "dinner", name: "Dinner", icon: Moon, hour: 18 },
  { mealType: "snack", name: "Snacks", icon: Apple, hour: 15 },
];

const roundNutritionValue = (value: number) => Math.round(value * 10) / 10;

const formatNutritionValue = (value: number) => {
  const roundedValue = roundNutritionValue(value);
  return Number.isInteger(roundedValue) ? `${roundedValue}` : `${roundedValue}`;
};

const createNutritionTotals = (): NutritionTotals => ({
  calories: 0,
  carbs: 0,
  protein: 0,
  fat: 0,
});

const calculateMealTotals = (items: MealItem[]): NutritionTotals => {
  return items.reduce((totals, item) => ({
    calories: roundNutritionValue(totals.calories + item.calories),
    carbs: roundNutritionValue(totals.carbs + item.carbs),
    protein: roundNutritionValue(totals.protein + item.protein),
    fat: roundNutritionValue(totals.fat + item.fat),
  }), createNutritionTotals());
};

const createMeal = (config: MealConfig): Meal => ({
  mealType: config.mealType,
  mealId: null,
  name: config.name,
  icon: config.icon,
  totals: createNutritionTotals(),
  items: [],
});

const createMeals = (): Meal[] => MEAL_CONFIGS.map(createMeal);

const getMealConfig = (mealType: NutritionMealType) => {
  return MEAL_CONFIGS.find((config) => config.mealType === mealType);
};

const formatMealItemName = (foodName: string, servingSize?: string | null) => {
  if (!servingSize) {
    return foodName;
  }

  return `${foodName} (${servingSize})`;
};

const mapBucketToMealItems = (bucket: NutritionMealBucket): MealItem[] => {
  return bucket.items.map((item) => ({
    id: item.id,
    name: formatMealItemName(item.foodName, item.servingSize),
    calories: roundNutritionValue(item.cal),
    carbs: roundNutritionValue(item.carb),
    protein: roundNutritionValue(item.protein),
    fat: roundNutritionValue(item.fat),
  }));
};

const bucketForMealType = (
  summary: NutritionDaySummary,
  mealType: NutritionMealType,
) => {
  if (mealType === "breakfast") {
    return summary.breakfast;
  }

  if (mealType === "lunch") {
    return summary.lunch;
  }

  if (mealType === "dinner") {
    return summary.dinner;
  }

  return summary.snacks;
};

const resolveItemType = (food: NutritionFood): NutritionItemType => {
  if (
    food.category === "ingredient"
    || food.category === "product"
    || food.category === "menu_item"
  ) {
    return food.category;
  }

  return "custom";
};

const buildEatenAt = (date: Date, mealType: NutritionMealType) => {
  const config = getMealConfig(mealType);

  return new Date(
    Date.UTC(
      date.getFullYear(),
      date.getMonth(),
      date.getDate(),
      config?.hour ?? 12,
      0,
      0,
    ),
  ).toISOString();
};

const buildUpsertMealItems = (
  foods: NutritionFood[],
  startingPosition: number,
): NutritionUpsertMealItem[] => {
  return foods.map((food, index) => {
    const itemType = resolveItemType(food);

    return {
      itemType,
      itemId: itemType === "custom" ? undefined : food.id,
      itemName: food.name,
      brandName: food.brand ?? undefined,
      quantity: 1,
      unit: food.servingSize,
      calories: roundNutritionValue(food.calories),
      proteinG: roundNutritionValue(food.protein),
      carbsG: roundNutritionValue(food.carbs),
      fatG: roundNutritionValue(food.fat),
      position: startingPosition + index,
    };
  });
};

const selectedDate = ref(new Date());
const datePickerOpen = ref(false);
const meals = ref<Meal[]>(createMeals());
const userStore = useUserStore();
const toast = useToast();
const savingMealType = ref<NutritionMealType | null>(null);
const hasLoadedSummary = ref(false);

const displayDate = computed(() => {
  return selectedDate.value.toLocaleDateString("en-US", {
    weekday: "long",
    month: "long",
    day: "numeric",
    year: "numeric",
  });
});

const formatDateParam = (date: Date) => {
  const year = date.getFullYear();
  const month = `${date.getMonth() + 1}`.padStart(2, "0");
  const day = `${date.getDate()}`.padStart(2, "0");
  return `${year}-${month}-${day}`;
};

const resetMeals = () => {
  meals.value = createMeals();
};

const loadDaySummary = async () => {
  const currentUser = userStore.getUser as { id?: string } | null;
  const userId = currentUser?.id?.trim();

  if (!userId) {
    resetMeals();
    return;
  }

  try {
    applySummary(await nutritionApi.getDaySummary(userId, formatDateParam(selectedDate.value)));
  } catch (error) {
    resetMeals();

    if (hasLoadedSummary.value) {
      toast.add({
        title: "Unable to load nutrition",
        description: error instanceof Error ? error.message : "Failed to fetch the day summary.",
        color: "error",
      });
    }
  } finally {
    hasLoadedSummary.value = true;
  }
};

watch([selectedDate, () => userStore.getUser?.id], () => {
  void loadDaySummary();
});

onMounted(() => {
  void loadDaySummary();
});

function onDateSelect(date: Date) {
  selectedDate.value = date;
  datePickerOpen.value = false;
}

const nutritionTotals = computed(() => {
  const totals = createNutritionTotals();

  for (const meal of meals.value) {
    totals.calories = roundNutritionValue(totals.calories + meal.totals.calories);
    totals.carbs = roundNutritionValue(totals.carbs + meal.totals.carbs);
    totals.protein = roundNutritionValue(totals.protein + meal.totals.protein);
    totals.fat = roundNutritionValue(totals.fat + meal.totals.fat);
  }

  return totals;
});

const summaryCards = computed(() => [
  { label: "Total Calories", value: formatNutritionValue(nutritionTotals.value.calories), unit: "kcal", valueClass: "text-chart-4", dotClass: "bg-chart-4" },
  { label: "Carbs", value: formatNutritionValue(nutritionTotals.value.carbs), unit: "g", valueClass: "text-chart-1", dotClass: "bg-chart-1" },
  { label: "Protein", value: formatNutritionValue(nutritionTotals.value.protein), unit: "g", valueClass: "text-chart-3", dotClass: "bg-chart-3" },
  { label: "Fat", value: formatNutritionValue(nutritionTotals.value.fat), unit: "g", valueClass: "text-chart-2", dotClass: "bg-chart-2" },
]);

const macros = computed<[Macro, Macro, Macro]>(() => {
  const carbCalories = nutritionTotals.value.carbs * 4;
  const proteinCalories = nutritionTotals.value.protein * 4;
  const fatCalories = nutritionTotals.value.fat * 9;
  const totalMacroCalories = carbCalories + proteinCalories + fatCalories;
  const toPercent = (macroCalories: number) => totalMacroCalories === 0
    ? 0
    : roundNutritionValue((macroCalories / totalMacroCalories) * 100);

  return [
    {
      name: "Carbs",
      percent: toPercent(carbCalories),
      color: "var(--chart-1)",
      labelColor: "text-chart-1",
      labelClass: "-top-6 left-1/2 -translate-x-1/2",
    },
    {
      name: "Protein",
      percent: toPercent(proteinCalories),
      color: "var(--chart-3)",
      labelColor: "text-chart-3",
      labelClass: "left-0 bottom-4 -translate-x-6",
    },
    {
      name: "Fat",
      percent: toPercent(fatCalories),
      color: "var(--chart-2)",
      labelColor: "text-chart-2",
      labelClass: "right-0 bottom-6 translate-x-6",
    },
  ];
});

const pieGradient = computed(() => {
  const [carb, protein, fat] = macros.value;
  const carbStop = carb.percent;
  const proteinStop = carbStop + protein.percent;
  return `conic-gradient(${carb.color} 0 ${carbStop}%, ${protein.color} ${carbStop}% ${proteinStop}%, ${fat.color} ${proteinStop}% 100%)`;
});

const applySummary = (summary: NutritionDaySummary) => {
  meals.value = meals.value.map((meal) => {
    const items = mapBucketToMealItems(bucketForMealType(summary, meal.mealType));

    return {
      ...meal,
      mealId: bucketForMealType(summary, meal.mealType).mealId,
      totals: calculateMealTotals(items),
      items,
    };
  });
};

const handleDeleteFood = async (mealType: NutritionMealType, itemId: string | null) => {
  if (!itemId || savingMealType.value !== null) return;
  const meal = meals.value.find((option) => option.mealType === mealType);
  const userId = userStore.getUser?.id?.trim();
  if (!meal?.mealId || !userId) return;

  savingMealType.value = mealType;
  try {
    const summary = await nutritionApi.upsertMeals({
      userId,
      meals: [{
        id: meal.mealId,
        mealType,
        eatenAt: buildEatenAt(selectedDate.value, mealType),
        itemOps: { upsert: [], deleteIds: [itemId] },
      }],
    });
    applySummary(summary);
    toast.add({ title: "Food removed", description: `Updated ${meal.name}.`, color: "success" });
  } catch (error) {
    toast.add({
      title: "Unable to remove food",
      description: error instanceof Error ? error.message : "Please try again.",
      color: "error",
    });
  } finally {
    savingMealType.value = null;
  }
};

const handleAddFoods = async (
  mealType: NutritionMealType,
  ingredients: NutritionFood[],
) => {
  if (ingredients.length === 0 || savingMealType.value !== null) {
    return;
  }

  const meal = meals.value.find((mealOption) => mealOption.mealType === mealType);
  if (!meal) {
    return;
  }

  const currentUser = userStore.getUser as { id?: string } | null;
  const userId = currentUser?.id?.trim();

  if (!userId) {
    toast.add({
      title: "Unable to save meal",
      description: "No signed-in user was found for this nutrition entry.",
      color: "error",
    });
    return;
  }

  const mealId = meal.mealId ?? crypto.randomUUID();

  savingMealType.value = mealType;

  try {
    const summary = await nutritionApi.upsertMeals({
      userId,
      meals: [
        {
          id: mealId,
          mealType,
          eatenAt: buildEatenAt(selectedDate.value, mealType),
          itemOps: {
            upsert: buildUpsertMealItems(ingredients, meal.items.length),
            deleteIds: [],
          },
        },
      ],
    });

    meal.mealId = mealId;
    applySummary(summary);

    toast.add({
      title: "Meal saved",
      description: `Saved ${ingredients.length} ${ingredients.length === 1 ? "food" : "foods"} to ${meal.name}.`,
      color: "success",
    });
  } catch (error) {
    toast.add({
      title: "Unable to save meal",
      description: error instanceof Error ? error.message : `Failed to save ${meal.name}.`,
      color: "error",
    });
  } finally {
    savingMealType.value = null;
  }
};

</script>

<template>
    <div class="flex-1 flex flex-col h-full bg-background">
        <div class="flex-1 overflow-auto p-6">
            <div class="max-w-6xl mx-auto space-y-6">
                <div class="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
                    <div>
                        <h1 class="text-2xl font-semibold text-foreground">Nutrition Tracker</h1>
                        <p class="text-sm text-muted-foreground">Log meals and review calories and macronutrients by day.</p>
                    </div>
                    <Popover v-model:open="datePickerOpen">
                        <PopoverTrigger as-child>
                            <Button
                                variant="outline"
                                class="h-9 justify-start gap-2 text-sm font-normal text-muted-foreground hover:text-foreground"
                            >
                                <CalendarDays class="h-4 w-4" />
                                {{ displayDate }}
                            </Button>
                        </PopoverTrigger>
                        <PopoverContent align="end" class="w-auto p-0">
                            <Calendar :model-value="selectedDate" @select="onDateSelect" />
                        </PopoverContent>
                    </Popover>
                </div>

                <div class="space-y-6">
                                <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
                                    <NutritionSummaryCard
                                        v-for="card in summaryCards"
                                        :key="card.label"
                                        :label="card.label"
                                        :value="card.value"
                                        :unit="card.unit"
                                        :value-class="card.valueClass"
                                        :dot-class="card.dotClass"
                                    />
                                </div>

                                <MacroDistributionCard :gradient="pieGradient" :macros="macros" />

                                <div class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-4 gap-4">
                                    <MealCard
                                        v-for="meal in meals"
                                        :key="meal.name"
                                        :meal="meal"
                                        @add-foods="(ingredients) => handleAddFoods(meal.mealType, ingredients)"
                                        @delete-food="(itemId) => handleDeleteFood(meal.mealType, itemId)"
                                    />
                                </div>

                </div>
            </div>
        </div>
    </div>
</template>
