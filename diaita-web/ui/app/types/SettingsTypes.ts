/** Sections of the user settings record, matching the service's `page` query values. */
export type UserSettingsPage =
  | "basic-demographics"
  | "activity-lifestyle"
  | "goals-priorities"
  | "training-background"
  | "nutrition-history";

export type BasicDemographics = {
  age: number;
  sex: string | null;
  gender: string | null;
  height: number;
  weight: number;
  bodyFatPercentage: number | null;
  leanMass: number | null;
  biologicalConsiderations: string | null;
  menstrualCycleInfo: string | null;
};

export type ActivityLevelLifestyle = {
  activityLevel: string;
  dailyStepCount: number | null;
  jobType: string | null;
  commuteTime: string | null;
  sleepDuration: number | null;
  sleepQuality: string | null;
  stressLevel: string | null;
  recoveryCapacity: string | null;
};

export type GoalsPriorities = {
  primaryGoal: string;
  secondaryGoals: string[] | null;
  timeframe: string | null;
  targetWeight: number | null;
  performanceMetric: string | null;
  aestheticGoals: string | null;
  healthGoals: string[] | null;
};

export type TrainingBackground = {
  trainingAge: string | null;
  trainingHistory: string | null;
  currentWorkoutRoutine: string | null;
  exercisePreferences: string[] | null;
  exerciseDislikes: string[] | null;
  equipmentAccess: string | null;
  timePerSession: number | null;
  daysPerWeek: number | null;
};

export type NutritionDietHistory = {
  currentDietPattern: string | null;
  calorieTrackingExperience: boolean | null;
  macronutrientPreferences: string | null;
  foodAllergies: string[] | null;
  dietaryRestrictions: string[] | null;
  culturalFoodPreferences: string | null;
  cookingSkillLevel: string | null;
  foodBudget: string | null;
  eatingSchedule: string | null;
  snackingHabits: string | null;
  alcoholIntake: string | null;
  supplementUse: string[] | null;
};

/** Maps each settings page to the payload the service returns and accepts for it. */
export type UserSettingsPayloads = {
  "basic-demographics": BasicDemographics;
  "activity-lifestyle": ActivityLevelLifestyle;
  "goals-priorities": GoalsPriorities;
  "training-background": TrainingBackground;
  "nutrition-history": NutritionDietHistory;
};

export type UserSettings = {
  [Page in UserSettingsPage]: UserSettingsPayloads[Page] | null;
};
