<script setup lang="ts">
import { TrendingUp, TrendingDown } from 'lucide-vue-next'

interface Props {
  title: string
  value: string
  change: number
  trend: string
  description: string
}

defineProps<Props>()

const isPositive = (change: number) => change >= 0
</script>

<template>
  <div class="bg-muted rounded-2xl p-5 relative">
    <div class="flex items-start justify-between mb-3">
      <span class="text-sm text-muted-foreground">{{ title }}</span>
      <span
        class="inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-xs font-medium bg-background border"
      >
        <component
          :is="isPositive(change) ? TrendingUp : TrendingDown"
          class="h-3 w-3"
        />
        {{ isPositive(change) ? '+' : '' }}{{ change }}%
      </span>
    </div>

    <div class="text-4xl font-bold text-foreground mb-4">
      {{ value }}
    </div>

    <div class="flex items-center gap-2 mb-1">
      <span class="text-sm font-medium text-foreground">{{ trend }}</span>
      <component
        :is="isPositive(change) ? TrendingUp : TrendingDown"
        class="h-4 w-4 text-muted-foreground"
      />
    </div>

    <p class="text-sm text-muted-foreground">{{ description }}</p>
  </div>
</template>
