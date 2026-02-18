<script setup lang="ts">
import GenericTabGroup from '~/components/GenericTabGroup.vue'
import ProfileSettings from '~/components/settings/ProfileSettings.vue'
import AccountSettings from '~/components/settings/AccountSettings.vue'
import NutritionSettings from '~/components/settings/NutritionSettings.vue'
import TrainingSettings from '~/components/settings/TrainingSettings.vue'
import GoalsSettings from '~/components/settings/GoalsSettings.vue'

const activeTab = ref('profile');

const tabs = [
    { value: "profile", label: "Profile" },
    { value: "account", label: "Account" },
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

const formDefaults = reactive({
    profile: {
        age: "29",
        sex: "Female",
        gender: "Woman",
        height: "170",
        weight: "68",
        bodyFat: "19",
        leanMass: "56.0",
        biological: "Post-pregnancy, thyroid condition",
        menstrual: "Regular 28-day cycle, tracking symptoms",
    },
    account: {
        firstName: "Jane",
        lastName: "Doe",
        email: "jane.doe@example.com",
        workoutsToTrackOnHomepage: "6",
    },
    nutrition: {
        dietPattern: "Mediterranean",
        calorieTracking: "yes",
        cookingSkill: "Intermediate",
        macroPreferences: "High protein, moderate carbs, low fat",
        allergies: "Tree nuts",
        dietaryRestrictions: "No dairy",
        culturalPreferences: "Mediterranean, Indian",
        foodBudget: "$100-$200",
        alcoholIntake: "Occasionally",
    },
    training: {
        trainingAge: "1-3 years",
        trainingHistory: "Started with bodyweight training, then moved to weightlifting.",
        workoutRoutine: "PPL split 6 days per week, focusing on compound movements.",
        exercisePreferences: "Deadlifts, incline bench",
        exerciseDislikes: "Burpees",
        equipmentAccess: "Gym access",
        timePerSession: "60",
        daysPerWeek: "4",
    },
    goals: {
        primaryGoal: "Lean muscle gain",
        secondaryGoals: "Improve mobility",
        timeframe: "6 months",
        targetWeight: "70 kg",
        performanceMetric: "5K under 25 minutes",
        aestheticGoals: "More definition in shoulders and core.",
        healthGoals: "Improve resting heart rate",
    },
});

const formState = reactive({
    profile: {
        age: "",
        sex: "",
        gender: "",
        height: "",
        weight: "",
        bodyFat: "",
        leanMass: "",
        biological: "",
        menstrual: "",
    },
    account: {
        firstName: "",
        lastName: "",
        email: "",
        workoutsToTrackOnHomepage: "",
    },
    nutrition: {
        dietPattern: "",
        calorieTracking: "",
        cookingSkill: "",
        macroPreferences: "",
        allergies: "",
        dietaryRestrictions: "",
        culturalPreferences: "",
        foodBudget: "",
        alcoholIntake: "",
    },
    training: {
        trainingAge: "",
        trainingHistory: "",
        workoutRoutine: "",
        exercisePreferences: "",
        exerciseDislikes: "",
        equipmentAccess: "",
        timePerSession: "",
        daysPerWeek: "",
    },
    goals: {
        primaryGoal: "",
        secondaryGoals: "",
        timeframe: "",
        targetWeight: "",
        performanceMetric: "",
        aestheticGoals: "",
        healthGoals: "",
    },
});

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

                    <div class="relative overflow-hidden">
                        <Transition name="settings-slide" mode="out-in">
                            <div :key="activeTab">
                                <ProfileSettings
                                    v-if="activeTab === 'profile'"
                                    :form-state="formState"
                                    :form-defaults="formDefaults"
                                    :placeholder-for="placeholderFor"
                                />

                                <AccountSettings
                                    v-else-if="activeTab === 'account'"
                                    :form-state="formState"
                                    :form-defaults="formDefaults"
                                    :placeholder-for="placeholderFor"
                                />

                                <NutritionSettings
                                    v-else-if="activeTab === 'nutrition'"
                                    :form-state="formState"
                                    :form-defaults="formDefaults"
                                    :placeholder-for="placeholderFor"
                                    :is-selected="isSelected"
                                    :yes-no-options="yesNoOptions"
                                    :diet-patterns="dietPatterns"
                                    :skill-levels="skillLevels"
                                    :budget-options="budgetOptions"
                                    :alcohol-options="alcoholOptions"
                                />

                                <TrainingSettings
                                    v-else-if="activeTab === 'training'"
                                    :form-state="formState"
                                    :form-defaults="formDefaults"
                                    :placeholder-for="placeholderFor"
                                    :training-age-options="trainingAgeOptions"
                                    :equipment-options="equipmentOptions"
                                />

                                <GoalsSettings
                                    v-else
                                    :form-state="formState"
                                    :form-defaults="formDefaults"
                                    :placeholder-for="placeholderFor"
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
