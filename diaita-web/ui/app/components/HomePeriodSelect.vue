<script setup lang="ts">
import type { Period, Range } from '~/types'
import Button from '~/components/ui/button/Button.vue'

const props = defineProps<{
  modelValue: Period
  range: Range
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: Period): void
}>()

const periods: { value: Period; label: string }[] = [
  { value: 'daily', label: 'Daily' },
  { value: 'weekly', label: 'Weekly' },
  { value: 'monthly', label: 'Monthly' },
]
</script>

<template>
  <div class="flex items-center gap-1 bg-muted p-1 rounded-lg">
    <Button
      v-for="period in periods"
      :key="period.value"
      :variant="modelValue === period.value ? 'default' : 'ghost'"
      size="sm"
      class="h-7 text-xs"
      @click="emit('update:modelValue', period.value)"
    >
      {{ period.label }}
    </Button>
  </div>
</template>
