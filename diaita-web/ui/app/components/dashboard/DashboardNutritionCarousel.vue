<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import {
  eachDayOfInterval,
  eachWeekOfInterval,
  eachMonthOfInterval,
  format,
  isSameDay,
  isSameMonth,
  isSameWeek,
  parseISO,
  subDays,
  subWeeks,
  subMonths
} from 'date-fns'
import { ChevronLeft, ChevronRight, Loader2 } from 'lucide-vue-next'
import { useNutritionSeries } from '~/composables/useNutritionSeries'
import { VisXYContainer, VisLine, VisAxis, VisCrosshair, VisTooltip } from '@unovis/vue'
import { useElementSize } from '@vueuse/core'
import Button from '~/components/ui/button/Button.vue'

type TimePeriod = 'daily' | 'weekly' | 'monthly'
type MetricKey = 'calories' | 'protein' | 'carbs' | 'fats'

type MetricPoint = {
  actual: number
  target: number
}

type DataRecord = {
  date: Date
  calories: MetricPoint
  protein: MetricPoint
  carbs: MetricPoint
  fats: MetricPoint
}

type MetricConfig = {
  key: MetricKey
  label: string
  unit: string
  subtitle: string
}

const ACTUAL_COLOR = 'oklch(0.7489 0.1486 158.6624)'
const PROJECTED_COLOR = 'oklch(0.7156 0.0605 248.6845)'

const cardRef = ref<HTMLElement | null>(null)
const { width } = useElementSize(cardRef)

const selectedPeriod = ref<TimePeriod>('daily')
const activeMetricIndex = ref(0)
const transitionName = ref<'slide-left' | 'slide-right'>('slide-left')

const periods = [
  { value: 'daily' as TimePeriod, label: 'Daily' },
  { value: 'weekly' as TimePeriod, label: 'Weekly' },
  { value: 'monthly' as TimePeriod, label: 'Monthly' }
]

const metrics: MetricConfig[] = [
  {
    key: 'calories',
    label: 'Calories',
    unit: 'kcal',
    subtitle: 'Energy intake over time'
  },
  {
    key: 'protein',
    label: 'Protein',
    unit: 'g',
    subtitle: 'Daily protein consistency'
  },
  {
    key: 'carbs',
    label: 'Carbs',
    unit: 'g',
    subtitle: 'Carbohydrate fueling trend'
  },
  {
    key: 'fats',
    label: 'Fats',
    unit: 'g',
    subtitle: 'Fat intake balance'
  }
]

const data = ref<DataRecord[]>([])
const { series, isLoading, error, loadSeries } = useNutritionSeries()

const activeMetric = computed(() => metrics[activeMetricIndex.value] ?? metrics[0]!)
const periodLabel = computed(() => periods.find(p => p.value === selectedPeriod.value)?.label.toLowerCase() || 'daily')
const chartWidth = computed(() => Math.max(width.value - 48, 240))

const x = (_: DataRecord, i: number) => i
const yActual = (d: DataRecord) => d[activeMetric.value.key].actual
const yTarget = (d: DataRecord) => d[activeMetric.value.key].target

const nextMetric = () => {
  transitionName.value = 'slide-left'
  activeMetricIndex.value = (activeMetricIndex.value + 1) % metrics.length
}

const prevMetric = () => {
  transitionName.value = 'slide-right'
  activeMetricIndex.value = activeMetricIndex.value === 0 ? metrics.length - 1 : activeMetricIndex.value - 1
}

/** How far back each period reaches, and how its points are bucketed. */
const PERIOD_WINDOW_DAYS: Record<TimePeriod, number> = {
  daily: 7,
  weekly: 84,
  monthly: 365
}

const bucketDates = (now: Date): Date[] => {
  switch (selectedPeriod.value) {
    case 'daily':
      return eachDayOfInterval({ start: subDays(now, PERIOD_WINDOW_DAYS.daily - 1), end: now })
    case 'weekly':
      return eachWeekOfInterval({ start: subWeeks(now, 11), end: now })
    case 'monthly':
    default:
      return eachMonthOfInterval({ start: subMonths(now, 11), end: now })
  }
}

const isInBucket = (day: Date, bucket: Date) => {
  switch (selectedPeriod.value) {
    case 'daily':
      return isSameDay(day, bucket)
    case 'weekly':
      return isSameWeek(day, bucket)
    case 'monthly':
    default:
      return isSameMonth(day, bucket)
  }
}

/**
 * Buckets the per-day series into the selected period. Weekly and monthly points are daily averages
 * rather than sums, so they stay comparable to the daily target line.
 */
const buildSeries = () => {
  const loaded = series.value
  if (!loaded) {
    data.value = []
    return
  }

  const targets = loaded.analytics
  const days = loaded.days.map((day) => ({ ...day, parsed: parseISO(day.date) }))

  data.value = bucketDates(new Date()).map((bucket) => {
    const inBucket = days.filter((day) => isInBucket(day.parsed, bucket))
    const logged = inBucket.filter((day) => day.totalCal > 0)
    const divisor = Math.max(logged.length, 1)

    const average = (total: number) => Math.round(total / divisor)

    return {
      date: bucket,
      calories: {
        actual: average(logged.reduce((sum, day) => sum + day.totalCal, 0)),
        target: Math.round(targets.recCal ?? 0)
      },
      protein: {
        actual: average(logged.reduce((sum, day) => sum + day.totalProtein, 0)),
        target: Math.round(targets.recProtein ?? 0)
      },
      carbs: {
        actual: average(logged.reduce((sum, day) => sum + day.totalCarb, 0)),
        target: Math.round(targets.recCarb ?? 0)
      },
      fats: {
        actual: average(logged.reduce((sum, day) => sum + day.totalFat, 0)),
        target: Math.round(targets.recFat ?? 0)
      }
    }
  })
}

const reload = async () => {
  const now = new Date()
  await loadSeries(subDays(now, PERIOD_WINDOW_DAYS[selectedPeriod.value] - 1), now)
  buildSeries()
}

const hasTargets = computed(() => data.value.some((point) => point[activeMetric.value.key].target > 0))
const hasData = computed(() => data.value.some((point) => point[activeMetric.value.key].actual > 0))

onMounted(() => {
  void reload()
})

watch(selectedPeriod, () => {
  void reload()
})

const formatXAxisDate = (date: Date) => {
  switch (selectedPeriod.value) {
    case 'daily':
      return format(date, 'EEE')
    case 'weekly':
      return format(date, 'MMM d')
    case 'monthly':
    default:
      return format(date, 'MMM')
  }
}

const xTicks = (i: number) => {
  if (!data.value[i]) return ''

  const total = data.value.length
  const interval = Math.max(1, Math.floor(total / 6))

  if (i % interval !== 0 && i !== total - 1) return ''
  return formatXAxisDate(data.value[i].date)
}

const formatMetricValue = (value: number) => {
  const rounded = Math.round(value)

  if (activeMetric.value.key === 'calories') {
    return `${rounded.toLocaleString()} ${activeMetric.value.unit}`
  }

  return `${rounded} ${activeMetric.value.unit}`
}

const yTickFormat = (value: number) => {
  if (activeMetric.value.key === 'calories') {
    const compact = new Intl.NumberFormat('en-US', {
      notation: 'compact',
      maximumFractionDigits: 1
    })

    return compact.format(value)
  }

  return `${Math.round(value)}`
}

const template = (d: DataRecord) => {
  const point = d[activeMetric.value.key]
  const target = point.target > 0
    ? `<br/>Target: ${formatMetricValue(point.target)}`
    : ''
  return `${format(d.date, 'MMM d, yyyy')}<br/>Logged: ${formatMetricValue(point.actual)}${target}`
}
</script>

<template>
  <div ref="cardRef" class="bg-muted rounded-2xl p-6">
    <div class="mb-6 flex flex-col gap-4 lg:flex-row lg:items-start lg:justify-between">
      <div>
        <h3 class="text-lg font-semibold text-foreground">Calorie Tracking</h3>
        <p class="text-sm text-muted-foreground">{{ periodLabel }} overview · {{ activeMetric.subtitle }}</p>
      </div>

      <div class="flex flex-col gap-3 sm:items-end">
        <div class="flex items-center gap-1 rounded-lg border bg-background p-1">
          <button
            v-for="period in periods"
            :key="period.value"
            class="rounded-md px-3 py-1.5 text-sm font-medium transition-colors"
            :class="[
              selectedPeriod === period.value
                ? 'bg-muted text-foreground'
                : 'text-muted-foreground hover:text-foreground'
            ]"
            @click="selectedPeriod = period.value"
          >
            {{ period.label }}
          </button>
        </div>

        <div class="flex items-center gap-4">
          <div class="flex items-center gap-3 text-xs text-muted-foreground">
            <span class="inline-flex items-center gap-1.5">
              <span class="h-2 w-2 rounded-full" :style="{ backgroundColor: ACTUAL_COLOR }" />
              Logged
            </span>
            <span v-if="hasTargets" class="inline-flex items-center gap-1.5">
              <span class="h-2 w-2 rounded-full" :style="{ backgroundColor: PROJECTED_COLOR }" />
              Target
            </span>
          </div>

          <div class="flex items-center gap-2">
            <span class="text-sm font-medium text-foreground">{{ activeMetric.label }}</span>
            <Button
              variant="ghost"
              size="icon"
              class="h-8 w-8"
              @click="prevMetric"
            >
              <ChevronLeft class="h-4 w-4" />
            </Button>
            <Button
              variant="ghost"
              size="icon"
              class="h-8 w-8"
              @click="nextMetric"
            >
              <ChevronRight class="h-4 w-4" />
            </Button>
          </div>
        </div>
      </div>
    </div>

    <div class="relative h-80 overflow-hidden">
      <div
        v-if="isLoading && data.length === 0"
        class="flex h-full items-center justify-center gap-2 text-muted-foreground"
      >
        <Loader2 class="h-4 w-4 animate-spin" />
        Loading nutrition history…
      </div>

      <p v-else-if="error" class="flex h-full items-center justify-center text-muted-foreground">
        {{ error }}
      </p>

      <p v-else-if="!hasData" class="flex h-full items-center justify-center text-muted-foreground">
        Log meals to see your {{ activeMetric.label.toLowerCase() }} trend.
      </p>

      <Transition v-else :name="transitionName" mode="out-in">
        <div :key="activeMetric.key" class="h-full">
          <VisXYContainer
            :data="data"
            :padding="{ top: 20, bottom: 10, left: 52, right: 12 }"
            class="h-full"
            :width="chartWidth"
          >
            <VisLine
              v-if="hasTargets"
              :x="x"
              :y="yTarget"
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
.slide-left-leave-active,
.slide-right-enter-active,
.slide-right-leave-active {
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

.slide-right-enter-from {
  opacity: 0;
  transform: translateX(-28px);
}

.slide-right-leave-to {
  opacity: 0;
  transform: translateX(28px);
}

.slide-left-enter-to,
.slide-left-leave-from,
.slide-right-enter-to,
.slide-right-leave-from {
  opacity: 1;
  transform: translateX(0);
}
</style>
