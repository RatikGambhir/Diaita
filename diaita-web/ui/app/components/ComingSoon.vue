<script setup lang="ts">
import { Construction, type LucideIcon } from 'lucide-vue-next'

interface Props {
    title?: string;
    description?: string;
    icon?: LucideIcon;
}

withDefaults(defineProps<Props>(), {
    title: "Coming Soon!",
    description: "This feature is under development.",
    icon: Construction,
});
</script>

<template>
    <div class="flex items-center justify-center py-20">
        <div class="text-center">
            <component :is="icon" class="h-16 w-16 text-muted-foreground mb-4 mx-auto" />
            <h3 class="text-2xl font-semibold text-foreground/70 mb-2">
                {{ title }}
            </h3>
            <p class="text-muted-foreground">
                {{ description }}
            </p>
        </div>
    </div>
</template>
