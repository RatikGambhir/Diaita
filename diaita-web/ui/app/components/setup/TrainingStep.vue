<script setup lang="ts">
import { ref } from 'vue'
import Input from '~/components/ui/input/Input.vue'
import Label from '~/components/ui/label/Label.vue'
import Textarea from '~/components/ui/textarea/Textarea.vue'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '~/components/ui/select'
import Badge from '~/components/ui/badge/Badge.vue'
import { X } from 'lucide-vue-next'

interface TrainingBackground {
  trainingAge: string
  trainingHistory: string
  currentWorkoutRoutine: string
  exercisePreferences: string[]
  exerciseDislikes: string[]
  equipmentAccess: string
  timePerSession: number | null
  daysPerWeek: number | null
}

interface Props {
  formData: TrainingBackground
}

const props = defineProps<Props>()
const emit = defineEmits<{
  'update:formData': [data: Partial<TrainingBackground>]
}>()

const trainingAgeOptions = [
  { value: 'beginner', label: 'Beginner (0-1 year)' },
  { value: 'novice', label: 'Novice (1-2 years)' },
  { value: 'intermediate', label: 'Intermediate (2-5 years)' },
  { value: 'advanced', label: 'Advanced (5-10 years)' },
  { value: 'expert', label: 'Expert (10+ years)' },
]

const equipmentOptions = [
  { value: 'full_gym', label: 'Full Gym Access' },
  { value: 'home_gym', label: 'Home Gym' },
  { value: 'basic_equipment', label: 'Basic Equipment (dumbbells, bands)' },
  { value: 'bodyweight_only', label: 'Bodyweight Only' },
  { value: 'outdoor', label: 'Outdoor / Park' },
]

const exerciseOptions = [
  'Weightlifting',
  'Running',
  'Cycling',
  'Swimming',
  'HIIT',
  'Yoga',
  'Pilates',
  'CrossFit',
  'Calisthenics',
  'Martial Arts',
  'Dance',
  'Sports',
]

const preferenceInput = ref('')
const dislikeInput = ref('')

const updateField = (field: keyof TrainingBackground, value: any) => {
  emit('update:formData', { [field]: value })
}

const handleNumberInput = (field: keyof TrainingBackground, value: string) => {
  const numValue = value === '' ? null : Number(value)
  updateField(field, numValue)
}

const toggleExercisePreference = (exercise: string) => {
  const current = props.formData.exercisePreferences
  if (current.includes(exercise)) {
    updateField('exercisePreferences', current.filter(e => e !== exercise))
  } else {
    updateField('exercisePreferences', [...current, exercise])
  }
}

const addDislike = () => {
  if (dislikeInput.value.trim()) {
    const newDislikes = [...props.formData.exerciseDislikes, dislikeInput.value.trim()]
    updateField('exerciseDislikes', newDislikes)
    dislikeInput.value = ''
  }
}

const removeDislike = (index: number) => {
  const newDislikes = props.formData.exerciseDislikes.filter((_, i) => i !== index)
  updateField('exerciseDislikes', newDislikes)
}
</script>

<template>
  <div>
    <h2 class="text-2xl font-semibold mb-2">Training Background</h2>
    <p class="text-muted-foreground mb-8">Tell us about your exercise experience and preferences</p>

    <div class="space-y-6">
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div class="space-y-2">
          <Label for="trainingAge">Training Experience</Label>
          <Select
            :model-value="formData.trainingAge"
            @update:model-value="updateField('trainingAge', $event)"
          >
            <SelectTrigger>
              <SelectValue placeholder="Select experience level" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem v-for="option in trainingAgeOptions" :key="option.value" :value="option.value">
                {{ option.label }}
              </SelectItem>
            </SelectContent>
          </Select>
        </div>

        <div class="space-y-2">
          <Label for="equipmentAccess">Equipment Access</Label>
          <Select
            :model-value="formData.equipmentAccess"
            @update:model-value="updateField('equipmentAccess', $event)"
          >
            <SelectTrigger>
              <SelectValue placeholder="Select equipment access" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem v-for="option in equipmentOptions" :key="option.value" :value="option.value">
                {{ option.label }}
              </SelectItem>
            </SelectContent>
          </Select>
        </div>

        <div class="space-y-2">
          <Label for="daysPerWeek">Days Per Week</Label>
          <Input
            id="daysPerWeek"
            type="number"
            min="1"
            max="7"
            :model-value="formData.daysPerWeek?.toString() ?? ''"
            @update:model-value="handleNumberInput('daysPerWeek', $event)"
            placeholder="4"
          />
        </div>

        <div class="space-y-2">
          <Label for="timePerSession">Time Per Session (minutes)</Label>
          <Input
            id="timePerSession"
            type="number"
            min="10"
            max="300"
            :model-value="formData.timePerSession?.toString() ?? ''"
            @update:model-value="handleNumberInput('timePerSession', $event)"
            placeholder="60"
          />
        </div>
      </div>

      <div class="space-y-2">
        <Label for="trainingHistory">Training History</Label>
        <Textarea
          id="trainingHistory"
          :model-value="formData.trainingHistory"
          @update:model-value="updateField('trainingHistory', $event)"
          placeholder="Describe your training background and experience..."
          class="min-h-[80px]"
        />
      </div>

      <div class="space-y-2">
        <Label for="currentWorkoutRoutine">Current Workout Routine</Label>
        <Textarea
          id="currentWorkoutRoutine"
          :model-value="formData.currentWorkoutRoutine"
          @update:model-value="updateField('currentWorkoutRoutine', $event)"
          placeholder="Describe your current workout routine if any..."
          class="min-h-[80px]"
        />
      </div>

      <div class="space-y-2">
        <Label>Exercise Preferences</Label>
        <p class="text-sm text-muted-foreground mb-2">Select activities you enjoy</p>
        <div class="flex flex-wrap gap-2">
          <button
            v-for="exercise in exerciseOptions"
            :key="exercise"
            type="button"
            :class="[
              'px-3 py-1.5 rounded-full text-sm border transition-colors',
              formData.exercisePreferences.includes(exercise)
                ? 'bg-primary text-primary-foreground border-primary'
                : 'bg-background text-foreground border-border hover:bg-accent'
            ]"
            @click="toggleExercisePreference(exercise)"
          >
            {{ exercise }}
          </button>
        </div>
      </div>

      <div class="space-y-2">
        <Label for="exerciseDislikes">Exercise Dislikes</Label>
        <div class="flex gap-2">
          <Input
            id="exerciseDislikes"
            v-model="dislikeInput"
            placeholder="Add exercises you dislike"
            @keydown.enter.prevent="addDislike"
          />
          <button
            type="button"
            class="px-4 py-2 bg-primary text-primary-foreground rounded-md text-sm"
            @click="addDislike"
          >
            Add
          </button>
        </div>
        <div v-if="formData.exerciseDislikes.length > 0" class="flex flex-wrap gap-2 mt-2">
          <Badge v-for="(dislike, index) in formData.exerciseDislikes" :key="index" variant="secondary" class="gap-1">
            {{ dislike }}
            <button type="button" @click="removeDislike(index)">
              <X class="w-3 h-3" />
            </button>
          </Badge>
        </div>
      </div>
    </div>
  </div>
</template>
