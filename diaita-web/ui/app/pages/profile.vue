<script setup lang="ts">
import axios from "axios";
import { CalendarDays, Dumbbell, TrendingUp } from "lucide-vue-next";
import Badge from "~/components/ui/badge/Badge.vue";
import { Alert, AlertDescription, AlertTitle } from "~/components/ui/alert";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "~/components/ui/card";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "~/components/ui/tabs";
import { userApi } from "~/api/user";
import { formatOptionLabel } from "~/lib/utils";
import type { Recommendation } from "~/types/ProfileTypes";

const userStore = useUserStore();
const router = useRouter();
const isLoading = ref(true);
const loadError = ref("");
const activeWeek = ref("");

const recommendation = computed(() => userStore.getRecommendation);
const training = computed(() => recommendation.value?.training ?? null);
const weeks = computed(() => training.value?.day_by_day_plan.weeks ?? []);

const summaryCards = computed(() => {
  if (!training.value) return [];
  return [
    { label: "Primary focus", value: formatOptionLabel(training.value.focus.primary), icon: Dumbbell },
    { label: "Days per week", value: String(training.value.split.days_per_week), icon: CalendarDays },
    { label: "Training phases", value: String(training.value.phases.length), icon: TrendingUp },
  ];
});

watch(weeks, (nextWeeks) => {
  if (!nextWeeks.length) {
    activeWeek.value = "";
    return;
  }
  if (!nextWeeks.some((week) => String(week.week_number) === activeWeek.value)) {
    activeWeek.value = String(nextWeeks[0]!.week_number);
  }
}, { immediate: true });

const redirectToSetup = async () => router.replace("/setup-profile");

const useRecommendation = (nextRecommendation: Recommendation) => {
  userStore.setRecommendation(nextRecommendation);
  const firstWeek = nextRecommendation.training.day_by_day_plan.weeks[0];
  activeWeek.value = firstWeek ? String(firstWeek.week_number) : "";
};

const loadRecommendation = async () => {
  const userId = userStore.getUser?.id?.trim();
  if (!userId) {
    await redirectToSetup();
    return;
  }
  if (userStore.getRecommendation) {
    useRecommendation(userStore.getRecommendation);
    isLoading.value = false;
    return;
  }
  loadError.value = "";
  try {
    useRecommendation(await userApi.getRecommendations(userId));
  } catch (error) {
    if (axios.isAxiosError(error) && error.response?.status === 404) {
      await redirectToSetup();
      return;
    }
    console.error("Error loading recommendations:", error);
    loadError.value = "Could not load your workout recommendation.";
  } finally {
    isLoading.value = false;
  }
};

onMounted(() => void loadRecommendation());
</script>

<template>
  <div class="app-page space-y-8">
    <AppPageHeader
      eyebrow="Training recommendation"
      title="Your plan"
      description="A structured training path built from the goals, experience, and constraints in your health profile."
    >
      <template v-if="training" #meta>
        <div class="mt-4 flex flex-wrap gap-2">
          <Badge variant="secondary">{{ formatOptionLabel(training.focus.primary) }}</Badge>
          <Badge variant="outline">{{ training.split.days_per_week }} days / week</Badge>
        </div>
      </template>
    </AppPageHeader>

    <div v-if="isLoading" class="grid border-y sm:grid-cols-3 sm:divide-x">
      <div v-for="index in 3" :key="index" class="h-28 animate-pulse bg-muted/45" />
    </div>

    <Alert v-else-if="loadError" variant="destructive">
      <AlertTitle>Recommendation unavailable</AlertTitle>
      <AlertDescription>{{ loadError }}</AlertDescription>
    </Alert>

    <template v-else-if="training">
      <section class="grid border-y sm:grid-cols-3 sm:divide-x">
        <div v-for="card in summaryCards" :key="card.label" class="flex min-h-28 items-center gap-4 border-b py-5 sm:border-b-0 sm:px-6 sm:first:pl-0">
          <component :is="card.icon" class="h-5 w-5 text-primary" />
          <div><p class="text-xl font-semibold sm:text-2xl">{{ card.value }}</p><p class="mt-1 text-xs font-semibold uppercase tracking-[0.1em] text-muted-foreground">{{ card.label }}</p></div>
        </div>
      </section>

      <section class="grid gap-5 lg:grid-cols-[1.15fr_.85fr]">
        <Card class="overflow-hidden">
          <CardHeader class="border-b">
            <p class="eyebrow">Program structure</p>
            <CardTitle class="display-title mt-2 text-2xl">Weekly split</CardTitle>
            <CardDescription>{{ training.split.groupings.join(" / ") }}</CardDescription>
          </CardHeader>
          <CardContent class="divide-y pt-6">
            <div v-for="(grouping, index) in training.split.groupings" :key="grouping" class="grid grid-cols-[2rem_1fr] items-center gap-3 py-3 first:pt-0 last:pb-0">
              <span class="font-mono text-xs text-muted-foreground">{{ String(index + 1).padStart(2, '0') }}</span>
              <span class="font-medium">{{ grouping }}</span>
            </div>
          </CardContent>
        </Card>

        <Card class="overflow-hidden">
          <CardHeader class="border-b">
            <p class="eyebrow">How to progress</p>
            <CardTitle class="display-title mt-2 text-2xl">Progression</CardTitle>
            <CardDescription>{{ training.progression_rules.load.increase }}</CardDescription>
          </CardHeader>
          <CardContent class="pt-6 text-sm">
            <p><span class="text-muted-foreground">Frequency</span><br><span class="mt-1 block font-medium">{{ training.progression_rules.load.frequency }}</span></p>
            <div class="mt-5 flex flex-wrap gap-2">
              <Badge v-for="metric in training.progression_rules.performance.metrics" :key="metric" variant="outline">{{ metric }}</Badge>
            </div>
          </CardContent>
        </Card>
      </section>

      <section>
        <div class="border-b pb-4">
          <p class="eyebrow">Program arc</p>
          <h2 class="display-title mt-2 text-3xl">Training phases</h2>
        </div>
        <div class="divide-y">
          <article v-for="(phase, index) in training.phases" :key="`${phase.name}-${phase.duration}`" class="grid gap-2 py-5 sm:grid-cols-[3rem_minmax(10rem,.7fr)_8rem_1fr] sm:items-baseline">
            <span class="font-mono text-xs text-muted-foreground">{{ String(index + 1).padStart(2, '0') }}</span>
            <p class="font-semibold">{{ phase.name }}</p>
            <p class="font-mono text-xs text-muted-foreground">{{ phase.duration }}</p>
            <p class="text-sm leading-6 text-muted-foreground">{{ phase.focus }}</p>
          </article>
        </div>
      </section>

      <Card v-if="weeks.length" class="overflow-hidden">
        <CardHeader class="border-b">
          <p class="eyebrow">Weekly prescription</p>
          <CardTitle class="display-title mt-2 text-3xl">Day-by-day plan</CardTitle>
          <CardDescription>Switch weeks to inspect the recommended work.</CardDescription>
        </CardHeader>
        <CardContent class="pt-6">
          <Tabs v-model="activeWeek" class="space-y-7">
            <TabsList class="h-auto w-full justify-start gap-6 overflow-x-auto rounded-none border-b bg-transparent p-0">
              <TabsTrigger
                v-for="week in weeks"
                :key="week.week_number"
                :value="String(week.week_number)"
                class="rounded-none border-b-2 border-transparent bg-transparent px-0 pb-3 pt-1 shadow-none data-[state=active]:border-primary data-[state=active]:bg-transparent data-[state=active]:shadow-none"
              >
                Week {{ week.week_number }}
              </TabsTrigger>
            </TabsList>

            <TabsContent v-for="week in weeks" :key="week.week_number" :value="String(week.week_number)" class="m-0">
              <div class="divide-y border-y">
                <article v-for="day in week.days" :key="`${week.week_number}-${day.day}`" class="grid gap-4 py-6 lg:grid-cols-[12rem_1fr]">
                  <div>
                    <p class="font-semibold">{{ day.day }}</p>
                    <p class="mt-1 text-sm text-muted-foreground">{{ day.focus }}</p>
                    <p class="mt-3 font-mono text-xs text-muted-foreground">{{ day.exercises.length }} exercises</p>
                  </div>
                  <div class="divide-y rounded-lg border bg-muted/20 px-4">
                    <div v-for="exercise in day.exercises" :key="`${day.day}-${exercise.exercise}`" class="grid gap-2 py-3 sm:grid-cols-[1fr_auto]">
                      <div>
                        <p class="font-medium">{{ exercise.exercise }}</p>
                        <p class="mt-0.5 font-mono text-xs text-muted-foreground">{{ exercise.sets }} sets × {{ exercise.reps }} · {{ exercise.rest_seconds }}s rest</p>
                        <p v-if="exercise.notes" class="mt-2 text-sm text-muted-foreground">{{ exercise.notes }}</p>
                      </div>
                    </div>
                  </div>
                </article>
              </div>
            </TabsContent>
          </Tabs>
        </CardContent>
      </Card>

      <Alert v-else>
        <AlertTitle>No weeks available</AlertTitle>
        <AlertDescription>This recommendation does not include a day-by-day plan yet.</AlertDescription>
      </Alert>
    </template>

    <Alert v-else>
      <AlertTitle>Recommendation unavailable</AlertTitle>
      <AlertDescription>No workout recommendation is currently saved for this account.</AlertDescription>
    </Alert>
  </div>
</template>
