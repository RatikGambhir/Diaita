<script setup lang="ts">
import Card from "~/components/ui/card/Card.vue"
import CardContent from "~/components/ui/card/CardContent.vue"
import CardHeader from "~/components/ui/card/CardHeader.vue"
import CardTitle from "~/components/ui/card/CardTitle.vue"
import { UtensilsCrossed, Plus } from "lucide-vue-next"
import Button from "~/components/ui/button/Button.vue"
</script>

<template>
    <div class="space-y-6">
        <Card>
            <CardHeader>
                <div class="flex items-center justify-between">
                    <CardTitle class="flex items-center gap-2">
                        <UtensilsCrossed class="h-5 w-5 text-primary" />
                        Saved Meals
                    </CardTitle>
                    <Button size="sm">
                        <Plus class="h-4 w-4 mr-2" />
                        Create Meal
                    </Button>
                </div>
            </CardHeader>
            <CardContent>
                <div class="text-center py-12 text-muted-foreground">
                    <UtensilsCrossed class="h-16 w-16 mx-auto mb-4 opacity-20" />
                    <p class="text-lg font-medium">Meal Templates Coming Soon</p>
                    <p class="text-sm mt-2">Save your favorite meals for quick logging</p>
                </div>
            </CardContent>
        </Card>
    </div>
</template>
