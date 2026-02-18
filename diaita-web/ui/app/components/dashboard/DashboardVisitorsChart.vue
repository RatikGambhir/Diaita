<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { eachDayOfInterval, eachWeekOfInterval, eachMonthOfInterval, format, subDays, subWeeks, subMonths } from 'date-fns'
import { ChevronLeft, ChevronRight } from 'lucide-vue-next'
import { VisXYContainer, VisLine, VisAxis, VisCrosshair, VisTooltip } from '@unovis/vue'
import { useElementSize } from '@vueuse/core'
import Button from '~/components/ui/button/Button.vue'

type TimePeriod = 'daily' | 'weekly' | 'monthly'
type MetricKey = 'calories' | 'protein' | 'carbs' | 'fats'

type MetricPoint = {
  actual: number
  projected: number
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

const activeMetric = computed(() => metrics[activeMetricIndex.value])
const periodLabel = computed(() => periods.find(p => p.value === selectedPeriod.value)?.label.toLowerCase() || 'daily')
const chartWidth = computed(() => Math.max(width.value - 48, 240))

const x = (_: DataRecord, i: number) => i
const yActual = (d: DataRecord) => d[activeMetric.value.key].actual
const yProjected = (d: DataRecord) => d[activeMetric.value.key].projected

const nextMetric = () => {
  transitionName.value = 'slide-left'
  activeMetricIndex.value = (activeMetricIndex.value + 1) % metrics.length
}

const prevMetric = () => {
  transitionName.value = 'slide-right'
  activeMetricIndex.value = activeMetricIndex.value === 0 ? metrics.length - 1 : activeMetricIndex.value - 1
}

const generateData = () => {
  const now = new Date()
  let dates: Date[]

  switch (selectedPeriod.value) {
    case 'daily':
      dates = eachDayOfInterval({ start: subDays(now, 6), end: now })
      break
    case 'weekly':
      dates = eachWeekOfInterval({ start: subWeeks(now, 11), end: now })
      break
    case 'monthly':
    default:
      dates = eachMonthOfInterval({ start: subMonths(now, 11), end: now })
      break
  }

  data.value = dates.map((date, index) => {
    const drift = index / Math.max(dates.length - 1, 1)
    const noise = () => (Math.random() - 0.5) * 2

    const caloriesActual = 1750 + 220 * Math.sin(index * 0.65) + 180 * drift + noise() * 80
    const proteinActual = 118 + 10 * Math.sin(index * 0.55 + 1.2) + 18 * drift + noise() * 6
    const carbsActual = 210 + 24 * Math.sin(index * 0.4 + 0.4) + 16 * drift + noise() * 10
    const fatsActual = 62 + 8 * Math.sin(index * 0.48 + 2.1) + 10 * drift + noise() * 5

    const caloriesProjected = caloriesActual * (1.04 + 0.012 * Math.sin(index * 0.5 + 0.8))
    const proteinProjected = proteinActual * (1.03 + 0.01 * Math.sin(index * 0.45 + 0.4))
    const carbsProjected = carbsActual * (1.025 + 0.01 * Math.sin(index * 0.42 + 1.1))
    const fatsProjected = fatsActual * (1.02 + 0.008 * Math.sin(index * 0.38 + 1.6))

    return {
      date,
      calories: {
        actual: Math.max(1200, Math.round(caloriesActual)),
        projected: Math.max(1200, Math.round(caloriesProjected))
      },
      protein: {
        actual: Math.max(70, Math.round(proteinActual)),
        projected: Math.max(70, Math.round(proteinProjected))
      },
      carbs: {
        actual: Math.max(120, Math.round(carbsActual)),
        projected: Math.max(120, Math.round(carbsProjected))
      },
      fats: {
        actual: Math.max(35, Math.round(fatsActual)),
        projected: Math.max(35, Math.round(fatsProjected))
      }
    }
  })
}

watch(selectedPeriod, generateData, { immediate: true })

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
  return `${format(d.date, 'MMM d, yyyy')}<br/>Actual: ${formatMetricValue(point.actual)}<br/>Projected: ${formatMetricValue(point.projected)}`
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
              Actual
            </span>
            <span class="inline-flex items-center gap-1.5">
              <span class="h-2 w-2 rounded-full" :style="{ backgroundColor: PROJECTED_COLOR }" />
              Projected
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
      <Transition :name="transitionName" mode="out-in">
        <div :key="activeMetric.key" class="h-full">
          <VisXYContainer
            :data="data"
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
