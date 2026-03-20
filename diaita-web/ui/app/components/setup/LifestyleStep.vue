<script setup lang="ts">
import Input from '~/components/ui/input/Input.vue'
import Label from '~/components/ui/label/Label.vue'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '~/components/ui/select'

interface LifestyleForm {
  activityLevel: string
  sleepDuration: number | null
  stressLevel: string
}

interface Props {
  formData: LifestyleForm
}

defineProps<Props>()
const emit = defineEmits<{
  'update:formData': [data: Partial<LifestyleForm>]
}>()

const activityLevels = [
  { value: 'sedentary', label: 'Sedentary (little or no exercise)' },
  { value: 'lightly_active', label: 'Lightly Active (1-3 days/week)' },
  { value: 'moderately_active', label: 'Moderately Active (3-5 days/week)' },
  { value: 'very_active', label: 'Very Active (6-7 days/week)' },
  { value: 'extremely_active', label: 'Extremely Active (athlete/physical job)' },
]

const stressLevelOptions = [
  { value: 'low', label: 'Low' },
  { value: 'moderate', label: 'Moderate' },
  { value: 'high', label: 'High' },
  { value: 'very_high', label: 'Very High' },
]

const updateField = (field: keyof LifestyleForm, value: string | number | null) => {
  emit('update:formData', { [field]: value })
}

const handleNumberInput = (field: keyof LifestyleForm, value: string | number) => {
  const normalizedValue = value === '' ? '' : String(value)
  const numValue = normalizedValue === '' ? null : Number(normalizedValue)
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
          @update:model-value="updateField('activityLevel', $event as string)"
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
        <Label for="sleepDuration">Average Sleep Duration (hours)</Label>
        <Input
          id="sleepDuration"
          type="number"
          min="0"
          max="24"
          step="0.5"
          :model-value="formData.sleepDuration?.toString() ?? ''"
          @update:model-value="handleNumberInput('sleepDuration', $event)"
          placeholder="7.5"
        />
      </div>

      <div class="space-y-2">
        <Label for="stressLevel">Stress Level</Label>
        <Select
          :model-value="formData.stressLevel"
          @update:model-value="updateField('stressLevel', $event as string)"
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
    </div>
  </div>
</template>
