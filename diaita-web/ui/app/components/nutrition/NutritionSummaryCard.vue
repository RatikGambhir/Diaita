<script setup lang="ts">
import Card from "~/components/ui/card/Card.vue"
import CardContent from "~/components/ui/card/CardContent.vue"

defineProps<{
    label: string;
    value: string | number;
    unit: string;
    valueClass: string;
    dotClass: string;
}>();
</script>

<template>
    <Card class="bg-card text-card-foreground border shadow-sm">
        <CardContent class="px-4 py-3">
            <div class="flex items-center gap-2 text-xs uppercase tracking-wide text-muted-foreground">
                <span class="h-2 w-2 rounded-full" :class="dotClass"></span>
                {{ label }}
            </div>
            <div class="mt-2 flex items-baseline gap-2">
                <span class="text-2xl font-semibold" :class="valueClass">{{ value }}</span>
                <span class="text-sm text-muted-foreground">{{ unit }}</span>
            </div>
        </CardContent>
    </Card>
</template>
