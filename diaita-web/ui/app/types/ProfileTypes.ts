export type BasicDemographics = {
  age: number | null;
  sex: string | null;
  gender: string | null;
  height: number | null;
  weight: number | null;
  bodyFatPercentage: number | null;
  leanMass: number | null;
  biologicalConsiderations: string | null;
  menstrualCycleInfo: string | null;
};

export type ActivityLifestyle = {
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

export type NutritionHistory = {
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

export type RegisterUserProfileRequest = {
  id: string;
  userId: string;
  basicDemographics: BasicDemographics;
  activityLifestyle: ActivityLifestyle;
  goals: GoalsPriorities;
  trainingBackground: TrainingBackground | null;
  nutritionHistory: NutritionHistory | null;
};

export type RegisteredUserProfile = {
  userId: string;
  basicDemographics: BasicDemographics | null;
  activityLifestyle: ActivityLifestyle | null;
  goals: GoalsPriorities | null;
  trainingBackground: TrainingBackground | null;
  nutritionHistory: NutritionHistory | null;
};

export type RecommendationExercise = {
  name: string;
  equipment: string;
  type: string;
};

export type WorkoutExercise = {
  exercise: string;
  sets: number;
  reps: string;
  rest_seconds: number;
  notes: string | null;
};

export type TrainingRecommendation = {
  focus: {
    primary: string;
    secondary: string[];
  };
  split: {
    days_per_week: number;
    groupings: string[];
  };
  exercise_library: {
    chest: RecommendationExercise[];
    back: RecommendationExercise[];
    shoulders: RecommendationExercise[];
    legs: RecommendationExercise[];
    arms: RecommendationExercise[];
    core: RecommendationExercise[];
  };
  phases: Array<{
    name: string;
    duration: string;
    focus: string;
  }>;
  day_by_day_plan: {
    weeks: Array<{
      week_number: number;
      days: Array<{
        day: string;
        focus: string;
        exercises: WorkoutExercise[];
      }>;
    }>;
  };
  progression_rules: {
    load: {
      increase: string;
      frequency: string;
    };
    performance: {
      metrics: string[];
    };
  };
};

export type Recommendation = {
  nutrition: {
    calories: {
      baseline: number;
      training_day: number;
      rest_day: number;
    };
    macros: {
      training_day: {
        protein: number;
        carbs: number;
        fat: number;
      };
      rest_day: {
        protein: number;
        carbs: number;
        fat: number;
      };
    };
    meal_structure: {
      meals: Array<{
        name: string;
        time: string;
        calorie_percent: number;
      }>;
    };
    foods: {
      proteins: string[];
      carbs: string[];
      fats: string[];
      vegetables: string[];
      fruits: string[];
    };
    checkins: {
      metrics: string[];
      frequency: string;
    };
    adjustment_rules: {
      plateau_trigger: string;
      adjustments: Array<{
        condition: string;
        action: string;
      }>;
    };
  };
  training: TrainingRecommendation;
};

export type RegisterUserProfileResponse = {
  profile: RegisteredUserProfile;
  recommendation: Recommendation;
};
