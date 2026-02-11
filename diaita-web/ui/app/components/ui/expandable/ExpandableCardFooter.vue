<script setup lang="ts">
defineProps<{
    class?: string;
}>();
</script>

<template>
    <div :class="['px-4 pb-4', $props.class]">
        <slot />
    </div>
</template>
