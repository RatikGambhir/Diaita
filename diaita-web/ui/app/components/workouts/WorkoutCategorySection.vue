<script setup lang="ts">
defineProps<{
    label: string;
    count: number;
    emptyText: string;
}>();
</script>

<template>
    <div class="space-y-2">
        <div class="flex items-center justify-between text-xs uppercase tracking-wide text-muted-foreground">
            <span>{{ label }}</span>
            <span>{{ count }}</span>
        </div>
        <div v-if="count === 0" class="text-sm text-muted-foreground italic">
            {{ emptyText }}
        </div>
        <ul v-else class="space-y-2">
            <slot />
        </ul>
    </div>
</template>
