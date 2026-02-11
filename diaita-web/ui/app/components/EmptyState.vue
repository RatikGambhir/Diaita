<script setup lang="ts">
import Button from '~/components/ui/button/Button.vue'
import { Inbox, type LucideIcon } from 'lucide-vue-next'

interface Props {
    icon?: LucideIcon;
    title: string;
    description?: string;
    actionLabel?: string;
    actionIcon?: LucideIcon;
}

const props = withDefaults(defineProps<Props>(), {
    icon: Inbox,
    description: "",
    actionLabel: "",
});

const emit = defineEmits<{
    action: [];
}>();
</script>

<template>
    <div class="flex flex-col items-center justify-center py-20">
        <component :is="icon" class="h-16 w-16 text-muted-foreground mb-4" />
        <h3 class="text-xl font-semibold text-foreground/70 mb-2">
            {{ title }}
        </h3>
        <p v-if="description" class="text-muted-foreground mb-6">
            {{ description }}
        </p>
        <Button
            v-if="actionLabel"
            size="lg"
            @click="emit('action')"
        >
            <component v-if="actionIcon" :is="actionIcon" class="h-4 w-4 mr-2" />
            {{ actionLabel }}
        </Button>
    </div>
</template>
