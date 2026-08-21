/** Selections the plan generator forms collect and pass through as prompt preferences. */
export type WorkoutGeneratorForm = {
  workoutType: string;
  duration: string;
  fitnessLevel: string;
  equipment: string;
  primaryGoal: string;
};

export type MealPlanGeneratorForm = {
  dietType: string;
  mealsPerDay: string;
  servings: string;
  restrictions: string;
  goal: string;
};

export type GeneratorForm = WorkoutGeneratorForm | MealPlanGeneratorForm;
