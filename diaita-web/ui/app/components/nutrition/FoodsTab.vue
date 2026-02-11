<script setup lang="ts">
import Card from "~/components/ui/card/Card.vue"
import CardContent from "~/components/ui/card/CardContent.vue"
import CardHeader from "~/components/ui/card/CardHeader.vue"
import CardTitle from "~/components/ui/card/CardTitle.vue"
import { Apple, Search, Plus } from "lucide-vue-next"
import Button from "~/components/ui/button/Button.vue"
import Input from "~/components/ui/input/Input.vue"
</script>

<template>
    <div class="space-y-6">
        <Card>
            <CardHeader>
                <div class="flex items-center justify-between">
                    <CardTitle class="flex items-center gap-2">
                        <Apple class="h-5 w-5 text-primary" />
                        Food Database
                    </CardTitle>
                    <Button size="sm">
                        <Plus class="h-4 w-4 mr-2" />
                        Add Custom Food
                    </Button>
                </div>
            </CardHeader>
            <CardContent>
                <div class="relative mb-6">
                    <Search class="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
                    <Input
                        placeholder="Search foods..."
                        class="pl-10"
                    />
                </div>
                <div class="text-center py-12 text-muted-foreground">
                    <Apple class="h-16 w-16 mx-auto mb-4 opacity-20" />
                    <p class="text-lg font-medium">Food Database Coming Soon</p>
                    <p class="text-sm mt-2">Browse and search thousands of foods to track your nutrition</p>
                </div>
            </CardContent>
        </Card>
    </div>
</template>
