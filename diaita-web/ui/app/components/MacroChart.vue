Ca<script setup lang="ts">
import { computed } from "vue";

interface MacroData {
    name: string;
    value: number;
}

const props = defineProps<{
    data: MacroData[];
}>();

const total = computed(() => props.data.reduce((sum, item) => sum + item.value, 0));

const chartSlices = computed(() => {
    let cumulativePercent = 0;
    return props.data.map((item) => {
        const percent = (item.value / total.value) * 100;
        const startPercent = cumulativePercent;
        cumulativePercent += percent;
        return {
            ...item,
            percent,
            startPercent,
        };
    });
});

const colorMap: { [key: string]: string } = {
    Protein: "var(--chart-3)",
    Carbs: "var(--chart-1)",
    Fats: "var(--chart-2)",
};

const getPieSlices = () => {
    let cumulativePercent = 0;
    return chartSlices.value.map((slice) => {
        const startAngle = (cumulativePercent / 100) * 360;
        const endAngle = ((cumulativePercent + slice.percent) / 100) * 360;
        cumulativePercent += slice.percent;

        const radius = 100;
        const innerRadius = 60;

        const startAngleRad = (startAngle - 90) * (Math.PI / 180);
        const endAngleRad = (endAngle - 90) * (Math.PI / 180);

        const x1 = 150 + radius * Math.cos(startAngleRad);
        const y1 = 150 + radius * Math.sin(startAngleRad);
        const x2 = 150 + radius * Math.cos(endAngleRad);
        const y2 = 150 + radius * Math.sin(endAngleRad);

        const xi1 = 150 + innerRadius * Math.cos(startAngleRad);
        const yi1 = 150 + innerRadius * Math.sin(startAngleRad);
        const xi2 = 150 + innerRadius * Math.cos(endAngleRad);
        const yi2 = 150 + innerRadius * Math.sin(endAngleRad);

        const largeArc = slice.percent > 50 ? 1 : 0;

        const pathData = [
            `M ${xi1} ${yi1}`,
            `L ${x1} ${y1}`,
            `A ${radius} ${radius} 0 ${largeArc} 1 ${x2} ${y2}`,
            `L ${xi2} ${yi2}`,
            `A ${innerRadius} ${innerRadius} 0 ${largeArc} 0 ${xi1} ${yi1}`,
            "Z",
        ].join(" ");

        return {
            ...slice,
            pathData,
            color: colorMap[slice.name] || "var(--muted-foreground)",
        };
    });
};
</script>

<template>
    <div class="w-full flex flex-col items-center justify-center">
        <svg
            viewBox="0 0 300 300"
            class="w-64 h-64"
        >
            <g v-for="slice in getPieSlices()" :key="slice.name">
                <path
                    :d="slice.pathData"
                    :fill="slice.color"
                    :opacity="0.8"
                    class="transition-opacity hover:opacity-100"
                />
            </g>
        </svg>
        <div class="mt-6 flex gap-6">
            <div v-for="item in data" :key="item.name" class="flex items-center gap-2">
                <div
                    class="w-4 h-4 rounded"
                    :style="{ backgroundColor: colorMap[item.name] || 'var(--muted-foreground)' }"
                ></div>
                <span class="text-sm font-medium">{{ item.name }}: {{ item.value }}g</span>
            </div>
        </div>
    </div>
</template>

<style scoped></style>
