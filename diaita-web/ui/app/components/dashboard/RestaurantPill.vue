<script setup lang="ts">
import { UtensilsCrossed } from 'lucide-vue-next'
import type { HTMLAttributes } from 'vue'
import { cn } from '~/lib/utils'

interface Props {
  name: string
  distance: string
  class?: HTMLAttributes['class']
}

const props = defineProps<Props>()
</script>

<template>
  <div
    :class="cn(
      'flex items-center gap-3 bg-slate-50 dark:bg-slate-800/50 rounded-2xl px-4 py-3.5 transition-colors hover:bg-slate-100 dark:hover:bg-slate-800 cursor-pointer',
      props.class
    )"
  >
    <UtensilsCrossed class="w-5 h-5 text-emerald-500 shrink-0" />
    <div class="min-w-0 flex-1">
      <p class="text-sm font-medium text-foreground truncate">{{ name }}</p>
      <p class="text-xs text-muted-foreground">{{ distance }}</p>
    </div>
  </div>
</template>
