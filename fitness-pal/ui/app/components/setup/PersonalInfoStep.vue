<script setup lang="ts">
import Input from '~/components/ui/input/Input.vue'
import Label from '~/components/ui/label/Label.vue'
import Textarea from '~/components/ui/textarea/Textarea.vue'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '~/components/ui/select'

interface BasicDemographics {
  age: number | null
  sex: string
  gender: string
  height: number | null
  weight: number | null
  bodyFatPercentage: number | null
  leanMass: number | null
  biologicalConsiderations: string
  menstrualCycleInfo: string
}

interface Props {
  formData: BasicDemographics
}

const props = defineProps<Props>()
const emit = defineEmits<{
  'update:formData': [data: Partial<BasicDemographics>]
}>()

const sexOptions = [
  { value: 'male', label: 'Male' },
  { value: 'female', label: 'Female' },
  { value: 'intersex', label: 'Intersex' },
  { value: 'prefer_not_to_say', label: 'Prefer not to say' },
]

const updateField = (field: keyof BasicDemographics, value: any) => {
  emit('update:formData', { [field]: value })
}

const handleNumberInput = (field: keyof BasicDemographics, value: string) => {
  const numValue = value === '' ? null : Number(value)
  updateField(field, numValue)
}
</script>

<template>
  <div>
    <h2 class="text-2xl font-semibold mb-2">Personal Information</h2>
    <p class="text-muted-foreground mb-8">Tell us about yourself so we can personalize your experience</p>

    <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
      <div class="space-y-2">
        <Label for="age">
          Age <span class="text-destructive">*</span>
        </Label>
        <Input
          id="age"
          type="number"
          min="1"
          max="120"
          :model-value="formData.age?.toString() ?? ''"
          @update:model-value="handleNumberInput('age', $event)"
          placeholder="25"
        />
      </div>

      <div class="space-y-2">
        <Label for="height">
          Height (inches) <span class="text-destructive">*</span>
        </Label>
        <Input
          id="height"
          type="number"
          min="20"
          max="120"
          :model-value="formData.height?.toString() ?? ''"
          @update:model-value="handleNumberInput('height', $event)"
          placeholder="70"
        />
      </div>

      <div class="space-y-2">
        <Label for="weight">
          Weight (lbs) <span class="text-destructive">*</span>
        </Label>
        <Input
          id="weight"
          type="number"
          min="50"
          max="1000"
          :model-value="formData.weight?.toString() ?? ''"
          @update:model-value="handleNumberInput('weight', $event)"
          placeholder="155"
        />
      </div>

      <div class="space-y-2">
        <Label for="sex">Sex</Label>
        <Select
          :model-value="formData.sex"
          @update:model-value="updateField('sex', $event)"
        >
          <SelectTrigger>
            <SelectValue placeholder="Select sex" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem v-for="option in sexOptions" :key="option.value" :value="option.value">
              {{ option.label }}
            </SelectItem>
          </SelectContent>
        </Select>
      </div>

      <div class="space-y-2">
        <Label for="gender">Gender Identity</Label>
        <Input
          id="gender"
          :model-value="formData.gender"
          @update:model-value="updateField('gender', $event)"
          placeholder="How do you identify?"
        />
      </div>

      <div class="space-y-2">
        <Label for="bodyFatPercentage">Body Fat %</Label>
        <Input
          id="bodyFatPercentage"
          type="number"
          min="1"
          max="60"
          step="0.1"
          :model-value="formData.bodyFatPercentage?.toString() ?? ''"
          @update:model-value="handleNumberInput('bodyFatPercentage', $event)"
          placeholder="15"
        />
      </div>

      <div class="space-y-2">
        <Label for="leanMass">Lean Mass (lbs)</Label>
        <Input
          id="leanMass"
          type="number"
          min="20"
          max="500"
          step="0.1"
          :model-value="formData.leanMass?.toString() ?? ''"
          @update:model-value="handleNumberInput('leanMass', $event)"
          placeholder="130"
        />
      </div>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mt-6">
      <div class="space-y-2">
        <Label for="biologicalConsiderations">Biological Considerations</Label>
        <Textarea
          id="biologicalConsiderations"
          :model-value="formData.biologicalConsiderations"
          @update:model-value="updateField('biologicalConsiderations', $event)"
          placeholder="Any biological factors we should consider (e.g., hormonal conditions, metabolic considerations)..."
          class="min-h-[80px]"
        />
      </div>

      <div class="space-y-2">
        <Label for="menstrualCycleInfo">Menstrual Cycle Info</Label>
        <Textarea
          id="menstrualCycleInfo"
          :model-value="formData.menstrualCycleInfo"
          @update:model-value="updateField('menstrualCycleInfo', $event)"
          placeholder="If applicable, any relevant menstrual cycle information..."
          class="min-h-[80px]"
        />
      </div>
    </div>
  </div>
</template>
