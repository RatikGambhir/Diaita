<script setup lang="ts">
import { ArrowLeft, LoaderCircle, Plus, Save, Trash2 } from "lucide-vue-next";
import { workoutApi } from "~/api/workouts";
import type { UpsertWorkout, UpsertWorkoutExercise, WorkoutLog } from "~/types/WorkoutTypes";
import Button from "~/components/ui/button/Button.vue";
import Card from "~/components/ui/card/Card.vue";
import CardContent from "~/components/ui/card/CardContent.vue";
import CardHeader from "~/components/ui/card/CardHeader.vue";
import Input from "~/components/ui/input/Input.vue";
import Textarea from "~/components/ui/textarea/Textarea.vue";

const route = useRoute();
const toast = useToast();
const workoutId = computed(() => String(route.params.id));
const workout = ref<WorkoutLog | null>(null);
const isLoading = ref(true);
const isSaving = ref(false);
const errorMessage = ref("");

const form = reactive({
  name: "",
  performedAt: "",
  durationMinutes: 0,
  notes: "",
  exercises: [] as UpsertWorkoutExercise[],
});

const toLocalInput = (iso: string) => {
  const date = new Date(iso);
  const local = new Date(date.getTime() - date.getTimezoneOffset() * 60_000);
  return local.toISOString().slice(0, 16);
};

const applyWorkout = (value: WorkoutLog) => {
  workout.value = value;
  form.name = value.name;
  form.performedAt = toLocalInput(value.performedAt);
  form.durationMinutes = value.durationMinutes;
  form.notes = value.notes ?? "";
  form.exercises = value.exercises.map((exercise) => ({ ...exercise }));
};

const load = async () => {
  isLoading.value = true;
  try {
    applyWorkout(await workoutApi.get(workoutId.value));
  } catch {
    errorMessage.value = "Workout not found.";
  } finally {
    isLoading.value = false;
  }
};

const addExercise = () => {
  form.exercises.push({
    exerciseId: null,
    exerciseName: "",
    category: "strength",
    sets: 3,
    reps: 10,
    weightKg: null,
    durationMinutes: null,
    distanceKm: null,
    notes: null,
  });
};

const optionalNumber = (value: unknown): number | null => {
  if (value === "" || value === null || value === undefined) return null;
  const number = Number(value);
  return Number.isFinite(number) ? number : null;
};

const buildPayload = (): UpsertWorkout => ({
  name: form.name.trim(),
  performedAt: new Date(form.performedAt).toISOString(),
  durationMinutes: Math.max(0, Number(form.durationMinutes) || 0),
  notes: form.notes.trim() || null,
  exercises: form.exercises.map((exercise) => ({
    ...exercise,
    exerciseName: exercise.exerciseName.trim(),
    category: exercise.category.trim() || "strength",
    sets: Math.max(0, Number(exercise.sets) || 0),
    reps: optionalNumber(exercise.reps),
    weightKg: optionalNumber(exercise.weightKg),
    durationMinutes: optionalNumber(exercise.durationMinutes),
    distanceKm: optionalNumber(exercise.distanceKm),
    notes: exercise.notes?.trim() || null,
  })),
});

const save = async () => {
  if (!form.name.trim()) {
    toast.add({ title: "Workout name required", description: "Give this session a name.", color: "error" });
    return;
  }
  if (form.exercises.some((exercise) => !exercise.exerciseName.trim())) {
    toast.add({ title: "Exercise name required", description: "Name or remove each exercise before saving.", color: "error" });
    return;
  }
  isSaving.value = true;
  try {
    applyWorkout(await workoutApi.update(workoutId.value, buildPayload()));
    toast.add({ title: "Workout saved", description: "Your workout details are up to date.", color: "success" });
  } catch (error) {
    toast.add({ title: "Unable to save workout", description: error instanceof Error ? error.message : "Please try again.", color: "error" });
  } finally {
    isSaving.value = false;
  }
};

const removeWorkout = async () => {
  if (!workout.value || !window.confirm(`Delete “${workout.value.name}”? This cannot be undone.`)) return;
  await workoutApi.remove(workout.value.id);
  await navigateTo("/workouts");
};

onMounted(load);
</script>

<template>
  <div class="app-page space-y-8">
      <button type="button" class="focus-ring inline-flex items-center gap-2 rounded-md text-sm font-semibold text-muted-foreground hover:text-foreground" @click="navigateTo('/workouts')">
        <ArrowLeft class="h-4 w-4" /> Workout history
      </button>

      <AppPageHeader
        eyebrow="Session detail"
        :title="workout?.name || 'Workout session'"
        description="Record the session while it is fresh. Strength, cardio, distance, and notes all belong here."
      >
        <template #actions>
          <Button variant="outline" :disabled="!workout" @click="removeWorkout"><Trash2 class="mr-2 h-4 w-4 text-destructive" />Delete</Button>
          <Button :disabled="isSaving || !workout" @click="save">
            <LoaderCircle v-if="isSaving" class="mr-2 h-4 w-4 animate-spin" />
            <Save v-else class="mr-2 h-4 w-4" />Save workout
          </Button>
        </template>
      </AppPageHeader>

      <div v-if="isLoading" class="py-20 text-center text-muted-foreground">Loading workout…</div>
      <div v-else-if="errorMessage" class="border-y border-destructive/40 py-6 text-destructive">{{ errorMessage }}</div>
      <template v-else>
        <Card class="overflow-hidden">
          <CardHeader class="border-b"><p class="eyebrow">01 · Basics</p><h2 class="display-title mt-2 text-2xl">Session details</h2></CardHeader>
          <CardContent class="grid gap-5 pt-6 md:grid-cols-2">
            <label class="space-y-2"><span class="text-sm font-medium">Workout name</span><Input v-model="form.name" maxlength="120" /></label>
            <label class="space-y-2"><span class="text-sm font-medium">Date and time</span><Input v-model="form.performedAt" type="datetime-local" /></label>
            <label class="space-y-2"><span class="text-sm font-medium">Duration (minutes)</span><Input v-model.number="form.durationMinutes" type="number" min="0" max="1440" /></label>
            <label class="space-y-2 md:col-span-2"><span class="text-sm font-medium">Notes</span><Textarea v-model="form.notes" placeholder="How did the workout feel?" /></label>
          </CardContent>
        </Card>

        <section class="space-y-5">
          <div class="flex flex-col gap-4 border-b pb-5 sm:flex-row sm:items-end sm:justify-between">
            <div><p class="eyebrow">02 · Work performed</p><h2 class="display-title mt-2 text-3xl">Exercises</h2><p class="mt-2 text-sm text-muted-foreground">Use weight and reps for lifting, or time and distance for cardio.</p></div>
            <Button variant="secondary" @click="addExercise"><Plus class="h-4 w-4" />Add exercise</Button>
          </div>

          <div v-if="form.exercises.length === 0" class="border-y py-14 text-center text-muted-foreground">No exercises yet. Add one to record this session.</div>
          <Card v-for="(exercise, index) in form.exercises" :key="exercise.id || index" class="overflow-hidden">
            <CardContent class="space-y-5 p-5 sm:p-6">
              <div class="flex items-start gap-3">
                <span class="hidden h-10 w-10 shrink-0 items-center justify-center bg-foreground font-mono text-sm font-semibold text-background sm:flex">{{ String(index + 1).padStart(2, '0') }}</span>
                <div class="grid flex-1 gap-3 md:grid-cols-2">
                  <label class="space-y-2"><span class="text-sm font-medium">Exercise</span><Input v-model="exercise.exerciseName" placeholder="Bench Press" /></label>
                  <label class="space-y-2"><span class="text-sm font-medium">Category</span><Input v-model="exercise.category" placeholder="strength, cardio, mobility" /></label>
                </div>
                <Button variant="ghost" size="icon" aria-label="Remove exercise" @click="form.exercises.splice(index, 1)"><Trash2 class="h-4 w-4 text-destructive" /></Button>
              </div>
              <div class="grid gap-3 border-y bg-muted/30 px-4 py-4 sm:grid-cols-2 lg:grid-cols-5">
                <label class="space-y-2"><span class="text-xs text-muted-foreground">Sets</span><Input v-model.number="exercise.sets" type="number" min="0" /></label>
                <label class="space-y-2"><span class="text-xs text-muted-foreground">Reps</span><Input :model-value="exercise.reps ?? ''" type="number" min="0" @update:model-value="exercise.reps = optionalNumber($event)" /></label>
                <label class="space-y-2"><span class="text-xs text-muted-foreground">Weight (kg)</span><Input :model-value="exercise.weightKg ?? ''" type="number" min="0" step="0.1" @update:model-value="exercise.weightKg = optionalNumber($event)" /></label>
                <label class="space-y-2"><span class="text-xs text-muted-foreground">Minutes</span><Input :model-value="exercise.durationMinutes ?? ''" type="number" min="0" @update:model-value="exercise.durationMinutes = optionalNumber($event)" /></label>
                <label class="space-y-2"><span class="text-xs text-muted-foreground">Distance (km)</span><Input :model-value="exercise.distanceKm ?? ''" type="number" min="0" step="0.01" @update:model-value="exercise.distanceKm = optionalNumber($event)" /></label>
              </div>
              <Input :model-value="exercise.notes ?? ''" placeholder="Exercise notes (optional)" @update:model-value="exercise.notes = String($event ?? '') || null" />
            </CardContent>
          </Card>
        </section>
      </template>
  </div>
</template>
