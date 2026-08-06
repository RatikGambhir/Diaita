export type PlaceholderFor = (value: string, fallback: string) => string;
export type IsSelected = (currentValue: string, defaultValue: string, targetValue: string) => boolean;

export type Option = {
    label: string;
    value: string;
};

/**
 * Every settings field is held as a string so inputs, selects and placeholders
 * can share one representation; conversion happens when the form is submitted.
 */
export type ProfileSection = {
    age: string;
    sex: string;
    gender: string;
    height: string;
    weight: string;
    bodyFat: string;
    leanMass: string;
    biological: string;
    menstrual: string;
};

export type AccountSection = {
    firstName: string;
    lastName: string;
    email: string;
    workoutsToTrackOnHomepage: string;
};

export type NutritionSection = {
    dietPattern: string;
    calorieTracking: string;
    cookingSkill: string;
    macroPreferences: string;
    allergies: string;
    dietaryRestrictions: string;
    culturalPreferences: string;
    foodBudget: string;
    alcoholIntake: string;
};

export type TrainingSection = {
    trainingAge: string;
    trainingHistory: string;
    workoutRoutine: string;
    exercisePreferences: string;
    exerciseDislikes: string;
    equipmentAccess: string;
    timePerSession: string;
    daysPerWeek: string;
};

export type GoalsSection = {
    primaryGoal: string;
    secondaryGoals: string;
    timeframe: string;
    targetWeight: string;
    performanceMetric: string;
    aestheticGoals: string;
    healthGoals: string;
};

export type SettingsFormState = {
    profile: ProfileSection;
    account: AccountSection;
    nutrition: NutritionSection;
    training: TrainingSection;
    goals: GoalsSection;
};

/** Same shape as the live form; holds the example values shown as placeholders. */
export type SettingsFormDefaults = SettingsFormState;
