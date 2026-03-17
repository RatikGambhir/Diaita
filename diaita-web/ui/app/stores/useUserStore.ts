import type { Session, User } from "@supabase/supabase-js";
import { defineStore } from "pinia";
import type { Recommendation, RegisteredUserProfile } from "~/types/ProfileTypes";

const USER_STATE_STORAGE_KEY = "diaita-user-state";

type PersistedUserState = {
  user: User | null;
  session: Session | null;
};

export type ProfileStatus = "idle" | "loading" | "loaded" | "missing";

export const useUserStore = defineStore("user", {
  state: () => ({
    user: null as User | null,
    session: null as Session | null,
    error: null as string | null,
    profile: null as RegisteredUserProfile | null,
    recommendation: null as Recommendation | null,
    profileStatus: "idle" as ProfileStatus,
  }),
  getters: {
    getUser: (state) => state.user,
    getSession: (state) => state.session,
    getError: (state) => state.error,
    getProfile: (state) => state.profile,
    getRecommendation: (state) => state.recommendation,
    getProfileStatus: (state) => state.profileStatus,
    hasCompletedProfile: (state) =>
      state.profileStatus === "loaded" && state.profile !== null,
  },
  actions: {
    addUserSession(user: User | null, session: Session | null) {
      const previousUserId = this.user?.id ?? null;
      const nextUserId = user?.id ?? null;

      if (previousUserId !== nextUserId) {
        this.resetProfileState();
      }

      this.user = user;
      this.session = session;
      this.persistAuthState();
    },
    setProfile(profile: RegisteredUserProfile | null) {
      this.profile = profile;
    },
    setRecommendation(recommendation: Recommendation | null) {
      this.recommendation = recommendation;
    },
    setProfileStatus(status: ProfileStatus) {
      this.profileStatus = status;
    },
    resetProfileState() {
      this.profile = null;
      this.recommendation = null;
      this.profileStatus = "idle";
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
      this.error = null;
      this.resetProfileState();

      if (import.meta.client) {
        localStorage.removeItem(USER_STATE_STORAGE_KEY);
      }
    },
  },
});
