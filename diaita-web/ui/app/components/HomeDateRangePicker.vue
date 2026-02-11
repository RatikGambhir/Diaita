<script setup lang="ts">
import type { Range } from '~/types'
import Button from '~/components/ui/button/Button.vue'
import { Calendar } from 'lucide-vue-next'

const props = defineProps<{
  modelValue: Range
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: Range): void
}>()

const formatDate = (date: Date) => {
  return date.toLocaleDateString('en-US', {
    month: 'short',
    day: 'numeric',
    year: 'numeric'
  })
}

const dateRangeText = computed(() => {
  return `${formatDate(props.modelValue.start)} - ${formatDate(props.modelValue.end)}`
})

const presets = [
  { label: 'Last 7 days', days: 7 },
  { label: 'Last 14 days', days: 14 },
  { label: 'Last 30 days', days: 30 },
  { label: 'Last 90 days', days: 90 },
]

const setPreset = (days: number) => {
  emit('update:modelValue', {
    start: new Date(Date.now() - days * 24 * 60 * 60 * 1000),
    end: new Date()
  })
}
</script>

<template>
  <div class="flex items-center gap-2">
    <Button variant="outline" size="sm" class="h-9">
      <Calendar class="h-4 w-4 mr-2" />
      {{ dateRangeText }}
    </Button>
    <div class="hidden md:flex items-center gap-1">
      <Button
        v-for="preset in presets"
        :key="preset.days"
        variant="ghost"
        size="sm"
        class="h-8 text-xs"
        @click="setPreset(preset.days)"
      >
        {{ preset.label }}
      </Button>
    </div>
  </div>
</template>
