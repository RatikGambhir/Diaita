import { apiClient } from "~/api/client";
import type {
  AuthSession,
  AuthUser,
  LoginRequest,
  RegisterRequest,
} from "~/types/AuthTypes";

export const authApi = {
  async register(payload: RegisterRequest): Promise<AuthSession> {
    const response = await apiClient.post<AuthSession>("/auth/register", payload);
    return response.data;
  },

  async login(payload: LoginRequest): Promise<AuthSession> {
    const response = await apiClient.post<AuthSession>("/auth/login", payload);
    return response.data;
  },

  async me(): Promise<AuthUser> {
    const response = await apiClient.get<AuthUser>("/auth/me");
    return response.data;
  },

  async logout(): Promise<void> {
    await apiClient.post("/auth/logout");
  },
};
