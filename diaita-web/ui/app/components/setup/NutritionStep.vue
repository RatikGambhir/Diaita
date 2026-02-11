<script setup lang="ts">
import { ref } from 'vue'
import Input from '~/components/ui/input/Input.vue'
import Label from '~/components/ui/label/Label.vue'
import Textarea from '~/components/ui/textarea/Textarea.vue'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '~/components/ui/select'
import Badge from '~/components/ui/badge/Badge.vue'
import { X } from 'lucide-vue-next'

interface NutritionHistory {
  currentDietPattern: string
  calorieTrackingExperience: boolean | null
  macronutrientPreferences: string
  foodAllergies: string[]
  dietaryRestrictions: string[]
  culturalFoodPreferences: string
  cookingSkillLevel: string
  foodBudget: string
  eatingSchedule: string
  snackingHabits: string
  alcoholIntake: string
  supplementUse: string[]
}

interface Props {
  formData: NutritionHistory
}

const props = defineProps<Props>()
const emit = defineEmits<{
  'update:formData': [data: Partial<NutritionHistory>]
}>()

const dietPatternOptions = [
  { value: 'standard', label: 'Standard / No specific diet' },
  { value: 'vegetarian', label: 'Vegetarian' },
  { value: 'vegan', label: 'Vegan' },
  { value: 'keto', label: 'Keto / Low Carb' },
  { value: 'paleo', label: 'Paleo' },
  { value: 'mediterranean', label: 'Mediterranean' },
  { value: 'intermittent_fasting', label: 'Intermittent Fasting' },
  { value: 'other', label: 'Other' },
]

const cookingSkillOptions = [
  { value: 'none', label: 'None - I don\'t cook' },
  { value: 'basic', label: 'Basic - Simple meals' },
  { value: 'intermediate', label: 'Intermediate - Comfortable cooking' },
  { value: 'advanced', label: 'Advanced - Love to cook' },
]

const budgetOptions = [
  { value: 'tight', label: 'Tight - Budget conscious' },
  { value: 'moderate', label: 'Moderate - Some flexibility' },
  { value: 'flexible', label: 'Flexible - Not a concern' },
]

const alcoholOptions = [
  { value: 'none', label: 'None' },
  { value: 'occasional', label: 'Occasional (1-2 drinks/week)' },
  { value: 'moderate', label: 'Moderate (3-7 drinks/week)' },
  { value: 'regular', label: 'Regular (8+ drinks/week)' },
]

const commonAllergies = ['Dairy', 'Gluten', 'Nuts', 'Shellfish', 'Eggs', 'Soy', 'Fish']
const commonRestrictions = ['Halal', 'Kosher', 'No pork', 'No beef', 'No red meat', 'Lactose-free']

const allergyInput = ref('')
const restrictionInput = ref('')
const supplementInput = ref('')

const updateField = (field: keyof NutritionHistory, value: any) => {
  emit('update:formData', { [field]: value })
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

const addSupplement = () => {
  if (supplementInput.value.trim()) {
    updateField('supplementUse', [...props.formData.supplementUse, supplementInput.value.trim()])
    supplementInput.value = ''
  }
}

const removeSupplement = (index: number) => {
  updateField('supplementUse', props.formData.supplementUse.filter((_, i) => i !== index))
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
            @update:model-value="updateField('currentDietPattern', $event)"
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
          <Label for="calorieTrackingExperience">Calorie Tracking Experience</Label>
          <Select
            :model-value="formData.calorieTrackingExperience === null ? '' : formData.calorieTrackingExperience.toString()"
            @update:model-value="updateField('calorieTrackingExperience', $event === 'true')"
          >
            <SelectTrigger>
              <SelectValue placeholder="Have you tracked calories before?" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="true">Yes, I have experience</SelectItem>
              <SelectItem value="false">No, I'm new to tracking</SelectItem>
            </SelectContent>
          </Select>
        </div>

        <div class="space-y-2">
          <Label for="cookingSkillLevel">Cooking Skill Level</Label>
          <Select
            :model-value="formData.cookingSkillLevel"
            @update:model-value="updateField('cookingSkillLevel', $event)"
          >
            <SelectTrigger>
              <SelectValue placeholder="Select cooking skill" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem v-for="option in cookingSkillOptions" :key="option.value" :value="option.value">
                {{ option.label }}
              </SelectItem>
            </SelectContent>
          </Select>
        </div>

        <div class="space-y-2">
          <Label for="foodBudget">Food Budget</Label>
          <Select
            :model-value="formData.foodBudget"
            @update:model-value="updateField('foodBudget', $event)"
          >
            <SelectTrigger>
              <SelectValue placeholder="Select budget" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem v-for="option in budgetOptions" :key="option.value" :value="option.value">
                {{ option.label }}
              </SelectItem>
            </SelectContent>
          </Select>
        </div>

        <div class="space-y-2">
          <Label for="alcoholIntake">Alcohol Intake</Label>
          <Select
            :model-value="formData.alcoholIntake"
            @update:model-value="updateField('alcoholIntake', $event)"
          >
            <SelectTrigger>
              <SelectValue placeholder="Select alcohol intake" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem v-for="option in alcoholOptions" :key="option.value" :value="option.value">
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
            placeholder="e.g., 3 meals + 2 snacks, IF 16:8"
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
                : 'bg-background text-foreground border-border hover:bg-accent'
            ]"
            @click="toggleAllergy(allergy)"
          >
            {{ allergy }}
          </button>
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
                : 'bg-background text-foreground border-border hover:bg-accent'
            ]"
            @click="toggleRestriction(restriction)"
          >
            {{ restriction }}
          </button>
        </div>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div class="space-y-2">
          <Label for="culturalFoodPreferences">Cultural Food Preferences</Label>
          <Input
            id="culturalFoodPreferences"
            :model-value="formData.culturalFoodPreferences"
            @update:model-value="updateField('culturalFoodPreferences', $event)"
            placeholder="e.g., Mediterranean, Asian, Latin American"
          />
        </div>

        <div class="space-y-2">
          <Label for="macronutrientPreferences">Macronutrient Preferences</Label>
          <Input
            id="macronutrientPreferences"
            :model-value="formData.macronutrientPreferences"
            @update:model-value="updateField('macronutrientPreferences', $event)"
            placeholder="e.g., High protein, Low carb"
          />
        </div>
      </div>

      <div class="space-y-2">
        <Label for="snackingHabits">Snacking Habits</Label>
        <Textarea
          id="snackingHabits"
          :model-value="formData.snackingHabits"
          @update:model-value="updateField('snackingHabits', $event)"
          placeholder="Describe your typical snacking patterns..."
          class="min-h-[60px]"
        />
      </div>

      <div class="space-y-2">
        <Label for="supplementUse">Current Supplements</Label>
        <div class="flex gap-2">
          <Input
            id="supplementUse"
            v-model="supplementInput"
            placeholder="Add supplements you take"
            @keydown.enter.prevent="addSupplement"
          />
          <button
            type="button"
            class="px-4 py-2 bg-primary text-primary-foreground rounded-md text-sm"
            @click="addSupplement"
          >
            Add
          </button>
        </div>
        <div v-if="formData.supplementUse.length > 0" class="flex flex-wrap gap-2 mt-2">
          <Badge v-for="(supplement, index) in formData.supplementUse" :key="index" variant="secondary" class="gap-1">
            {{ supplement }}
            <button type="button" @click="removeSupplement(index)">
              <X class="w-3 h-3" />
            </button>
          </Badge>
        </div>
      </div>
    </div>
  </div>
</template>
