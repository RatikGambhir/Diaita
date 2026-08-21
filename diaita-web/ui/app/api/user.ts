import { apiClient } from "~/api/client";
import type {
  Recommendation,
  RegisterUserProfileRequest,
  RegisterUserProfileResponse,
  RegisteredUserProfile,
} from "~/types/ProfileTypes";

export const userApi = {
  async createUserProfile(
    data: RegisterUserProfileRequest,
  ): Promise<RegisterUserProfileResponse> {
    const response = await apiClient.post<RegisterUserProfileResponse>(
      "/user/profile",
      data,
    );
    return response.data;
  },

  async getUserProfile(userId: string): Promise<RegisteredUserProfile> {
    const response = await apiClient.get<RegisteredUserProfile>(
      `/user/profile/${encodeURIComponent(userId)}`,
    );
    return response.data;
  },

  async getRecommendations(userId: string): Promise<Recommendation> {
    const response = await apiClient.get<Recommendation>(
      `/users/${encodeURIComponent(userId)}/recommendations`,
    );
    return response.data;
  },

  /**
   * Regenerates the user's plan from their stored profile. `preferences` are extra free-form hints
   * for this run only (what the plan generator form collects); they are not persisted.
   */
  async generateRecommendations(
    userId: string,
    preferences: Record<string, string> = {},
  ): Promise<Recommendation> {
    const response = await apiClient.post<Recommendation>(
      `/users/${encodeURIComponent(userId)}/recommendations/generate`,
      { preferences },
    );
    return response.data;
  },
};
