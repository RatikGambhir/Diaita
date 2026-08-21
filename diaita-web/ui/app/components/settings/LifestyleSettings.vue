<script setup lang="ts">
import Card from '~/components/ui/card/Card.vue'
import CardContent from '~/components/ui/card/CardContent.vue'
import Input from '~/components/ui/input/Input.vue'
import Select from '~/components/ui/select/Select.vue'
import SelectContent from '~/components/ui/select/SelectContent.vue'
import SelectItem from '~/components/ui/select/SelectItem.vue'
import SelectTrigger from '~/components/ui/select/SelectTrigger.vue'
import SelectValue from '~/components/ui/select/SelectValue.vue'
import Separator from '~/components/ui/separator/Separator.vue'
import Label from '~/components/ui/label/Label.vue'
import SettingsActions from '~/components/settings/SettingsActions.vue'
import type { LifestyleFormState } from '~/components/settings/mappers'
import type { Option } from '~/components/settings/types'

/**
 * Bound as a model rather than a plain prop so the section's fields are edited in place without
 * writing through a read-only prop.
 */
const form = defineModel<LifestyleFormState>({ required: true });

defineProps<{
    saving?: boolean;
    activityLevels: Option[];
    sleepQualityOptions: Option[];
    stressLevels: Option[];
    recoveryOptions: Option[];
}>();

const emit = defineEmits<{
    save: [];
    cancel: [];
}>();
</script>

<template>
    <div class="space-y-6">
        <Card class="shadow-sm">
            <CardContent class="p-6 space-y-6">
                <div class="space-y-1">
                    <h2 class="text-lg font-semibold">Lifestyle</h2>
                    <p class="text-sm text-muted-foreground">
                        Daily activity, sleep, and recovery — these shape your calorie and training targets.
                    </p>
                </div>

                <Separator />

                <div class="grid grid-cols-1 gap-4 md:grid-cols-2">
                    <div class="space-y-2">
                        <Label>Activity Level</Label>
                        <p class="text-xs text-muted-foreground">How active you are outside of training.</p>
                        <Select v-model="form.activityLevel">
                            <SelectTrigger>
                                <SelectValue placeholder="Select activity level" />
                            </SelectTrigger>
                            <SelectContent>
                                <SelectItem
                                    v-for="option in activityLevels"
                                    :key="option.value"
                                    :value="option.value"
                                >
                                    {{ option.label }}
                                </SelectItem>
                            </SelectContent>
                        </Select>
                    </div>

                    <div class="space-y-2">
                        <Label>Daily Step Count</Label>
                        <p class="text-xs text-muted-foreground">Typical steps per day (optional).</p>
                        <Input
                            v-model="form.dailyStepCount"
                            type="number"
                            min="0"
                            placeholder="e.g., 8000"
                        />
                    </div>
                </div>

                <Separator />

                <div class="grid grid-cols-1 gap-4 md:grid-cols-2">
                    <div class="space-y-2">
                        <Label>Job Type</Label>
                        <p class="text-xs text-muted-foreground">Sedentary, on your feet, physically demanding, etc.</p>
                        <Input
                            v-model="form.jobType"
                            placeholder="e.g., Desk job"
                        />
                    </div>

                    <div class="space-y-2">
                        <Label>Commute Time</Label>
                        <p class="text-xs text-muted-foreground">How long you spend commuting each day.</p>
                        <Input
                            v-model="form.commuteTime"
                            placeholder="e.g., 45 minutes"
                        />
                    </div>
                </div>

                <Separator />

                <div class="grid grid-cols-1 gap-4 md:grid-cols-2">
                    <div class="space-y-2">
                        <Label>Sleep Duration</Label>
                        <p class="text-xs text-muted-foreground">Average hours of sleep per night.</p>
                        <Input
                            v-model="form.sleepDuration"
                            type="number"
                            min="0"
                            max="24"
                            step="0.5"
                            placeholder="e.g., 7.5"
                        />
                    </div>

                    <div class="space-y-2">
                        <Label>Sleep Quality</Label>
                        <p class="text-xs text-muted-foreground">How rested you usually feel.</p>
                        <Select v-model="form.sleepQuality">
                            <SelectTrigger>
                                <SelectValue placeholder="Select sleep quality" />
                            </SelectTrigger>
                            <SelectContent>
                                <SelectItem
                                    v-for="option in sleepQualityOptions"
                                    :key="option.value"
                                    :value="option.value"
                                >
                                    {{ option.label }}
                                </SelectItem>
                            </SelectContent>
                        </Select>
                    </div>
                </div>

                <Separator />

                <div class="grid grid-cols-1 gap-4 md:grid-cols-2">
                    <div class="space-y-2">
                        <Label>Stress Level</Label>
                        <p class="text-xs text-muted-foreground">Your typical day-to-day stress.</p>
                        <Select v-model="form.stressLevel">
                            <SelectTrigger>
                                <SelectValue placeholder="Select stress level" />
                            </SelectTrigger>
                            <SelectContent>
                                <SelectItem
                                    v-for="option in stressLevels"
                                    :key="option.value"
                                    :value="option.value"
                                >
                                    {{ option.label }}
                                </SelectItem>
                            </SelectContent>
                        </Select>
                    </div>

                    <div class="space-y-2">
                        <Label>Recovery Capacity</Label>
                        <p class="text-xs text-muted-foreground">How quickly you bounce back between sessions.</p>
                        <Select v-model="form.recoveryCapacity">
                            <SelectTrigger>
                                <SelectValue placeholder="Select recovery capacity" />
                            </SelectTrigger>
                            <SelectContent>
                                <SelectItem
                                    v-for="option in recoveryOptions"
                                    :key="option.value"
                                    :value="option.value"
                                >
                                    {{ option.label }}
                                </SelectItem>
                            </SelectContent>
                        </Select>
                    </div>
                </div>
            </CardContent>
        </Card>

        <SettingsActions
            :saving="saving"
            @save="emit('save')"
            @cancel="emit('cancel')"
        />
    </div>
</template>
