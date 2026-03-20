<script setup lang="ts">
import { ref } from 'vue'
import Input from '~/components/ui/input/Input.vue'
import Label from '~/components/ui/label/Label.vue'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '~/components/ui/select'
import Badge from '~/components/ui/badge/Badge.vue'
import { X } from 'lucide-vue-next'

interface NutritionForm {
  currentDietPattern: string
  dietaryRestrictions: string[]
  foodAllergies: string[]
  eatingSchedule: string
}

interface Props {
  formData: NutritionForm
}

const props = defineProps<Props>()
const emit = defineEmits<{
  'update:formData': [data: Partial<NutritionForm>]
}>()

const dietPatternOptions = [
  { value: 'standard', label: 'Standard / No specific diet' },
  { value: 'high_protein', label: 'High Protein' },
  { value: 'vegetarian', label: 'Vegetarian' },
  { value: 'vegan', label: 'Vegan' },
  { value: 'keto', label: 'Keto / Low Carb' },
  { value: 'mediterranean', label: 'Mediterranean' },
]

const commonAllergies = ['Dairy', 'Gluten', 'Nuts', 'Shellfish', 'Eggs', 'Soy', 'Fish']
const commonRestrictions = ['Vegetarian', 'Vegan', 'Halal', 'Kosher', 'No pork', 'No beef', 'Lactose-free']

const allergyInput = ref('')
const restrictionInput = ref('')

const updateField = (
  field: keyof NutritionForm,
  value: NutritionForm[keyof NutritionForm],
) => {
  emit('update:formData', { [field]: value })
}

const addItem = (field: 'foodAllergies' | 'dietaryRestrictions', value: string) => {
  const next = value.trim()
  if (!next) return
  const current = props.formData[field]
  if (!current.includes(next)) {
    updateField(field, [...current, next])
  }
}

const removeItem = (field: 'foodAllergies' | 'dietaryRestrictions', index: number) => {
  updateField(field, props.formData[field].filter((_, i) => i !== index))
}

const toggleAllergy = (allergy: string) => {
  const current = props.formData.foodAllergies
  if (current.includes(allergy)) {
    updateField('foodAllergies', current.filter(a => a !== allergy))
  } else {
    updateField('foodAllergies', [...current, allergy])
  }
}

const toggleRestriction = (restriction: string) => {
  const current = props.formData.dietaryRestrictions
  if (current.includes(restriction)) {
    updateField('dietaryRestrictions', current.filter(r => r !== restriction))
  } else {
    updateField('dietaryRestrictions', [...current, restriction])
  }
}

const addAllergy = () => {
  addItem('foodAllergies', allergyInput.value)
  allergyInput.value = ''
}

const addRestriction = () => {
  addItem('dietaryRestrictions', restrictionInput.value)
  restrictionInput.value = ''
}
</script>

<template>
  <div>
    <h2 class="text-2xl font-semibold mb-2">Nutrition & Diet History</h2>
    <p class="text-muted-foreground mb-8">Tell us about your eating habits and preferences</p>

    <div class="space-y-6">
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div class="space-y-2">
          <Label for="currentDietPattern">Current Diet Pattern</Label>
          <Select
            :model-value="formData.currentDietPattern"
            @update:model-value="updateField('currentDietPattern', $event as string)"
          >
            <SelectTrigger>
              <SelectValue placeholder="Select diet pattern" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem v-for="option in dietPatternOptions" :key="option.value" :value="option.value">
                {{ option.label }}
              </SelectItem>
            </SelectContent>
          </Select>
        </div>

        <div class="space-y-2">
          <Label for="eatingSchedule">Eating Schedule</Label>
          <Input
            id="eatingSchedule"
            :model-value="formData.eatingSchedule"
            @update:model-value="updateField('eatingSchedule', $event)"
            placeholder="e.g., 3 meals + 1 snack"
          />
        </div>
      </div>

      <div class="space-y-2">
        <Label>Food Allergies</Label>
        <p class="text-sm text-muted-foreground mb-2">Select any that apply</p>
        <div class="flex flex-wrap gap-2">
          <button
            v-for="allergy in commonAllergies"
            :key="allergy"
            type="button"
            :class="[
              'px-3 py-1.5 rounded-full text-sm border transition-colors',
              formData.foodAllergies.includes(allergy)
                ? 'bg-destructive text-destructive-foreground border-destructive'
                : 'bg-background text-foreground border-border hover:bg-white'
            ]"
            @click="toggleAllergy(allergy)"
          >
            {{ allergy }}
          </button>
        </div>
        <div class="flex gap-2 mt-2">
          <Input
            v-model="allergyInput"
            placeholder="Add custom allergy"
            @keydown.enter.prevent="addAllergy"
          />
          <button
            type="button"
            class="px-4 py-2 bg-primary text-primary-foreground rounded-md text-sm"
            @click="addAllergy"
          >
            Add
          </button>
        </div>
        <div v-if="formData.foodAllergies.length > 0" class="flex flex-wrap gap-2 mt-2">
          <Badge v-for="(item, index) in formData.foodAllergies" :key="index" variant="destructive" class="gap-1">
            {{ item }}
            <button type="button" @click="removeItem('foodAllergies', index)">
              <X class="w-3 h-3" />
            </button>
          </Badge>
        </div>
      </div>

      <div class="space-y-2">
        <Label>Dietary Restrictions</Label>
        <p class="text-sm text-muted-foreground mb-2">Select any that apply</p>
        <div class="flex flex-wrap gap-2">
          <button
            v-for="restriction in commonRestrictions"
            :key="restriction"
            type="button"
            :class="[
              'px-3 py-1.5 rounded-full text-sm border transition-colors',
              formData.dietaryRestrictions.includes(restriction)
                ? 'bg-primary text-primary-foreground border-primary'
                : 'bg-background text-foreground border-border hover:bg-white'
            ]"
            @click="toggleRestriction(restriction)"
          >
            {{ restriction }}
          </button>
        </div>
        <div class="flex gap-2 mt-2">
          <Input
            v-model="restrictionInput"
            placeholder="Add custom restriction"
            @keydown.enter.prevent="addRestriction"
          />
          <button
            type="button"
            class="px-4 py-2 bg-primary text-primary-foreground rounded-md text-sm"
            @click="addRestriction"
          >
            Add
          </button>
        </div>
        <div v-if="formData.dietaryRestrictions.length > 0" class="flex flex-wrap gap-2 mt-2">
          <Badge v-for="(item, index) in formData.dietaryRestrictions" :key="index" variant="secondary" class="gap-1">
            {{ item }}
            <button type="button" @click="removeItem('dietaryRestrictions', index)">
              <X class="w-3 h-3" />
            </button>
          </Badge>
        </div>
      </div>
    </div>
  </div>
</template>
