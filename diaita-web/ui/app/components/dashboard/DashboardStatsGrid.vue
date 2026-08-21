<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { Loader2 } from 'lucide-vue-next'
import DashboardStatCard from './DashboardStatCard.vue'
import { useNutritionSeries } from '~/composables/useNutritionSeries'

const { summary, isLoading, error, loadToday } = useNutritionSeries()

onMounted(() => {
  void loadToday()
})

const round = (value: number) => Math.round(value)

/**
 * Change is measured against the user's own historical daily average, so a first day of logging
 * shows 0% rather than a fabricated trend.
 */
const percentChange = (actual: number, reference: number | null | undefined) => {
  if (!reference || reference <= 0) {
    return 0
  }
  return Math.round(((actual - reference) / reference) * 100)
}

const describeTarget = (actual: number, target: number | null, unit: string) => {
  if (target == null) {
    return 'Set up your profile to get a daily target'
  }

  const remaining = round(target - actual)
  return remaining >= 0
    ? `${remaining}${unit} remaining of ${round(target)}${unit} goal`
    : `${Math.abs(remaining)}${unit} over your ${round(target)}${unit} goal`
}

const describeTrend = (actual: number, target: number | null) => {
  if (target == null) {
    return 'No target yet'
  }
  if (actual > target * 1.05) {
    return 'Above target today'
  }
  if (actual < target * 0.85) {
    return 'Below target today'
  }
  return 'On track for daily goal'
}

const stats = computed(() => {
  const today = summary.value
  if (!today) {
    return []
  }

  const analytics = today.analytics
  const historical = analytics.historical

  return [
    {
      title: 'Calories Today',
      actual: today.totalCal,
      target: analytics.recCal,
      average: historical?.avgCal,
      unit: 'kcal',
      suffix: '',
    },
    {
      title: 'Protein',
      actual: today.totalProtein,
      target: analytics.recProtein,
      average: historical?.avgProtein,
      unit: 'g',
      suffix: 'g',
    },
    {
      title: 'Carbohydrates',
      actual: today.totalCarb,
      target: analytics.recCarb,
      average: historical?.avgCarbs,
      unit: 'g',
      suffix: 'g',
    },
    {
      title: 'Fats',
      actual: today.totalFat,
      target: analytics.recFat,
      average: historical?.avgFat,
      unit: 'g',
      suffix: 'g',
    },
  ].map((stat) => ({
    title: stat.title,
    value: `${round(stat.actual).toLocaleString('en-US')}${stat.suffix}`,
    change: percentChange(stat.actual, stat.average),
    trend: describeTrend(stat.actual, stat.target),
    description: describeTarget(stat.actual, stat.target, stat.unit),
  }))
})
</script>

<template>
  <div
    v-if="isLoading && stats.length === 0"
    class="mb-6 flex items-center justify-center gap-2 rounded-2xl bg-muted py-10 text-muted-foreground"
  >
    <Loader2 class="h-4 w-4 animate-spin" />
    Loading today's nutrition…
  </div>

  <p
    v-else-if="error"
    class="mb-6 rounded-2xl bg-muted px-5 py-6 text-sm text-muted-foreground"
  >
    {{ error }}
  </p>

  <p
    v-else-if="stats.length === 0"
    class="mb-6 rounded-2xl bg-muted px-5 py-6 text-sm text-muted-foreground"
  >
    Log a meal to see today's macros here.
  </p>

  <div v-else class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
    <DashboardStatCard
      v-for="stat in stats"
      :key="stat.title"
      :title="stat.title"
      :value="stat.value"
      :change="stat.change"
      :trend="stat.trend"
      :description="stat.description"
    />
  </div>
</template>
