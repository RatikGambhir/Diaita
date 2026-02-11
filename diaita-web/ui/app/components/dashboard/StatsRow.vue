<script setup lang="ts">
import { Flame, Footprints, Moon } from 'lucide-vue-next'
import StatCard from './StatCard.vue'

interface StatData {
  label: string
  value: string | number
  unit: string
}

interface Props {
  caloriesBurned?: StatData
  stepsTaken?: StatData
  hoursSlept?: StatData
}

const props = withDefaults(defineProps<Props>(), {
  caloriesBurned: () => ({ label: 'Calories Burned', value: '1,247', unit: 'kcal' }),
  stepsTaken: () => ({ label: 'Steps Taken', value: '8,432', unit: 'steps' }),
  hoursSlept: () => ({ label: 'Hours Slept', value: '7.5', unit: 'hrs' })
})
</script>

<template>
  <div class="grid grid-cols-1 md:grid-cols-3 gap-5 md:gap-6">
    <StatCard
      :label="caloriesBurned.label"
      :value="caloriesBurned.value"
      :unit="caloriesBurned.unit"
      :icon="Flame"
      icon-color="text-orange-500"
      blob-color="bg-orange-200"
    />
    <StatCard
      :label="stepsTaken.label"
      :value="stepsTaken.value"
      :unit="stepsTaken.unit"
      :icon="Footprints"
      icon-color="text-blue-500"
      blob-color="bg-blue-200"
    />
    <StatCard
      :label="hoursSlept.label"
      :value="hoursSlept.value"
      :unit="hoursSlept.unit"
      :icon="Moon"
      icon-color="text-indigo-500"
      blob-color="bg-pink-200"
    />
  </div>
</template>
