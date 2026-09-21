<script setup lang="ts">
import { ref } from "vue"
import type { FunctionalComponent } from "vue"
import type { LucideProps } from "lucide-vue-next"
import type { NutritionFood } from "~/types/NutritionTypes"
import Button from "~/components/ui/button/Button.vue"
import Card from "~/components/ui/card/Card.vue"
import CardContent from "~/components/ui/card/CardContent.vue"
import AddFoodModal from "~/components/nutrition/AddFoodModal.vue"
import { Plus, Trash2 } from "lucide-vue-next"

defineProps<{
    meal: {
        name: string;
        icon: FunctionalComponent<LucideProps>;
        totals: { calories: number; carbs: number; protein: number; fat: number };
        items: Array<{ id: string | null; name: string; calories: number; carbs: number; protein: number; fat: number }>;
    };
}>();

const emit = defineEmits<{
    (e: "add-foods", ingredients: NutritionFood[]): void;
    (e: "delete-food", itemId: string | null): void;
}>();

const addFoodOpen = ref(false);

const handleSaveFoods = (ingredients: NutritionFood[]) => {
    emit("add-foods", ingredients);
};
</script>

<template>
    <Card class="overflow-hidden">
        <CardContent class="p-0">
            <div class="flex items-center justify-between border-b px-5 py-4">
                <div class="flex items-center gap-3">
                    <span class="grid h-9 w-9 place-items-center rounded-full bg-secondary text-primary">
                        <component :is="meal.icon" class="h-4 w-4" />
                    </span>
                    <div>
                        <p class="font-semibold">{{ meal.name }}</p>
                        <p class="text-xs text-muted-foreground">{{ meal.items.length }} {{ meal.items.length === 1 ? 'item' : 'items' }}</p>
                    </div>
                </div>
                <Button size="sm" variant="outline" @click="addFoodOpen = true">
                    <Plus class="h-3.5 w-3.5" />
                    Add food
                </Button>
            </div>

            <div class="grid grid-cols-4 divide-x border-b bg-muted/35 px-2 py-3 text-xs text-muted-foreground">
                <div class="px-2"><p>Calories</p><p class="mt-1 font-mono text-base font-semibold text-foreground">{{ meal.totals.calories }}</p></div>
                <div class="px-2"><p>Carbs</p><p class="mt-1 font-mono text-base font-semibold text-chart-1">{{ meal.totals.carbs }}g</p></div>
                <div class="px-2"><p>Protein</p><p class="mt-1 font-mono text-base font-semibold text-chart-3">{{ meal.totals.protein }}g</p></div>
                <div class="px-2"><p>Fat</p><p class="mt-1 font-mono text-base font-semibold text-chart-2">{{ meal.totals.fat }}g</p></div>
            </div>

            <div v-if="meal.items.length" class="divide-y">
                <div
                    v-for="(item, index) in meal.items"
                    :key="`${item.name}-${index}`"
                    class="flex items-center justify-between gap-4 px-5 py-3.5"
                >
                    <div class="min-w-0 space-y-1">
                        <div class="text-sm font-medium text-foreground">{{ item.name }}</div>
                        <div class="flex flex-wrap items-center gap-x-3 gap-y-1 font-mono text-[11px] text-muted-foreground">
                            <span>{{ item.calories }} cal</span>
                            <span>{{ item.carbs }}c</span>
                            <span>{{ item.protein }}p</span>
                            <span>{{ item.fat }}f</span>
                        </div>
                    </div>
                    <button
                        type="button"
                        class="focus-ring rounded-md p-2 text-muted-foreground transition-colors hover:bg-destructive/10 hover:text-destructive disabled:opacity-40"
                        :disabled="!item.id"
                        :aria-label="`Remove ${item.name}`"
                        @click="emit('delete-food', item.id)"
                    >
                            <Trash2 class="h-4 w-4" />
                    </button>
                </div>
            </div>
            <div v-else class="px-5 py-8 text-sm text-muted-foreground">Nothing logged for {{ meal.name.toLowerCase() }} yet.</div>
        </CardContent>
    </Card>

    <AddFoodModal
        v-model:open="addFoodOpen"
        :meal-name="meal.name"
        @save-foods="handleSaveFoods"
    />
</template>
