<script setup lang="ts">
import Button from '~/components/ui/button/Button.vue'
import Card from '~/components/ui/card/Card.vue'
import CardContent from '~/components/ui/card/CardContent.vue'
import Input from '~/components/ui/input/Input.vue'
import Textarea from '~/components/ui/textarea/Textarea.vue'
import Select from '~/components/ui/select/Select.vue'
import SelectContent from '~/components/ui/select/SelectContent.vue'
import SelectItem from '~/components/ui/select/SelectItem.vue'
import SelectTrigger from '~/components/ui/select/SelectTrigger.vue'
import SelectValue from '~/components/ui/select/SelectValue.vue'
import Separator from '~/components/ui/separator/Separator.vue'
import Label from '~/components/ui/label/Label.vue'
import type { IsSelected, NutritionSection, Option, PlaceholderFor } from '~/components/settings/types'

const form = defineModel<NutritionSection>({ required: true });

defineProps<{
    defaults: NutritionSection;
    placeholderFor: PlaceholderFor;
    isSelected: IsSelected;
    yesNoOptions: Option[];
    dietPatterns: Option[];
    skillLevels: Option[];
    budgetOptions: Option[];
    alcoholOptions: Option[];
}>();
</script>

<template>
    <div class="space-y-6">
        <Card class="shadow-sm">
            <CardContent class="p-6 space-y-6">
                <div class="space-y-1">
                    <h2 class="text-lg font-semibold">Nutrition</h2>
                    <p class="text-sm text-muted-foreground">
                        Configure your nutrition preferences and dietary information.
                    </p>
                </div>

                <Separator />

                <div class="grid grid-cols-1 gap-4 md:grid-cols-2">
                    <div class="space-y-2">
                        <Label>Current Diet Pattern</Label>
                        <p class="text-xs text-muted-foreground">What dietary pattern do you currently follow?</p>
                        <Select v-model="form.dietPattern">
                            <SelectTrigger>
                                <SelectValue :placeholder="placeholderFor(defaults.dietPattern, 'Select a diet pattern')" />
                            </SelectTrigger>
                            <SelectContent>
                                <SelectItem v-for="option in dietPatterns" :key="option.value" :value="option.value">
                                    {{ option.label }}
                                </SelectItem>
                            </SelectContent>
                        </Select>
                    </div>
                    <div class="space-y-2">
                        <Label>Cooking Skill Level</Label>
                        <p class="text-xs text-muted-foreground">How would you rate your cooking skills?</p>
                        <Select v-model="form.cookingSkill">
                            <SelectTrigger>
                                <SelectValue :placeholder="placeholderFor(defaults.cookingSkill, 'Select skill level')" />
                            </SelectTrigger>
                            <SelectContent>
                                <SelectItem v-for="option in skillLevels" :key="option.value" :value="option.value">
                                    {{ option.label }}
                                </SelectItem>
                            </SelectContent>
                        </Select>
                    </div>
                </div>

                <Separator />

                <div class="grid grid-cols-1 gap-4 md:grid-cols-2">
                    <div class="space-y-2">
                        <Label>Food Budget</Label>
                        <p class="text-xs text-muted-foreground">Your weekly/monthly food budget.</p>
                        <Select v-model="form.foodBudget">
                            <SelectTrigger>
                                <SelectValue :placeholder="placeholderFor(defaults.foodBudget, 'Select budget range')" />
                            </SelectTrigger>
                            <SelectContent>
                                <SelectItem v-for="option in budgetOptions" :key="option.value" :value="option.value">
                                    {{ option.label }}
                                </SelectItem>
                            </SelectContent>
                        </Select>
                    </div>
                    <div class="space-y-2">
                        <Label>Alcohol Intake</Label>
                        <p class="text-xs text-muted-foreground">How often do you consume alcohol?</p>
                        <Select v-model="form.alcoholIntake">
                            <SelectTrigger>
                                <SelectValue :placeholder="placeholderFor(defaults.alcoholIntake, 'Select frequency')" />
                            </SelectTrigger>
                            <SelectContent>
                                <SelectItem v-for="option in alcoholOptions" :key="option.value" :value="option.value">
                                    {{ option.label }}
                                </SelectItem>
                            </SelectContent>
                        </Select>
                    </div>
                </div>

                <Separator />

                <div class="grid grid-cols-1 gap-4 md:grid-cols-2">
                    <div class="space-y-2">
                        <Label>Calorie Tracking Experience</Label>
                        <p class="text-xs text-muted-foreground">Have you tracked calories before?</p>
                        <div class="flex gap-3">
                            <Button
                                variant="outline"
                                class="h-10 w-20"
                                :class="isSelected(form.calorieTracking, defaults.calorieTracking, 'yes') ? 'border-foreground text-foreground' : ''"
                                @click="form.calorieTracking = 'yes'"
                            >
                                {{ yesNoOptions[0].label }}
                            </Button>
                            <Button
                                variant="outline"
                                class="h-10 w-20"
                                :class="isSelected(form.calorieTracking, defaults.calorieTracking, 'no') ? 'border-foreground text-foreground' : ''"
                                @click="form.calorieTracking = 'no'"
                            >
                                {{ yesNoOptions[1].label }}
                            </Button>
                        </div>
                    </div>
                    <div class="space-y-2">
                        <Label>Cultural Food Preferences</Label>
                        <p class="text-xs text-muted-foreground">Any cultural or regional food preferences?</p>
                        <Input
                            v-model="form.culturalPreferences"
                            :placeholder="placeholderFor(defaults.culturalPreferences, 'e.g., Mediterranean, Asian, Indian')"
                        />
                    </div>
                </div>

                <Separator />

                <div class="grid grid-cols-1 gap-4 md:grid-cols-2">
                    <div class="space-y-2">
                        <Label>Food Allergies</Label>
                        <p class="text-xs text-muted-foreground">List any food allergies you have.</p>
                        <Input
                            v-model="form.allergies"
                            :placeholder="placeholderFor(defaults.allergies, 'Add a food allergy...')"
                        />
                    </div>
                    <div class="space-y-2">
                        <Label>Dietary Restrictions</Label>
                        <p class="text-xs text-muted-foreground">Any dietary restrictions or foods you avoid?</p>
                        <Input
                            v-model="form.dietaryRestrictions"
                            :placeholder="placeholderFor(defaults.dietaryRestrictions, 'Add a dietary restriction...')"
                        />
                    </div>
                </div>

                <Separator />

                <div class="grid grid-cols-1 gap-4">
                    <div class="space-y-2">
                        <Label>Macronutrient Preferences</Label>
                        <p class="text-xs text-muted-foreground">Any specific preferences for protein, carbs, or fats?</p>
                        <Textarea
                            v-model="form.macroPreferences"
                            :placeholder="placeholderFor(defaults.macroPreferences, 'e.g., High protein, moderate carbs, low fat')"
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
