<script setup lang="ts">
import { inject } from "vue"
import { expandableContextKey } from "./context"

const props = withDefaults(
    defineProps<{
        asChild?: boolean;
        disabled?: boolean;
    }>(),
    {
        asChild: true,
        disabled: false,
    },
)

const context = inject(expandableContextKey)

const onClick = () => {
    if (!context || props.disabled) {
        return
    }
    context.toggle()
}
</script>

<template>
    <div
        :class="asChild ? 'contents' : ''"
        role="button"
        :aria-expanded="context?.isExpanded?.value"
        @click="onClick"
    >
        <slot />
    </div>
</template>
