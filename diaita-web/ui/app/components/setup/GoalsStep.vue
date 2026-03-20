<script setup lang="ts">
import Label from '~/components/ui/label/Label.vue'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '~/components/ui/select'

interface GoalsForm {
  primaryGoal: string
  timeframe: string
}

interface Props {
  formData: GoalsForm
}

defineProps<Props>()
const emit = defineEmits<{
  'update:formData': [data: Partial<GoalsForm>]
}>()

const primaryGoalOptions = [
  { value: 'lose_weight', label: 'Lose Weight' },
  { value: 'build_muscle', label: 'Build Muscle' },
  { value: 'improve_endurance', label: 'Improve Endurance' },
  { value: 'maintain_weight', label: 'Maintain Weight' },
  { value: 'improve_health', label: 'Improve Overall Health' },
  { value: 'increase_strength', label: 'Increase Strength' },
  { value: 'improve_flexibility', label: 'Improve Flexibility' },
  { value: 'body_recomposition', label: 'Body Recomposition' },
]

const timeframeOptions = [
  { value: '4_weeks', label: '4 Weeks' },
  { value: '8_weeks', label: '8 Weeks' },
  { value: '12_weeks', label: '12 Weeks' },
  { value: '24_weeks', label: '24 Weeks' },
  { value: 'ongoing', label: 'Ongoing / Lifestyle' },
]

const updateField = (field: keyof GoalsForm, value: string) => {
  emit('update:formData', { [field]: value })
}
</script>

<template>
  <div>
    <h2 class="text-2xl font-semibold mb-2">Goals & Priorities</h2>
    <p class="text-muted-foreground mb-8">What are you looking to achieve?</p>

    <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
      <div class="space-y-2">
        <Label for="primaryGoal">
          Primary Goal <span class="text-destructive">*</span>
        </Label>
        <Select
          :model-value="formData.primaryGoal"
          @update:model-value="updateField('primaryGoal', $event as string)"
        >
          <SelectTrigger>
            <SelectValue placeholder="Select your main goal" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem v-for="option in primaryGoalOptions" :key="option.value" :value="option.value">
              {{ option.label }}
            </SelectItem>
          </SelectContent>
        </Select>
      </div>

      <div class="space-y-2">
        <Label for="timeframe">Timeframe</Label>
        <Select
          :model-value="formData.timeframe"
          @update:model-value="updateField('timeframe', $event as string)"
        >
          <SelectTrigger>
            <SelectValue placeholder="Select timeframe" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem v-for="option in timeframeOptions" :key="option.value" :value="option.value">
              {{ option.label }}
            </SelectItem>
          </SelectContent>
        </Select>
      </div>
    </div>
  </div>
</template>
