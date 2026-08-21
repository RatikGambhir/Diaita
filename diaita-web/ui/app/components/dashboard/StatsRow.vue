<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { Dumbbell, Footprints, Moon, Timer } from 'lucide-vue-next'
import StatCard from './StatCard.vue'
import { settingsApi } from '~/api/settings'
import { useWorkoutStats } from '~/composables/useWorkoutStats'
import { toIsoDate } from '~/composables/useNutritionSeries'
import { useUserStore } from '~/stores/useUserStore'
import type { ActivityLevelLifestyle } from '~/types/SettingsTypes'

const DAYS_IN_WINDOW = 7

const userStore = useUserStore()
const { stats, loadStats } = useWorkoutStats()
const lifestyle = ref<ActivityLevelLifestyle | null>(null)

const load = async () => {
  const end = new Date()
  const start = new Date(end)
  start.setDate(start.getDate() - (DAYS_IN_WINDOW - 1))

  await loadStats({ start: toIsoDate(start), end: toIsoDate(end) })

  const userId = userStore.getUser?.id?.trim()
  if (!userId) {
    lifestyle.value = null
    return
  }

  try {
    lifestyle.value = await settingsApi.get(userId, 'activity-lifestyle')
  } catch {
    // Lifestyle is supplementary here; its tiles fall back to a prompt rather than an error.
    lifestyle.value = null
  }
}

onMounted(() => {
  void load()
})

watch(() => userStore.getUser?.id, () => {
  void load()
})

const trainingMinutes = computed(() =>
  Math.round((stats.value?.totalDurationSeconds ?? 0) / 60),
)

const tiles = computed(() => [
  {
    label: 'Workouts This Week',
    value: stats.value?.workoutCount ?? 0,
    unit: stats.value?.workoutCount === 1 ? 'session' : 'sessions',
    icon: Dumbbell,
    iconColor: 'text-orange-500',
    blobColor: 'bg-orange-200',
  },
  {
    label: 'Training Time',
    value: trainingMinutes.value,
    unit: 'min',
    icon: Timer,
    iconColor: 'text-emerald-500',
    blobColor: 'bg-emerald-200',
  },
  {
    label: 'Daily Steps',
    value: lifestyle.value?.dailyStepCount?.toLocaleString('en-US') ?? '—',
    unit: 'steps',
    icon: Footprints,
    iconColor: 'text-blue-500',
    blobColor: 'bg-blue-200',
  },
  {
    label: 'Hours Slept',
    value: lifestyle.value?.sleepDuration ?? '—',
    unit: 'hrs',
    icon: Moon,
    iconColor: 'text-indigo-500',
    blobColor: 'bg-pink-200',
  },
])
</script>

<template>
  <div class="grid grid-cols-1 gap-5 sm:grid-cols-2 xl:grid-cols-4 md:gap-6">
    <StatCard
      v-for="tile in tiles"
      :key="tile.label"
      :label="tile.label"
      :value="tile.value"
      :unit="tile.unit"
      :icon="tile.icon"
      :icon-color="tile.iconColor"
      :blob-color="tile.blobColor"
    />
  </div>
</template>
