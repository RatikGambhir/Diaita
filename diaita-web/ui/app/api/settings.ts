import axios from "axios";
import { apiClient } from "~/api/client";
import type {
  UserSettingsPage,
  UserSettingsPayloads,
} from "~/types/SettingsTypes";

const settingsPath = (userId: string) =>
  `/user/settings/${encodeURIComponent(userId)}`;

export const settingsApi = {
  /**
   * Reads one settings section. A section the user has never filled in returns 404, which is a
   * normal empty state rather than an error, so it resolves to null.
   */
  async get<Page extends UserSettingsPage>(
    userId: string,
    page: Page,
  ): Promise<UserSettingsPayloads[Page] | null> {
    try {
      const response = await apiClient.get<UserSettingsPayloads[Page]>(
        settingsPath(userId),
        { params: { page, action: "get" } },
      );

      return response.data;
    } catch (error) {
      if (axios.isAxiosError(error) && error.response?.status === 404) {
        return null;
      }

      throw error;
    }
  },

  async update<Page extends UserSettingsPage>(
    userId: string,
    page: Page,
    payload: UserSettingsPayloads[Page],
  ): Promise<UserSettingsPayloads[Page]> {
    const response = await apiClient.put<UserSettingsPayloads[Page]>(
      settingsPath(userId),
      payload,
      { params: { page, action: "update" } },
    );

    return response.data;
  },

  async remove(userId: string, page: UserSettingsPage): Promise<void> {
    await apiClient.delete(settingsPath(userId), {
      params: { page, action: "delete" },
    });
  },
};
