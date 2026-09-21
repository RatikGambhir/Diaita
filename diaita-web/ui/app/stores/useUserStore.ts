import { defineStore } from "pinia";
import type { AuthSession, AuthUser } from "~/types/AuthTypes";
import type { Recommendation, RegisteredUserProfile } from "~/types/ProfileTypes";

export const AUTH_STATE_STORAGE_KEY = "diaita-auth-state";

type PersistedAuthState = {
  user: AuthUser;
  accessToken: string;
  expiresAt: string;
};

export type ProfileStatus = "idle" | "loading" | "loaded" | "missing";

export const useUserStore = defineStore("user", {
  state: () => ({
    user: null as AuthUser | null,
    accessToken: null as string | null,
    expiresAt: null as string | null,
    error: null as string | null,
    profile: null as RegisteredUserProfile | null,
    recommendation: null as Recommendation | null,
    profileStatus: "idle" as ProfileStatus,
  }),
  getters: {
    getUser: (state) => state.user,
    getAccessToken: (state) => state.accessToken,
    getError: (state) => state.error,
    getProfile: (state) => state.profile,
    getRecommendation: (state) => state.recommendation,
    getProfileStatus: (state) => state.profileStatus,
    isAuthenticated: (state) => Boolean(
      state.user
      && state.accessToken
      && state.expiresAt
      && Date.parse(state.expiresAt) > Date.now(),
    ),
    hasCompletedProfile: (state) => state.profileStatus === "loaded" && state.profile !== null,
  },
  actions: {
    setSession(session: AuthSession) {
      if (this.user?.id !== session.user.id) this.resetProfileState();
      this.user = session.user;
      this.accessToken = session.accessToken;
      this.expiresAt = session.expiresAt;
      this.error = null;
      this.persistAuthState();
    },
    setUser(user: AuthUser) {
      this.user = user;
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
      if (!import.meta.client) return;
      const storedValue = localStorage.getItem(AUTH_STATE_STORAGE_KEY);
      if (!storedValue) return;
      try {
        const parsed = JSON.parse(storedValue) as PersistedAuthState;
        if (!parsed.accessToken || !parsed.user || Date.parse(parsed.expiresAt) <= Date.now()) {
          this.clearSession();
          return;
        }
        this.user = parsed.user;
        this.accessToken = parsed.accessToken;
        this.expiresAt = parsed.expiresAt;
      } catch {
        this.clearSession();
      }
    },
    persistAuthState() {
      if (!import.meta.client) return;
      if (!this.user || !this.accessToken || !this.expiresAt) {
        localStorage.removeItem(AUTH_STATE_STORAGE_KEY);
        return;
      }
      localStorage.setItem(AUTH_STATE_STORAGE_KEY, JSON.stringify({
        user: this.user,
        accessToken: this.accessToken,
        expiresAt: this.expiresAt,
      } satisfies PersistedAuthState));
    },
    clearSession() {
      this.user = null;
      this.accessToken = null;
      this.expiresAt = null;
      this.error = null;
      this.resetProfileState();
      if (import.meta.client) localStorage.removeItem(AUTH_STATE_STORAGE_KEY);
    },
  },
});
