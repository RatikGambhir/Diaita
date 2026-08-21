import { reactive, ref, watch } from "vue";
import { supabase } from "~/utils";
import { useToast } from "~/composables/useToast";
import { useUserStore } from "~/stores/useUserStore";

export const DEFAULT_HOMEPAGE_WORKOUT_COUNT = 6;

export type AccountForm = {
  firstName: string;
  lastName: string;
  email: string;
  workoutsToTrackOnHomepage: string;
};

/**
 * The account tab is not part of the settings service: name and email belong to the Supabase auth
 * user, and the homepage workout count is a per-device display preference kept in a cookie.
 */
export const useAccountSettings = () => {
  const userStore = useUserStore();
  const toast = useToast();

  const homepageWorkoutCount = useCookie<number>("diaita-homepage-workout-count", {
    default: () => DEFAULT_HOMEPAGE_WORKOUT_COUNT,
    maxAge: 60 * 60 * 24 * 365,
    sameSite: "lax",
  });

  const accountForm = reactive<AccountForm>({
    firstName: "",
    lastName: "",
    email: "",
    workoutsToTrackOnHomepage: String(
      homepageWorkoutCount.value ?? DEFAULT_HOMEPAGE_WORKOUT_COUNT,
    ),
  });

  const isSavingAccount = ref(false);

  const resetAccountForm = () => {
    const user = userStore.getUser;
    const metadata = (user?.user_metadata ?? {}) as Record<string, unknown>;

    accountForm.firstName =
      typeof metadata.first_name === "string" ? metadata.first_name : "";
    accountForm.lastName =
      typeof metadata.last_name === "string" ? metadata.last_name : "";
    accountForm.email = user?.email ?? "";
    accountForm.workoutsToTrackOnHomepage = String(
      homepageWorkoutCount.value ?? DEFAULT_HOMEPAGE_WORKOUT_COUNT,
    );
  };

  watch(() => userStore.getUser?.id, () => {
    resetAccountForm();
  }, { immediate: true });

  const saveAccount = async () => {
    if (!userStore.getUser?.id) {
      toast.add({
        title: "Unable to save account",
        description: "No signed-in user was found.",
        color: "error",
      });
      return;
    }

    const parsedCount = Number(accountForm.workoutsToTrackOnHomepage.trim());
    if (!Number.isFinite(parsedCount) || parsedCount < 1) {
      toast.add({
        title: "Invalid workout count",
        description: "Enter how many recent workouts to show — at least 1.",
        color: "error",
      });
      return;
    }

    isSavingAccount.value = true;

    try {
      const { data, error } = await supabase.auth.updateUser({
        data: {
          first_name: accountForm.firstName.trim(),
          last_name: accountForm.lastName.trim(),
        },
      });

      if (error) {
        throw error;
      }

      homepageWorkoutCount.value = Math.round(parsedCount);

      if (data.user) {
        userStore.addUserSession(data.user, userStore.getSession);
      }

      resetAccountForm();

      toast.add({ title: "Account saved", color: "success" });
    } catch (error) {
      toast.add({
        title: "Unable to save account",
        description:
          error instanceof Error ? error.message : "The change was not saved.",
        color: "error",
      });
    } finally {
      isSavingAccount.value = false;
    }
  };

  return {
    accountForm,
    homepageWorkoutCount,
    isSavingAccount,
    resetAccountForm,
    saveAccount,
  };
};
