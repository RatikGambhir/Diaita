export type WorkoutCardExercise = {
  id: number | string;
  name: string;
  sets: unknown[];
};

export type WorkoutCard = {
  id: number;
  name: string;
  duration: string;
  startTime: string;
  endTime: string;
  date: string;
  isExpanded: boolean;
  exercises: WorkoutCardExercise[];
};

export type WorkoutExerciseLog = {
  id: string;
  exerciseId: number | null;
  exerciseName: string;
  category: string;
  sets: number;
  reps: number | null;
  weightKg: number | null;
  durationMinutes: number | null;
  distanceKm: number | null;
  notes: string | null;
};

export type WorkoutLog = {
  id: string;
  name: string;
  performedAt: string;
  durationMinutes: number;
  notes: string | null;
  exercises: WorkoutExerciseLog[];
  totalVolumeKg: number;
};

export type UpsertWorkoutExercise = Omit<WorkoutExerciseLog, "id"> & { id?: string };

export type UpsertWorkout = {
  name: string;
  performedAt: string;
  durationMinutes: number;
  notes?: string | null;
  exercises: UpsertWorkoutExercise[];
};

export type WorkoutStats = {
  workoutsLast30Days: number;
  minutesLast30Days: number;
  totalVolumeKg: number;
};

export type Exercise = {
  id: number | null;
  exercise: string;
  exerciseType: string | null;
  exerciseVariation: string | null;
  primaryFitnessFocus: string | null;
  secondaryFitnessFocus: string | null;
  description: string | null;
};

export type ExerciseSearchResponse = {
  exercises: Exercise[];
  pagination: {
    total: number;
    page: number;
    pageSize: number;
    totalPages: number;
    hasMore: boolean;
    hasPrevious: boolean;
  };
};
