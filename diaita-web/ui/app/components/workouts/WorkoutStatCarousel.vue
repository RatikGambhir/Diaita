<script setup lang="ts">
import type { Component } from 'vue'

interface StatCard {
  icon: Component
  label: string
  value: string
  change: string
}

defineProps<{
  stats: StatCard[]
}>()
</script>

<template>
  <div class="grid grid-cols-1 gap-4 sm:grid-cols-2 xl:grid-cols-4">
    <div
      v-for="(stat, i) in stats"
      :key="i"
      class="h-full rounded-xl bg-card p-4 shadow-sm"
    >
      <div class="mb-3 flex items-center gap-3">
        <div class="rounded-lg bg-primary/10 p-2">
          <component :is="stat.icon" class="h-4 w-4 text-primary" />
        </div>
        <span class="text-sm text-muted-foreground">{{ stat.label }}</span>
      </div>
      <p class="text-2xl text-foreground">{{ stat.value }}</p>
      <p class="text-sm text-primary">{{ stat.change }}</p>
    </div>
  </div>
</template>
