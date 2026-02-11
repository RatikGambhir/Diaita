<script setup lang="ts">
import Card from "~/components/ui/card/Card.vue"
import CardContent from "~/components/ui/card/CardContent.vue"
import CardHeader from "~/components/ui/card/CardHeader.vue"
import CardTitle from "~/components/ui/card/CardTitle.vue"

defineProps<{
    resources: Array<{
        label: string;
        value: number;
        icon: any;
        iconBg: string;
        iconColor: string;
    }>;
}>();
</script>

<template>
    <Card class="bg-card text-card-foreground border shadow-sm">
        <CardHeader class="pb-2">
            <CardTitle class="text-lg font-semibold">Your Nutrition Resources</CardTitle>
            <p class="text-sm text-muted-foreground">Overview of available resources and tools</p>
        </CardHeader>
        <CardContent class="pt-4">
            <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
                <div
                    v-for="resource in resources"
                    :key="resource.label"
                    class="flex flex-col items-center text-center gap-3"
                >
                    <div class="h-12 w-12 rounded-xl flex items-center justify-center" :class="resource.iconBg">
                        <component :is="resource.icon" class="h-6 w-6" :class="resource.iconColor" />
                    </div>
                    <div class="text-2xl font-semibold">{{ resource.value }}</div>
                    <div class="text-sm text-muted-foreground">{{ resource.label }}</div>
                </div>
            </div>
        </CardContent>
    </Card>
</template>
