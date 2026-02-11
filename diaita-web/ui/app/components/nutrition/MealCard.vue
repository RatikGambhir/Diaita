<script setup lang="ts">
import Button from "~/components/ui/button/Button.vue"
import Card from "~/components/ui/card/Card.vue"
import CardContent from "~/components/ui/card/CardContent.vue"
import { Pencil, Plus, Trash2 } from "lucide-vue-next"

defineProps<{
    meal: {
        name: string;
        icon: any;
        totals: { calories: number; carbs: number; protein: number; fat: number };
        items: Array<{ name: string; calories: number; carbs: number; protein: number; fat: number }>;
    };
}>();
</script>

<template>
    <Card class="bg-card text-card-foreground border shadow-sm">
        <CardContent class="p-5">
            <div class="flex items-center justify-between">
                <div class="flex items-center gap-2 text-sm font-semibold">
                    <component :is="meal.icon" class="h-4 w-4 text-primary" />
                    {{ meal.name }}
                </div>
                <Button size="sm" variant="secondary" class="h-7 px-3 text-xs">
                    <Plus class="h-3 w-3 mr-1" />
                    Add Food
                </Button>
            </div>

            <div class="mt-4 grid grid-cols-4 gap-2 text-xs text-muted-foreground">
                <div class="text-center">
                    <div class="text-muted-foreground">Calories</div>
                    <div class="text-base font-semibold text-foreground">{{ meal.totals.calories }}</div>
                </div>
                <div class="text-center">
                    <div class="text-muted-foreground">Carbs</div>
                    <div class="text-base font-semibold text-chart-1">{{ meal.totals.carbs }}g</div>
                </div>
                <div class="text-center">
                    <div class="text-muted-foreground">Protein</div>
                    <div class="text-base font-semibold text-chart-3">{{ meal.totals.protein }}g</div>
                </div>
                <div class="text-center">
                    <div class="text-muted-foreground">Fat</div>
                    <div class="text-base font-semibold text-chart-2">{{ meal.totals.fat }}g</div>
                </div>
            </div>

            <div class="mt-4 space-y-3">
                <div
                    v-for="item in meal.items"
                    :key="item.name"
                    class="bg-muted rounded-lg px-4 py-3 flex items-center justify-between"
                >
                    <div class="space-y-1">
                        <div class="text-sm font-medium text-foreground">{{ item.name }}</div>
                        <div class="flex flex-wrap items-center gap-3 text-xs text-muted-foreground">
                            <div class="flex items-center gap-1">
                                <span class="h-1.5 w-1.5 rounded-full bg-chart-4"></span>
                                {{ item.calories }} cal
                            </div>
                            <div class="flex items-center gap-1">
                                <span class="h-1.5 w-1.5 rounded-full bg-chart-1"></span>
                                {{ item.carbs }}g carbs
                            </div>
                            <div class="flex items-center gap-1">
                                <span class="h-1.5 w-1.5 rounded-full bg-chart-3"></span>
                                {{ item.protein }}g protein
                            </div>
                            <div class="flex items-center gap-1">
                                <span class="h-1.5 w-1.5 rounded-full bg-chart-2"></span>
                                {{ item.fat }}g fat
                            </div>
                        </div>
                    </div>
                    <div class="flex items-center gap-3 text-muted-foreground">
                        <Pencil class="h-4 w-4 cursor-pointer hover:text-foreground transition-colors" />
                        <Trash2 class="h-4 w-4 text-destructive cursor-pointer hover:text-destructive/80 transition-colors" />
                    </div>
                </div>
            </div>
        </CardContent>
    </Card>
</template>
