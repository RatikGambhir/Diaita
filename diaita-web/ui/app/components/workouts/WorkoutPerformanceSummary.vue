<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import Card from '~/components/ui/card/Card.vue'
import CardHeader from '~/components/ui/card/CardHeader.vue'
import CardContent from '~/components/ui/card/CardContent.vue'
import { Loader2 } from 'lucide-vue-next'
import { useWorkoutStats } from '~/composables/useWorkoutStats'
import { formatWorkoutDate, formatWorkoutDuration, formatWorkoutVolume } from '~/utils/workouts'

const DAYS_IN_WINDOW = 90

const { stats, isLoading, error, loadStats } = useWorkoutStats()

const rangeStart = ref('')
const rangeEnd = ref('')

onMounted(() => {
  const end = new Date()
  const start = new Date(end)
  start.setDate(start.getDate() - (DAYS_IN_WINDOW - 1))

  rangeStart.value = start.toISOString().slice(0, 10)
  rangeEnd.value = end.toISOString().slice(0, 10)

  void loadStats({ start: rangeStart.value, end: rangeEnd.value })
})

const summaryTiles = computed(() => {
  const current = stats.value
  if (!current) {
    return []
  }

  return [
    { label: 'Workouts', value: `${current.workoutCount}` },
    { label: 'Total time', value: formatWorkoutDuration(current.totalDurationSeconds) },
    { label: 'Average session', value: formatWorkoutDuration(current.averageDurationSeconds) },
    { label: 'Total volume', value: formatWorkoutVolume(current.totalVolumeKg) },
    { label: 'Working sets', value: `${current.totalSets}` },
    { label: 'Cardio time', value: formatWorkoutDuration(current.totalCardioSeconds) },
  ]
})

const recentDays = computed(() => (stats.value?.daily ?? []).slice(-14).reverse())

const peakVolume = computed(() =>
  Math.max(1, ...recentDays.value.map((point) => point.volumeKg)),
)
</script>

<template>
  <Card>
    <CardHeader>
      <h2 class="text-lg font-semibold">Performance</h2>
      <p class="text-sm text-muted-foreground">
        Volume, consistency, and session length over the last {{ DAYS_IN_WINDOW }} days.
      </p>
    </CardHeader>
    <CardContent class="space-y-6">
      <div
        v-if="isLoading"
        class="flex items-center justify-center gap-2 py-10 text-muted-foreground"
      >
        <Loader2 class="h-4 w-4 animate-spin" />
        Loading performance…
      </div>

      <p v-else-if="error" class="py-10 text-center text-destructive">{{ error }}</p>

      <p
        v-else-if="!stats || stats.workoutCount === 0"
        class="py-10 text-center text-muted-foreground"
      >
        Log a workout to start tracking performance trends.
      </p>

      <template v-else>
        <div class="grid grid-cols-2 gap-4 md:grid-cols-3 lg:grid-cols-6">
          <div
            v-for="tile in summaryTiles"
            :key="tile.label"
            class="rounded-lg border border-border p-4"
          >
            <p class="text-xs uppercase tracking-wide text-muted-foreground">{{ tile.label }}</p>
            <p class="mt-1 text-lg font-semibold">{{ tile.value }}</p>
          </div>
        </div>

        <div>
          <h3 class="mb-3 text-sm font-semibold text-foreground">Recent sessions</h3>
          <ul class="space-y-2">
            <li
              v-for="point in recentDays"
              :key="point.date"
              class="flex items-center gap-3 text-sm"
            >
              <span class="w-28 shrink-0 text-muted-foreground">
                {{ formatWorkoutDate(point.date) }}
              </span>
              <span class="h-2 flex-1 overflow-hidden rounded-full bg-muted">
                <span
                  class="block h-full rounded-full bg-primary"
                  :style="{ width: `${Math.round((point.volumeKg / peakVolume) * 100)}%` }"
                />
              </span>
              <span class="w-24 shrink-0 text-right text-muted-foreground">
                {{ formatWorkoutVolume(point.volumeKg) }}
              </span>
            </li>
          </ul>
        </div>
      </template>
    </CardContent>
  </Card>
</template>
