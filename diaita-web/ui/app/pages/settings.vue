<script setup lang="ts">
import { computed, onMounted, reactive, watch } from 'vue'
import { Loader2 } from 'lucide-vue-next'
import GenericTabGroup from '~/components/GenericTabGroup.vue'
import ProfileSettings from '~/components/settings/ProfileSettings.vue'
import AccountSettings from '~/components/settings/AccountSettings.vue'
import LifestyleSettings from '~/components/settings/LifestyleSettings.vue'
import NutritionSettings from '~/components/settings/NutritionSettings.vue'
import TrainingSettings from '~/components/settings/TrainingSettings.vue'
import GoalsSettings from '~/components/settings/GoalsSettings.vue'
import { useUserSettings } from '~/composables/useUserSettings'
import { useAccountSettings } from '~/composables/useAccountSettings'
import { useToast } from '~/composables/useToast'
import { useUserStore } from '~/stores/useUserStore'
import {
    emptyGoalsForm,
    emptyLifestyleForm,
    emptyNutritionForm,
    emptyProfileForm,
    emptyTrainingForm,
    formToGoals,
    formToLifestyle,
    formToNutrition,
    formToProfile,
    formToTraining,
    goalsToForm,
    lifestyleToForm,
    nutritionToForm,
    profileToForm,
    trainingToForm,
} from '~/components/settings/mappers'

const activeTab = ref('profile');

const toast = useToast();
const userStore = useUserStore();
const { settings, isLoading, savingPage, loadSettings, saveSection } = useUserSettings();
const {
    accountForm,
    isSavingAccount,
    resetAccountForm,
    saveAccount,
} = useAccountSettings();

const tabs = [
    { value: "profile", label: "Profile" },
    { value: "account", label: "Account" },
    { value: "lifestyle", label: "Lifestyle" },
    { value: "nutrition", label: "Nutrition" },
    { value: "training", label: "Training" },
    { value: "goals", label: "Goals" },
];

const yesNoOptions = [
    { label: "Yes", value: "yes" },
    { label: "No", value: "no" },
];

const dietPatterns = [
    { label: "Balanced", value: "balanced" },
    { label: "Mediterranean", value: "mediterranean" },
    { label: "Plant-based", value: "plant-based" },
    { label: "Low carb", value: "low-carb" },
];

const skillLevels = [
    { label: "Beginner", value: "beginner" },
    { label: "Intermediate", value: "intermediate" },
    { label: "Advanced", value: "advanced" },
];

const trainingAgeOptions = [
    { label: "Less than 1 year", value: "0-1" },
    { label: "1-3 years", value: "1-3" },
    { label: "3-5 years", value: "3-5" },
    { label: "5+ years", value: "5plus" },
];

const equipmentOptions = [
    { label: "Gym access", value: "gym" },
    { label: "Home dumbbells", value: "dumbbells" },
    { label: "Bodyweight only", value: "bodyweight" },
];

const budgetOptions = [
    { label: "Under $50", value: "under-50" },
    { label: "$50-$100", value: "50-100" },
    { label: "$100-$200", value: "100-200" },
    { label: "$200+", value: "200-plus" },
];

const alcoholOptions = [
    { label: "Never", value: "never" },
    { label: "Occasionally", value: "occasionally" },
    { label: "Weekly", value: "weekly" },
];

const activityLevels = [
    { label: "Sedentary", value: "sedentary" },
    { label: "Lightly active", value: "lightly-active" },
    { label: "Moderately active", value: "moderately-active" },
    { label: "Very active", value: "very-active" },
    { label: "Extremely active", value: "extremely-active" },
];

const sleepQualityOptions = [
    { label: "Poor", value: "poor" },
    { label: "Fair", value: "fair" },
    { label: "Good", value: "good" },
    { label: "Excellent", value: "excellent" },
];

const stressLevels = [
    { label: "Low", value: "low" },
    { label: "Moderate", value: "moderate" },
    { label: "High", value: "high" },
];

const recoveryOptions = [
    { label: "Slow", value: "slow" },
    { label: "Average", value: "average" },
    { label: "Fast", value: "fast" },
];

/**
 * Placeholder hints only. Real values live in formState, which is populated from the service — these
 * are the example strings shown while a field is still empty.
 */
const formDefaults = {
    profile: {
        age: "", sex: "", gender: "", height: "", weight: "",
        bodyFat: "", leanMass: "", biological: "", menstrual: "",
    },
    account: { firstName: "", lastName: "", email: "", workoutsToTrackOnHomepage: "" },
    nutrition: {
        dietPattern: "", calorieTracking: "", cookingSkill: "", macroPreferences: "",
        allergies: "", dietaryRestrictions: "", culturalPreferences: "",
        foodBudget: "", alcoholIntake: "",
    },
    training: {
        trainingAge: "", trainingHistory: "", workoutRoutine: "", exercisePreferences: "",
        exerciseDislikes: "", equipmentAccess: "", timePerSession: "", daysPerWeek: "",
    },
    goals: {
        primaryGoal: "", secondaryGoals: "", timeframe: "", targetWeight: "",
        performanceMetric: "", aestheticGoals: "", healthGoals: "",
    },
};

const formState = reactive({
    profile: emptyProfileForm(),
    account: accountForm,
    lifestyle: emptyLifestyleForm(),
    nutrition: emptyNutritionForm(),
    training: emptyTrainingForm(),
    goals: emptyGoalsForm(),
});

/** Re-seeds every form from the loaded sections; also serves as the Cancel handler. */
const resetForms = () => {
    Object.assign(formState.profile, profileToForm(settings['basic-demographics']));
    Object.assign(formState.lifestyle, lifestyleToForm(settings['activity-lifestyle']));
    Object.assign(formState.nutrition, nutritionToForm(settings['nutrition-history']));
    Object.assign(formState.training, trainingToForm(settings['training-background']));
    Object.assign(formState.goals, goalsToForm(settings['goals-priorities']));
    resetAccountForm();
};

onMounted(async () => {
    await loadSettings();
    resetForms();
});

// A sign-in or sign-out that lands while this page is open should re-read the settings.
watch(() => userStore.getUser?.id, async (userId, previousUserId) => {
    if (userId === previousUserId) {
        return;
    }
    await loadSettings();
    resetForms();
});

const warnMissing = (field: string) => {
    toast.add({
        title: `${field} is required`,
        description: `Add a ${field.toLowerCase()} before saving this section.`,
        color: 'error',
    });
};

const saveProfile = async () => {
    const payload = formToProfile(formState.profile, settings['basic-demographics']);

    if (payload.age <= 0 || payload.height <= 0 || payload.weight <= 0) {
        warnMissing('Age, height and weight');
        return;
    }

    if (await saveSection('basic-demographics', payload, 'Profile')) {
        Object.assign(formState.profile, profileToForm(settings['basic-demographics']));
    }
};

const saveLifestyle = async () => {
    const payload = formToLifestyle(formState.lifestyle, settings['activity-lifestyle']);

    if (!payload.activityLevel) {
        warnMissing('Activity level');
        return;
    }

    if (await saveSection('activity-lifestyle', payload, 'Lifestyle')) {
        Object.assign(formState.lifestyle, lifestyleToForm(settings['activity-lifestyle']));
    }
};

const saveNutrition = async () => {
    const payload = formToNutrition(formState.nutrition, settings['nutrition-history']);

    if (await saveSection('nutrition-history', payload, 'Nutrition')) {
        Object.assign(formState.nutrition, nutritionToForm(settings['nutrition-history']));
    }
};

const saveTraining = async () => {
    const payload = formToTraining(formState.training, settings['training-background']);

    if (await saveSection('training-background', payload, 'Training')) {
        Object.assign(formState.training, trainingToForm(settings['training-background']));
    }
};

const saveGoals = async () => {
    const payload = formToGoals(formState.goals, settings['goals-priorities']);

    if (!payload.primaryGoal) {
        warnMissing('Primary goal');
        return;
    }

    if (await saveSection('goals-priorities', payload, 'Goals')) {
        Object.assign(formState.goals, goalsToForm(settings['goals-priorities']));
    }
};

const cancelProfile = () => Object.assign(formState.profile, profileToForm(settings['basic-demographics']));
const cancelLifestyle = () => Object.assign(formState.lifestyle, lifestyleToForm(settings['activity-lifestyle']));
const cancelNutrition = () => Object.assign(formState.nutrition, nutritionToForm(settings['nutrition-history']));
const cancelTraining = () => Object.assign(formState.training, trainingToForm(settings['training-background']));
const cancelGoals = () => Object.assign(formState.goals, goalsToForm(settings['goals-priorities']));

const placeholderFor = (value: string, fallback: string) => {
    if (!value || value.trim() === "") {
        return fallback;
    }
    return value;
};

const isSelected = (currentValue: string, defaultValue: string, targetValue: string) => {
    const resolvedValue = currentValue || defaultValue;
    return resolvedValue === targetValue;
};

const isSaving = (page: string) => savingPage.value === page;
const isBusy = computed(() => isLoading.value);
</script>

<template>
    <div class="flex-1 flex flex-col h-full bg-background">
        <div class="flex-1 overflow-auto px-4 pb-10 pt-4 sm:px-6 sm:pt-5 lg:px-8">
            <div class="w-full">
                <GenericTabGroup
                    v-model="activeTab"
                    :tabs="tabs"
                    tabs-list-class="h-11 px-1 py-1"
                    tab-trigger-class="px-4 py-1.5 text-sm"
                >
                    <template #leading>
                        <h1 class="text-xl font-semibold text-slate-700">Settings</h1>
                    </template>

                    <div
                        v-if="isBusy"
                        class="flex items-center justify-center gap-2 py-16 text-muted-foreground"
                    >
                        <Loader2 class="h-4 w-4 animate-spin" />
                        Loading settings…
                    </div>

                    <div v-else class="relative overflow-hidden">
                        <Transition name="settings-slide" mode="out-in">
                            <div :key="activeTab">
                                <ProfileSettings
                                    v-if="activeTab === 'profile'"
                                    :form-state="formState"
                                    :form-defaults="formDefaults"
                                    :placeholder-for="placeholderFor"
                                    :saving="isSaving('basic-demographics')"
                                    @save="saveProfile"
                                    @cancel="cancelProfile"
                                />

                                <AccountSettings
                                    v-else-if="activeTab === 'account'"
                                    :form-state="formState"
                                    :form-defaults="formDefaults"
                                    :placeholder-for="placeholderFor"
                                    :saving="isSavingAccount"
                                    @save="saveAccount"
                                    @cancel="resetAccountForm"
                                />

                                <LifestyleSettings
                                    v-else-if="activeTab === 'lifestyle'"
                                    v-model="formState.lifestyle"
                                    :saving="isSaving('activity-lifestyle')"
                                    :activity-levels="activityLevels"
                                    :sleep-quality-options="sleepQualityOptions"
                                    :stress-levels="stressLevels"
                                    :recovery-options="recoveryOptions"
                                    @save="saveLifestyle"
                                    @cancel="cancelLifestyle"
                                />

                                <NutritionSettings
                                    v-else-if="activeTab === 'nutrition'"
                                    :form-state="formState"
                                    :form-defaults="formDefaults"
                                    :placeholder-for="placeholderFor"
                                    :is-selected="isSelected"
                                    :saving="isSaving('nutrition-history')"
                                    :yes-no-options="yesNoOptions"
                                    :diet-patterns="dietPatterns"
                                    :skill-levels="skillLevels"
                                    :budget-options="budgetOptions"
                                    :alcohol-options="alcoholOptions"
                                    @save="saveNutrition"
                                    @cancel="cancelNutrition"
                                />

                                <TrainingSettings
                                    v-else-if="activeTab === 'training'"
                                    :form-state="formState"
                                    :form-defaults="formDefaults"
                                    :placeholder-for="placeholderFor"
                                    :saving="isSaving('training-background')"
                                    :training-age-options="trainingAgeOptions"
                                    :equipment-options="equipmentOptions"
                                    @save="saveTraining"
                                    @cancel="cancelTraining"
                                />

                                <GoalsSettings
                                    v-else
                                    :form-state="formState"
                                    :form-defaults="formDefaults"
                                    :placeholder-for="placeholderFor"
                                    :saving="isSaving('goals-priorities')"
                                    @save="saveGoals"
                                    @cancel="cancelGoals"
                                />
                            </div>
                        </Transition>
                    </div>
                </GenericTabGroup>
            </div>
        </div>
    </div>
</template>

<style scoped>
.settings-slide-enter-active,
.settings-slide-leave-active {
    transition: transform 260ms cubic-bezier(0.22, 1, 0.36, 1), opacity 220ms ease;
    will-change: transform, opacity;
}

.settings-slide-enter-from {
    opacity: 0;
    transform: translateX(28px);
}

.settings-slide-leave-to {
    opacity: 0;
    transform: translateX(-28px);
}

.settings-slide-enter-to,
.settings-slide-leave-from {
    opacity: 1;
    transform: translateX(0);
}
</style>
