<script setup lang="ts">
import { computed } from 'vue'
import Card from "~/components/ui/card/Card.vue"
import CardHeader from "~/components/ui/card/CardHeader.vue"
import CardContent from "~/components/ui/card/CardContent.vue"
import CardTitle from "~/components/ui/card/CardTitle.vue"

const props = defineProps<{
    gradient: string;
    macros: Array<{
        name: string;
        percent: number;
        color: string;
        labelColor: string;
        labelClass: string;
    }>;
}>();

const totalCalories = computed(() => {
    return props.macros.reduce((sum, macro) => sum + macro.percent, 0)
})
</script>

<template>
    <Card class="bg-card text-card-foreground border shadow-sm">
        <CardHeader class="pb-2">
            <CardTitle class="text-sm font-semibold">Macro Distribution</CardTitle>
        </CardHeader>
        <CardContent class="pt-4">
            <div class="flex flex-col sm:flex-row items-center justify-center gap-8">
                <!-- Donut Chart -->
                <div class="relative flex-shrink-0">
                    <div
                        class="h-44 w-44 rounded-full"
                        :style="{ background: gradient }"
                    >
                        <!-- Center hole -->
                        <div class="absolute inset-0 flex items-center justify-center">
                            <div class="h-24 w-24 rounded-full bg-card flex flex-col items-center justify-center">
                                <span class="text-2xl font-bold text-foreground">{{ totalCalories }}%</span>
                                <span class="text-xs text-muted-foreground">Total</span>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Legend -->
                <div class="flex flex-col gap-4">
                    <div
                        v-for="macro in macros"
                        :key="`legend-${macro.name}`"
                        class="flex items-center gap-3"
                    >
                        <span
                            class="h-3 w-3 rounded-full flex-shrink-0"
                            :style="{ backgroundColor: macro.color }"
                        ></span>
                        <div class="flex flex-col">
                            <span class="text-sm font-medium text-foreground">{{ macro.name }}</span>
                            <span class="text-xs text-muted-foreground">{{ macro.percent }}%</span>
                        </div>
                    </div>
                </div>
            </div>
        </CardContent>
    </Card>
</template>
