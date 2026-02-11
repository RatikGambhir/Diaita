<script setup lang="ts">
import { ref, computed } from 'vue'
import { Circle, Clock, Zap, Dumbbell, Target, Sparkles, Loader2 } from 'lucide-vue-next'
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
  workoutType: string
  duration: string
  fitnessLevel: string
  equipment: string
  primaryGoal: string
}

const formData = ref<FormData>({
  workoutType: '',
  duration: '',
  fitnessLevel: '',
  equipment: '',
  primaryGoal: ''
})

const workoutTypes = [
  { value: 'strength', label: 'Strength Training' },
  { value: 'cardio', label: 'Cardio' },
  { value: 'hiit', label: 'HIIT' },
  { value: 'yoga', label: 'Yoga' },
  { value: 'flexibility', label: 'Flexibility & Mobility' },
  { value: 'crossfit', label: 'CrossFit' }
]

const durations = [
  { value: '15', label: '15 minutes' },
  { value: '30', label: '30 minutes' },
  { value: '45', label: '45 minutes' },
  { value: '60', label: '60 minutes' },
  { value: '90', label: '90 minutes' }
]

const fitnessLevels = [
  { value: 'beginner', label: 'Beginner' },
  { value: 'intermediate', label: 'Intermediate' },
  { value: 'advanced', label: 'Advanced' },
  { value: 'athlete', label: 'Athlete' }
]

const equipmentOptions = [
  { value: 'none', label: 'No Equipment (Bodyweight)' },
  { value: 'minimal', label: 'Minimal (Dumbbells, Bands)' },
  { value: 'home-gym', label: 'Home Gym' },
  { value: 'full-gym', label: 'Full Gym Access' }
]

const goals = [
  { value: 'weight-loss', label: 'Weight Loss' },
  { value: 'muscle-gain', label: 'Muscle Gain' },
  { value: 'endurance', label: 'Build Endurance' },
  { value: 'flexibility', label: 'Improve Flexibility' },
  { value: 'general-fitness', label: 'General Fitness' },
  { value: 'stress-relief', label: 'Stress Relief' }
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
  { key: 'workoutType', label: 'Workout Type', placeholder: 'Select workout type', icon: Circle, iconColor: 'text-orange-500', options: workoutTypes },
  { key: 'duration', label: 'Duration', placeholder: 'Select duration', icon: Clock, iconColor: 'text-blue-500', options: durations },
  { key: 'fitnessLevel', label: 'Fitness Level', placeholder: 'Select fitness level', icon: Zap, iconColor: 'text-yellow-500', options: fitnessLevels },
  { key: 'equipment', label: 'Equipment Available', placeholder: 'Select equipment', icon: Dumbbell, iconColor: 'text-purple-500', options: equipmentOptions },
  { key: 'primaryGoal', label: 'Primary Goal', placeholder: 'Select your goal', icon: Target, iconColor: 'text-emerald-500', options: goals }
] as const
</script>

<template>
  <div class="space-y-5">
    <div
      v-for="field in fields"
      :key="field.key"
      class="space-y-2"
    >
      <div class="flex items-center gap-2">
        <component :is="field.icon" :class="['w-4 h-4', field.iconColor]" />
        <label class="text-sm font-medium text-foreground">{{ field.label }}</label>
      </div>

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

    <Button
      class="w-full h-14 rounded-2xl text-base font-medium bg-gradient-to-r from-orange-500 to-orange-600 hover:from-orange-600 hover:to-orange-700 text-white shadow-lg shadow-orange-500/20 hover:shadow-orange-500/30 transition-all duration-200 disabled:from-slate-300 disabled:to-slate-400 disabled:shadow-none"
      :disabled="!isFormComplete || loading"
      @click="handleSubmit"
    >
      <Loader2 v-if="loading" class="w-5 h-5 mr-2 animate-spin" />
      <Sparkles v-else class="w-5 h-5 mr-2" />
      Generate My Workout
    </Button>
  </div>
</template>
