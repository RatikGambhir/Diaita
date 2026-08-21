import { apiClient } from "~/api/client";
import type {
  CreateWorkoutRequest,
  ExerciseSearchParams,
  ExerciseSearchResponse,
  UpdateWorkoutRequest,
  Workout,
  WorkoutListResponse,
  WorkoutStats,
} from "~/types/WorkoutTypes";

type ListWorkoutsParams = {
  userId: string;
  query?: string;
  page?: number;
  pageSize?: number;
};

type WorkoutStatsParams = {
  userId: string;
  start?: string;
  end?: string;
};

export const workoutsApi = {
  async list({
    userId,
    query,
    page = 0,
    pageSize = 50,
  }: ListWorkoutsParams): Promise<WorkoutListResponse> {
    const trimmedQuery = query?.trim();

    const response = await apiClient.get<WorkoutListResponse>("/workouts", {
      params: {
        userId,
        page,
        pageSize,
        ...(trimmedQuery ? { query: trimmedQuery } : {}),
      },
    });

    return response.data;
  },

  async get(workoutId: string, userId: string): Promise<Workout> {
    const response = await apiClient.get<Workout>(
      `/workouts/${encodeURIComponent(workoutId)}`,
      { params: { userId } },
    );

    return response.data;
  },

  async create(payload: CreateWorkoutRequest): Promise<Workout> {
    const response = await apiClient.post<Workout>("/workouts", payload);
    return response.data;
  },

  async update(
    workoutId: string,
    payload: UpdateWorkoutRequest,
  ): Promise<Workout> {
    const response = await apiClient.put<Workout>(
      `/workouts/${encodeURIComponent(workoutId)}`,
      payload,
    );

    return response.data;
  },

  async remove(workoutId: string, userId: string): Promise<void> {
    await apiClient.delete(`/workouts/${encodeURIComponent(workoutId)}`, {
      params: { userId },
    });
  },

  async stats({ userId, start, end }: WorkoutStatsParams): Promise<WorkoutStats> {
    const response = await apiClient.get<WorkoutStats>("/workouts/stats", {
      params: {
        userId,
        ...(start ? { start } : {}),
        ...(end ? { end } : {}),
      },
    });

    return response.data;
  },

  /**
   * Searches the read-only exercise library. The service rejects a request with no filters, so an
   * empty search resolves to an empty list rather than a 400.
   */
  async searchExercises(
    params: ExerciseSearchParams,
  ): Promise<ExerciseSearchResponse> {
    const hasFilter = Boolean(
      params.exercise?.trim()
      || params.exerciseType?.trim()
      || params.exerciseVariation?.trim()
      || params.primaryFitnessFocus?.trim(),
    );

    if (!hasFilter) {
      return {
        exercises: [],
        pagination: {
          total: 0,
          page: 0,
          pageSize: params.pageSize ?? 20,
          totalPages: 0,
          hasMore: false,
          hasPrevious: false,
        },
      };
    }

    const response = await apiClient.post<ExerciseSearchResponse>(
      "/workouts/search",
      params,
    );

    return response.data;
  },
};
