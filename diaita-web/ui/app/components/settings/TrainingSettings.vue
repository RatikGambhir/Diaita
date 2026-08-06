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
import type { Option, PlaceholderFor, TrainingSection } from '~/components/settings/types'

const form = defineModel<TrainingSection>({ required: true });

defineProps<{
    defaults: TrainingSection;
    placeholderFor: PlaceholderFor;
    trainingAgeOptions: Option[];
    equipmentOptions: Option[];
}>();
</script>

<template>
    <div class="space-y-6">
        <Card class="shadow-sm">
            <CardContent class="p-6 space-y-6">
                <div class="space-y-1">
                    <h2 class="text-lg font-semibold">Training</h2>
                    <p class="text-sm text-muted-foreground">
                        Configure your training preferences and workout information.
                    </p>
                </div>

                <Separator />

                <div class="grid grid-cols-1 gap-4 md:grid-cols-2">
                    <div class="space-y-2">
                        <Label>Training Age</Label>
                        <p class="text-xs text-muted-foreground">How long have you been training?</p>
                        <Select v-model="form.trainingAge">
                            <SelectTrigger>
                                <SelectValue :placeholder="placeholderFor(defaults.trainingAge, 'Select training age')" />
                            </SelectTrigger>
                            <SelectContent>
                                <SelectItem v-for="option in trainingAgeOptions" :key="option.value" :value="option.value">
                                    {{ option.label }}
                                </SelectItem>
                            </SelectContent>
                        </Select>
                    </div>

                    <div class="space-y-2">
                        <Label>Equipment Access</Label>
                        <p class="text-xs text-muted-foreground">What equipment do you have access to?</p>
                        <Select v-model="form.equipmentAccess">
                            <SelectTrigger>
                                <SelectValue :placeholder="placeholderFor(defaults.equipmentAccess, 'Select equipment access')" />
                            </SelectTrigger>
                            <SelectContent>
                                <SelectItem v-for="option in equipmentOptions" :key="option.value" :value="option.value">
                                    {{ option.label }}
                                </SelectItem>
                            </SelectContent>
                        </Select>
                    </div>
                </div>

                <Separator />

                <div class="grid grid-cols-1 gap-4 md:grid-cols-2">
                    <div class="space-y-2">
                        <Label>Time Per Session</Label>
                        <p class="text-xs text-muted-foreground">Average workout duration in minutes.</p>
                        <Input
                            v-model="form.timePerSession"
                            :placeholder="placeholderFor(defaults.timePerSession, 'e.g., 60')"
                        />
                    </div>
                    <div class="space-y-2">
                        <Label>Days Per Week</Label>
                        <p class="text-xs text-muted-foreground">How many days per week do you train?</p>
                        <Input
                            v-model="form.daysPerWeek"
                            :placeholder="placeholderFor(defaults.daysPerWeek, 'e.g., 4')"
                        />
                    </div>
                </div>

                <Separator />

                <div class="grid grid-cols-1 gap-4 md:grid-cols-2">
                    <div class="space-y-2">
                        <Label>Exercise Preferences</Label>
                        <p class="text-xs text-muted-foreground">Exercises you enjoy and prefer to include.</p>
                        <Input
                            v-model="form.exercisePreferences"
                            :placeholder="placeholderFor(defaults.exercisePreferences, '+ Add preference')"
                        />
                    </div>
                    <div class="space-y-2">
                        <Label>Exercise Dislikes</Label>
                        <p class="text-xs text-muted-foreground">Exercises you prefer to avoid.</p>
                        <Input
                            v-model="form.exerciseDislikes"
                            :placeholder="placeholderFor(defaults.exerciseDislikes, '+ Add dislike')"
                        />
                    </div>
                </div>

                <Separator />

                <div class="grid grid-cols-1 gap-4">
                    <div class="space-y-2">
                        <Label>Training History</Label>
                        <p class="text-xs text-muted-foreground">Describe your training background and experience.</p>
                        <Textarea
                            v-model="form.trainingHistory"
                            :rows="3"
                            :placeholder="placeholderFor(defaults.trainingHistory, 'e.g., Started with bodyweight training, then moved to weightlifting...')"
                        />
                    </div>
                </div>

                <Separator />

                <div class="grid grid-cols-1 gap-4">
                    <div class="space-y-2">
                        <Label>Current Workout Routine</Label>
                        <p class="text-xs text-muted-foreground">Describe your current workout routine.</p>
                        <Textarea
                            v-model="form.workoutRoutine"
                            :rows="3"
                            :placeholder="placeholderFor(defaults.workoutRoutine, 'e.g., PPL split 6 days per week, focusing on compound movements...')"
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
