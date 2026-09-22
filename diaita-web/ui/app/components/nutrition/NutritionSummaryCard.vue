<script setup lang="ts">
defineProps<{
    label: string;
    value: string | number;
    unit: string;
    valueClass: string;
    dotClass: string;
}>();
</script>

<template>
  <div class="flex min-h-28 flex-col justify-center px-5 py-4 first:pl-0 last:pr-0">
    <div class="flex items-center gap-2 text-[11px] font-semibold uppercase tracking-[0.11em] text-muted-foreground">
      <span class="h-2 w-2" :class="dotClass" />
      {{ label }}
    </div>
    <div class="mt-3 flex items-baseline gap-2">
      <span class="font-mono text-3xl font-semibold tracking-tight" :class="valueClass">{{ value }}</span>
      <span class="text-xs text-muted-foreground">{{ unit }}</span>
    </div>
  </div>
</template>
