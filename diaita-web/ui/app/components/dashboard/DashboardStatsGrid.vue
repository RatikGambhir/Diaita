<script setup lang="ts">
import DashboardStatCard from './DashboardStatCard.vue'

const stats = [
  {
    title: 'Calories Today',
    value: '1,847',
    change: 8.2,
    trend: 'On track for daily goal',
    description: '153 remaining of 2,000 goal'
  },
  {
    title: 'Protein',
    value: '124g',
    change: 15.3,
    trend: 'Above target this week',
    description: 'Goal: 120g daily'
  },
  {
    title: 'Carbohydrates',
    value: '186g',
    change: -5.2,
    trend: 'Slightly under target',
    description: 'Goal: 200g daily'
  },
  {
    title: 'Fats',
    value: '52g',
    change: 2.1,
    trend: 'Within healthy range',
    description: 'Goal: 55g daily'
  }
]
</script>

<template>
  <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
    <DashboardStatCard
      v-for="stat in stats"
      :key="stat.title"
      :title="stat.title"
      :value="stat.value"
      :change="stat.change"
      :trend="stat.trend"
      :description="stat.description"
    />
  </div>
</template>
