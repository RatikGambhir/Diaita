<script setup lang="ts">
import { Circle } from 'lucide-vue-next'

defineProps<{
  name: string
  detail: string
}>()
</script>

<template>
  <li class="flex items-center gap-2 text-sm">
    <Circle class="h-2 w-2 fill-current text-muted-foreground" />
    <span class="font-medium">{{ name }}</span>
    <span class="text-muted-foreground">
      - {{ detail }}
    </span>
  </li>
</template>
