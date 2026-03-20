<script setup lang="ts">
import { ref } from 'vue'
import Input from '~/components/ui/input/Input.vue'
import Label from '~/components/ui/label/Label.vue'
import Textarea from '~/components/ui/textarea/Textarea.vue'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '~/components/ui/select'
import Badge from '~/components/ui/badge/Badge.vue'
import { X } from 'lucide-vue-next'

interface TrainingForm {
  trainingAge: string
  trainingHistory: string
  equipmentAccess: string
  daysPerWeek: number | null
  timePerSession: number | null
  injuries: string[]
  chronicConditions: string[]
  mobilityRestrictions: string[]
  doctorRestrictions: string
}

interface Props {
  formData: TrainingForm
}

const props = defineProps<Props>()
const emit = defineEmits<{
  'update:formData': [data: Partial<TrainingForm>]
}>()

const trainingAgeOptions = [
  { value: 'beginner', label: 'Beginner (0-1 year)' },
  { value: '1_year', label: '1 Year' },
  { value: '2_years', label: '2 Years' },
  { value: '3_5_years', label: '3-5 Years' },
  { value: '5_plus_years', label: '5+ Years' },
]

const equipmentOptions = [
  { value: 'full_gym', label: 'Full Gym Access' },
  { value: 'home_gym', label: 'Home Gym' },
  { value: 'basic_equipment', label: 'Basic Equipment (dumbbells, bands)' },
  { value: 'bodyweight_only', label: 'Bodyweight Only' },
]

const injuryInput = ref('')
const chronicConditionInput = ref('')
const mobilityRestrictionInput = ref('')

const updateField = (
  field: keyof TrainingForm,
  value: TrainingForm[keyof TrainingForm],
) => {
  emit('update:formData', { [field]: value })
}

const handleNumberInput = (field: keyof TrainingForm, value: string | number) => {
  const normalizedValue = value === '' ? '' : String(value)
  const numValue = normalizedValue === '' ? null : Number(normalizedValue)
  updateField(field, numValue)
}

const addItem = (field: 'injuries' | 'chronicConditions' | 'mobilityRestrictions', value: string) => {
  const next = value.trim()
  if (!next) return
  const current = props.formData[field]
  if (!current.includes(next)) {
    updateField(field, [...current, next])
  }
}

const removeItem = (field: 'injuries' | 'chronicConditions' | 'mobilityRestrictions', index: number) => {
  updateField(field, props.formData[field].filter((_, i) => i !== index))
}

const addInjury = () => {
  addItem('injuries', injuryInput.value)
  injuryInput.value = ''
}

const addChronicCondition = () => {
  addItem('chronicConditions', chronicConditionInput.value)
  chronicConditionInput.value = ''
}

const addMobilityRestriction = () => {
  addItem('mobilityRestrictions', mobilityRestrictionInput.value)
  mobilityRestrictionInput.value = ''
}
</script>

<template>
  <div>
    <h2 class="text-2xl font-semibold mb-2">Training Background</h2>
    <p class="text-muted-foreground mb-8">Tell us about your exercise background and limitations</p>

    <div class="space-y-6">
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div class="space-y-2">
          <Label for="trainingAge">Training Experience</Label>
          <Select
            :model-value="formData.trainingAge"
            @update:model-value="updateField('trainingAge', $event as string)"
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
            @update:model-value="updateField('equipmentAccess', $event as string)"
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
            min="0"
            max="14"
            :model-value="formData.daysPerWeek?.toString() ?? ''"
            @update:model-value="handleNumberInput('daysPerWeek', $event)"
            placeholder="5"
          />
        </div>

        <div class="space-y-2">
          <Label for="timePerSession">Time Per Session (minutes)</Label>
          <Input
            id="timePerSession"
            type="number"
            min="0"
            max="1440"
            :model-value="formData.timePerSession?.toString() ?? ''"
            @update:model-value="handleNumberInput('timePerSession', $event)"
            placeholder="75"
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
        <Label for="doctorRestrictions">Doctor Restrictions</Label>
        <Textarea
          id="doctorRestrictions"
          :model-value="formData.doctorRestrictions"
          @update:model-value="updateField('doctorRestrictions', $event)"
          placeholder="Any medical guidance or loading restrictions..."
          class="min-h-[80px]"
        />
      </div>

      <div class="space-y-2">
        <Label for="injuries">Injuries</Label>
        <div class="flex gap-2">
          <Input
            id="injuries"
            v-model="injuryInput"
            placeholder="Add an injury and press Enter"
            @keydown.enter.prevent="addInjury"
          />
          <button
            type="button"
            class="px-4 py-2 bg-primary text-primary-foreground rounded-md text-sm"
            @click="addInjury"
          >
            Add
          </button>
        </div>
        <div v-if="formData.injuries.length > 0" class="flex flex-wrap gap-2 mt-2">
          <Badge v-for="(item, index) in formData.injuries" :key="index" variant="secondary" class="gap-1">
            {{ item }}
            <button type="button" @click="removeItem('injuries', index)">
              <X class="w-3 h-3" />
            </button>
          </Badge>
        </div>
      </div>

      <div class="space-y-2">
        <Label for="chronicConditions">Chronic Conditions</Label>
        <div class="flex gap-2">
          <Input
            id="chronicConditions"
            v-model="chronicConditionInput"
            placeholder="Add a condition and press Enter"
            @keydown.enter.prevent="addChronicCondition"
          />
          <button
            type="button"
            class="px-4 py-2 bg-primary text-primary-foreground rounded-md text-sm"
            @click="addChronicCondition"
          >
            Add
          </button>
        </div>
        <div v-if="formData.chronicConditions.length > 0" class="flex flex-wrap gap-2 mt-2">
          <Badge v-for="(item, index) in formData.chronicConditions" :key="index" variant="secondary" class="gap-1">
            {{ item }}
            <button type="button" @click="removeItem('chronicConditions', index)">
              <X class="w-3 h-3" />
            </button>
          </Badge>
        </div>
      </div>

      <div class="space-y-2">
        <Label for="mobilityRestrictions">Mobility Restrictions</Label>
        <div class="flex gap-2">
          <Input
            id="mobilityRestrictions"
            v-model="mobilityRestrictionInput"
            placeholder="Add a restriction and press Enter"
            @keydown.enter.prevent="addMobilityRestriction"
          />
          <button
            type="button"
            class="px-4 py-2 bg-primary text-primary-foreground rounded-md text-sm"
            @click="addMobilityRestriction"
          >
            Add
          </button>
        </div>
        <div v-if="formData.mobilityRestrictions.length > 0" class="flex flex-wrap gap-2 mt-2">
          <Badge v-for="(item, index) in formData.mobilityRestrictions" :key="index" variant="secondary" class="gap-1">
            {{ item }}
            <button type="button" @click="removeItem('mobilityRestrictions', index)">
              <X class="w-3 h-3" />
            </button>
          </Badge>
        </div>
      </div>
    </div>
  </div>
</template>
