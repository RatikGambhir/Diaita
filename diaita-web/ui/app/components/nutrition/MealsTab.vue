<script setup lang="ts">
import { computed, onMounted, ref, watch } from "vue"
import Card from "~/components/ui/card/Card.vue"
import CardContent from "~/components/ui/card/CardContent.vue"
import CardHeader from "~/components/ui/card/CardHeader.vue"
import CardTitle from "~/components/ui/card/CardTitle.vue"
import { Loader2, Search, UtensilsCrossed } from "lucide-vue-next"
import Input from "~/components/ui/input/Input.vue"
import Badge from "~/components/ui/badge/Badge.vue"
import HealthyRestaurantsCard from "~/components/dashboard/HealthyRestaurantsCard.vue"
import { nutritionApi } from "~/api/nutrition"
import { toIsoDate } from "~/composables/useNutritionSeries"
import { useUserStore } from "~/stores/useUserStore"
import type { NutritionDaySummary, NutritionMealBucket } from "~/types/NutritionTypes"

const DAYS_OF_HISTORY = 14

type LoggedMeal = {
    key: string
    date: string
    mealName: string
    items: string[]
    calories: number
    protein: number
    carbs: number
    fat: number
}

const userStore = useUserStore()
const query = ref("")
const meals = ref<LoggedMeal[]>([])
const isLoading = ref(false)
const error = ref<string | null>(null)

const round = (value: number) => Math.round(value)

const bucketsOf = (summary: NutritionDaySummary): Array<[string, NutritionMealBucket]> => [
    ["Breakfast", summary.breakfast],
    ["Lunch", summary.lunch],
    ["Dinner", summary.dinner],
    ["Snacks", summary.snacks],
]

/**
 * Builds the recent-meals list from the day summaries the service already exposes, so this tab
 * shows what the user actually logged rather than a placeholder.
 */
const loadMeals = async () => {
    const userId = userStore.getUser?.id?.trim()
    if (!userId) {
        meals.value = []
        return
    }

    isLoading.value = true
    error.value = null

    try {
        const dates = Array.from({ length: DAYS_OF_HISTORY }, (_, offset) => {
            const date = new Date()
            date.setDate(date.getDate() - offset)
            return toIsoDate(date)
        })

        const summaries = await Promise.all(
            dates.map((date) => nutritionApi.getDaySummary(userId, date)),
        )

        meals.value = summaries.flatMap((summary) =>
            bucketsOf(summary)
                .filter(([, bucket]) => bucket.items.length > 0)
                .map(([mealName, bucket]) => ({
                    key: `${summary.date}-${mealName}`,
                    date: summary.date,
                    mealName,
                    items: bucket.items.map((item) => item.foodName),
                    calories: bucket.items.reduce((sum, item) => sum + item.cal, 0),
                    protein: bucket.items.reduce((sum, item) => sum + item.protein, 0),
                    carbs: bucket.items.reduce((sum, item) => sum + item.carb, 0),
                    fat: bucket.items.reduce((sum, item) => sum + item.fat, 0),
                })),
        )
    } catch (loadError) {
        meals.value = []
        error.value =
            loadError instanceof Error ? loadError.message : "Failed to load your meals."
    } finally {
        isLoading.value = false
    }
}

onMounted(() => {
    void loadMeals()
})

watch(() => userStore.getUser?.id, () => {
    void loadMeals()
})

const filteredMeals = computed(() => {
    const search = query.value.trim().toLowerCase()
    if (!search) {
        return meals.value
    }

    return meals.value.filter(
        (meal) =>
            meal.mealName.toLowerCase().includes(search)
            || meal.items.some((item) => item.toLowerCase().includes(search)),
    )
})
</script>

<template>
    <div class="space-y-6">
        <Card>
            <CardHeader>
                <CardTitle class="flex items-center gap-2">
                    <Search class="h-5 w-5 text-primary" />
                    Search Meals
                </CardTitle>
                <p class="text-sm text-muted-foreground">
                    Filter the meals you have logged over the last {{ DAYS_OF_HISTORY }} days.
                </p>
            </CardHeader>
            <CardContent>
                <Input
                    v-model="query"
                    placeholder="Search meals or ingredients..."
                />
            </CardContent>
        </Card>

        <HealthyRestaurantsCard />

        <Card>
            <CardHeader>
                <CardTitle class="flex items-center gap-2">
                    <UtensilsCrossed class="h-5 w-5 text-primary" />
                    Recent Meals
                </CardTitle>
            </CardHeader>
            <CardContent>
                <div
                    v-if="isLoading"
                    class="flex items-center justify-center gap-2 py-12 text-muted-foreground"
                >
                    <Loader2 class="h-4 w-4 animate-spin" />
                    Loading your meals…
                </div>

                <p v-else-if="error" class="py-12 text-center text-destructive">
                    {{ error }}
                </p>

                <div v-else-if="meals.length === 0" class="py-12 text-center text-muted-foreground">
                    <UtensilsCrossed class="mx-auto mb-4 h-16 w-16 opacity-20" />
                    <p class="text-lg font-medium">No meals logged yet</p>
                    <p class="mt-2 text-sm">Add foods on the Today tab and they will show up here.</p>
                </div>

                <p
                    v-else-if="filteredMeals.length === 0"
                    class="py-12 text-center text-muted-foreground"
                >
                    No logged meals match that search.
                </p>

                <ul v-else class="space-y-2">
                    <li
                        v-for="meal in filteredMeals"
                        :key="meal.key"
                        class="rounded-lg border border-border bg-card px-4 py-3"
                    >
                        <div class="flex flex-wrap items-center justify-between gap-2">
                            <div class="min-w-0">
                                <p class="font-medium">{{ meal.mealName }}</p>
                                <p class="truncate text-sm text-muted-foreground">
                                    {{ meal.items.join(', ') }}
                                </p>
                            </div>
                            <div class="flex items-center gap-2">
                                <Badge variant="secondary">{{ meal.date }}</Badge>
                                <span class="text-sm font-medium">{{ round(meal.calories) }} kcal</span>
                            </div>
                        </div>
                        <p class="mt-1 text-xs text-muted-foreground">
                            {{ round(meal.protein) }}g protein · {{ round(meal.carbs) }}g carbs ·
                            {{ round(meal.fat) }}g fat
                        </p>
                    </li>
                </ul>
            </CardContent>
        </Card>
    </div>
</template>
