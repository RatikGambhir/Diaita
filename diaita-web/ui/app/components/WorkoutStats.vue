<script setup lang="ts">
import type { Period, Range, Stat } from '~/types'
import { randomInt } from '~/utils'
import Card from '~/components/ui/card/Card.vue'
import CardContent from '~/components/ui/card/CardContent.vue'
import Badge from '~/components/ui/badge/Badge.vue'
import { Users, PieChart, CircleDollarSign, ShoppingCart } from 'lucide-vue-next'

const props = defineProps<{
  period: Period
  range: Range
}>()

function formatCurrency(value: number): string {
  return value.toLocaleString('en-US', {
    style: 'currency',
    currency: 'USD',
    maximumFractionDigits: 0
  })
}

const iconMap: Record<string, any> = {
  'i-lucide-users': Users,
  'i-lucide-chart-pie': PieChart,
  'i-lucide-circle-dollar-sign': CircleDollarSign,
  'i-lucide-shopping-cart': ShoppingCart
}

const baseStats = [{
  title: 'Calories',
  icon: 'i-lucide-users',
  minValue: 400,
  maxValue: 1000,
  minVariation: -15,
  maxVariation: 25
}, {
  title: 'Protein',
  icon: 'i-lucide-chart-pie',
  minValue: 1000,
  maxValue: 2000,
  minVariation: -10,
  maxVariation: 20
}, {
  title: 'Fat',
  icon: 'i-lucide-circle-dollar-sign',
  minValue: 200000,
  maxValue: 500000,
  minVariation: -20,
  maxVariation: 30,
  formatter: formatCurrency
}, {
  title: 'Carbs',
  icon: 'i-lucide-shopping-cart',
  minValue: 100,
  maxValue: 300,
  minVariation: -5,
  maxVariation: 15
}]

const { data: stats } = await useAsyncData<Stat[]>('stats', async () => {
  return baseStats.map((stat) => {
    const value = randomInt(stat.minValue, stat.maxValue)
    const variation = randomInt(stat.minVariation, stat.maxVariation)

    return {
      title: stat.title,
      icon: stat.icon,
      value: stat.formatter ? stat.formatter(value) : value,
      variation
    }
  })
}, {
  watch: [() => props.period, () => props.range],
  default: () => []
})
</script>

<template>
  <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
    <Card
      v-for="(stat, index) in stats"
      :key="index"
      class="hover:shadow-md transition-shadow"
    >
      <CardContent class="p-4">
        <div class="flex items-start gap-3">
          <div class="p-2.5 rounded-full bg-primary/10 ring ring-inset ring-primary/25">
            <component :is="iconMap[stat.icon] || Users" class="h-5 w-5 text-primary" />
          </div>
          <div class="flex-1">
            <p class="text-xs uppercase text-muted-foreground mb-1">{{ stat.title }}</p>
            <div class="flex items-center gap-2">
              <span class="text-2xl font-semibold">{{ stat.value }}</span>
              <Badge
                :variant="stat.variation > 0 ? 'default' : 'destructive'"
                class="text-xs"
              >
                {{ stat.variation > 0 ? '+' : '' }}{{ stat.variation }}%
              </Badge>
            </div>
          </div>
        </div>
      </CardContent>
    </Card>
  </div>
</template>