export type WorkoutExerciseCategory = "lifting" | "cardio" | "mobility";

export const WORKOUT_EXERCISE_CATEGORIES: WorkoutExerciseCategory[] = [
  "lifting",
  "cardio",
  "mobility",
];

export type WorkoutExercise = {
  id: string;
  exerciseId: number | null;
  name: string;
  category: WorkoutExerciseCategory;
  position: number;
  sets: number | null;
  reps: number | null;
  weightKg: number | null;
  durationSeconds: number | null;
  intensity: string | null;
  target: string | null;
  notes: string | null;
};

export type WorkoutTotals = {
  exerciseCount: number;
  liftingCount: number;
  cardioCount: number;
  mobilityCount: number;
  totalSets: number;
  totalVolumeKg: number;
  totalCardioSeconds: number;
};

export type Workout = {
  id: string;
  userId: string;
  name: string;
  performedAt: string;
  durationSeconds: number | null;
  notes: string | null;
  exercises: WorkoutExercise[];
  totals: WorkoutTotals;
};

export type WorkoutPagination = {
  total: number;
  page: number;
  pageSize: number;
  totalPages: number;
  hasMore: boolean;
  hasPrevious: boolean;
};

export type WorkoutListResponse = {
  workouts: Workout[];
  pagination: WorkoutPagination;
};

/** Payload shape for creating or updating a single logged exercise. */
export type UpsertWorkoutExercise = {
  id?: string;
  exerciseId?: number | null;
  name: string;
  category: WorkoutExerciseCategory;
  position?: number;
  sets?: number | null;
  reps?: number | null;
  weightKg?: number | null;
  durationSeconds?: number | null;
  intensity?: string | null;
  target?: string | null;
  notes?: string | null;
};

export type CreateWorkoutRequest = {
  userId: string;
  name: string;
  performedAt?: string;
  durationSeconds?: number | null;
  notes?: string | null;
  exercises?: UpsertWorkoutExercise[];
};

export type UpdateWorkoutRequest = {
  userId: string;
  name?: string;
  performedAt?: string;
  durationSeconds?: number | null;
  notes?: string | null;
  exerciseOps?: {
    upsert?: UpsertWorkoutExercise[];
    deleteIds?: string[];
  };
};

export type WorkoutCategoryBreakdown = {
  lifting: number;
  cardio: number;
  mobility: number;
};

export type WorkoutDailyPoint = {
  date: string;
  workoutCount: number;
  durationSeconds: number;
  volumeKg: number;
};

export type WorkoutStats = {
  workoutCount: number;
  totalDurationSeconds: number;
  averageDurationSeconds: number;
  totalVolumeKg: number;
  totalSets: number;
  totalCardioSeconds: number;
  exerciseCount: number;
  byCategory: WorkoutCategoryBreakdown;
  daily: WorkoutDailyPoint[];
};

/** A row from the read-only exercise library backing the exercise picker. */
export type ExerciseLibraryEntry = {
  id: number | null;
  exercise: string;
  exerciseType: string | null;
  exerciseVariation: string | null;
  primaryFitnessFocus: string | null;
  secondaryFitnessFocus: string | null;
  description: string | null;
};

export type ExerciseSearchResponse = {
  exercises: ExerciseLibraryEntry[];
  pagination: WorkoutPagination;
};

export type ExerciseSearchParams = {
  exercise?: string;
  exerciseType?: string;
  exerciseVariation?: string;
  primaryFitnessFocus?: string;
  page?: number;
  pageSize?: number;
};
