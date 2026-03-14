import { supabase } from "~/utils";
import { useUserStore } from "~/stores/useUserStore";

export default defineNuxtPlugin(async () => {
  const userStore = useUserStore();
  userStore.hydrateAuthState();

  const {
    data: { session },
  } = await supabase.auth.getSession();

  userStore.addUserSession(session?.user ?? null, session ?? null);

  supabase.auth.onAuthStateChange((_event, nextSession) => {
    userStore.addUserSession(nextSession?.user ?? null, nextSession ?? null);
  });
});
