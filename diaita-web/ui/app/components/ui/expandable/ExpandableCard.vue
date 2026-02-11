<script setup lang="ts">
import { computed, inject, onBeforeUnmount, ref } from "vue"
import { expandableContextKey } from "./context"

const props = withDefaults(
    defineProps<{
        collapsedSize?: { width?: number | string; height?: number | string };
        expandedSize?: { width?: number | string; height?: number | string };
        hoverToExpand?: boolean;
        expandDelay?: number;
        collapseDelay?: number;
        class?: string;
    }>(),
    {
        hoverToExpand: false,
        expandDelay: 0,
        collapseDelay: 0,
    },
)

const context = inject(expandableContextKey)
const timeoutId = ref<number | null>(null)

const normalizeSize = (value?: number | string) => {
    if (value === undefined) {
        return undefined
    }
    if (typeof value === "number") {
        return `${value}px`
    }
    return value
}

const cardStyle = computed(() => {
    const size = context?.isExpanded.value ? props.expandedSize : props.collapsedSize
    return {
        width: normalizeSize(size?.width),
        height: normalizeSize(size?.height),
    }
})

const clearTimer = () => {
    if (timeoutId.value !== null) {
        window.clearTimeout(timeoutId.value)
        timeoutId.value = null
    }
}

const scheduleExpand = (value: boolean) => {
    if (!context) {
        return
    }
    clearTimer()
    const delay = value ? props.expandDelay : props.collapseDelay
    if (delay && delay > 0) {
        timeoutId.value = window.setTimeout(() => {
            context.setExpanded(value)
            timeoutId.value = null
        }, delay)
        return
    }
    context.setExpanded(value)
}

const onMouseEnter = () => {
    if (!props.hoverToExpand) {
        return
    }
    scheduleExpand(true)
}

const onMouseLeave = () => {
    if (!props.hoverToExpand) {
        return
    }
    scheduleExpand(false)
}

onBeforeUnmount(() => {
    clearTimer()
})
</script>

<template>
    <div
        :class="[
            'overflow-hidden transition-all duration-300 ease-out',
            props.class,
        ]"
        :style="cardStyle"
        @mouseenter="onMouseEnter"
        @mouseleave="onMouseLeave"
    >
        <slot />
    </div>
</template>
