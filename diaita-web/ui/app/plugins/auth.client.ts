import { authApi } from "~/api/auth";
import { useUserStore } from "~/stores/useUserStore";

export default defineNuxtPlugin(async () => {
  const userStore = useUserStore();
  userStore.hydrateAuthState();
  if (!userStore.isAuthenticated) return;

  try {
    userStore.setUser(await authApi.me());
  } catch {
    userStore.clearSession();
  }
});
