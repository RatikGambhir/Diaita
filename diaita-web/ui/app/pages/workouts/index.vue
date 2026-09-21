<script setup lang="ts">
import { Activity, Clock, Dumbbell, Plus, Search, Trash2, TrendingUp } from "lucide-vue-next";
import { workoutApi } from "~/api/workouts";
import type { Exercise, WorkoutLog, WorkoutStats } from "~/types/WorkoutTypes";
import Button from "~/components/ui/button/Button.vue";
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
  <div class="app-page space-y-8">
      <AppPageHeader
        eyebrow="Training ledger"
        title="Workouts"
        description="Capture the work. Keep the useful detail. Let consistency—not memory—show your progress."
      >
        <template #actions>
          <Button @click="isAddWorkoutModalOpen = true"><Plus class="h-4 w-4" />New workout</Button>
        </template>
      </AppPageHeader>

      <section class="grid border-y sm:grid-cols-3 sm:divide-x">
        <div class="flex min-h-28 items-center gap-4 border-b py-5 sm:border-b-0 sm:px-6 sm:first:pl-0">
          <Dumbbell class="h-5 w-5 text-primary" />
          <div><p class="font-mono text-3xl font-semibold">{{ stats?.workoutsLast30Days ?? 0 }}</p><p class="mt-1 text-xs font-semibold uppercase tracking-[0.1em] text-muted-foreground">Sessions · 30 days</p></div>
        </div>
        <div class="flex min-h-28 items-center gap-4 border-b py-5 sm:border-b-0 sm:px-6">
          <Clock class="h-5 w-5 text-primary" />
          <div><p class="font-mono text-3xl font-semibold">{{ stats?.minutesLast30Days ?? 0 }}</p><p class="mt-1 text-xs font-semibold uppercase tracking-[0.1em] text-muted-foreground">Minutes · 30 days</p></div>
        </div>
        <div class="flex min-h-28 items-center gap-4 py-5 sm:px-6">
          <TrendingUp class="h-5 w-5 text-primary" />
          <div><p class="font-mono text-3xl font-semibold">{{ Math.round(stats?.totalVolumeKg ?? 0).toLocaleString() }} <span class="text-sm font-normal text-muted-foreground">kg</span></p><p class="mt-1 text-xs font-semibold uppercase tracking-[0.1em] text-muted-foreground">Lifetime volume</p></div>
        </div>
      </section>

      <GenericTabGroup
        v-model="activeTab"
        :tabs="[{ value: 'workouts', label: 'Workouts' }, { value: 'exercises', label: 'Exercise library' }]"
      >
        <div v-if="activeTab === 'workouts'" class="space-y-5">
          <div class="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
            <div class="relative max-w-lg flex-1">
              <Search class="absolute left-3 top-3 h-4 w-4 text-muted-foreground" />
              <Input v-model="searchQuery" class="pl-10" placeholder="Search your workouts" />
            </div>
            <p class="text-sm text-muted-foreground">{{ filteredWorkouts.length }} {{ filteredWorkouts.length === 1 ? 'session' : 'sessions' }}</p>
          </div>

          <div v-if="isLoading" class="py-16 text-center text-muted-foreground">Loading workouts…</div>
          <div v-else-if="errorMessage" class="rounded-lg border border-destructive/40 p-4 text-destructive">{{ errorMessage }}</div>
          <div v-else-if="filteredWorkouts.length === 0" class="border-y py-16 text-center">
            <Dumbbell class="mx-auto mb-4 h-8 w-8 text-primary" />
            <p class="font-semibold">No workouts match this view.</p>
            <p class="mt-1 text-sm text-muted-foreground">Create a session or try a different search.</p>
          </div>
          <div v-else class="divide-y border-y">
            <article v-for="(workout, index) in filteredWorkouts" :key="workout.id" class="group grid gap-4 py-5 sm:grid-cols-[2.5rem_minmax(0,1fr)_auto] sm:items-center">
              <span class="hidden font-mono text-xs text-muted-foreground sm:block">{{ String(index + 1).padStart(2, '0') }}</span>
              <button type="button" class="focus-ring min-w-0 rounded-md text-left" @click="navigateTo(`/workouts/${workout.id}`)">
                <h2 class="truncate text-lg font-semibold group-hover:text-primary">{{ workout.name }}</h2>
                <p class="mt-1 text-sm text-muted-foreground">{{ new Date(workout.performedAt).toLocaleString() }}</p>
                <div class="mt-3 flex flex-wrap gap-x-5 gap-y-1 font-mono text-xs text-muted-foreground">
                  <span>{{ workout.durationMinutes }} min</span>
                  <span>{{ workout.exercises.length }} exercises</span>
                  <span>{{ Math.round(workout.totalVolumeKg).toLocaleString() }} kg volume</span>
                </div>
              </button>
              <div class="flex items-center gap-1">
                <Button variant="ghost" size="sm" @click="navigateTo(`/workouts/${workout.id}`)">Open</Button>
                <Button variant="ghost" size="icon" :aria-label="`Delete ${workout.name}`" @click="deleteWorkout(workout)"><Trash2 class="h-4 w-4 text-destructive" /></Button>
              </div>
            </article>
          </div>
        </div>

        <div v-else class="space-y-5">
          <form class="flex max-w-xl gap-2" @submit.prevent="searchExercises">
            <div class="relative flex-1"><Search class="absolute left-3 top-3 h-4 w-4 text-muted-foreground" /><Input v-model="exerciseQuery" class="pl-10" placeholder="Search bench, run, row…" /></div>
            <Button type="submit">Search</Button>
          </form>
          <div v-if="exercises.length" class="grid border-y md:grid-cols-2">
            <div v-for="exercise in exercises" :key="exercise.id ?? exercise.exercise" class="flex items-start gap-3 border-b p-5 md:odd:border-r">
                <Activity class="mt-1 h-5 w-5 text-primary" />
                <div><p class="font-medium">{{ exercise.exercise }}</p><p class="text-sm capitalize text-muted-foreground">{{ exercise.exerciseType }} · {{ exercise.primaryFitnessFocus }}</p><p v-if="exercise.description" class="mt-1 text-xs text-muted-foreground">{{ exercise.description }}</p></div>
            </div>
          </div>
          <p v-else class="py-12 text-center text-muted-foreground">Search the local exercise catalog.</p>
        </div>
      </GenericTabGroup>
    <WorkoutAddModal v-model="isAddWorkoutModalOpen" @create="createWorkout" />
  </div>
</template>
