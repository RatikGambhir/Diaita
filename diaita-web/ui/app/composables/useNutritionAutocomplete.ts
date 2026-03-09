import type { Ref } from "vue";
import { computed, onBeforeUnmount, ref, watch } from "vue";
import { nutritionApi } from "~/api/nutrition";
import type {
  NutritionAutocompleteSuggestion,
  NutritionSearchFilter,
} from "~/types/nutrition";

const AUTOCOMPLETE_DEBOUNCE_MS = 150;
const AUTOCOMPLETE_MIN_QUERY_LENGTH = 2;

export function useNutritionAutocomplete(
  query: Ref<string>,
  filter: Ref<NutritionSearchFilter>,
) {
  const suggestions = ref<NutritionAutocompleteSuggestion[]>([]);
  const isLoading = ref(false);
  const error = ref<string | null>(null);
  const normalizedQuery = computed(() => query.value.trim());

  let debounceTimeout: ReturnType<typeof setTimeout> | null = null;
  let latestRequestId = 0;

  const clearState = () => {
    suggestions.value = [];
    isLoading.value = false;
    error.value = null;
  };

  const executeAutocomplete = async (
    nextQuery: string,
    nextFilter: NutritionSearchFilter,
  ) => {
    const requestId = ++latestRequestId;
    isLoading.value = true;
    error.value = null;

    try {
      const nextSuggestions = await nutritionApi.autocompleteFoods({
        query: nextQuery,
        filter: nextFilter,
      });

      if (requestId !== latestRequestId) {
        return;
      }

      suggestions.value = nextSuggestions;
    } catch {
      if (requestId !== latestRequestId) {
        return;
      }

      suggestions.value = [];
      error.value = "Unable to load suggestions right now.";
    } finally {
      if (requestId === latestRequestId) {
        isLoading.value = false;
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

      if (nextQuery.length < AUTOCOMPLETE_MIN_QUERY_LENGTH) {
        latestRequestId++;
        clearState();
        return;
      }

      debounceTimeout = setTimeout(() => {
        debounceTimeout = null;
        void executeAutocomplete(nextQuery, nextFilter);
      }, AUTOCOMPLETE_DEBOUNCE_MS);

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
    suggestions,
    isLoading,
    error,
    minQueryLength: AUTOCOMPLETE_MIN_QUERY_LENGTH,
  };
}
