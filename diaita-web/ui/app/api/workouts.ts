import { apiClient } from "~/api/client";
import type {
  ExerciseSearchResponse,
  UpsertWorkout,
  WorkoutLog,
  WorkoutStats,
} from "~/types/WorkoutTypes";

export const workoutApi = {
  async list(query?: string): Promise<WorkoutLog[]> {
    const response = await apiClient.get<WorkoutLog[]>("/workouts", { params: { query } });
    return response.data;
  },
  async get(id: string): Promise<WorkoutLog> {
    const response = await apiClient.get<WorkoutLog>(`/workouts/${encodeURIComponent(id)}`);
    return response.data;
  },
  async create(payload: UpsertWorkout): Promise<WorkoutLog> {
    const response = await apiClient.post<WorkoutLog>("/workouts", payload);
    return response.data;
  },
  async update(id: string, payload: UpsertWorkout): Promise<WorkoutLog> {
    const response = await apiClient.put<WorkoutLog>(`/workouts/${encodeURIComponent(id)}`, payload);
    return response.data;
  },
  async remove(id: string): Promise<void> {
    await apiClient.delete(`/workouts/${encodeURIComponent(id)}`);
  },
  async stats(): Promise<WorkoutStats> {
    const response = await apiClient.get<WorkoutStats>("/workouts/stats");
    return response.data;
  },
  async searchExercises(query: string): Promise<ExerciseSearchResponse> {
    const response = await apiClient.post<ExerciseSearchResponse>("/workouts/search", {
      query,
      page: 0,
      pageSize: 50,
    });
    return response.data;
  },
};
