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
};
