<script setup lang="ts">
import { Activity, Clock, Dumbbell, Plus, Search, Trash2, TrendingUp } from "lucide-vue-next";
import { workoutApi } from "~/api/workouts";
import type { Exercise, WorkoutLog, WorkoutStats } from "~/types/WorkoutTypes";
import Badge from "~/components/ui/badge/Badge.vue";
import Button from "~/components/ui/button/Button.vue";
import Card from "~/components/ui/card/Card.vue";
import CardContent from "~/components/ui/card/CardContent.vue";
import CardHeader from "~/components/ui/card/CardHeader.vue";
import Input from "~/components/ui/input/Input.vue";
import WorkoutAddModal from "~/components/WorkoutAddModal.vue";
import GenericTabGroup from "~/components/GenericTabGroup.vue";

const activeTab = ref("workouts");
const workouts = ref<WorkoutLog[]>([]);
const stats = ref<WorkoutStats | null>(null);
const exercises = ref<Exercise[]>([]);
const searchQuery = ref("");
const exerciseQuery = ref("");
const isLoading = ref(true);
const errorMessage = ref("");
const isAddWorkoutModalOpen = ref(false);
const toast = useToast();

const filteredWorkouts = computed(() => {
  const query = searchQuery.value.trim().toLowerCase();
  return query
    ? workouts.value.filter((workout) => workout.name.toLowerCase().includes(query))
    : workouts.value;
});

const load = async () => {
  isLoading.value = true;
  errorMessage.value = "";
  try {
    [workouts.value, stats.value] = await Promise.all([workoutApi.list(), workoutApi.stats()]);
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : "Unable to load workouts";
  } finally {
    isLoading.value = false;
  }
};

const createWorkout = async (name: string) => {
  try {
    const workout = await workoutApi.create({
      name,
      performedAt: new Date().toISOString(),
      durationMinutes: 0,
      exercises: [],
    });
    await navigateTo(`/workouts/${workout.id}`);
  } catch (error) {
    toast.add({ title: "Unable to create workout", description: error instanceof Error ? error.message : "Please try again.", color: "error" });
  }
};

const deleteWorkout = async (workout: WorkoutLog) => {
  if (!window.confirm(`Delete “${workout.name}”? This cannot be undone.`)) return;
  await workoutApi.remove(workout.id);
  workouts.value = workouts.value.filter((item) => item.id !== workout.id);
  stats.value = await workoutApi.stats();
};

const searchExercises = async () => {
  const query = exerciseQuery.value.trim();
  exercises.value = query ? (await workoutApi.searchExercises(query)).exercises : [];
};

onMounted(load);
</script>

<template>
  <div class="flex-1 overflow-auto p-6">
    <div class="mx-auto max-w-7xl space-y-6">
      <header>
        <h1 class="text-2xl font-semibold">Workout Tracker</h1>
        <p class="mt-1 text-sm text-muted-foreground">Log sessions, exercises, volume, cardio, and notes.</p>
      </header>

      <section class="grid gap-4 sm:grid-cols-3">
        <Card>
          <CardContent class="flex items-center gap-4 p-5">
            <Dumbbell class="h-8 w-8 text-primary" />
            <div><p class="text-2xl font-semibold">{{ stats?.workoutsLast30Days ?? 0 }}</p><p class="text-sm text-muted-foreground">Workouts / 30 days</p></div>
          </CardContent>
        </Card>
        <Card>
          <CardContent class="flex items-center gap-4 p-5">
            <Clock class="h-8 w-8 text-primary" />
            <div><p class="text-2xl font-semibold">{{ stats?.minutesLast30Days ?? 0 }}</p><p class="text-sm text-muted-foreground">Minutes / 30 days</p></div>
          </CardContent>
        </Card>
        <Card>
          <CardContent class="flex items-center gap-4 p-5">
            <TrendingUp class="h-8 w-8 text-primary" />
            <div><p class="text-2xl font-semibold">{{ Math.round(stats?.totalVolumeKg ?? 0).toLocaleString() }} kg</p><p class="text-sm text-muted-foreground">Total lifting volume</p></div>
          </CardContent>
        </Card>
      </section>

      <GenericTabGroup
        v-model="activeTab"
        :tabs="[{ value: 'workouts', label: 'Workouts' }, { value: 'exercises', label: 'Exercise library' }]"
      >
        <div v-if="activeTab === 'workouts'" class="space-y-5">
          <div class="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
            <div class="relative max-w-md flex-1">
              <Search class="absolute left-3 top-3 h-4 w-4 text-muted-foreground" />
              <Input v-model="searchQuery" class="pl-10" placeholder="Search your workouts" />
            </div>
            <Button @click="isAddWorkoutModalOpen = true"><Plus class="mr-2 h-4 w-4" />New workout</Button>
          </div>

          <div v-if="isLoading" class="py-16 text-center text-muted-foreground">Loading workouts…</div>
          <div v-else-if="errorMessage" class="rounded-lg border border-destructive/40 p-4 text-destructive">{{ errorMessage }}</div>
          <div v-else-if="filteredWorkouts.length === 0" class="rounded-xl border border-dashed py-16 text-center">
            <Dumbbell class="mx-auto mb-3 h-10 w-10 text-muted-foreground" />
            <p class="font-medium">No workouts yet</p>
            <p class="mt-1 text-sm text-muted-foreground">Create your first session to start tracking progress.</p>
          </div>
          <div v-else class="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
            <Card v-for="workout in filteredWorkouts" :key="workout.id" class="transition-shadow hover:shadow-md">
              <CardHeader class="flex-row items-start justify-between gap-3">
                <button type="button" class="min-w-0 text-left" @click="navigateTo(`/workouts/${workout.id}`)">
                  <h2 class="truncate text-lg font-semibold">{{ workout.name }}</h2>
                  <p class="mt-1 text-sm text-muted-foreground">{{ new Date(workout.performedAt).toLocaleString() }}</p>
                </button>
                <Button variant="ghost" size="icon" aria-label="Delete workout" @click="deleteWorkout(workout)"><Trash2 class="h-4 w-4 text-destructive" /></Button>
              </CardHeader>
              <CardContent class="space-y-4">
                <div class="flex flex-wrap gap-2">
                  <Badge variant="secondary">{{ workout.durationMinutes }} min</Badge>
                  <Badge variant="outline">{{ workout.exercises.length }} exercises</Badge>
                  <Badge variant="outline">{{ Math.round(workout.totalVolumeKg) }} kg volume</Badge>
                </div>
                <Button variant="outline" class="w-full" @click="navigateTo(`/workouts/${workout.id}`)">Open workout</Button>
              </CardContent>
            </Card>
          </div>
        </div>

        <div v-else class="space-y-5">
          <form class="flex max-w-xl gap-2" @submit.prevent="searchExercises">
            <div class="relative flex-1"><Search class="absolute left-3 top-3 h-4 w-4 text-muted-foreground" /><Input v-model="exerciseQuery" class="pl-10" placeholder="Search bench, run, row…" /></div>
            <Button type="submit">Search</Button>
          </form>
          <div v-if="exercises.length" class="grid gap-3 md:grid-cols-2">
            <Card v-for="exercise in exercises" :key="exercise.id ?? exercise.exercise">
              <CardContent class="flex items-start gap-3 p-4">
                <Activity class="mt-1 h-5 w-5 text-primary" />
                <div><p class="font-medium">{{ exercise.exercise }}</p><p class="text-sm capitalize text-muted-foreground">{{ exercise.exerciseType }} · {{ exercise.primaryFitnessFocus }}</p><p v-if="exercise.description" class="mt-1 text-xs text-muted-foreground">{{ exercise.description }}</p></div>
              </CardContent>
            </Card>
          </div>
          <p v-else class="py-12 text-center text-muted-foreground">Search the local exercise catalog.</p>
        </div>
      </GenericTabGroup>
    </div>
    <WorkoutAddModal v-model="isAddWorkoutModalOpen" @create="createWorkout" />
  </div>
</template>
