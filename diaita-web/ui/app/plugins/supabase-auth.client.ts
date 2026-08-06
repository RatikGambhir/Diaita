import { useUserStore } from "~/stores/useUserStore";

/**
 * Restores the persisted auth state on the client before the app renders.
 *
 * Note: this does not currently subscribe to `supabase.auth.onAuthStateChange`,
 * so the store is only as fresh as what was last persisted by `addUserSession`.
 */
export default defineNuxtPlugin(() => {
  useUserStore().hydrateAuthState();
});
