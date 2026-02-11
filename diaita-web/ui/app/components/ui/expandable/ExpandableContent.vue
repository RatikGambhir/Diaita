<script setup lang="ts">
import { computed, inject } from "vue"
import { expandableContextKey } from "./context"

const props = withDefaults(
    defineProps<{
        preset?: "fade" | "slide-up" | "blur-sm" | "blur-md";
        keepMounted?: boolean;
        class?: string;
    }>(),
    {
        preset: "fade",
        keepMounted: false,
    },
)

const context = inject(expandableContextKey)

const wrapperClasses = computed(() => {
    if (props.preset === "slide-up") {
        return "expandable-slide-up"
    }
    if (props.preset === "blur-sm") {
        return "expandable-blur-sm"
    }
    if (props.preset === "blur-md") {
        return "expandable-blur-md"
    }
    return "expandable-fade"
})

const shouldRender = computed(() => props.keepMounted || context?.isExpanded.value)
</script>

<template>
    <Transition :name="wrapperClasses">
        <div v-if="shouldRender" v-show="props.keepMounted ? context?.isExpanded.value : true" :class="$props.class">
            <slot />
        </div>
    </Transition>
</template>

<style scoped>
.expandable-fade-enter-active,
.expandable-fade-leave-active {
    transition: opacity 0.2s ease;
}
.expandable-fade-enter-from,
.expandable-fade-leave-to {
    opacity: 0;
}

.expandable-slide-up-enter-active,
.expandable-slide-up-leave-active {
    transition: opacity 0.2s ease, transform 0.25s ease;
}
.expandable-slide-up-enter-from,
.expandable-slide-up-leave-to {
    opacity: 0;
    transform: translateY(12px);
}

.expandable-blur-sm-enter-active,
.expandable-blur-sm-leave-active,
.expandable-blur-md-enter-active,
.expandable-blur-md-leave-active {
    transition: opacity 0.2s ease, filter 0.2s ease;
}
.expandable-blur-sm-enter-from,
.expandable-blur-sm-leave-to {
    opacity: 0;
    filter: blur(2px);
}
.expandable-blur-md-enter-from,
.expandable-blur-md-leave-to {
    opacity: 0;
    filter: blur(6px);
}
</style>
