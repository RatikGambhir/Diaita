import { computed, ref } from "vue";
import { nutritionApi } from "~/api/nutrition";
import type { NutritionFood, NutritionSearchFilter } from "~/types/NutritionTypes";

const SEARCH_DEBOUNCE_MS = 250;
const MIN_QUERY_LENGTH = 2;

export function useNutritionSearch(
  initialFilter: NutritionSearchFilter = "ingredient",
) {
  const query = ref("");
  const filter = ref<NutritionSearchFilter>(initialFilter);
  const hasSearched = ref(false);
  const normalizedQuery = computed(() => query.value.trim());
  const searchPlaceholder = computed(() =>
    filter.value === "ingredient"
      ? "Search ingredients..."
      : "Search products...",
  );

  const {
    data: results,
    isLoading: isSearching,
    error,
    reset: resetRequest,
  } = useDebouncedRequest<[string, NutritionSearchFilter], NutritionFood[]>({
    sources: [normalizedQuery, filter],
    shouldRun: nextQuery => nextQuery.length >= MIN_QUERY_LENGTH,
    request: (nextQuery, nextFilter) =>
      nutritionApi.searchFoods({ query: nextQuery, filter: nextFilter }),
    emptyValue: [],
    errorMessage: "Unable to search foods right now.",
    debounceMs: SEARCH_DEBOUNCE_MS,
    onReset: () => {
      hasSearched.value = false;
    },
    onSuccess: () => {
      hasSearched.value = true;
    },
    onError: () => {
      hasSearched.value = true;
    },
  });

  const reset = () => {
    query.value = "";
    filter.value = initialFilter;
    resetRequest();
  };

  return {
    query,
    filter,
    results,
    isSearching,
    error,
    hasSearched,
    normalizedQuery,
    searchPlaceholder,
    minQueryLength: MIN_QUERY_LENGTH,
    reset,
  };
}
