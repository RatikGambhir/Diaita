<script setup lang="ts">
import axios from "axios";
import { CalendarDays, Dumbbell, TrendingUp } from "lucide-vue-next";
import Badge from "~/components/ui/badge/Badge.vue";
import {
  Alert,
  AlertDescription,
  AlertTitle,
} from "~/components/ui/alert";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "~/components/ui/card";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "~/components/ui/tabs";
import { userApi } from "~/api/user";
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
  if (!training.value) {
    return [];
  }

  return [
    {
      label: "Primary focus",
      value: training.value.focus.primary,
      icon: Dumbbell,
    },
    {
      label: "Days per week",
      value: String(training.value.split.days_per_week),
      icon: CalendarDays,
    },
    {
      label: "Phases",
      value: String(training.value.phases.length),
      icon: TrendingUp,
    },
  ];
});

watch(
  weeks,
  (nextWeeks) => {
    if (!nextWeeks.length) {
      activeWeek.value = "";
      return;
    }

    if (!nextWeeks.some((week) => String(week.week_number) === activeWeek.value)) {
      activeWeek.value = String(nextWeeks[0]!.week_number);
    }
  },
  { immediate: true },
);

const redirectToSetup = async () => {
  await router.replace("/setup-profile");
};

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
    const nextRecommendation = await userApi.getRecommendations(userId);
    useRecommendation(nextRecommendation);
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

onMounted(() => {
  void loadRecommendation();
});
</script>

<template>
  <div class="flex-1 overflow-auto">
    <div class="mx-auto flex max-w-7xl flex-col gap-6 p-6">
      <header class="flex flex-col gap-3 md:flex-row md:items-end md:justify-between">
        <div>
          <Badge variant="outline" class="mb-3">Profile</Badge>
          <h1 class="text-3xl font-bold tracking-tight">Workout Recommendation</h1>
          <p class="text-sm text-muted-foreground">
            Your saved training plan, loaded from the onboarding recommendation.
          </p>
        </div>
        <div
          v-if="training"
          class="flex flex-wrap items-center gap-2 text-sm text-muted-foreground"
        >
          <Badge variant="secondary">{{ training.focus.primary }}</Badge>
          <Badge variant="secondary">{{ training.split.days_per_week }} days / week</Badge>
        </div>
      </header>

      <div v-if="isLoading" class="grid gap-4 md:grid-cols-3">
        <Card v-for="index in 3" :key="index" class="min-h-32 animate-pulse bg-muted/40" />
      </div>

      <Alert v-else-if="loadError" variant="destructive">
        <AlertTitle>Recommendation unavailable</AlertTitle>
        <AlertDescription>{{ loadError }}</AlertDescription>
      </Alert>

      <template v-else-if="training">
        <section class="grid gap-4 md:grid-cols-3">
          <Card v-for="card in summaryCards" :key="card.label">
            <CardHeader class="pb-3">
              <CardDescription>{{ card.label }}</CardDescription>
              <CardTitle class="flex items-center gap-3 text-2xl">
                <component :is="card.icon" class="h-5 w-5 text-primary" />
                {{ card.value }}
              </CardTitle>
            </CardHeader>
          </Card>
        </section>

        <section class="grid gap-4 lg:grid-cols-[1.3fr_1fr]">
          <Card>
            <CardHeader>
              <CardTitle>Weekly Split</CardTitle>
              <CardDescription>
                {{ training.split.groupings.join(" / ") }}
              </CardDescription>
            </CardHeader>
            <CardContent class="flex flex-wrap gap-2">
              <Badge
                v-for="grouping in training.split.groupings"
                :key="grouping"
                variant="secondary"
                class="px-3 py-1"
              >
                {{ grouping }}
              </Badge>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>Progression</CardTitle>
              <CardDescription>
                {{ training.progression_rules.load.increase }}
              </CardDescription>
            </CardHeader>
            <CardContent class="space-y-3 text-sm text-muted-foreground">
              <p>Frequency: {{ training.progression_rules.load.frequency }}</p>
              <div class="flex flex-wrap gap-2">
                <Badge
                  v-for="metric in training.progression_rules.performance.metrics"
                  :key="metric"
                  variant="outline"
                >
                  {{ metric }}
                </Badge>
              </div>
            </CardContent>
          </Card>
        </section>

        <Card>
          <CardHeader>
            <CardTitle>Training Phases</CardTitle>
            <CardDescription>
              {{ training.phases.length }} phases in the current recommendation.
            </CardDescription>
          </CardHeader>
          <CardContent class="grid gap-3 md:grid-cols-3">
            <div
              v-for="phase in training.phases"
              :key="`${phase.name}-${phase.duration}`"
              class="rounded-lg border bg-muted/20 p-4"
            >
              <p class="text-sm text-muted-foreground">{{ phase.duration }}</p>
              <p class="mt-1 font-medium">{{ phase.name }}</p>
              <p class="mt-2 text-sm text-muted-foreground">{{ phase.focus }}</p>
            </div>
          </CardContent>
        </Card>

        <Card v-if="weeks.length">
          <CardHeader>
            <CardTitle>Day-by-Day Plan</CardTitle>
            <CardDescription>
              Switch weeks to inspect the recommended split.
            </CardDescription>
          </CardHeader>
          <CardContent>
            <Tabs v-model="activeWeek" class="space-y-6">
              <TabsList class="h-auto flex-wrap justify-start gap-2 bg-transparent p-0">
                <TabsTrigger
                  v-for="week in weeks"
                  :key="week.week_number"
                  :value="String(week.week_number)"
                  class="rounded-full border bg-background px-4 py-2 data-[state=active]:bg-primary data-[state=active]:text-primary-foreground"
                >
                  Week {{ week.week_number }}
                </TabsTrigger>
              </TabsList>

              <TabsContent
                v-for="week in weeks"
                :key="week.week_number"
                :value="String(week.week_number)"
                class="m-0"
              >
                <div class="grid gap-4 xl:grid-cols-2">
                  <Card
                    v-for="day in week.days"
                    :key="`${week.week_number}-${day.day}`"
                    class="border-muted"
                  >
                    <CardHeader class="pb-3">
                      <div class="flex items-start justify-between gap-3">
                        <div>
                          <CardTitle class="text-lg">{{ day.day }}</CardTitle>
                          <CardDescription>{{ day.focus }}</CardDescription>
                        </div>
                        <Badge variant="secondary">{{ day.exercises.length }} exercises</Badge>
                      </div>
                    </CardHeader>
                    <CardContent class="space-y-3">
                      <div
                        v-for="exercise in day.exercises"
                        :key="`${day.day}-${exercise.exercise}`"
                        class="rounded-lg border bg-background p-3"
                      >
                        <div class="flex flex-wrap items-start justify-between gap-3">
                          <div>
                            <p class="font-medium">{{ exercise.exercise }}</p>
                            <p class="text-sm text-muted-foreground">
                              {{ exercise.sets }} sets x {{ exercise.reps }}
                            </p>
                          </div>
                          <Badge variant="outline">{{ exercise.rest_seconds }}s rest</Badge>
                        </div>
                        <p
                          v-if="exercise.notes"
                          class="mt-2 text-sm text-muted-foreground"
                        >
                          {{ exercise.notes }}
                        </p>
                      </div>
                    </CardContent>
                  </Card>
                </div>
              </TabsContent>
            </Tabs>
          </CardContent>
        </Card>

        <Alert v-else>
          <AlertTitle>No weeks available</AlertTitle>
          <AlertDescription>
            This recommendation does not include a day-by-day plan yet.
          </AlertDescription>
        </Alert>
      </template>

      <Alert v-else>
        <AlertTitle>Recommendation unavailable</AlertTitle>
        <AlertDescription>
          No workout recommendation is currently saved for this account.
        </AlertDescription>
      </Alert>
    </div>
  </div>
</template>
