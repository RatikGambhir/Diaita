import { ref } from "vue";
import { workoutsApi } from "~/api/workouts";
import { useUserStore } from "~/stores/useUserStore";
import type { WorkoutStats } from "~/types/WorkoutTypes";

type StatsRange = {
  start?: string;
  end?: string;
};

/**
 * Fetches aggregated workout stats for the signed-in user. Shared by the dashboard tiles and the
 * workouts performance tab, which want the same numbers over different windows.
 */
export const useWorkoutStats = () => {
  const userStore = useUserStore();

  const stats = ref<WorkoutStats | null>(null);
  const isLoading = ref(false);
  const error = ref<string | null>(null);

  const loadStats = async (range: StatsRange = {}) => {
    const userId = userStore.getUser?.id?.trim();
    if (!userId) {
      stats.value = null;
      return;
    }

    isLoading.value = true;
    error.value = null;

    try {
      stats.value = await workoutsApi.stats({ userId, ...range });
    } catch (statsError) {
      stats.value = null;
      error.value =
        statsError instanceof Error
          ? statsError.message
          : "Failed to load workout stats.";
    } finally {
      isLoading.value = false;
    }
  };

  return {
    stats,
    isLoading,
    error,
    loadStats,
  };
};
