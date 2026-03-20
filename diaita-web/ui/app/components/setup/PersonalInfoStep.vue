<script setup lang="ts">
import Input from '~/components/ui/input/Input.vue'
import Label from '~/components/ui/label/Label.vue'

interface PersonalInfoForm {
  age: number | null
  height: number | null
  weight: number | null
}

interface Props {
  formData: PersonalInfoForm
}

defineProps<Props>()
const emit = defineEmits<{
  'update:formData': [data: Partial<PersonalInfoForm>]
}>()

const updateField = (field: keyof PersonalInfoForm, value: number | null) => {
  emit('update:formData', { [field]: value })
}

const handleNumberInput = (field: keyof PersonalInfoForm, value: string | number) => {
  const normalizedValue = value === '' ? '' : String(value)
  const numValue = normalizedValue === '' ? null : Number(normalizedValue)
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
          min="13"
          max="120"
          class="bg-background"
          :model-value="formData.age?.toString() ?? ''"
          @update:model-value="handleNumberInput('age', $event)"
          placeholder="25"
        />
      </div>

      <div class="space-y-2">
        <Label for="height">
          Height (cm) <span class="text-destructive">*</span>
        </Label>
        <Input
          id="height"
          type="number"
          min="1"
          :model-value="formData.height?.toString() ?? ''"
          @update:model-value="handleNumberInput('height', $event)"
          placeholder="178.5"
        />
      </div>

      <div class="space-y-2">
        <Label for="weight">
          Weight (kg) <span class="text-destructive">*</span>
        </Label>
        <Input
          id="weight"
          type="number"
          min="1"
          :model-value="formData.weight?.toString() ?? ''"
          @update:model-value="handleNumberInput('weight', $event)"
          placeholder="78.2"
        />
      </div>
    </div>
  </div>
</template>
