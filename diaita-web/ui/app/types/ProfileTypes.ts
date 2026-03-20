export type RegisterUserProfileRequest = {
  userId: string;
  age: number;
  height: number;
  weight: number;
  primaryGoal: string;
  timeframe: string | null;
  activityLevel: string;
  sleepDuration: number | null;
  stressLevel: string | null;
  trainingHistory: string | null;
  trainingAge: string | null;
  equipmentAccess: string | null;
  daysPerWeek: number | null;
  timePerSession: number | null;
  injuries: string[] | null;
  chronicConditions: string[] | null;
  mobilityRestrictions: string[] | null;
  doctorRestrictions: string | null;
  dietaryRestrictions: string[] | null;
  foodAllergies: string[] | null;
  currentDietPattern: string | null;
  eatingSchedule: string | null;
};

export type RegisteredUserProfile = RegisterUserProfileRequest;

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
