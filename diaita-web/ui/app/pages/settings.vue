<script setup lang="ts">
import GenericTabGroup from '~/components/GenericTabGroup.vue'
import GenericTabPanel from '~/components/GenericTabPanel.vue'
import ProfileSettings from '~/components/settings/ProfileSettings.vue'
import NutritionSettings from '~/components/settings/NutritionSettings.vue'
import TrainingSettings from '~/components/settings/TrainingSettings.vue'
import GoalsSettings from '~/components/settings/GoalsSettings.vue'

const activeTab = ref('profile');

const tabs = [
    { value: "profile", label: "Profile" },
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
        <header class="flex h-16 items-center justify-between px-8 shrink-0">
            <h1 class="text-xl font-semibold text-slate-700">Settings</h1>
        </header>

        <div class="flex-1 overflow-auto px-8 pb-10">
            <div class="max-w-4xl mx-auto">
                <GenericTabGroup
                    v-model="activeTab"
                    :tabs="tabs"
                    tab-trigger-class="text-base px-5 py-2"
                >
                    <GenericTabPanel value="profile" class="space-y-6">
                        <ProfileSettings
                            :form-state="formState"
                            :form-defaults="formDefaults"
                            :placeholder-for="placeholderFor"
                        />
                    </GenericTabPanel>

                    <GenericTabPanel value="nutrition" class="space-y-6">
                        <NutritionSettings
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
                    </GenericTabPanel>

                    <GenericTabPanel value="training" class="space-y-6">
                        <TrainingSettings
                            :form-state="formState"
                            :form-defaults="formDefaults"
                            :placeholder-for="placeholderFor"
                            :training-age-options="trainingAgeOptions"
                            :equipment-options="equipmentOptions"
                        />
                    </GenericTabPanel>

                    <GenericTabPanel value="goals" class="space-y-6">
                        <GoalsSettings
                            :form-state="formState"
                            :form-defaults="formDefaults"
                            :placeholder-for="placeholderFor"
                        />
                    </GenericTabPanel>
                </GenericTabGroup>
            </div>
        </div>
    </div>
</template>
