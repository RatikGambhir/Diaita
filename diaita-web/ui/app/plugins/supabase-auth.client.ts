import type { Session } from "@supabase/supabase-js";
import { supabase } from "~/utils";
import { useUserStore } from "~/stores/useUserStore";

/**
 * Keeps the Pinia user store in step with the real Supabase session.
 *
 * The store is first seeded from localStorage so a refresh renders without an auth flash, then
 * reconciled against Supabase — which is what picks up a magic-link session, a token refresh, or a
 * sign-out that happened in another tab.
 */
export default defineNuxtPlugin(async () => {
  const userStore = useUserStore();
  const { fetchProfile } = useUserProfile();

  userStore.hydrateAuthState();

  const syncSession = async (session: Session | null) => {
    userStore.addUserSession(session?.user ?? null, session ?? null);

    if (!session?.user?.id) {
      userStore.resetProfileState();
      return;
    }

    try {
      await fetchProfile();
    } catch (error) {
      // A profile that fails to load must not block the session from being established; the
      // profile-setup banner and route guards handle a missing profile on their own.
      console.error("Error bootstrapping user profile:", error);
    }
  };

  try {
    const {
      data: { session },
    } = await supabase.auth.getSession();

    await syncSession(session);
  } catch (error) {
    console.error("Error restoring Supabase session:", error);
  }

  supabase.auth.onAuthStateChange((_event, nextSession) => {
    void syncSession(nextSession);
  });
});
