<script setup lang="ts">
import Card from "~/components/ui/card/Card.vue"
import CardContent from "~/components/ui/card/CardContent.vue"
import CardHeader from "~/components/ui/card/CardHeader.vue"
import CardTitle from "~/components/ui/card/CardTitle.vue"
import FoodListItem from "~/components/nutrition/FoodListItem.vue"
import { Apple, Loader2, Search } from "lucide-vue-next"
import Input from "~/components/ui/input/Input.vue"
import ToggleGroup from "~/components/ui/toggle-group/ToggleGroup.vue"
import ToggleGroupItem from "~/components/ui/toggle-group/ToggleGroupItem.vue"
import { useNutritionSearch } from "~/composables/useNutritionSearch"

const {
    query,
    filter,
    results,
    isSearching,
    error,
    hasSearched,
    searchPlaceholder,
} = useNutritionSearch("ingredient")
</script>

<template>
    <div class="space-y-6">
        <Card>
            <CardHeader>
                <div class="flex items-center justify-between gap-4">
                    <CardTitle class="flex items-center gap-2">
                        <Apple class="h-5 w-5 text-primary" />
                        Food Database
                    </CardTitle>
                    <ToggleGroup v-model="filter" type="single" variant="outline" size="sm">
                        <ToggleGroupItem value="ingredient">Ingredients</ToggleGroupItem>
                        <ToggleGroupItem value="product">Products</ToggleGroupItem>
                    </ToggleGroup>
                </div>
                <p class="text-sm text-muted-foreground">
                    Search the food database to check calories and macros before you log a meal.
                </p>
            </CardHeader>
            <CardContent>
                <div class="relative mb-6">
                    <Search class="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
                    <Input
                        v-model="query"
                        :placeholder="searchPlaceholder"
                        class="pl-10"
                    />
                </div>

                <div
                    v-if="isSearching"
                    class="flex items-center justify-center gap-2 py-12 text-muted-foreground"
                >
                    <Loader2 class="h-4 w-4 animate-spin" />
                    Searching…
                </div>

                <p v-else-if="error" class="py-12 text-center text-destructive">
                    {{ error }}
                </p>

                <div v-else-if="!hasSearched" class="py-12 text-center text-muted-foreground">
                    <Apple class="mx-auto mb-4 h-16 w-16 opacity-20" />
                    <p class="text-lg font-medium">Search the food database</p>
                    <p class="mt-2 text-sm">Look up any ingredient or packaged product to see its nutrition.</p>
                </div>

                <p v-else-if="results.length === 0" class="py-12 text-center text-muted-foreground">
                    No foods matched that search.
                </p>

                <div v-else class="space-y-2">
                    <FoodListItem
                        v-for="food in results"
                        :key="food.id"
                        :item="food"
                    />
                </div>
            </CardContent>
        </Card>
    </div>
</template>
