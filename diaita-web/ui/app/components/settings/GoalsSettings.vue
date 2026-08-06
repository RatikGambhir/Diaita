<script setup lang="ts">
import Button from '~/components/ui/button/Button.vue'
import Card from '~/components/ui/card/Card.vue'
import CardContent from '~/components/ui/card/CardContent.vue'
import Input from '~/components/ui/input/Input.vue'
import Textarea from '~/components/ui/textarea/Textarea.vue'
import Separator from '~/components/ui/separator/Separator.vue'
import Label from '~/components/ui/label/Label.vue'
import type { GoalsSection, PlaceholderFor } from '~/components/settings/types'

const form = defineModel<GoalsSection>({ required: true });

defineProps<{
    defaults: GoalsSection;
    placeholderFor: PlaceholderFor;
}>();
</script>

<template>
    <div class="space-y-6">
        <Card class="shadow-sm">
            <CardContent class="p-6 space-y-6">
                <div class="space-y-1">
                    <h2 class="text-lg font-semibold">Goals</h2>
                    <p class="text-sm text-muted-foreground">
                        Configure your fitness goals and targets.
                    </p>
                </div>

                <Separator />

                <div class="grid grid-cols-1 gap-4 md:grid-cols-2">
                    <div class="space-y-2">
                        <Label>Primary Goal</Label>
                        <p class="text-xs text-muted-foreground">Your main fitness objective.</p>
                        <Input
                            v-model="form.primaryGoal"
                            :placeholder="placeholderFor(defaults.primaryGoal, 'Add your primary goal...')"
                        />
                    </div>
                    <div class="space-y-2">
                        <Label>Secondary Goals</Label>
                        <p class="text-xs text-muted-foreground">Additional objectives you want to achieve.</p>
                        <Input
                            v-model="form.secondaryGoals"
                            :placeholder="placeholderFor(defaults.secondaryGoals, 'Add a secondary goal...')"
                        />
                    </div>
                </div>

                <Separator />

                <div class="grid grid-cols-1 gap-4 md:grid-cols-2">
                    <div class="space-y-2">
                        <Label>Timeframe</Label>
                        <p class="text-xs text-muted-foreground">When do you want to achieve your goals?</p>
                        <Input
                            v-model="form.timeframe"
                            :placeholder="placeholderFor(defaults.timeframe, 'Add a target timeframe...')"
                        />
                    </div>
                    <div class="space-y-2">
                        <Label>Target Weight</Label>
                        <p class="text-xs text-muted-foreground">Your desired weight goal.</p>
                        <Input
                            v-model="form.targetWeight"
                            :placeholder="placeholderFor(defaults.targetWeight, 'Add a target weight...')"
                        />
                    </div>
                </div>

                <Separator />

                <div class="grid grid-cols-1 gap-4 md:grid-cols-2">
                    <div class="space-y-2">
                        <Label>Performance Metric</Label>
                        <p class="text-xs text-muted-foreground">Track a specific performance indicator.</p>
                        <Input
                            v-model="form.performanceMetric"
                            :placeholder="placeholderFor(defaults.performanceMetric, 'Add a performance metric...')"
                        />
                    </div>
                    <div class="space-y-2">
                        <Label>Health Goals</Label>
                        <p class="text-xs text-muted-foreground">Specific health improvements you're targeting.</p>
                        <Input
                            v-model="form.healthGoals"
                            :placeholder="placeholderFor(defaults.healthGoals, 'Add a health goal...')"
                        />
                    </div>
                </div>

                <Separator />

                <div class="grid grid-cols-1 gap-4">
                    <div class="space-y-2">
                        <Label>Aesthetic Goals</Label>
                        <p class="text-xs text-muted-foreground">Describe your desired physical appearance goals.</p>
                        <Textarea
                            v-model="form.aestheticGoals"
                            :placeholder="placeholderFor(defaults.aestheticGoals, 'Add your aesthetic goals...')"
                            :rows="3"
                        />
                    </div>
                </div>
            </CardContent>
        </Card>

        <div class="flex justify-end gap-3">
            <Button variant="ghost" class="text-muted-foreground">Cancel</Button>
            <Button class="bg-[#d96d54] text-white hover:bg-[#c75f49]">Save changes</Button>
        </div>
    </div>
</template>
