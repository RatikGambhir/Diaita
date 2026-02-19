<script setup lang="ts">
import Button from "~/components/ui/button/Button.vue"

type FoodItem = {
    name: string;
    calories: number;
    carbs: number;
    protein: number;
    fat: number;
    servingSize: string;
};

type ActionVariant = "default" | "destructive" | "outline" | "secondary" | "ghost" | "link";

const props = withDefaults(defineProps<{
    item: FoodItem;
    actionLabel?: string;
    actionVariant?: ActionVariant;
    actionDisabled?: boolean;
}>(), {
    actionLabel: "",
    actionVariant: "secondary",
    actionDisabled: false,
});

const emit = defineEmits<{
    (e: "action"): void;
}>();
</script>

<template>
    <div class="border border-border rounded-lg px-3 py-3 bg-card">
        <div class="flex items-start justify-between gap-3">
            <div class="space-y-1">
                <div class="text-sm font-medium text-foreground">{{ props.item.name }}</div>
                <div class="text-xs text-muted-foreground">{{ props.item.servingSize }}</div>
                <div class="flex flex-wrap items-center gap-3 text-xs text-muted-foreground">
                    <div class="flex items-center gap-1">
                        <span class="h-1.5 w-1.5 rounded-full bg-chart-4"></span>
                        {{ props.item.calories }} cal
                    </div>
                    <div class="flex items-center gap-1">
                        <span class="h-1.5 w-1.5 rounded-full bg-chart-1"></span>
                        {{ props.item.carbs }}g carbs
                    </div>
                    <div class="flex items-center gap-1">
                        <span class="h-1.5 w-1.5 rounded-full bg-chart-3"></span>
                        {{ props.item.protein }}g protein
                    </div>
                    <div class="flex items-center gap-1">
                        <span class="h-1.5 w-1.5 rounded-full bg-chart-2"></span>
                        {{ props.item.fat }}g fat
                    </div>
                </div>
            </div>

            <Button
                v-if="props.actionLabel"
                size="sm"
                :variant="props.actionVariant"
                class="h-8 px-3 text-xs"
                :disabled="props.actionDisabled"
                @click="emit('action')"
            >
                {{ props.actionLabel }}
            </Button>
        </div>
    </div>
</template>
