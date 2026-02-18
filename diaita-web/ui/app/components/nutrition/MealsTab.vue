<script setup lang="ts">
import Card from "~/components/ui/card/Card.vue"
import CardContent from "~/components/ui/card/CardContent.vue"
import CardHeader from "~/components/ui/card/CardHeader.vue"
import CardTitle from "~/components/ui/card/CardTitle.vue"
import { Search, UtensilsCrossed, Plus } from "lucide-vue-next"
import Button from "~/components/ui/button/Button.vue"
import Input from "~/components/ui/input/Input.vue"
import HealthyRestaurantsCard from "~/components/dashboard/HealthyRestaurantsCard.vue"
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
                    Find meal ideas, nearby healthy options, or specific menu items.
                </p>
            </CardHeader>
            <CardContent>
                <div class="flex flex-col gap-3 sm:flex-row">
                    <Input
                        placeholder="Search meals, ingredients, or restaurants..."
                        class="sm:flex-1"
                    />
                    <Button class="sm:min-w-28">
                        <Search class="mr-2 h-4 w-4" />
                        Search
                    </Button>
                </div>
            </CardContent>
        </Card>

        <HealthyRestaurantsCard />

        <Card>
            <CardHeader>
                <div class="flex items-center justify-between">
                    <CardTitle class="flex items-center gap-2">
                        <UtensilsCrossed class="h-5 w-5 text-primary" />
                        Saved Meals
                    </CardTitle>
                    <Button size="sm">
                        <Plus class="mr-2 h-4 w-4" />
                        Create Meal
                    </Button>
                </div>
            </CardHeader>
            <CardContent>
                <div class="py-12 text-center text-muted-foreground">
                    <UtensilsCrossed class="mx-auto mb-4 h-16 w-16 opacity-20" />
                    <p class="text-lg font-medium">Meal Templates Coming Soon</p>
                    <p class="mt-2 text-sm">Save your favorite meals for quick logging</p>
                </div>
            </CardContent>
        </Card>
    </div>
</template>
