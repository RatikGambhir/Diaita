<script setup lang="ts">
import { ref, computed } from 'vue'
import { Utensils, Calendar, Users, Apple, Target, Sparkles, Loader2 } from 'lucide-vue-next'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue
} from '~/components/ui/select'
import Button from '~/components/ui/button/Button.vue'

interface Props {
  loading?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  loading: false
})

const emit = defineEmits<{
  submit: [data: FormData]
}>()

interface FormData {
  dietType: string
  mealsPerDay: string
  servings: string
  restrictions: string
  goal: string
}

const formData = ref<FormData>({
  dietType: '',
  mealsPerDay: '',
  servings: '',
  restrictions: '',
  goal: ''
})

const dietTypes = [
  { value: 'balanced', label: 'Balanced' },
  { value: 'keto', label: 'Keto' },
  { value: 'paleo', label: 'Paleo' },
  { value: 'vegan', label: 'Vegan' },
  { value: 'vegetarian', label: 'Vegetarian' },
  { value: 'mediterranean', label: 'Mediterranean' }
]

const mealsOptions = [
  { value: '2', label: '2 meals per day' },
  { value: '3', label: '3 meals per day' },
  { value: '4', label: '4 meals per day' },
  { value: '5', label: '5 meals per day' },
  { value: '6', label: '6 meals per day' }
]

const servingsOptions = [
  { value: '1', label: '1 person' },
  { value: '2', label: '2 people' },
  { value: '3', label: '3 people' },
  { value: '4', label: '4 people' },
  { value: '5+', label: '5+ people' }
]

const restrictionsOptions = [
  { value: 'none', label: 'No Restrictions' },
  { value: 'gluten-free', label: 'Gluten-Free' },
  { value: 'dairy-free', label: 'Dairy-Free' },
  { value: 'nut-free', label: 'Nut-Free' },
  { value: 'low-sodium', label: 'Low Sodium' }
]

const goals = [
  { value: 'weight-loss', label: 'Weight Loss' },
  { value: 'muscle-gain', label: 'Muscle Building' },
  { value: 'maintenance', label: 'Maintenance' },
  { value: 'energy', label: 'More Energy' },
  { value: 'health', label: 'Overall Health' }
]

const isFormComplete = computed(() => {
  return Object.values(formData.value).every(value => value !== '')
})

const handleSubmit = () => {
  if (isFormComplete.value && !props.loading) {
    emit('submit', formData.value)
  }
}

const fields = [
  { key: 'dietType', label: 'Diet Type', placeholder: 'Select diet type', icon: Utensils, iconColor: 'text-emerald-500', options: dietTypes },
  { key: 'mealsPerDay', label: 'Meals Per Day', placeholder: 'Select meals per day', icon: Calendar, iconColor: 'text-blue-500', options: mealsOptions },
  { key: 'servings', label: 'Number of Servings', placeholder: 'Select servings', icon: Users, iconColor: 'text-purple-500', options: servingsOptions },
  { key: 'restrictions', label: 'Dietary Restrictions', placeholder: 'Select restrictions', icon: Apple, iconColor: 'text-red-500', options: restrictionsOptions },
  { key: 'goal', label: 'Primary Goal', placeholder: 'Select your goal', icon: Target, iconColor: 'text-orange-500', options: goals }
] as const
</script>

<template>
  <div class="space-y-5">
    <!-- Form fields -->
    <div
      v-for="field in fields"
      :key="field.key"
      class="space-y-2"
    >
      <!-- Label with icon -->
      <div class="flex items-center gap-2">
        <component :is="field.icon" :class="['w-4 h-4', field.iconColor]" />
        <label class="text-sm font-medium text-foreground">{{ field.label }}</label>
      </div>

      <!-- Select -->
      <Select v-model="formData[field.key]" :disabled="loading">
        <SelectTrigger class="w-full h-12 rounded-xl border-input bg-background shadow-sm">
          <SelectValue :placeholder="field.placeholder" />
        </SelectTrigger>
        <SelectContent>
          <SelectItem
            v-for="option in field.options"
            :key="option.value"
            :value="option.value"
          >
            {{ option.label }}
          </SelectItem>
        </SelectContent>
      </Select>
    </div>

    <!-- Submit button -->
    <Button
      class="w-full h-14 rounded-2xl text-base font-medium bg-gradient-to-r from-emerald-500 to-emerald-600 hover:from-emerald-600 hover:to-emerald-700 text-white shadow-lg shadow-emerald-500/20 hover:shadow-emerald-500/30 transition-all duration-200 disabled:from-slate-300 disabled:to-slate-400 disabled:shadow-none"
      :disabled="!isFormComplete || loading"
      @click="handleSubmit"
    >
      <Loader2 v-if="loading" class="w-5 h-5 mr-2 animate-spin" />
      <Sparkles v-else class="w-5 h-5 mr-2" />
      Generate My Meal Plan
    </Button>
  </div>
</template>
