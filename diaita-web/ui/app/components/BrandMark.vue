<script setup lang="ts">
import { cn } from "~/lib/utils"

const props = withDefaults(defineProps<{
  compact?: boolean
  inverse?: boolean
  class?: string
}>(), {
  compact: false,
  inverse: false,
  class: "",
})
</script>

<template>
  <span :class="cn('inline-flex items-center gap-2.5', props.class)">
    <span
      aria-hidden="true"
      :class="cn(
        'relative grid h-8 w-8 place-items-center border',
        inverse ? 'border-sidebar-border bg-sidebar-accent' : 'border-foreground/20 bg-foreground',
      )"
    >
      <span :class="cn('h-3.5 w-3.5 border-l-2 border-t-2', inverse ? 'border-sidebar-primary' : 'border-primary-foreground')" />
      <span :class="cn('absolute bottom-[7px] right-[7px] h-1.5 w-1.5', inverse ? 'bg-sidebar-primary' : 'bg-primary')" />
    </span>
    <span v-if="!compact" :class="cn('display-title text-[1.35rem] leading-none', inverse ? 'text-sidebar-foreground' : 'text-foreground')">
      Diaita
    </span>
  </span>
</template>
