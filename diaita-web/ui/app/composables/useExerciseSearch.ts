import { computed, onBeforeUnmount, ref, watch } from "vue";
import { workoutsApi } from "~/api/workouts";
import type { ExerciseLibraryEntry } from "~/types/WorkoutTypes";

const SEARCH_DEBOUNCE_MS = 250;
const MIN_QUERY_LENGTH = 2;

/**
 * Debounced search over the read-only exercise library (POST /workouts/search). Stale responses are
 * discarded so a slow request can never overwrite the results of a newer one.
 */
export function useExerciseSearch(pageSize = 25) {
  const query = ref("");
  const results = ref<ExerciseLibraryEntry[]>([]);
  const isSearching = ref(false);
  const error = ref<string | null>(null);
  const hasSearched = ref(false);

  const normalizedQuery = computed(() => query.value.trim());
  const isQueryTooShort = computed(
    () => normalizedQuery.value.length > 0 && normalizedQuery.value.length < MIN_QUERY_LENGTH,
  );

  let debounceTimeout: ReturnType<typeof setTimeout> | null = null;
  let latestRequestId = 0;

  const clearPendingSearch = () => {
    if (debounceTimeout) {
      clearTimeout(debounceTimeout);
      debounceTimeout = null;
    }
  };

  const reset = () => {
    latestRequestId++;
    clearPendingSearch();
    query.value = "";
    results.value = [];
    isSearching.value = false;
    error.value = null;
    hasSearched.value = false;
  };

  const executeSearch = async (nextQuery: string) => {
    const requestId = ++latestRequestId;
    isSearching.value = true;
    error.value = null;

    try {
      const response = await workoutsApi.searchExercises({
        exercise: nextQuery,
        pageSize,
      });

      if (requestId !== latestRequestId) {
        return;
      }

      results.value = response.exercises;
    } catch (searchError) {
      if (requestId !== latestRequestId) {
        return;
      }

      results.value = [];
      error.value =
        searchError instanceof Error
          ? searchError.message
          : "Failed to search exercises.";
    } finally {
      if (requestId === latestRequestId) {
        isSearching.value = false;
        hasSearched.value = true;
      }
    }
  };

  watch(normalizedQuery, (nextQuery) => {
    clearPendingSearch();

    if (nextQuery.length < MIN_QUERY_LENGTH) {
      latestRequestId++;
      results.value = [];
      isSearching.value = false;
      hasSearched.value = false;
      return;
    }

    debounceTimeout = setTimeout(() => {
      void executeSearch(nextQuery);
    }, SEARCH_DEBOUNCE_MS);
  });

  onBeforeUnmount(clearPendingSearch);

  return {
    query,
    results,
    isSearching,
    error,
    hasSearched,
    isQueryTooShort,
    minQueryLength: MIN_QUERY_LENGTH,
    reset,
  };
}
