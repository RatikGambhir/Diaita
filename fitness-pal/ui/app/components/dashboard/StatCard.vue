<script setup lang="ts">
import type { Component, HTMLAttributes } from 'vue'
import { cn } from '~/lib/utils'

interface Props {
  label: string
  value: string | number
  unit: string
  icon: Component
  iconColor?: string
  blobColor?: string
  class?: HTMLAttributes['class']
}

const props = withDefaults(defineProps<Props>(), {
  iconColor: 'text-primary',
  blobColor: 'bg-primary/10'
})
</script>

<template>
  <div
    :class="cn(
      'relative bg-card rounded-2xl p-5 md:p-6 shadow-sm overflow-hidden transition-all duration-200 ease-out hover:shadow-md motion-reduce:hover:shadow-sm min-h-[120px] flex flex-col justify-between',
      props.class
    )"
  >
    <div
      :class="cn(
        'absolute -top-6 -right-6 w-24 h-24 rounded-full opacity-60 blur-xl',
        props.blobColor
      )"
    />

    <div class="relative z-10 mb-3">
      <component
        :is="icon"
        :class="cn('w-6 h-6', props.iconColor)"
      />
    </div>

    <div class="relative z-10">
      <p class="text-sm text-muted-foreground mb-1">{{ label }}</p>
      <div class="flex items-baseline gap-1.5">
        <span class="text-3xl md:text-4xl font-semibold text-foreground tracking-tight">{{ value }}</span>
        <span class="text-base text-muted-foreground">{{ unit }}</span>
      </div>
    </div>
  </div>
</template>
