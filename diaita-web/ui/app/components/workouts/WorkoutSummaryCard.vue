<script setup lang="ts">
import { computed } from 'vue'
import { TrendingUp, Flame, Award } from 'lucide-vue-next'
import Card from '~/components/ui/card/Card.vue'
import CardContent from '~/components/ui/card/CardContent.vue'

interface Props {
  progress?: string
  intensity?: string
  achievement?: string
}

const props = defineProps<Props>()

const items = computed(() => [
  { icon: TrendingUp, label: 'Progress', value: props.progress || '—' },
  { icon: Flame, label: 'Intensity', value: props.intensity || '—' },
  { icon: Award, label: 'Achievement', value: props.achievement || '—' },
])
</script>

<template>
  <Card>
    <CardContent class="p-6">
      <div class="grid grid-cols-1 gap-6 md:grid-cols-3">
        <div v-for="item in items" :key="item.label" class="flex items-start gap-3">
          <div class="shrink-0 rounded-lg bg-primary/10 p-3">
            <component :is="item.icon" class="h-5 w-5 text-primary" />
          </div>
          <div>
            <p class="text-sm text-muted-foreground">{{ item.label }}</p>
            <p class="mt-1 text-foreground">{{ item.value }}</p>
          </div>
        </div>
      </div>
    </CardContent>
  </Card>
</template>
