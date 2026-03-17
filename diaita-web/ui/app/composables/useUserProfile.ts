import axios from "axios";
import { userApi } from "~/api/user";
import type { RegisteredUserProfile } from "~/types/ProfileTypes";

let inFlightProfileRequest: Promise<RegisteredUserProfile | null> | null = null;

export const useUserProfile = () => {
  const userStore = useUserStore();

  const fetchProfile = async (
    options: { force?: boolean } = {},
  ): Promise<RegisteredUserProfile | null> => {
    const userId = userStore.getUser?.id?.trim();
    if (!userId) {
      userStore.resetProfileState();
      return null;
    }

    if (!options.force) {
      if (
        userStore.getProfileStatus === "loaded"
        && userStore.getProfile?.userId === userId
      ) {
        return userStore.getProfile;
      }

      if (userStore.getProfileStatus === "missing") {
        return null;
      }

      if (userStore.getProfileStatus === "loading" && inFlightProfileRequest) {
        return inFlightProfileRequest;
      }
    }

    userStore.setProfileStatus("loading");

    inFlightProfileRequest = userApi
      .getUserProfile(userId)
      .then((profile) => {
        userStore.setProfile(profile);
        userStore.setProfileStatus("loaded");
        return profile;
      })
      .catch((error: unknown) => {
        if (axios.isAxiosError(error) && error.response?.status === 404) {
          userStore.setProfile(null);
          userStore.setRecommendation(null);
          userStore.setProfileStatus("missing");
          return null;
        }

        userStore.setProfileStatus("idle");
        throw error;
      })
      .finally(() => {
        inFlightProfileRequest = null;
      });

    return inFlightProfileRequest;
  };

  const hasCompletedProfile = computed(() => userStore.hasCompletedProfile);

  return {
    fetchProfile,
    hasCompletedProfile,
  };
};
