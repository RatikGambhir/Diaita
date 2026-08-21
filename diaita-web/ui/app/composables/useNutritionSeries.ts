import { ref } from "vue";
import { nutritionApi } from "~/api/nutrition";
import { useUserStore } from "~/stores/useUserStore";
import type {
  NutritionDailySeries,
  NutritionDaySummary,
} from "~/types/NutritionTypes";

export const toIsoDate = (date: Date) => {
  const year = date.getFullYear();
  const month = `${date.getMonth() + 1}`.padStart(2, "0");
  const day = `${date.getDate()}`.padStart(2, "0");
  return `${year}-${month}-${day}`;
};

/**
 * Loads the signed-in user's nutrition history: today's summary for the stat tiles, and a per-day
 * series for the trend charts. Both carry the recommended macro targets when one has been generated.
 */
export const useNutritionSeries = () => {
  const userStore = useUserStore();

  const summary = ref<NutritionDaySummary | null>(null);
  const series = ref<NutritionDailySeries | null>(null);
  const isLoading = ref(false);
  const error = ref<string | null>(null);

  const describe = (unknownError: unknown) =>
    unknownError instanceof Error
      ? unknownError.message
      : "Failed to load nutrition data.";

  const loadToday = async () => {
    const userId = userStore.getUser?.id?.trim();
    if (!userId) {
      summary.value = null;
      return;
    }

    isLoading.value = true;
    error.value = null;

    try {
      summary.value = await nutritionApi.getDaySummary(userId, toIsoDate(new Date()));
    } catch (summaryError) {
      summary.value = null;
      error.value = describe(summaryError);
    } finally {
      isLoading.value = false;
    }
  };

  const loadSeries = async (start: Date, end: Date) => {
    const userId = userStore.getUser?.id?.trim();
    if (!userId) {
      series.value = null;
      return;
    }

    isLoading.value = true;
    error.value = null;

    try {
      series.value = await nutritionApi.getDailySeries(
        userId,
        toIsoDate(start),
        toIsoDate(end),
      );
    } catch (seriesError) {
      series.value = null;
      error.value = describe(seriesError);
    } finally {
      isLoading.value = false;
    }
  };

  return {
    summary,
    series,
    isLoading,
    error,
    loadToday,
    loadSeries,
  };
};
