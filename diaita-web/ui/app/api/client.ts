import axios from "axios";

const FALLBACK_API_BASE_URL = "http://localhost:8080";

/**
 * Resolves the service base URL from Nuxt's runtime config.
 *
 * This is read per request rather than once at module load, because
 * `useRuntimeConfig()` needs a Nuxt app instance and there is none yet while
 * modules are being evaluated. The previous `process.env` read resolved to
 * `undefined` in the browser, so every client-side call silently fell back to
 * localhost regardless of how the deployment was configured.
 */
const resolveBaseUrl = (): string => {
  try {
    const configuredUrl = useRuntimeConfig().public.apiBaseUrl;
    return configuredUrl || FALLBACK_API_BASE_URL;
  } catch {
    return FALLBACK_API_BASE_URL;
  }
};

export const apiClient = axios.create({
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: true,
});

apiClient.interceptors.request.use((config) => {
  config.baseURL = resolveBaseUrl();
  return config;
});
