<script setup lang="ts">
import { computed, ref, watch } from "vue"
import { ChevronLeft, ChevronRight } from "lucide-vue-next"
import { cn } from "~/lib/utils"

const props = defineProps<{
  modelValue?: Date
}>()

const emit = defineEmits<{
  "update:modelValue": [value: Date]
  "select": [value: Date]
}>()

const WEEKDAYS = ["Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"]
const MONTHS = [
  "January", "February", "March", "April", "May", "June",
  "July", "August", "September", "October", "November", "December",
]

const currentMonth = ref(props.modelValue ? props.modelValue.getMonth() : new Date().getMonth())
const currentYear = ref(props.modelValue ? props.modelValue.getFullYear() : new Date().getFullYear())
const slideDirection = ref<"left" | "right">("left")
const isTransitioning = ref(false)
const animatingDay = ref<number | null>(null)

watch(() => props.modelValue, (val) => {
  if (val) {
    currentMonth.value = val.getMonth()
    currentYear.value = val.getFullYear()
  }
})

const monthYearLabel = computed(() => {
  return `${MONTHS[currentMonth.value]} ${currentYear.value}`
})

const daysInMonth = computed(() => {
  return new Date(currentYear.value, currentMonth.value + 1, 0).getDate()
})

const firstDayOfMonth = computed(() => {
  return new Date(currentYear.value, currentMonth.value, 1).getDay()
})

const calendarDays = computed(() => {
  const days: (number | null)[] = []
  for (let i = 0; i < firstDayOfMonth.value; i++) {
    days.push(null)
  }
  for (let i = 1; i <= daysInMonth.value; i++) {
    days.push(i)
  }
  return days
})

const today = new Date()

function isToday(day: number): boolean {
  return (
    day === today.getDate() &&
    currentMonth.value === today.getMonth() &&
    currentYear.value === today.getFullYear()
  )
}

function isSelected(day: number): boolean {
  if (!props.modelValue) return false
  return (
    day === props.modelValue.getDate() &&
    currentMonth.value === props.modelValue.getMonth() &&
    currentYear.value === props.modelValue.getFullYear()
  )
}

function selectDate(day: number) {
  animatingDay.value = day
  setTimeout(() => {
    animatingDay.value = null
  }, 300)
  const date = new Date(currentYear.value, currentMonth.value, day)
  emit("update:modelValue", date)
  emit("select", date)
}

function prevMonth() {
  slideDirection.value = "right"
  isTransitioning.value = true
  setTimeout(() => {
    if (currentMonth.value === 0) {
      currentMonth.value = 11
      currentYear.value--
    } else {
      currentMonth.value--
    }
    setTimeout(() => {
      isTransitioning.value = false
    }, 50)
  }, 150)
}

function nextMonth() {
  slideDirection.value = "left"
  isTransitioning.value = true
  setTimeout(() => {
    if (currentMonth.value === 11) {
      currentMonth.value = 0
      currentYear.value++
    } else {
      currentMonth.value++
    }
    setTimeout(() => {
      isTransitioning.value = false
    }, 50)
  }, 150)
}
</script>

<template>
  <div class="p-3 select-none">
    <div class="flex items-center justify-between mb-4">
      <button
        type="button"
        class="inline-flex h-7 w-7 items-center justify-center rounded-md hover:bg-accent hover:text-accent-foreground transition-colors"
        @click="prevMonth"
      >
        <ChevronLeft class="h-4 w-4" />
      </button>
      <span class="text-sm font-medium">{{ monthYearLabel }}</span>
      <button
        type="button"
        class="inline-flex h-7 w-7 items-center justify-center rounded-md hover:bg-accent hover:text-accent-foreground transition-colors"
        @click="nextMonth"
      >
        <ChevronRight class="h-4 w-4" />
      </button>
    </div>

    <!-- Weekday headers -->
    <div class="grid grid-cols-7 mb-1">
      <div
        v-for="day in WEEKDAYS"
        :key="day"
        class="flex h-8 w-9 items-center justify-center text-xs font-medium text-muted-foreground"
      >
        {{ day }}
      </div>
    </div>

    <!-- Day grid -->
    <div
      :class="cn(
        'grid grid-cols-7 transition-all duration-200',
        isTransitioning ? 'opacity-0 scale-95' : 'opacity-100 scale-100',
      )"
    >
      <div v-for="(day, index) in calendarDays" :key="`${currentMonth}-${currentYear}-${index}`" class="flex items-center justify-center">
        <button
          v-if="day"
          type="button"
          :class="cn(
            'relative h-9 w-9 rounded-md text-sm transition-all duration-200 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring',
            isSelected(day)
              ? 'bg-orange-500 text-white font-semibold shadow-sm'
              : 'hover:bg-accent hover:text-accent-foreground',
            isToday(day) && !isSelected(day) ? 'font-bold text-orange-500' : '',
            animatingDay === day ? 'scale-125' : 'scale-100',
          )"
          @click="selectDate(day)"
        >
          {{ day }}
          <span
            v-if="isToday(day) && !isSelected(day)"
            class="absolute bottom-1 left-1/2 h-1 w-1 -translate-x-1/2 rounded-full bg-orange-500"
          />
        </button>
      </div>
    </div>
  </div>
</template>
