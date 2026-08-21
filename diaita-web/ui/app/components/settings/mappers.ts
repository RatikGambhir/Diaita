import type {
  ActivityLevelLifestyle,
  BasicDemographics,
  GoalsPriorities,
  NutritionDietHistory,
  TrainingBackground,
} from "~/types/SettingsTypes";

/**
 * The settings forms bind to plain strings, while the service stores typed fields and string
 * arrays. These helpers translate in both directions, and keep fields the UI does not expose
 * (snacking habits, supplement use, …) intact by folding edits onto the loaded section.
 */

export const textOf = (value: string | number | null | undefined): string =>
  value == null ? "" : String(value);

export const listToText = (values: string[] | null | undefined): string =>
  (values ?? []).join(", ");

export const textToList = (value: string): string[] | null => {
  const items = value
    .split(",")
    .map((item) => item.trim())
    .filter((item) => item.length > 0);

  return items.length > 0 ? items : null;
};

export const textToNumber = (value: string): number | null => {
  const trimmed = value.trim();
  if (!trimmed) {
    return null;
  }

  const parsed = Number(trimmed);
  return Number.isFinite(parsed) ? parsed : null;
};

export const textToInteger = (value: string): number | null => {
  const parsed = textToNumber(value);
  return parsed == null ? null : Math.round(parsed);
};

export const textToNullable = (value: string): string | null =>
  value.trim() ? value.trim() : null;

export type ProfileFormState = {
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

export type LifestyleFormState = {
  activityLevel: string;
  dailyStepCount: string;
  jobType: string;
  commuteTime: string;
  sleepDuration: string;
  sleepQuality: string;
  stressLevel: string;
  recoveryCapacity: string;
};

export type NutritionFormState = {
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

export type TrainingFormState = {
  trainingAge: string;
  trainingHistory: string;
  workoutRoutine: string;
  exercisePreferences: string;
  exerciseDislikes: string;
  equipmentAccess: string;
  timePerSession: string;
  daysPerWeek: string;
};

export type GoalsFormState = {
  primaryGoal: string;
  secondaryGoals: string;
  timeframe: string;
  targetWeight: string;
  performanceMetric: string;
  aestheticGoals: string;
  healthGoals: string;
};

export const emptyProfileForm = (): ProfileFormState => ({
  age: "",
  sex: "",
  gender: "",
  height: "",
  weight: "",
  bodyFat: "",
  leanMass: "",
  biological: "",
  menstrual: "",
});

export const emptyLifestyleForm = (): LifestyleFormState => ({
  activityLevel: "",
  dailyStepCount: "",
  jobType: "",
  commuteTime: "",
  sleepDuration: "",
  sleepQuality: "",
  stressLevel: "",
  recoveryCapacity: "",
});

export const emptyNutritionForm = (): NutritionFormState => ({
  dietPattern: "",
  calorieTracking: "",
  cookingSkill: "",
  macroPreferences: "",
  allergies: "",
  dietaryRestrictions: "",
  culturalPreferences: "",
  foodBudget: "",
  alcoholIntake: "",
});

export const emptyTrainingForm = (): TrainingFormState => ({
  trainingAge: "",
  trainingHistory: "",
  workoutRoutine: "",
  exercisePreferences: "",
  exerciseDislikes: "",
  equipmentAccess: "",
  timePerSession: "",
  daysPerWeek: "",
});

export const emptyGoalsForm = (): GoalsFormState => ({
  primaryGoal: "",
  secondaryGoals: "",
  timeframe: "",
  targetWeight: "",
  performanceMetric: "",
  aestheticGoals: "",
  healthGoals: "",
});

export const profileToForm = (
  section: BasicDemographics | null,
): ProfileFormState => {
  if (!section) {
    return emptyProfileForm();
  }

  return {
    age: textOf(section.age),
    sex: textOf(section.sex),
    gender: textOf(section.gender),
    height: textOf(section.height),
    weight: textOf(section.weight),
    bodyFat: textOf(section.bodyFatPercentage),
    leanMass: textOf(section.leanMass),
    biological: textOf(section.biologicalConsiderations),
    menstrual: textOf(section.menstrualCycleInfo),
  };
};

export const formToProfile = (
  form: ProfileFormState,
  current: BasicDemographics | null,
): BasicDemographics => ({
  ...(current ?? {}),
  age: textToInteger(form.age) ?? 0,
  sex: textToNullable(form.sex),
  gender: textToNullable(form.gender),
  height: textToInteger(form.height) ?? 0,
  weight: textToInteger(form.weight) ?? 0,
  bodyFatPercentage: textToNumber(form.bodyFat),
  leanMass: textToNumber(form.leanMass),
  biologicalConsiderations: textToNullable(form.biological),
  menstrualCycleInfo: textToNullable(form.menstrual),
});

export const lifestyleToForm = (
  section: ActivityLevelLifestyle | null,
): LifestyleFormState => {
  if (!section) {
    return emptyLifestyleForm();
  }

  return {
    activityLevel: textOf(section.activityLevel),
    dailyStepCount: textOf(section.dailyStepCount),
    jobType: textOf(section.jobType),
    commuteTime: textOf(section.commuteTime),
    sleepDuration: textOf(section.sleepDuration),
    sleepQuality: textOf(section.sleepQuality),
    stressLevel: textOf(section.stressLevel),
    recoveryCapacity: textOf(section.recoveryCapacity),
  };
};

export const formToLifestyle = (
  form: LifestyleFormState,
  current: ActivityLevelLifestyle | null,
): ActivityLevelLifestyle => ({
  ...(current ?? {}),
  activityLevel: form.activityLevel.trim(),
  dailyStepCount: textToInteger(form.dailyStepCount),
  jobType: textToNullable(form.jobType),
  commuteTime: textToNullable(form.commuteTime),
  sleepDuration: textToNumber(form.sleepDuration),
  sleepQuality: textToNullable(form.sleepQuality),
  stressLevel: textToNullable(form.stressLevel),
  recoveryCapacity: textToNullable(form.recoveryCapacity),
});

export const nutritionToForm = (
  section: NutritionDietHistory | null,
): NutritionFormState => {
  if (!section) {
    return emptyNutritionForm();
  }

  return {
    dietPattern: textOf(section.currentDietPattern),
    calorieTracking:
      section.calorieTrackingExperience == null
        ? ""
        : section.calorieTrackingExperience
          ? "yes"
          : "no",
    cookingSkill: textOf(section.cookingSkillLevel),
    macroPreferences: textOf(section.macronutrientPreferences),
    allergies: listToText(section.foodAllergies),
    dietaryRestrictions: listToText(section.dietaryRestrictions),
    culturalPreferences: textOf(section.culturalFoodPreferences),
    foodBudget: textOf(section.foodBudget),
    alcoholIntake: textOf(section.alcoholIntake),
  };
};

export const formToNutrition = (
  form: NutritionFormState,
  current: NutritionDietHistory | null,
): NutritionDietHistory => ({
  // Preserves eatingSchedule, snackingHabits and supplementUse, which have no field in this form.
  ...(current ?? {}),
  currentDietPattern: textToNullable(form.dietPattern),
  calorieTrackingExperience:
    form.calorieTracking === "" ? null : form.calorieTracking === "yes",
  macronutrientPreferences: textToNullable(form.macroPreferences),
  foodAllergies: textToList(form.allergies),
  dietaryRestrictions: textToList(form.dietaryRestrictions),
  culturalFoodPreferences: textToNullable(form.culturalPreferences),
  cookingSkillLevel: textToNullable(form.cookingSkill),
  foodBudget: textToNullable(form.foodBudget),
  eatingSchedule: current?.eatingSchedule ?? null,
  snackingHabits: current?.snackingHabits ?? null,
  alcoholIntake: textToNullable(form.alcoholIntake),
  supplementUse: current?.supplementUse ?? null,
});

export const trainingToForm = (
  section: TrainingBackground | null,
): TrainingFormState => {
  if (!section) {
    return emptyTrainingForm();
  }

  return {
    trainingAge: textOf(section.trainingAge),
    trainingHistory: textOf(section.trainingHistory),
    workoutRoutine: textOf(section.currentWorkoutRoutine),
    exercisePreferences: listToText(section.exercisePreferences),
    exerciseDislikes: listToText(section.exerciseDislikes),
    equipmentAccess: textOf(section.equipmentAccess),
    timePerSession: textOf(section.timePerSession),
    daysPerWeek: textOf(section.daysPerWeek),
  };
};

export const formToTraining = (
  form: TrainingFormState,
  current: TrainingBackground | null,
): TrainingBackground => ({
  ...(current ?? {}),
  trainingAge: textToNullable(form.trainingAge),
  trainingHistory: textToNullable(form.trainingHistory),
  currentWorkoutRoutine: textToNullable(form.workoutRoutine),
  exercisePreferences: textToList(form.exercisePreferences),
  exerciseDislikes: textToList(form.exerciseDislikes),
  equipmentAccess: textToNullable(form.equipmentAccess),
  timePerSession: textToInteger(form.timePerSession),
  daysPerWeek: textToInteger(form.daysPerWeek),
});

export const goalsToForm = (section: GoalsPriorities | null): GoalsFormState => {
  if (!section) {
    return emptyGoalsForm();
  }

  return {
    primaryGoal: textOf(section.primaryGoal),
    secondaryGoals: listToText(section.secondaryGoals),
    timeframe: textOf(section.timeframe),
    targetWeight: textOf(section.targetWeight),
    performanceMetric: textOf(section.performanceMetric),
    aestheticGoals: textOf(section.aestheticGoals),
    healthGoals: listToText(section.healthGoals),
  };
};

export const formToGoals = (
  form: GoalsFormState,
  current: GoalsPriorities | null,
): GoalsPriorities => ({
  ...(current ?? {}),
  primaryGoal: form.primaryGoal.trim(),
  secondaryGoals: textToList(form.secondaryGoals),
  timeframe: textToNullable(form.timeframe),
  targetWeight: textToNumber(form.targetWeight),
  performanceMetric: textToNullable(form.performanceMetric),
  aestheticGoals: textToNullable(form.aestheticGoals),
  healthGoals: textToList(form.healthGoals),
});
