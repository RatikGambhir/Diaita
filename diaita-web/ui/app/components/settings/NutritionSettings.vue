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
import type { IsSelected, Option, PlaceholderFor, SettingsFormDefaults, SettingsFormState } from '~/components/settings/types'

defineProps<{
    formState: SettingsFormState;
    formDefaults: SettingsFormDefaults;
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
                        <Select v-model="formState.nutrition.dietPattern">
                            <SelectTrigger>
                                <SelectValue :placeholder="placeholderFor(formDefaults.nutrition.dietPattern, 'Select a diet pattern')" />
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
                        <Select v-model="formState.nutrition.cookingSkill">
                            <SelectTrigger>
                                <SelectValue :placeholder="placeholderFor(formDefaults.nutrition.cookingSkill, 'Select skill level')" />
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
                        <Select v-model="formState.nutrition.foodBudget">
                            <SelectTrigger>
                                <SelectValue :placeholder="placeholderFor(formDefaults.nutrition.foodBudget, 'Select budget range')" />
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
                        <Select v-model="formState.nutrition.alcoholIntake">
                            <SelectTrigger>
                                <SelectValue :placeholder="placeholderFor(formDefaults.nutrition.alcoholIntake, 'Select frequency')" />
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
                                :class="isSelected(formState.nutrition.calorieTracking, formDefaults.nutrition.calorieTracking, 'yes') ? 'border-foreground text-foreground' : ''"
                                @click="formState.nutrition.calorieTracking = 'yes'"
                            >
                                {{ yesNoOptions[0].label }}
                            </Button>
                            <Button
                                variant="outline"
                                class="h-10 w-20"
                                :class="isSelected(formState.nutrition.calorieTracking, formDefaults.nutrition.calorieTracking, 'no') ? 'border-foreground text-foreground' : ''"
                                @click="formState.nutrition.calorieTracking = 'no'"
                            >
                                {{ yesNoOptions[1].label }}
                            </Button>
                        </div>
                    </div>
                    <div class="space-y-2">
                        <Label>Cultural Food Preferences</Label>
                        <p class="text-xs text-muted-foreground">Any cultural or regional food preferences?</p>
                        <Input
                            v-model="formState.nutrition.culturalPreferences"
                            :placeholder="placeholderFor(formDefaults.nutrition.culturalPreferences, 'e.g., Mediterranean, Asian, Indian')"
                        />
                    </div>
                </div>

                <Separator />

                <div class="grid grid-cols-1 gap-4 md:grid-cols-2">
                    <div class="space-y-2">
                        <Label>Food Allergies</Label>
                        <p class="text-xs text-muted-foreground">List any food allergies you have.</p>
                        <Input
                            v-model="formState.nutrition.allergies"
                            :placeholder="placeholderFor(formDefaults.nutrition.allergies, 'Add a food allergy...')"
                        />
                    </div>
                    <div class="space-y-2">
                        <Label>Dietary Restrictions</Label>
                        <p class="text-xs text-muted-foreground">Any dietary restrictions or foods you avoid?</p>
                        <Input
                            v-model="formState.nutrition.dietaryRestrictions"
                            :placeholder="placeholderFor(formDefaults.nutrition.dietaryRestrictions, 'Add a dietary restriction...')"
                        />
                    </div>
                </div>

                <Separator />

                <div class="grid grid-cols-1 gap-4">
                    <div class="space-y-2">
                        <Label>Macronutrient Preferences</Label>
                        <p class="text-xs text-muted-foreground">Any specific preferences for protein, carbs, or fats?</p>
                        <Textarea
                            v-model="formState.nutrition.macroPreferences"
                            :placeholder="placeholderFor(formDefaults.nutrition.macroPreferences, 'e.g., High protein, moderate carbs, low fat')"
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
