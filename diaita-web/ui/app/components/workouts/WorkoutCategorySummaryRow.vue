<script setup lang="ts">
defineProps<{
    label: string;
    percentage: number;
}>();
</script>

<template>
    <div class="flex items-center justify-between text-sm">
        <span class="font-medium">{{ label }}</span>
        <span class="text-muted-foreground">{{ percentage }}%</span>
    </div>
</template>
