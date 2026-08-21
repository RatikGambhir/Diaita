<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { format, isSameWeek, parseISO, subWeeks } from 'date-fns'
import { ChevronLeft, ChevronRight, Loader2 } from 'lucide-vue-next'
import { VisXYContainer, VisLine, VisAxis, VisCrosshair, VisTooltip } from '@unovis/vue'
import { useElementSize } from '@vueuse/core'
import Button from '~/components/ui/button/Button.vue'
import { workoutsApi } from '~/api/workouts'
import { useUserStore } from '~/stores/useUserStore'
import type { Workout } from '~/types/WorkoutTypes'

type WorkoutPoint = {
  date: Date
  best: number
  average: number
}

type ExerciseTrend = {
  key: string
  label: string
  description: string
  unit: string
  points: WorkoutPoint[]
}

const WEEKS_IN_WINDOW = 12
const BEST_COLOR = 'oklch(0.7489 0.1486 158.6624)'
const AVERAGE_COLOR = 'oklch(0.7156 0.0605 248.6845)'

const cardRef = ref<HTMLElement | null>(null)
const { width } = useElementSize(cardRef)
const userStore = useUserStore()

const transitionName = ref<'slide-left' | 'slide-right'>('slide-left')
const activeIndex = ref(0)
const workouts = ref<Workout[]>([])
const isLoading = ref(false)
const error = ref<string | null>(null)

const weekStarts = computed(() =>
  Array.from({ length: WEEKS_IN_WINDOW }, (_, offset) =>
    subWeeks(new Date(), WEEKS_IN_WINDOW - 1 - offset),
  ),
)

const loadWorkouts = async () => {
  const userId = userStore.getUser?.id?.trim()
  if (!userId) {
    workouts.value = []
    return
  }

  isLoading.value = true
  error.value = null

  try {
    const end = new Date()
    const start = subWeeks(end, WEEKS_IN_WINDOW - 1)

    // The list endpoint returns each workout with its exercises, so one page covers the window.
    const response = await workoutsApi.list({
      userId,
      pageSize: 100,
    })

    workouts.value = response.workouts.filter((workout) => {
      const performedOn = new Date(workout.performedAt)
      return performedOn >= start && performedOn <= end
    })
  } catch (loadError) {
    workouts.value = []
    error.value =
      loadError instanceof Error ? loadError.message : 'Failed to load workout performance.'
  } finally {
    isLoading.value = false
  }
}

onMounted(() => {
  void loadWorkouts()
})

watch(() => userStore.getUser?.id, () => {
  void loadWorkouts()
})

/**
 * Builds a per-exercise weekly trend from the user's own logged lifts: the heaviest working set that
 * week and the average load across its sets. Only lifting entries with a recorded weight qualify,
 * and an exercise needs at least two distinct weeks before a line is worth drawing.
 */
const trends = computed<ExerciseTrend[]>(() => {
  const byExercise = new Map<string, Array<{ date: Date; weightKg: number }>>()

  workouts.value.forEach((workout) => {
    const performedOn = parseISO(workout.performedAt)

    workout.exercises.forEach((exercise) => {
      if (exercise.category !== 'lifting' || !exercise.weightKg || exercise.weightKg <= 0) {
        return
      }

      const key = exercise.name.trim().toLowerCase()
      const entries = byExercise.get(key) ?? []
      entries.push({ date: performedOn, weightKg: exercise.weightKg })
      byExercise.set(key, entries)
    })
  })

  const labelFor = (key: string) =>
    key.replace(/\b\w/g, (character) => character.toUpperCase())

  return [...byExercise.entries()]
    .map(([key, entries]) => {
      const points = weekStarts.value.map((weekStart) => {
        const inWeek = entries.filter((entry) => isSameWeek(entry.date, weekStart))

        if (inWeek.length === 0) {
          return { date: weekStart, best: 0, average: 0 }
        }

        const weights = inWeek.map((entry) => entry.weightKg)
        return {
          date: weekStart,
          best: Math.max(...weights),
          average: Math.round(weights.reduce((sum, w) => sum + w, 0) / weights.length),
        }
      })

      return {
        key,
        label: labelFor(key),
        description: 'Heaviest and average logged load per week.',
        unit: 'kg',
        points,
        weeksLogged: points.filter((point) => point.best > 0).length,
      }
    })
    .filter((trend) => trend.weeksLogged >= 2)
    .sort((a, b) => b.weeksLogged - a.weeksLogged)
    .slice(0, 6)
    .map(({ weeksLogged: _weeksLogged, ...trend }) => trend)
})

const activeTrend = computed(() => trends.value[activeIndex.value] ?? null)
const series = computed<WorkoutPoint[]>(() => activeTrend.value?.points ?? [])
const chartWidth = computed(() => Math.max(width.value - 48, 240))

watch(trends, (next) => {
  if (activeIndex.value >= next.length) {
    activeIndex.value = 0
  }
})

const x = (_: WorkoutPoint, i: number) => i
const yBest = (d: WorkoutPoint) => d.best
const yAverage = (d: WorkoutPoint) => d.average

const nextExercise = () => {
  if (trends.value.length === 0) {
    return
  }
  transitionName.value = 'slide-left'
  activeIndex.value = (activeIndex.value + 1) % trends.value.length
}

const prevExercise = () => {
  if (trends.value.length === 0) {
    return
  }
  transitionName.value = 'slide-right'
  activeIndex.value = activeIndex.value === 0 ? trends.value.length - 1 : activeIndex.value - 1
}

const xTicks = (i: number) => {
  const point = series.value[i]
  if (!point) {
    return ''
  }

  const interval = Math.max(1, Math.floor(series.value.length / 6))
  if (i % interval !== 0 && i !== series.value.length - 1) {
    return ''
  }

  return format(point.date, 'MMM d')
}

const yTickFormat = (value: number) => `${Math.round(value)}`

const formatValue = (value: number) => `${Math.round(value)} kg`

const template = (d: WorkoutPoint) => {
  if (d.best === 0) {
    return `${format(d.date, 'MMM d, yyyy')}<br/>Nothing logged`
  }

  return `${format(d.date, 'MMM d, yyyy')}<br/>Best: ${formatValue(d.best)}<br/>Average: ${formatValue(d.average)}`
}
</script>

<template>
  <div ref="cardRef" class="rounded-2xl bg-muted p-6">
    <div class="mb-6 flex items-start justify-between gap-4">
      <div>
        <h3 class="text-lg font-semibold text-foreground">Workout Performance</h3>
        <p class="text-sm text-muted-foreground">
          {{ activeTrend?.description ?? 'Weekly load progression from your logged lifts.' }}
        </p>
      </div>

      <div v-if="activeTrend" class="flex items-center gap-4">
        <div class="flex items-center gap-3 text-xs text-muted-foreground">
          <span class="inline-flex items-center gap-1.5">
            <span class="h-2 w-2 rounded-full" :style="{ backgroundColor: BEST_COLOR }" />
            Best
          </span>
          <span class="inline-flex items-center gap-1.5">
            <span class="h-2 w-2 rounded-full" :style="{ backgroundColor: AVERAGE_COLOR }" />
            Average
          </span>
        </div>

        <div class="flex items-center gap-2">
          <span class="text-sm font-medium text-foreground">{{ activeTrend.label }}</span>
          <Button
            v-if="trends.length > 1"
            variant="ghost"
            size="icon"
            class="h-8 w-8"
            @click="prevExercise"
          >
            <ChevronLeft class="h-4 w-4" />
          </Button>
          <Button
            v-if="trends.length > 1"
            variant="ghost"
            size="icon"
            class="h-8 w-8"
            @click="nextExercise"
          >
            <ChevronRight class="h-4 w-4" />
          </Button>
        </div>
      </div>
    </div>

    <div class="relative h-80 overflow-hidden">
      <div
        v-if="isLoading && trends.length === 0"
        class="flex h-full items-center justify-center gap-2 text-muted-foreground"
      >
        <Loader2 class="h-4 w-4 animate-spin" />
        Loading workout performance…
      </div>

      <p v-else-if="error" class="flex h-full items-center justify-center text-muted-foreground">
        {{ error }}
      </p>

      <p
        v-else-if="!activeTrend"
        class="flex h-full items-center justify-center px-8 text-center text-muted-foreground"
      >
        Log the same lift with a weight in at least two different weeks to see its progression here.
      </p>

      <Transition v-else :name="transitionName" mode="out-in">
        <div :key="activeTrend.key" class="h-full">
          <VisXYContainer
            :data="series"
            :padding="{ top: 20, bottom: 10, left: 52, right: 12 }"
            class="h-full"
            :width="chartWidth"
          >
            <VisLine
              :x="x"
              :y="yAverage"
              :color="AVERAGE_COLOR"
              :curve-type="'linear'"
              :line-width="2"
            />

            <VisLine
              :x="x"
              :y="yBest"
              :color="BEST_COLOR"
              :curve-type="'linear'"
              :line-width="2.5"
            />

            <VisAxis
              type="y"
              :y="yBest"
              :tick-format="yTickFormat"
              :grid-line="true"
            />

            <VisAxis
              type="x"
              :x="x"
              :tick-format="xTicks"
              :grid-line="false"
            />

            <VisCrosshair :color="BEST_COLOR" :template="template" />

            <VisTooltip />
          </VisXYContainer>
        </div>
      </Transition>
    </div>
  </div>
</template>

<style scoped>
.slide-left-enter-active,
.slide-left-leave-active,
.slide-right-enter-active,
.slide-right-leave-active {
  transition: opacity 220ms ease, transform 220ms ease;
}

.slide-left-enter-from {
  opacity: 0;
  transform: translateX(24px);
}

.slide-left-leave-to {
  opacity: 0;
  transform: translateX(-24px);
}

.slide-right-enter-from {
  opacity: 0;
  transform: translateX(-24px);
}

.slide-right-leave-to {
  opacity: 0;
  transform: translateX(24px);
}
</style>
