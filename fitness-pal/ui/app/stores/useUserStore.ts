import { defineStore } from "pinia";
import type { UserProfile } from "~/types";

export const useUserStore = defineStore("user", {
  state: () => ({ user: {}, session: {}, error: null, profile: null as UserProfile | null }),
  getters: {
    getUser: (state) => state.user,
    getSession: (state) => state.session,
    getError: (state) => state.error,
    getProfile: (state) => state.profile,
    hasCompletedProfile: (state) => state.profile !== null,
  },
  actions: {
    addUserSession(user: object, session: object) {
      this.user = user;
      this.session = session;
    },
    setProfile(profile: UserProfile | null) {
      this.profile = profile;
    },
    clearSession() {
      this.user = {};
      this.session = {};
      this.profile = null;
      this.error = null;
    },
  },
});
