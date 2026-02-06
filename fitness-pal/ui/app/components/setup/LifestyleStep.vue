<script setup lang="ts">
import Input from '~/components/ui/input/Input.vue'
import Label from '~/components/ui/label/Label.vue'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '~/components/ui/select'

interface ActivityLifestyle {
  activityLevel: string
  dailyStepCount: number | null
  jobType: string
  commuteTime: string
  sleepDuration: number | null
  sleepQuality: string
  stressLevel: string
  recoveryCapacity: string
}

interface Props {
  formData: ActivityLifestyle
}

const props = defineProps<Props>()
const emit = defineEmits<{
  'update:formData': [data: Partial<ActivityLifestyle>]
}>()

const activityLevels = [
  { value: 'sedentary', label: 'Sedentary (little or no exercise)' },
  { value: 'lightly_active', label: 'Lightly Active (1-3 days/week)' },
  { value: 'moderately_active', label: 'Moderately Active (3-5 days/week)' },
  { value: 'very_active', label: 'Very Active (6-7 days/week)' },
  { value: 'extremely_active', label: 'Extremely Active (athlete/physical job)' },
]

const sleepQualityOptions = [
  { value: 'excellent', label: 'Excellent' },
  { value: 'good', label: 'Good' },
  { value: 'fair', label: 'Fair' },
  { value: 'poor', label: 'Poor' },
]

const stressLevelOptions = [
  { value: 'low', label: 'Low' },
  { value: 'moderate', label: 'Moderate' },
  { value: 'high', label: 'High' },
  { value: 'very_high', label: 'Very High' },
]

const recoveryCapacityOptions = [
  { value: 'excellent', label: 'Excellent - Recover quickly' },
  { value: 'good', label: 'Good - Normal recovery' },
  { value: 'fair', label: 'Fair - Takes a bit longer' },
  { value: 'poor', label: 'Poor - Slow recovery' },
]

const updateField = (field: keyof ActivityLifestyle, value: any) => {
  emit('update:formData', { [field]: value })
}

const handleNumberInput = (field: keyof ActivityLifestyle, value: string) => {
  const numValue = value === '' ? null : Number(value)
  updateField(field, numValue)
}
</script>

<template>
  <div>
    <h2 class="text-2xl font-semibold mb-2">Lifestyle & Activity</h2>
    <p class="text-muted-foreground mb-8">Help us understand your daily routine and activity patterns</p>

    <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
      <div class="space-y-2">
        <Label for="activityLevel">
          Activity Level <span class="text-destructive">*</span>
        </Label>
        <Select
          :model-value="formData.activityLevel"
          @update:model-value="updateField('activityLevel', $event)"
        >
          <SelectTrigger>
            <SelectValue placeholder="Select activity level" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem v-for="level in activityLevels" :key="level.value" :value="level.value">
              {{ level.label }}
            </SelectItem>
          </SelectContent>
        </Select>
      </div>

      <div class="space-y-2">
        <Label for="dailyStepCount">Average Daily Step Count</Label>
        <Input
          id="dailyStepCount"
          type="number"
          min="0"
          max="50000"
          :model-value="formData.dailyStepCount?.toString() ?? ''"
          @update:model-value="handleNumberInput('dailyStepCount', $event)"
          placeholder="8000"
        />
      </div>

      <div class="space-y-2">
        <Label for="jobType">Job Type</Label>
        <Input
          id="jobType"
          :model-value="formData.jobType"
          @update:model-value="updateField('jobType', $event)"
          placeholder="e.g., Desk job, Manual labor, Healthcare"
        />
      </div>

      <div class="space-y-2">
        <Label for="commuteTime">Commute Time</Label>
        <Input
          id="commuteTime"
          :model-value="formData.commuteTime"
          @update:model-value="updateField('commuteTime', $event)"
          placeholder="e.g., 30 min driving, 1 hour public transit"
        />
      </div>

      <div class="space-y-2">
        <Label for="sleepDuration">Average Sleep Duration (hours)</Label>
        <Input
          id="sleepDuration"
          type="number"
          min="1"
          max="16"
          step="0.5"
          :model-value="formData.sleepDuration?.toString() ?? ''"
          @update:model-value="handleNumberInput('sleepDuration', $event)"
          placeholder="7.5"
        />
      </div>

      <div class="space-y-2">
        <Label for="sleepQuality">Sleep Quality</Label>
        <Select
          :model-value="formData.sleepQuality"
          @update:model-value="updateField('sleepQuality', $event)"
        >
          <SelectTrigger>
            <SelectValue placeholder="Select sleep quality" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem v-for="option in sleepQualityOptions" :key="option.value" :value="option.value">
              {{ option.label }}
            </SelectItem>
          </SelectContent>
        </Select>
      </div>

      <div class="space-y-2">
        <Label for="stressLevel">Stress Level</Label>
        <Select
          :model-value="formData.stressLevel"
          @update:model-value="updateField('stressLevel', $event)"
        >
          <SelectTrigger>
            <SelectValue placeholder="Select stress level" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem v-for="option in stressLevelOptions" :key="option.value" :value="option.value">
              {{ option.label }}
            </SelectItem>
          </SelectContent>
        </Select>
      </div>

      <div class="space-y-2">
        <Label for="recoveryCapacity">Recovery Capacity</Label>
        <Select
          :model-value="formData.recoveryCapacity"
          @update:model-value="updateField('recoveryCapacity', $event)"
        >
          <SelectTrigger>
            <SelectValue placeholder="Select recovery capacity" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem v-for="option in recoveryCapacityOptions" :key="option.value" :value="option.value">
              {{ option.label }}
            </SelectItem>
          </SelectContent>
        </Select>
      </div>
    </div>
  </div>
</template>
