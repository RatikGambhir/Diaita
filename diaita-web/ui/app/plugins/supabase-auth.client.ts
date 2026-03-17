import type { Session } from "@supabase/supabase-js";
import { supabase } from "~/utils";
import { useUserStore } from "~/stores/useUserStore";

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
      console.error("Error bootstrapping user profile:", error);
    }
  };

  const {
    data: { session },
  } = await supabase.auth.getSession();

  await syncSession(session);

  supabase.auth.onAuthStateChange((_event, nextSession) => {
    void syncSession(nextSession);
  });
});
