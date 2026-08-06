/** Payloads emitted by the generator forms, shared with their parent card. */

export type WorkoutFormData = {
  workoutType: string;
  duration: string;
  fitnessLevel: string;
  equipment: string;
  primaryGoal: string;
};

export type MealPlanFormData = {
  dietType: string;
  mealsPerDay: string;
  servings: string;
  restrictions: string;
  goal: string;
};
