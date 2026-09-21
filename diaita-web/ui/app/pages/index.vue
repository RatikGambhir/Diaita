<script setup lang="ts">
import { Apple, ArrowRight, Clock, Dumbbell, Flame, Plus, TrendingUp } from "lucide-vue-next";
import { nutritionApi } from "~/api/nutrition";
import { workoutApi } from "~/api/workouts";
import { useUserStore } from "~/stores/useUserStore";
import type { NutritionDaySummary } from "~/types/NutritionTypes";
import type { WorkoutLog, WorkoutStats } from "~/types/WorkoutTypes";
import Button from "~/components/ui/button/Button.vue";
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
  void useUserProfile().fetchProfile();
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

const firstName = computed(() => userStore.getUser?.displayName?.trim().split(/\s+/)[0] || "there");
const todayLabel = computed(() => new Intl.DateTimeFormat("en-US", {
  weekday: "long",
  month: "long",
  day: "numeric",
}).format(new Date()));

const macroRows = computed(() => [
  { label: "Carbohydrate", value: Math.round(nutrition.value?.totalCarb ?? 0), unit: "g", color: "bg-chart-1" },
  { label: "Protein", value: Math.round(nutrition.value?.totalProtein ?? 0), unit: "g", color: "bg-chart-3" },
  { label: "Fat", value: Math.round(nutrition.value?.totalFat ?? 0), unit: "g", color: "bg-chart-2" },
]);
</script>

<template>
  <div class="app-page space-y-8">
    <AppPageHeader
      :eyebrow="todayLabel"
      :title="`Today, ${firstName}`"
      description="A clear read on what you have eaten, how you have trained, and what is worth doing next."
    >
      <template #actions>
        <Button variant="outline" @click="navigateTo('/nutrition')"><Apple class="h-4 w-4" />Log food</Button>
        <Button @click="navigateTo('/workouts')"><Plus class="h-4 w-4" />Start workout</Button>
      </template>
    </AppPageHeader>

    <ProfileSetupBanner />

    <section class="grid border-y sm:grid-cols-2 xl:grid-cols-4">
      <div v-for="(card, index) in cards" :key="card.label" class="flex min-h-28 items-center gap-4 border-b py-5 sm:px-5 xl:border-b-0" :class="index % 2 === 0 ? 'sm:border-r' : index < 3 ? 'xl:border-r' : ''">
        <component :is="card.icon" class="h-5 w-5 shrink-0 text-primary" />
        <div>
          <p class="font-mono text-2xl font-semibold tracking-tight">{{ card.value }} <span class="text-xs font-normal text-muted-foreground">{{ card.suffix }}</span></p>
          <p class="mt-1 text-xs font-semibold uppercase tracking-[0.1em] text-muted-foreground">{{ card.label }}</p>
        </div>
      </div>
    </section>

    <section class="grid gap-8 lg:grid-cols-[minmax(0,1.15fr)_minmax(20rem,.85fr)]">
      <article class="overflow-hidden rounded-xl bg-foreground text-background">
        <div class="flex items-start justify-between border-b border-background/15 px-6 py-5 sm:px-8">
          <div>
            <p class="eyebrow text-background/55">Today’s nutrition</p>
            <p class="mt-2 text-sm text-background/65">Live from your food ledger</p>
          </div>
          <NuxtLink to="/nutrition" class="focus-ring flex items-center gap-2 rounded-md text-sm font-semibold text-background/75 hover:text-background">
            Open log <ArrowRight class="h-4 w-4" />
          </NuxtLink>
        </div>
        <div class="grid gap-8 px-6 py-8 sm:grid-cols-[1fr_1.2fr] sm:px-8 sm:py-10">
          <div>
            <p class="font-mono text-6xl font-semibold tracking-[-0.07em] sm:text-7xl">{{ Math.round(nutrition?.totalCal ?? 0).toLocaleString() }}</p>
            <p class="mt-2 text-sm font-medium text-background/55">kilocalories logged</p>
          </div>
          <div class="divide-y divide-background/15 border-y border-background/15">
            <div v-for="macro in macroRows" :key="macro.label" class="grid grid-cols-[8px_1fr_auto] items-center gap-3 py-3.5">
              <span class="h-2 w-2" :class="macro.color" />
              <span class="text-sm text-background/65">{{ macro.label }}</span>
              <span class="font-mono text-sm font-semibold">{{ macro.value }} {{ macro.unit }}</span>
            </div>
          </div>
        </div>
      </article>

      <article>
        <div class="flex items-end justify-between border-b pb-4">
          <div>
            <p class="eyebrow">Training log</p>
            <h2 class="display-title mt-2 text-2xl">Recent sessions</h2>
          </div>
          <NuxtLink to="/workouts" class="focus-ring rounded-md p-2 text-muted-foreground hover:bg-muted hover:text-foreground" aria-label="Open workout history">
            <ArrowRight class="h-5 w-5" />
          </NuxtLink>
        </div>

        <div v-if="recentWorkouts.length" class="divide-y">
          <button
            v-for="(workout, index) in recentWorkouts"
            :key="workout.id"
            type="button"
            class="focus-ring group grid w-full grid-cols-[2rem_1fr_auto] items-center gap-3 py-4 text-left"
            @click="navigateTo(`/workouts/${workout.id}`)"
          >
            <span class="font-mono text-xs text-muted-foreground">{{ String(index + 1).padStart(2, '0') }}</span>
            <span class="min-w-0">
              <span class="block truncate font-semibold group-hover:text-primary">{{ workout.name }}</span>
              <span class="mt-0.5 block text-xs text-muted-foreground">{{ new Date(workout.performedAt).toLocaleDateString() }} · {{ workout.exercises.length }} exercises</span>
            </span>
            <TrendingUp class="h-4 w-4 text-muted-foreground group-hover:text-primary" />
          </button>
        </div>
        <div v-else class="border-b py-12">
          <Dumbbell class="mb-4 h-7 w-7 text-primary" />
          <p class="font-semibold">Your training ledger is open.</p>
          <p class="mt-1 max-w-sm text-sm leading-6 text-muted-foreground">Log a first session and your latest work will appear here.</p>
        </div>
      </article>
    </section>
  </div>
</template>
