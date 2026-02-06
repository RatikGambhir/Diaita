<script setup lang="ts">
import { ref } from 'vue'
import Input from '~/components/ui/input/Input.vue'
import Label from '~/components/ui/label/Label.vue'
import Textarea from '~/components/ui/textarea/Textarea.vue'
import Badge from '~/components/ui/badge/Badge.vue'
import { X, AlertCircle } from 'lucide-vue-next'

interface MedicalHistory {
  injuries: string[]
  chronicConditions: string[]
  painPatterns: string
  mobilityRestrictions: string[]
  medications: string[]
  doctorRestrictions: string
}

interface Props {
  formData: MedicalHistory
}

const props = defineProps<Props>()
const emit = defineEmits<{
  'update:formData': [data: Partial<MedicalHistory>]
}>()

const injuryInput = ref('')
const conditionInput = ref('')
const restrictionInput = ref('')
const medicationInput = ref('')

const updateField = (field: keyof MedicalHistory, value: any) => {
  emit('update:formData', { [field]: value })
}

const addInjury = () => {
  if (injuryInput.value.trim()) {
    updateField('injuries', [...props.formData.injuries, injuryInput.value.trim()])
    injuryInput.value = ''
  }
}

const removeInjury = (index: number) => {
  updateField('injuries', props.formData.injuries.filter((_, i) => i !== index))
}

const addCondition = () => {
  if (conditionInput.value.trim()) {
    updateField('chronicConditions', [...props.formData.chronicConditions, conditionInput.value.trim()])
    conditionInput.value = ''
  }
}

const removeCondition = (index: number) => {
  updateField('chronicConditions', props.formData.chronicConditions.filter((_, i) => i !== index))
}

const addRestriction = () => {
  if (restrictionInput.value.trim()) {
    updateField('mobilityRestrictions', [...props.formData.mobilityRestrictions, restrictionInput.value.trim()])
    restrictionInput.value = ''
  }
}

const removeRestriction = (index: number) => {
  updateField('mobilityRestrictions', props.formData.mobilityRestrictions.filter((_, i) => i !== index))
}

const addMedication = () => {
  if (medicationInput.value.trim()) {
    updateField('medications', [...props.formData.medications, medicationInput.value.trim()])
    medicationInput.value = ''
  }
}

const removeMedication = (index: number) => {
  updateField('medications', props.formData.medications.filter((_, i) => i !== index))
}
</script>

<template>
  <div>
    <h2 class="text-2xl font-semibold mb-2">Medical History</h2>
    <p class="text-muted-foreground mb-4">Help us understand any health considerations</p>

    <div class="flex items-start gap-2 p-4 mb-8 bg-muted/50 rounded-lg border">
      <AlertCircle class="w-5 h-5 text-muted-foreground mt-0.5 flex-shrink-0" />
      <p class="text-sm text-muted-foreground">
        This information helps us provide safer recommendations. All information is kept confidential.
        This step is optional - skip if you prefer not to share.
      </p>
    </div>

    <div class="space-y-6">
      <div class="space-y-2">
        <Label for="injuries">Past or Current Injuries</Label>
        <div class="flex gap-2">
          <Input
            id="injuries"
            v-model="injuryInput"
            placeholder="e.g., ACL tear, shoulder impingement"
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
          <Badge v-for="(injury, index) in formData.injuries" :key="index" variant="secondary" class="gap-1">
            {{ injury }}
            <button type="button" @click="removeInjury(index)">
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
            v-model="conditionInput"
            placeholder="e.g., Diabetes, Hypertension, Asthma"
            @keydown.enter.prevent="addCondition"
          />
          <button
            type="button"
            class="px-4 py-2 bg-primary text-primary-foreground rounded-md text-sm"
            @click="addCondition"
          >
            Add
          </button>
        </div>
        <div v-if="formData.chronicConditions.length > 0" class="flex flex-wrap gap-2 mt-2">
          <Badge v-for="(condition, index) in formData.chronicConditions" :key="index" variant="secondary" class="gap-1">
            {{ condition }}
            <button type="button" @click="removeCondition(index)">
              <X class="w-3 h-3" />
            </button>
          </Badge>
        </div>
      </div>

      <div class="space-y-2">
        <Label for="painPatterns">Pain Patterns</Label>
        <Textarea
          id="painPatterns"
          :model-value="formData.painPatterns"
          @update:model-value="updateField('painPatterns', $event)"
          placeholder="Describe any recurring pain or discomfort you experience..."
          class="min-h-[80px]"
        />
      </div>

      <div class="space-y-2">
        <Label for="mobilityRestrictions">Mobility Restrictions</Label>
        <div class="flex gap-2">
          <Input
            id="mobilityRestrictions"
            v-model="restrictionInput"
            placeholder="e.g., Limited shoulder ROM, Knee issues"
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
        <div v-if="formData.mobilityRestrictions.length > 0" class="flex flex-wrap gap-2 mt-2">
          <Badge v-for="(restriction, index) in formData.mobilityRestrictions" :key="index" variant="secondary" class="gap-1">
            {{ restriction }}
            <button type="button" @click="removeRestriction(index)">
              <X class="w-3 h-3" />
            </button>
          </Badge>
        </div>
      </div>

      <div class="space-y-2">
        <Label for="medications">Current Medications</Label>
        <div class="flex gap-2">
          <Input
            id="medications"
            v-model="medicationInput"
            placeholder="Add medications you're currently taking"
            @keydown.enter.prevent="addMedication"
          />
          <button
            type="button"
            class="px-4 py-2 bg-primary text-primary-foreground rounded-md text-sm"
            @click="addMedication"
          >
            Add
          </button>
        </div>
        <div v-if="formData.medications.length > 0" class="flex flex-wrap gap-2 mt-2">
          <Badge v-for="(medication, index) in formData.medications" :key="index" variant="secondary" class="gap-1">
            {{ medication }}
            <button type="button" @click="removeMedication(index)">
              <X class="w-3 h-3" />
            </button>
          </Badge>
        </div>
      </div>

      <div class="space-y-2">
        <Label for="doctorRestrictions">Doctor's Restrictions</Label>
        <Textarea
          id="doctorRestrictions"
          :model-value="formData.doctorRestrictions"
          @update:model-value="updateField('doctorRestrictions', $event)"
          placeholder="Any exercise or dietary restrictions from your doctor..."
          class="min-h-[80px]"
        />
      </div>
    </div>
  </div>
</template>
