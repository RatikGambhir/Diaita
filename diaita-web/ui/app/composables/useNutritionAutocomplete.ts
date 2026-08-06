import type { Ref } from "vue";
import { computed } from "vue";
import { nutritionApi } from "~/api/nutrition";
import type {
  NutritionAutocompleteSuggestion,
  NutritionSearchFilter,
} from "~/types/NutritionTypes";

const AUTOCOMPLETE_DEBOUNCE_MS = 150;
const AUTOCOMPLETE_MIN_QUERY_LENGTH = 2;

export function useNutritionAutocomplete(
  query: Ref<string>,
  filter: Ref<NutritionSearchFilter>,
) {
  const normalizedQuery = computed(() => query.value.trim());

  const {
    data: suggestions,
    isLoading,
    error,
  } = useDebouncedRequest<
    [string, NutritionSearchFilter],
    NutritionAutocompleteSuggestion[]
  >({
    sources: [normalizedQuery, filter],
    shouldRun: nextQuery => nextQuery.length >= AUTOCOMPLETE_MIN_QUERY_LENGTH,
    request: (nextQuery, nextFilter) =>
      nutritionApi.autocompleteFoods({ query: nextQuery, filter: nextFilter }),
    emptyValue: [],
    errorMessage: "Unable to load suggestions right now.",
    debounceMs: AUTOCOMPLETE_DEBOUNCE_MS,
  });

  return {
    suggestions,
    isLoading,
    error,
    minQueryLength: AUTOCOMPLETE_MIN_QUERY_LENGTH,
  };
}
