<script setup lang="ts">
import { computed } from 'vue'
import Card from "~/components/ui/card/Card.vue"
import CardHeader from "~/components/ui/card/CardHeader.vue"
import CardContent from "~/components/ui/card/CardContent.vue"
import CardTitle from "~/components/ui/card/CardTitle.vue"

const props = defineProps<{
    macros: Array<{
        name: string;
        percent: number;
        color: string;
        labelColor: string;
        labelClass: string;
    }>;
}>();

const totalCalories = computed(() => {
    return Math.round(props.macros.reduce((sum, macro) => sum + macro.percent, 0))
})

const formatPercent = (value: number) => {
    const roundedValue = Math.round(value * 10) / 10
    return Number.isInteger(roundedValue) ? `${roundedValue}` : `${roundedValue}`
}

const circumference = 2 * Math.PI * 48
const segments = computed(() => {
    let offset = 0
    return props.macros.map((macro) => {
        const length = (macro.percent / 100) * circumference
        const segment = { ...macro, dash: `${length} ${circumference - length}`, offset: -offset }
        offset += length
        return segment
    })
})
</script>

<template>
    <Card class="overflow-hidden">
        <CardHeader class="border-b pb-5">
            <p class="eyebrow">Nutrient balance</p>
            <CardTitle class="display-title mt-2 text-2xl">Macro distribution</CardTitle>
        </CardHeader>
        <CardContent class="grid items-center gap-8 pt-6 sm:grid-cols-[11rem_1fr]">
                <div class="relative mx-auto h-40 w-40">
                    <svg class="h-full w-full -rotate-90" viewBox="0 0 112 112" aria-label="Macronutrient distribution chart" role="img">
                        <circle cx="56" cy="56" r="48" fill="none" stroke="var(--muted)" stroke-width="12" />
                        <circle
                            v-for="segment in segments"
                            :key="segment.name"
                            cx="56"
                            cy="56"
                            r="48"
                            fill="none"
                            :stroke="segment.color"
                            stroke-width="12"
                            :stroke-dasharray="segment.dash"
                            :stroke-dashoffset="segment.offset"
                        />
                    </svg>
                    <div class="absolute inset-0 grid place-content-center text-center">
                        <span class="font-mono text-2xl font-semibold">{{ totalCalories }}%</span>
                        <span class="text-[11px] uppercase tracking-[0.12em] text-muted-foreground">mapped</span>
                    </div>
                </div>

                <div class="divide-y border-y">
                    <div
                        v-for="macro in macros"
                        :key="`legend-${macro.name}`"
                        class="grid grid-cols-[8px_1fr_auto] items-center gap-3 py-3"
                    >
                        <span
                            class="h-2 w-2 flex-shrink-0"
                            :style="{ backgroundColor: macro.color }"
                        />
                        <span class="text-sm font-medium text-foreground">{{ macro.name }}</span>
                        <span class="font-mono text-sm text-muted-foreground">{{ formatPercent(macro.percent) }}%</span>
                    </div>
                </div>
        </CardContent>
    </Card>
</template>
