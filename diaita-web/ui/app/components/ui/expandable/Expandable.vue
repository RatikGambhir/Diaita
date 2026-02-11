<script setup lang="ts">
import { provide, ref } from "vue"
import { expandableContextKey } from "./context"

const props = withDefaults(
    defineProps<{
        expandDirection?: "both" | "vertical" | "horizontal";
        expandBehavior?: "replace" | "push";
        initialDelay?: number;
        onExpandStart?: () => void;
        onExpandEnd?: () => void;
    }>(),
    {
        expandDirection: "vertical",
        expandBehavior: "replace",
        initialDelay: 0,
    },
)

const isExpanded = ref(false)
const pendingTimeout = ref<number | null>(null)

const setExpanded = (value: boolean) => {
    if (isExpanded.value === value) {
        return
    }

    if (pendingTimeout.value !== null) {
        window.clearTimeout(pendingTimeout.value)
        pendingTimeout.value = null
    }

    props.onExpandStart?.()
    isExpanded.value = value
    pendingTimeout.value = window.setTimeout(() => {
        props.onExpandEnd?.()
        pendingTimeout.value = null
    }, 180)
}

const toggle = () => {
    const delay = props.initialDelay ?? 0
    if (delay > 0) {
        window.setTimeout(() => setExpanded(!isExpanded.value), delay * 1000)
        return
    }
    setExpanded(!isExpanded.value)
}

provide(expandableContextKey, {
    isExpanded,
    toggle,
    setExpanded,
})
</script>

<template>
    <slot />
</template>
