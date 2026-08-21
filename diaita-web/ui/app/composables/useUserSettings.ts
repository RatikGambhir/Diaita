import { reactive, ref } from "vue";
import { settingsApi } from "~/api/settings";
import { useToast } from "~/composables/useToast";
import { useUserStore } from "~/stores/useUserStore";
import type {
  UserSettings,
  UserSettingsPage,
  UserSettingsPayloads,
} from "~/types/SettingsTypes";

export const USER_SETTINGS_PAGES: UserSettingsPage[] = [
  "basic-demographics",
  "activity-lifestyle",
  "goals-priorities",
  "training-background",
  "nutrition-history",
];

const emptySettings = (): UserSettings => ({
  "basic-demographics": null,
  "activity-lifestyle": null,
  "goals-priorities": null,
  "training-background": null,
  "nutrition-history": null,
});

const describeError = (error: unknown, fallback: string): string => {
  if (typeof error === "object" && error !== null && "response" in error) {
    const response = (error as { response?: { data?: unknown } }).response;
    if (typeof response?.data === "string" && response.data.trim()) {
      return response.data;
    }
  }

  return error instanceof Error ? error.message : fallback;
};

/**
 * Reads and writes the user's settings sections through the service. Sections load in parallel and
 * each saves independently, so one tab's failure never discards another tab's edits.
 */
export const useUserSettings = () => {
  const userStore = useUserStore();
  const toast = useToast();

  const settings = reactive<UserSettings>(emptySettings());
  const isLoading = ref(false);
  const savingPage = ref<UserSettingsPage | null>(null);
  const loadError = ref<string | null>(null);

  const requireUserId = (): string | null => userStore.getUser?.id?.trim() ?? null;

  const loadSettings = async () => {
    const userId = requireUserId();
    if (!userId) {
      Object.assign(settings, emptySettings());
      return;
    }

    isLoading.value = true;
    loadError.value = null;

    try {
      const sections = await Promise.all(
        USER_SETTINGS_PAGES.map((page) => settingsApi.get(userId, page)),
      );

      USER_SETTINGS_PAGES.forEach((page, index) => {
        // Indices line up with USER_SETTINGS_PAGES, but TypeScript can't narrow the union per page.
        settings[page] = sections[index] as never;
      });
    } catch (error) {
      Object.assign(settings, emptySettings());
      loadError.value = describeError(error, "Failed to load your settings.");
      toast.add({
        title: "Unable to load settings",
        description: loadError.value,
        color: "error",
      });
    } finally {
      isLoading.value = false;
    }
  };

  const saveSection = async <Page extends UserSettingsPage>(
    page: Page,
    payload: UserSettingsPayloads[Page],
    label: string,
  ): Promise<boolean> => {
    const userId = requireUserId();
    if (!userId) {
      toast.add({
        title: "Unable to save settings",
        description: "No signed-in user was found.",
        color: "error",
      });
      return false;
    }

    savingPage.value = page;

    try {
      settings[page] = (await settingsApi.update(userId, page, payload)) as never;
      toast.add({
        title: `${label} saved`,
        color: "success",
      });
      return true;
    } catch (error) {
      toast.add({
        title: `Unable to save ${label.toLowerCase()}`,
        description: describeError(error, "The change was not saved."),
        color: "error",
      });
      return false;
    } finally {
      savingPage.value = null;
    }
  };

  return {
    settings,
    isLoading,
    savingPage,
    loadError,
    loadSettings,
    saveSection,
  };
};
