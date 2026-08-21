import type {
  UpsertWorkoutExercise,
  Workout,
  WorkoutExercise,
  WorkoutExerciseCategory,
} from "~/types/WorkoutTypes";

export const WORKOUT_CATEGORY_LABELS: Record<WorkoutExerciseCategory, string> = {
  lifting: "Lifting",
  cardio: "Cardio",
  mobility: "Mobility",
};

/** Seconds to `h:mm`, the format the workout cards and table display. */
export function formatWorkoutDuration(seconds: number | null | undefined): string {
  if (!seconds || seconds <= 0) {
    return "0:00";
  }

  const hours = Math.floor(seconds / 3600);
  const minutes = Math.floor((seconds % 3600) / 60);
  return `${hours}:${`${minutes}`.padStart(2, "0")}`;
}

/** Minutes rendered as `12 min`, used for cardio and mobility rows. */
export function formatWorkoutMinutes(seconds: number | null | undefined): string {
  if (!seconds || seconds <= 0) {
    return "—";
  }

  return `${Math.round(seconds / 60)} min`;
}

export function formatWorkoutDate(performedAt: string): string {
  const date = new Date(performedAt);

  if (Number.isNaN(date.getTime())) {
    return performedAt;
  }

  return date.toLocaleDateString("en-US", {
    month: "short",
    day: "numeric",
    year: "numeric",
  });
}

export function formatWorkoutVolume(kilograms: number): string {
  return `${Math.round(kilograms).toLocaleString("en-US")} kg`;
}

export function exercisesByCategory(
  exercises: WorkoutExercise[],
  category: WorkoutExerciseCategory,
): WorkoutExercise[] {
  return exercises.filter((exercise) => exercise.category === category);
}

/**
 * The second column of an exercise row: sets x reps for lifting, elapsed time otherwise.
 */
export function exercisePrimaryMetric(exercise: WorkoutExercise): string {
  if (exercise.category === "lifting") {
    if (exercise.sets == null && exercise.reps == null) {
      return "—";
    }
    return `${exercise.sets ?? 0}x${exercise.reps ?? 0}`;
  }

  return formatWorkoutMinutes(exercise.durationSeconds);
}

/**
 * The third column of an exercise row: load for lifting, intensity for cardio, targets for mobility.
 */
export function exerciseSecondaryMetric(exercise: WorkoutExercise): string {
  if (exercise.category === "lifting") {
    return exercise.weightKg == null ? "—" : `${exercise.weightKg} kg`;
  }

  if (exercise.category === "cardio") {
    return exercise.intensity?.trim() || "—";
  }

  return exercise.target?.trim() || "—";
}

/** Human-readable one-liner used in the collapsed workout card. */
export function exerciseSummaryDetail(exercise: WorkoutExercise): string {
  const primary = exercisePrimaryMetric(exercise);
  const secondary = exerciseSecondaryMetric(exercise);

  if (primary === "—" && secondary === "—") {
    return "No details";
  }
  if (secondary === "—") {
    return primary;
  }
  if (primary === "—") {
    return secondary;
  }

  return `${primary} · ${secondary}`;
}

export function categoryPercent(
  workout: Workout,
  category: WorkoutExerciseCategory,
): number {
  const total = workout.totals.exerciseCount;
  if (total === 0) {
    return 0;
  }

  const counts: Record<WorkoutExerciseCategory, number> = {
    lifting: workout.totals.liftingCount,
    cardio: workout.totals.cardioCount,
    mobility: workout.totals.mobilityCount,
  };

  return Math.round((counts[category] / total) * 100);
}

const parseNumber = (value: string): number | null => {
  const trimmed = value.trim();
  if (!trimmed) {
    return null;
  }

  const parsed = Number(trimmed);
  return Number.isFinite(parsed) ? parsed : null;
};

/**
 * Turns the free-text columns the exercise editor collects back into the typed fields the API
 * expects. Lifting takes `sets x reps` and a weight; cardio and mobility take minutes plus a label.
 */
export function toUpsertExercise(
  exercise: WorkoutExercise,
  draft: { name: string; primary: string; secondary: string },
): UpsertWorkoutExercise {
  const base: UpsertWorkoutExercise = {
    id: exercise.id.startsWith("draft-") ? undefined : exercise.id,
    exerciseId: exercise.exerciseId,
    name: draft.name.trim(),
    category: exercise.category,
    position: exercise.position,
    sets: null,
    reps: null,
    weightKg: null,
    durationSeconds: null,
    intensity: null,
    target: null,
    notes: exercise.notes,
  };

  if (exercise.category === "lifting") {
    const [rawSets, rawReps] = draft.primary.split(/[x×]/i);
    return {
      ...base,
      sets: rawSets ? parseNumber(rawSets) : null,
      reps: rawReps ? parseNumber(rawReps) : null,
      weightKg: parseNumber(draft.secondary.replace(/[^0-9.]/g, "")),
    };
  }

  const minutes = parseNumber(draft.primary.replace(/[^0-9.]/g, ""));
  const durationSeconds = minutes == null ? null : Math.round(minutes * 60);
  const label = draft.secondary.trim() || null;

  return exercise.category === "cardio"
    ? { ...base, durationSeconds, intensity: label }
    : { ...base, durationSeconds, target: label };
}
