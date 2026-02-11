<script setup lang="ts">
import Button from '~/components/ui/button/Button.vue'
import Card from '~/components/ui/card/Card.vue'
import CardContent from '~/components/ui/card/CardContent.vue'
import Input from '~/components/ui/input/Input.vue'
import Textarea from '~/components/ui/textarea/Textarea.vue'
import Separator from '~/components/ui/separator/Separator.vue'
import Label from '~/components/ui/label/Label.vue'
import type { PlaceholderFor, SettingsFormDefaults, SettingsFormState } from '~/components/settings/types'

defineProps<{
    formState: SettingsFormState;
    formDefaults: SettingsFormDefaults;
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

                <div class="grid gap-4 md:grid-cols-[220px,1fr]">
                    <div>
                        <Label>Primary Goal</Label>
                        <p class="text-xs text-muted-foreground">Your main fitness objective.</p>
                    </div>
                    <Input
                        v-model="formState.goals.primaryGoal"
                        :placeholder="placeholderFor(formDefaults.goals.primaryGoal, 'Add your primary goal...')"
                    />
                </div>

                <Separator />

                <div class="grid gap-4 md:grid-cols-[220px,1fr]">
                    <div>
                        <Label>Secondary Goals</Label>
                        <p class="text-xs text-muted-foreground">Additional objectives you want to achieve.</p>
                    </div>
                    <Input
                        v-model="formState.goals.secondaryGoals"
                        :placeholder="placeholderFor(formDefaults.goals.secondaryGoals, 'Add a secondary goal...')"
                    />
                </div>

                <Separator />

                <div class="grid gap-4 md:grid-cols-[220px,1fr]">
                    <div>
                        <Label>Timeframe</Label>
                        <p class="text-xs text-muted-foreground">When do you want to achieve your goals?</p>
                    </div>
                    <Input
                        v-model="formState.goals.timeframe"
                        :placeholder="placeholderFor(formDefaults.goals.timeframe, 'Add a target timeframe...')"
                    />
                </div>

                <Separator />

                <div class="grid gap-4 md:grid-cols-[220px,1fr]">
                    <div>
                        <Label>Target Weight</Label>
                        <p class="text-xs text-muted-foreground">Your desired weight goal.</p>
                    </div>
                    <Input
                        v-model="formState.goals.targetWeight"
                        :placeholder="placeholderFor(formDefaults.goals.targetWeight, 'Add a target weight...')"
                    />
                </div>

                <Separator />

                <div class="grid gap-4 md:grid-cols-[220px,1fr]">
                    <div>
                        <Label>Performance Metric</Label>
                        <p class="text-xs text-muted-foreground">Track a specific performance indicator.</p>
                    </div>
                    <Input
                        v-model="formState.goals.performanceMetric"
                        :placeholder="placeholderFor(formDefaults.goals.performanceMetric, 'Add a performance metric...')"
                    />
                </div>

                <Separator />

                <div class="grid gap-4 md:grid-cols-[220px,1fr] items-start">
                    <div>
                        <Label>Aesthetic Goals</Label>
                        <p class="text-xs text-muted-foreground">Describe your desired physical appearance goals.</p>
                    </div>
                    <Textarea
                        v-model="formState.goals.aestheticGoals"
                        :placeholder="placeholderFor(formDefaults.goals.aestheticGoals, 'Add your aesthetic goals...')"
                        :rows="3"
                    />
                </div>

                <Separator />

                <div class="grid gap-4 md:grid-cols-[220px,1fr]">
                    <div>
                        <Label>Health Goals</Label>
                        <p class="text-xs text-muted-foreground">Specific health improvements you're targeting.</p>
                    </div>
                    <Input
                        v-model="formState.goals.healthGoals"
                        :placeholder="placeholderFor(formDefaults.goals.healthGoals, 'Add a health goal...')"
                    />
                </div>
            </CardContent>
        </Card>

        <div class="flex justify-end gap-3">
            <Button variant="ghost" class="text-muted-foreground">Cancel</Button>
            <Button class="bg-[#d96d54] text-white hover:bg-[#c75f49]">Save changes</Button>
        </div>
    </div>
</template>
