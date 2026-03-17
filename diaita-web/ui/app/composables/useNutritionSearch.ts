import { computed, onBeforeUnmount, ref, watch } from "vue";
import { nutritionApi } from "~/api/nutrition";
import type { NutritionFood, NutritionSearchFilter } from "~/types/NutritionTypes";

const SEARCH_DEBOUNCE_MS = 250;
const MIN_QUERY_LENGTH = 2;

export function useNutritionSearch(
  initialFilter: NutritionSearchFilter = "ingredient",
) {
  const query = ref("");
  const filter = ref<NutritionSearchFilter>(initialFilter);
  const results = ref<NutritionFood[]>([]);
  const isSearching = ref(false);
  const error = ref<string | null>(null);
  const hasSearched = ref(false);
  const normalizedQuery = computed(() => query.value.trim());
  const searchPlaceholder = computed(() =>
    filter.value === "ingredient"
      ? "Search ingredients..."
      : "Search products...",
  );

  let debounceTimeout: ReturnType<typeof setTimeout> | null = null;
  let latestRequestId = 0;

  const clearSearchState = () => {
    results.value = [];
    isSearching.value = false;
    error.value = null;
    hasSearched.value = false;
  };

  const reset = () => {
    latestRequestId++;

    if (debounceTimeout) {
      clearTimeout(debounceTimeout);
      debounceTimeout = null;
    }

    query.value = "";
    filter.value = initialFilter;
    clearSearchState();
  };

  const executeSearch = async (
    nextQuery: string,
    nextFilter: NutritionSearchFilter,
  ) => {
    const requestId = ++latestRequestId;
    isSearching.value = true;
    error.value = null;

    try {
      const foods = await nutritionApi.searchFoods({
        query: nextQuery,
        filter: nextFilter,
      });

      if (requestId !== latestRequestId) {
        return;
      }

      results.value = foods;
      hasSearched.value = true;
    } catch {
      if (requestId !== latestRequestId) {
        return;
      }

      results.value = [];
      error.value = "Unable to search foods right now.";
      hasSearched.value = true;
    } finally {
      if (requestId === latestRequestId) {
        isSearching.value = false;
      }
    }
  };

  watch(
    [normalizedQuery, filter],
    ([nextQuery, nextFilter], _, onCleanup) => {
      if (debounceTimeout) {
        clearTimeout(debounceTimeout);
        debounceTimeout = null;
      }

      if (!nextQuery) {
        latestRequestId++;
        clearSearchState();
        return;
      }

      if (nextQuery.length < MIN_QUERY_LENGTH) {
        latestRequestId++;
        results.value = [];
        isSearching.value = false;
        error.value = null;
        hasSearched.value = false;
        return;
      }

      debounceTimeout = setTimeout(() => {
        debounceTimeout = null;
        void executeSearch(nextQuery, nextFilter);
      }, SEARCH_DEBOUNCE_MS);

      onCleanup(() => {
        if (debounceTimeout) {
          clearTimeout(debounceTimeout);
          debounceTimeout = null;
        }
      });
    },
    { immediate: true },
  );

  onBeforeUnmount(() => {
    latestRequestId++;

    if (debounceTimeout) {
      clearTimeout(debounceTimeout);
    }
  });

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
