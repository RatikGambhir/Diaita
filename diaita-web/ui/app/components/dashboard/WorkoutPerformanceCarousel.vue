<script setup lang="ts">
import { computed, ref } from 'vue'
import { eachWeekOfInterval, format, subWeeks } from 'date-fns'
import { ChevronRight } from 'lucide-vue-next'
import { VisXYContainer, VisLine, VisAxis, VisCrosshair, VisTooltip } from '@unovis/vue'
import { useElementSize } from '@vueuse/core'
import Button from '~/components/ui/button/Button.vue'

type WorkoutMetric = {
  key: 'bench' | 'squat' | 'deadlift' | 'pace'
  label: string
  description: string
  unit: string
  values: number[]
}

type WorkoutPoint = {
  date: Date
  actual: number
  projected: number
}

const ACTUAL_COLOR = 'oklch(0.7489 0.1486 158.6624)'
const PROJECTED_COLOR = 'oklch(0.7156 0.0605 248.6845)'

const cardRef = ref<HTMLElement | null>(null)
const { width } = useElementSize(cardRef)

const transitionName = ref<'slide-left'>('slide-left')
const activeWorkoutIndex = ref(0)

const dates = eachWeekOfInterval({
  start: subWeeks(new Date(), 11),
  end: new Date()
})

const workoutMetrics: WorkoutMetric[] = [
  {
    key: 'bench',
    label: 'Bench Press',
    description: 'Estimated 1RM progression over recent sessions.',
    unit: 'kg',
    values: [74, 75, 76, 78, 79, 80, 81, 83, 84, 86, 87, 89]
  },
  {
    key: 'squat',
    label: 'Squat',
    description: 'Lower-body strength trend over 12 weeks.',
    unit: 'kg',
    values: [108, 110, 112, 114, 117, 119, 121, 124, 126, 129, 132, 135]
  },
  {
    key: 'deadlift',
    label: 'Deadlift',
    description: 'Posterior-chain strength trend over 12 weeks.',
    unit: 'kg',
    values: [130, 132, 134, 137, 139, 142, 144, 147, 150, 153, 156, 160]
  },
  {
    key: 'pace',
    label: 'Running Pace',
    description: 'Average pace improving over recent runs.',
    unit: 'min/km',
    values: [430, 426, 421, 418, 414, 409, 405, 401, 397, 394, 390, 386]
  }
]

const chartWidth = computed(() => Math.max(width.value - 48, 240))
const activeWorkout = computed(() => workoutMetrics[activeWorkoutIndex.value])

const series = computed<WorkoutPoint[]>(() => {
  return dates.map((date, index) => {
    const actual = activeWorkout.value.values[index] ?? activeWorkout.value.values[activeWorkout.value.values.length - 1]

    const projectedMultiplier = activeWorkout.value.key === 'pace'
      ? 0.97 - 0.004 * Math.sin(index * 0.5 + 0.8)
      : 1.03 + 0.01 * Math.sin(index * 0.45 + 0.3)

    return {
      date,
      actual: Math.round(actual),
      projected: Math.round(actual * projectedMultiplier)
    }
  })
})

const x = (_: WorkoutPoint, i: number) => i
const yActual = (d: WorkoutPoint) => d.actual
const yProjected = (d: WorkoutPoint) => d.projected

const nextWorkout = () => {
  transitionName.value = 'slide-left'
  activeWorkoutIndex.value = (activeWorkoutIndex.value + 1) % workoutMetrics.length
}

const xTicks = (i: number) => {
  if (!series.value[i]) return ''

  const interval = Math.max(1, Math.floor(series.value.length / 6))
  if (i % interval !== 0 && i !== series.value.length - 1) return ''

  return format(series.value[i].date, 'MMM d')
}

const formatPace = (seconds: number) => {
  const mins = Math.floor(seconds / 60)
  const secs = Math.round(seconds % 60)
  return `${mins}:${secs.toString().padStart(2, '0')}`
}

const yTickFormat = (value: number) => {
  if (activeWorkout.value.key === 'pace') {
    return formatPace(value)
  }

  return `${Math.round(value)}`
}

const formatValue = (value: number) => {
  if (activeWorkout.value.key === 'pace') {
    return `${formatPace(value)} ${activeWorkout.value.unit}`
  }

  return `${Math.round(value)} ${activeWorkout.value.unit}`
}

const template = (d: WorkoutPoint) => {
  return `${format(d.date, 'MMM d, yyyy')}<br/>Actual: ${formatValue(d.actual)}<br/>Projected: ${formatValue(d.projected)}`
}
</script>

<template>
  <div ref="cardRef" class="rounded-2xl bg-muted p-6">
    <div class="mb-6 flex items-start justify-between gap-4">
      <div>
        <h3 class="text-lg font-semibold text-foreground">Workout Performance</h3>
        <p class="text-sm text-muted-foreground">{{ activeWorkout.description }}</p>
      </div>

      <div class="flex items-center gap-4">
        <div class="flex items-center gap-3 text-xs text-muted-foreground">
          <span class="inline-flex items-center gap-1.5">
            <span class="h-2 w-2 rounded-full" :style="{ backgroundColor: ACTUAL_COLOR }" />
            Actual
          </span>
          <span class="inline-flex items-center gap-1.5">
            <span class="h-2 w-2 rounded-full" :style="{ backgroundColor: PROJECTED_COLOR }" />
            Projected
          </span>
        </div>

        <div class="flex items-center gap-3">
          <span class="text-sm font-medium text-foreground">{{ activeWorkout.label }}</span>
          <Button
            variant="ghost"
            size="icon"
            class="h-8 w-8"
            @click="nextWorkout"
          >
            <ChevronRight class="h-4 w-4" />
          </Button>
        </div>
      </div>
    </div>

    <div class="relative h-80 overflow-hidden">
      <Transition :name="transitionName" mode="out-in">
        <div :key="activeWorkout.key" class="h-full">
          <VisXYContainer
            :data="series"
            :padding="{ top: 20, bottom: 10, left: 52, right: 12 }"
            class="h-full"
            :width="chartWidth"
          >
            <VisLine
              :x="x"
              :y="yProjected"
              :color="PROJECTED_COLOR"
              :curve-type="'linear'"
              :line-width="2"
            />

            <VisLine
              :x="x"
              :y="yActual"
              :color="ACTUAL_COLOR"
              :curve-type="'linear'"
              :line-width="2.5"
            />

            <VisAxis
              type="y"
              :y="yActual"
              :tick-format="yTickFormat"
              :grid-line="true"
            />

            <VisAxis
              type="x"
              :x="x"
              :tick-format="xTicks"
              :grid-line="false"
            />

            <VisCrosshair
              :color="ACTUAL_COLOR"
              :template="template"
            />

            <VisTooltip />
          </VisXYContainer>
        </div>
      </Transition>
    </div>
  </div>
</template>

<style scoped>
.unovis-xy-container {
  --vis-crosshair-line-stroke-color: var(--foreground);
  --vis-crosshair-circle-stroke-color: var(--background);

  --vis-axis-grid-color: var(--border);
  --vis-axis-tick-color: transparent;
  --vis-axis-tick-label-color: var(--muted-foreground);

  --vis-tooltip-background-color: var(--background);
  --vis-tooltip-border-color: var(--border);
  --vis-tooltip-text-color: var(--foreground);
}

.slide-left-enter-active,
.slide-left-leave-active {
  transition: transform 260ms cubic-bezier(0.22, 1, 0.36, 1), opacity 220ms ease;
}

.slide-left-enter-from {
  opacity: 0;
  transform: translateX(28px);
}

.slide-left-leave-to {
  opacity: 0;
  transform: translateX(-28px);
}

.slide-left-enter-to,
.slide-left-leave-from {
  opacity: 1;
  transform: translateX(0);
}
</style>
