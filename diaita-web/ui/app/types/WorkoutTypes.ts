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
