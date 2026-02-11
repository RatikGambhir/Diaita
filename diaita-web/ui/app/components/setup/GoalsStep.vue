<script setup lang="ts">
import { ref, watch } from 'vue'
import Input from '~/components/ui/input/Input.vue'
import Label from '~/components/ui/label/Label.vue'
import Textarea from '~/components/ui/textarea/Textarea.vue'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '~/components/ui/select'
import Badge from '~/components/ui/badge/Badge.vue'
import { X } from 'lucide-vue-next'

interface GoalsPriorities {
  primaryGoal: string
  secondaryGoals: string[]
  timeframe: string
  targetWeight: number | null
  performanceMetric: string
  aestheticGoals: string
  healthGoals: string[]
}

interface Props {
  formData: GoalsPriorities
}

const props = defineProps<Props>()
const emit = defineEmits<{
  'update:formData': [data: Partial<GoalsPriorities>]
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
  { value: '1_month', label: '1 Month' },
  { value: '3_months', label: '3 Months' },
  { value: '6_months', label: '6 Months' },
  { value: '1_year', label: '1 Year' },
  { value: 'ongoing', label: 'Ongoing / Lifestyle' },
]

const healthGoalOptions = [
  'Lower cholesterol',
  'Reduce blood pressure',
  'Improve blood sugar',
  'Better sleep',
  'More energy',
  'Reduce stress',
  'Improve mobility',
  'Heart health',
]

const secondaryGoalInput = ref('')
const healthGoalInput = ref('')

const updateField = (field: keyof GoalsPriorities, value: any) => {
  emit('update:formData', { [field]: value })
}

const handleNumberInput = (field: keyof GoalsPriorities, value: string) => {
  const numValue = value === '' ? null : Number(value)
  updateField(field, numValue)
}

const addSecondaryGoal = () => {
  if (secondaryGoalInput.value.trim()) {
    const newGoals = [...props.formData.secondaryGoals, secondaryGoalInput.value.trim()]
    updateField('secondaryGoals', newGoals)
    secondaryGoalInput.value = ''
  }
}

const removeSecondaryGoal = (index: number) => {
  const newGoals = props.formData.secondaryGoals.filter((_, i) => i !== index)
  updateField('secondaryGoals', newGoals)
}

const toggleHealthGoal = (goal: string) => {
  const current = props.formData.healthGoals
  if (current.includes(goal)) {
    updateField('healthGoals', current.filter(g => g !== goal))
  } else {
    updateField('healthGoals', [...current, goal])
  }
}
</script>

<template>
  <div>
    <h2 class="text-2xl font-semibold mb-2">Goals & Priorities</h2>
    <p class="text-muted-foreground mb-8">What are you looking to achieve?</p>

    <div class="space-y-6">
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div class="space-y-2">
          <Label for="primaryGoal">
            Primary Goal <span class="text-destructive">*</span>
          </Label>
          <Select
            :model-value="formData.primaryGoal"
            @update:model-value="updateField('primaryGoal', $event)"
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
            @update:model-value="updateField('timeframe', $event)"
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

        <div class="space-y-2">
          <Label for="targetWeight">Target Weight (lbs)</Label>
          <Input
            id="targetWeight"
            type="number"
            min="50"
            max="700"
            step="0.1"
            :model-value="formData.targetWeight?.toString() ?? ''"
            @update:model-value="handleNumberInput('targetWeight', $event)"
            placeholder="155"
          />
        </div>

        <div class="space-y-2">
          <Label for="performanceMetric">Performance Metric</Label>
          <Input
            id="performanceMetric"
            :model-value="formData.performanceMetric"
            @update:model-value="updateField('performanceMetric', $event)"
            placeholder="e.g., Run 5K in 25 min, Bench press 100kg"
          />
        </div>
      </div>

      <div class="space-y-2">
        <Label for="secondaryGoals">Secondary Goals</Label>
        <div class="flex gap-2">
          <Input
            id="secondaryGoals"
            v-model="secondaryGoalInput"
            placeholder="Add a secondary goal and press Enter"
            @keydown.enter.prevent="addSecondaryGoal"
          />
          <button
            type="button"
            class="px-4 py-2 bg-primary text-primary-foreground rounded-md text-sm"
            @click="addSecondaryGoal"
          >
            Add
          </button>
        </div>
        <div v-if="formData.secondaryGoals.length > 0" class="flex flex-wrap gap-2 mt-2">
          <Badge v-for="(goal, index) in formData.secondaryGoals" :key="index" variant="secondary" class="gap-1">
            {{ goal }}
            <button type="button" @click="removeSecondaryGoal(index)">
              <X class="w-3 h-3" />
            </button>
          </Badge>
        </div>
      </div>

      <div class="space-y-2">
        <Label for="aestheticGoals">Aesthetic Goals</Label>
        <Textarea
          id="aestheticGoals"
          :model-value="formData.aestheticGoals"
          @update:model-value="updateField('aestheticGoals', $event)"
          placeholder="Describe any body composition or appearance goals..."
          class="min-h-[80px]"
        />
      </div>

      <div class="space-y-2">
        <Label>Health Goals</Label>
        <p class="text-sm text-muted-foreground mb-2">Select all that apply</p>
        <div class="flex flex-wrap gap-2">
          <button
            v-for="goal in healthGoalOptions"
            :key="goal"
            type="button"
            :class="[
              'px-3 py-1.5 rounded-full text-sm border transition-colors',
              formData.healthGoals.includes(goal)
                ? 'bg-primary text-primary-foreground border-primary'
                : 'bg-background text-foreground border-border hover:bg-accent'
            ]"
            @click="toggleHealthGoal(goal)"
          >
            {{ goal }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
