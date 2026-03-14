import { defineStore } from "pinia";
import type { UserProfile } from "~/types";

const USER_STATE_STORAGE_KEY = "diaita-user-state";

type PersistedUserState = {
  user: object | null;
  session: object | null;
};

export const useUserStore = defineStore("user", {
  state: () => ({
    user: null as object | null,
    session: null as object | null,
    error: null as string | null,
    profile: null as UserProfile | null,
  }),
  getters: {
    getUser: (state) => state.user,
    getSession: (state) => state.session,
    getError: (state) => state.error,
    getProfile: (state) => state.profile,
    hasCompletedProfile: (state) => state.profile !== null,
  },
  actions: {
    addUserSession(user: object | null, session: object | null) {
      this.user = user;
      this.session = session;
      this.persistAuthState();
    },
    setProfile(profile: UserProfile | null) {
      this.profile = profile;
    },
    hydrateAuthState() {
      if (!import.meta.client) {
        return;
      }

      const storedValue = localStorage.getItem(USER_STATE_STORAGE_KEY);
      if (!storedValue) {
        return;
      }

      try {
        const parsed = JSON.parse(storedValue) as PersistedUserState;
        this.user = parsed.user ?? null;
        this.session = parsed.session ?? null;
      } catch {
        localStorage.removeItem(USER_STATE_STORAGE_KEY);
      }
    },
    persistAuthState() {
      if (!import.meta.client) {
        return;
      }

      localStorage.setItem(
        USER_STATE_STORAGE_KEY,
        JSON.stringify({
          user: this.user,
          session: this.session,
        } satisfies PersistedUserState),
      );
    },
    clearSession() {
      this.user = null;
      this.session = null;
      this.profile = null;
      this.error = null;

      if (import.meta.client) {
        localStorage.removeItem(USER_STATE_STORAGE_KEY);
      }
    },
  },
});
