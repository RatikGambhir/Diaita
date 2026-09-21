<script setup lang="ts">
import { Apple, ArrowRight, Clock, Dumbbell, Flame, Plus, TrendingUp } from "lucide-vue-next";
import { nutritionApi } from "~/api/nutrition";
import { workoutApi } from "~/api/workouts";
import { useUserStore } from "~/stores/useUserStore";
import type { NutritionDaySummary } from "~/types/NutritionTypes";
import type { WorkoutLog, WorkoutStats } from "~/types/WorkoutTypes";
import Button from "~/components/ui/button/Button.vue";
import Card from "~/components/ui/card/Card.vue";
import CardContent from "~/components/ui/card/CardContent.vue";
import CardHeader from "~/components/ui/card/CardHeader.vue";
import ProfileSetupBanner from "~/components/ProfileSetupBanner.vue";

const userStore = useUserStore();
const nutrition = ref<NutritionDaySummary | null>(null);
const workoutStats = ref<WorkoutStats | null>(null);
const recentWorkouts = ref<WorkoutLog[]>([]);

const today = () => {
  const date = new Date();
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
};

onMounted(async () => {
  const userId = userStore.getUser?.id;
  if (!userId) return;
  const [nutritionResult, statsResult, workoutsResult] = await Promise.allSettled([
    nutritionApi.getDaySummary(userId, today()),
    workoutApi.stats(),
    workoutApi.list(),
  ]);
  if (nutritionResult.status === "fulfilled") nutrition.value = nutritionResult.value;
  if (statsResult.status === "fulfilled") workoutStats.value = statsResult.value;
  if (workoutsResult.status === "fulfilled") recentWorkouts.value = workoutsResult.value.slice(0, 4);
});

const cards = computed(() => [
  { label: "Calories today", value: Math.round(nutrition.value?.totalCal ?? 0).toLocaleString(), suffix: "kcal", icon: Flame },
  { label: "Protein today", value: Math.round(nutrition.value?.totalProtein ?? 0).toLocaleString(), suffix: "g", icon: Apple },
  { label: "Workouts / 30 days", value: String(workoutStats.value?.workoutsLast30Days ?? 0), suffix: "sessions", icon: Dumbbell },
  { label: "Training time / 30 days", value: String(workoutStats.value?.minutesLast30Days ?? 0), suffix: "minutes", icon: Clock },
]);
</script>

<template>
  <div class="flex-1 overflow-auto p-6">
    <div class="mx-auto max-w-7xl space-y-6">
      <header class="flex flex-col gap-4 sm:flex-row sm:items-end sm:justify-between">
        <div><p class="text-sm text-muted-foreground">Welcome back</p><h1 class="text-3xl font-bold">{{ userStore.getUser?.displayName }}</h1></div>
        <div class="flex gap-2"><Button variant="outline" @click="navigateTo('/nutrition')"><Apple class="mr-2 h-4 w-4" />Log food</Button><Button @click="navigateTo('/workouts')"><Plus class="mr-2 h-4 w-4" />New workout</Button></div>
      </header>

      <ProfileSetupBanner />

      <section class="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
        <Card v-for="card in cards" :key="card.label">
          <CardContent class="flex items-center gap-4 p-5">
            <div class="rounded-xl bg-primary/10 p-3"><component :is="card.icon" class="h-5 w-5 text-primary" /></div>
            <div><p class="text-2xl font-semibold">{{ card.value }} <span class="text-sm font-normal text-muted-foreground">{{ card.suffix }}</span></p><p class="text-sm text-muted-foreground">{{ card.label }}</p></div>
          </CardContent>
        </Card>
      </section>

      <section class="grid gap-6 lg:grid-cols-2">
        <Card>
          <CardHeader class="flex-row items-center justify-between"><div><h2 class="text-lg font-semibold">Today’s nutrition</h2><p class="text-sm text-muted-foreground">Live totals from your food log</p></div><Button variant="ghost" size="sm" @click="navigateTo('/nutrition')">Open <ArrowRight class="ml-2 h-4 w-4" /></Button></CardHeader>
          <CardContent class="grid grid-cols-2 gap-4">
            <div class="rounded-lg bg-muted p-4"><p class="text-sm text-muted-foreground">Carbohydrates</p><p class="mt-1 text-xl font-semibold">{{ Math.round(nutrition?.totalCarb ?? 0) }} g</p></div>
            <div class="rounded-lg bg-muted p-4"><p class="text-sm text-muted-foreground">Fat</p><p class="mt-1 text-xl font-semibold">{{ Math.round(nutrition?.totalFat ?? 0) }} g</p></div>
            <div class="rounded-lg bg-muted p-4"><p class="text-sm text-muted-foreground">Protein</p><p class="mt-1 text-xl font-semibold">{{ Math.round(nutrition?.totalProtein ?? 0) }} g</p></div>
            <div class="rounded-lg bg-muted p-4"><p class="text-sm text-muted-foreground">Calories</p><p class="mt-1 text-xl font-semibold">{{ Math.round(nutrition?.totalCal ?? 0) }} kcal</p></div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader class="flex-row items-center justify-between"><div><h2 class="text-lg font-semibold">Recent workouts</h2><p class="text-sm text-muted-foreground">Your latest logged sessions</p></div><Button variant="ghost" size="sm" @click="navigateTo('/workouts')">Open <ArrowRight class="ml-2 h-4 w-4" /></Button></CardHeader>
          <CardContent>
            <div v-if="recentWorkouts.length" class="space-y-2">
              <button v-for="workout in recentWorkouts" :key="workout.id" type="button" class="flex w-full items-center justify-between rounded-lg border p-3 text-left hover:bg-muted" @click="navigateTo(`/workouts/${workout.id}`)">
                <div><p class="font-medium">{{ workout.name }}</p><p class="text-xs text-muted-foreground">{{ new Date(workout.performedAt).toLocaleDateString() }} · {{ workout.exercises.length }} exercises</p></div><TrendingUp class="h-4 w-4 text-primary" />
              </button>
            </div>
            <div v-else class="py-12 text-center text-muted-foreground">Log a workout to see progress here.</div>
          </CardContent>
        </Card>
      </section>
    </div>
  </div>
</template>
