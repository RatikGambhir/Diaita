<script setup lang="ts">
defineProps<{
  eyebrow?: string
  title: string
  description?: string
}>()
</script>

<template>
  <header class="flex flex-col gap-5 border-b pb-7 sm:flex-row sm:items-end sm:justify-between">
    <div class="max-w-2xl">
      <p v-if="eyebrow" class="eyebrow mb-3">{{ eyebrow }}</p>
      <h1 class="display-title text-4xl leading-[1.05] text-foreground sm:text-5xl">{{ title }}</h1>
      <p v-if="description" class="mt-3 max-w-xl text-sm leading-6 text-muted-foreground sm:text-base">
        {{ description }}
      </p>
      <slot name="meta" />
    </div>
    <div v-if="$slots.actions" class="flex shrink-0 flex-wrap items-center gap-2">
      <slot name="actions" />
    </div>
  </header>
</template>
