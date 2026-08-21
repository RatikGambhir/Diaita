import { computed, ref } from "vue";
import { workoutsApi } from "~/api/workouts";
import { useToast } from "~/composables/useToast";
import { useUserStore } from "~/stores/useUserStore";
import type {
  CreateWorkoutRequest,
  UpdateWorkoutRequest,
  Workout,
} from "~/types/WorkoutTypes";

const describeError = (error: unknown, fallback: string): string => {
  if (typeof error === "object" && error !== null && "response" in error) {
    const response = (error as { response?: { data?: unknown } }).response;
    if (typeof response?.data === "string" && response.data.trim()) {
      return response.data;
    }
  }

  return error instanceof Error ? error.message : fallback;
};

/**
 * Loads and mutates the signed-in user's logged workouts. Every mutation refreshes from the
 * service response rather than patching local state, so the list always reflects what was stored.
 */
export const useWorkouts = () => {
  const userStore = useUserStore();
  const toast = useToast();

  const workouts = ref<Workout[]>([]);
  const isLoading = ref(false);
  const isMutating = ref(false);
  const loadError = ref<string | null>(null);

  const currentUserId = computed(() => userStore.getUser?.id?.trim() ?? null);

  const requireUserId = (action: string): string | null => {
    const userId = currentUserId.value;
    if (!userId) {
      toast.add({
        title: `Unable to ${action}`,
        description: "No signed-in user was found.",
        color: "error",
      });
    }
    return userId ?? null;
  };

  const loadWorkouts = async (query?: string) => {
    const userId = currentUserId.value;
    if (!userId) {
      workouts.value = [];
      return;
    }

    isLoading.value = true;
    loadError.value = null;

    try {
      const response = await workoutsApi.list({ userId, query });
      workouts.value = response.workouts;
    } catch (error) {
      workouts.value = [];
      loadError.value = describeError(error, "Failed to load workouts.");
      toast.add({
        title: "Unable to load workouts",
        description: loadError.value,
        color: "error",
      });
    } finally {
      isLoading.value = false;
    }
  };

  const createWorkout = async (
    payload: Omit<CreateWorkoutRequest, "userId">,
  ): Promise<Workout | null> => {
    const userId = requireUserId("create workout");
    if (!userId) {
      return null;
    }

    isMutating.value = true;

    try {
      const workout = await workoutsApi.create({ ...payload, userId });
      workouts.value = [workout, ...workouts.value];
      toast.add({
        title: "Workout created",
        description: `${workout.name} is ready to fill in.`,
        color: "success",
      });
      return workout;
    } catch (error) {
      toast.add({
        title: "Unable to create workout",
        description: describeError(error, "Failed to create the workout."),
        color: "error",
      });
      return null;
    } finally {
      isMutating.value = false;
    }
  };

  const updateWorkout = async (
    workoutId: string,
    payload: Omit<UpdateWorkoutRequest, "userId">,
  ): Promise<Workout | null> => {
    const userId = requireUserId("save workout");
    if (!userId) {
      return null;
    }

    isMutating.value = true;

    try {
      const workout = await workoutsApi.update(workoutId, { ...payload, userId });
      workouts.value = workouts.value.map((item) =>
        item.id === workout.id ? workout : item,
      );
      return workout;
    } catch (error) {
      toast.add({
        title: "Unable to save workout",
        description: describeError(error, "Failed to save the workout."),
        color: "error",
      });
      return null;
    } finally {
      isMutating.value = false;
    }
  };

  const deleteWorkout = async (workoutId: string): Promise<boolean> => {
    const userId = requireUserId("delete workout");
    if (!userId) {
      return false;
    }

    isMutating.value = true;

    try {
      await workoutsApi.remove(workoutId, userId);
      workouts.value = workouts.value.filter((item) => item.id !== workoutId);
      toast.add({ title: "Workout deleted", color: "success" });
      return true;
    } catch (error) {
      toast.add({
        title: "Unable to delete workout",
        description: describeError(error, "Failed to delete the workout."),
        color: "error",
      });
      return false;
    } finally {
      isMutating.value = false;
    }
  };

  return {
    workouts,
    isLoading,
    isMutating,
    loadError,
    currentUserId,
    loadWorkouts,
    createWorkout,
    updateWorkout,
    deleteWorkout,
    describeError,
  };
};
