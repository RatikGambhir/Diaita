import axios from "axios";
import { AUTH_STATE_STORAGE_KEY } from "~/stores/useUserStore";

const API_BASE_URL = import.meta.env.NUXT_PUBLIC_API_URL || "http://localhost:8080";

export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: false,
});

apiClient.interceptors.request.use((config) => {
  if (import.meta.client) {
    const stored = localStorage.getItem(AUTH_STATE_STORAGE_KEY);
    if (stored) {
      try {
        const token = (JSON.parse(stored) as { accessToken?: string }).accessToken;
        if (token) config.headers.Authorization = `Bearer ${token}`;
      } catch {
        localStorage.removeItem(AUTH_STATE_STORAGE_KEY);
      }
    }
  }
  return config;
});
