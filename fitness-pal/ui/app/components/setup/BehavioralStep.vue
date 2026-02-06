<script setup lang="ts">
import Label from '~/components/ui/label/Label.vue'
import Textarea from '~/components/ui/textarea/Textarea.vue'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '~/components/ui/select'
import { AlertCircle } from 'lucide-vue-next'

interface BehavioralFactors {
  motivationLevel: string
  consistencyHistory: string
  accountabilityPreference: string
  pastSuccessFailurePatterns: string
  relationshipWithFood: string
  disorderedEatingHistory: string
  stressEatingTendencies: string
  supportSystem: string
}

interface Props {
  formData: BehavioralFactors
}

const props = defineProps<Props>()
const emit = defineEmits<{
  'update:formData': [data: Partial<BehavioralFactors>]
}>()

const motivationOptions = [
  { value: 'very_high', label: 'Very High - Extremely motivated' },
  { value: 'high', label: 'High - Ready to commit' },
  { value: 'moderate', label: 'Moderate - Willing but need support' },
  { value: 'low', label: 'Low - Struggling to start' },
]

const consistencyOptions = [
  { value: 'excellent', label: 'Excellent - Very consistent' },
  { value: 'good', label: 'Good - Mostly consistent' },
  { value: 'fair', label: 'Fair - Inconsistent at times' },
  { value: 'poor', label: 'Poor - Often fall off track' },
]

const accountabilityOptions = [
  { value: 'self', label: 'Self-motivated' },
  { value: 'partner', label: 'Workout partner / Friend' },
  { value: 'coach', label: 'Coach / Trainer' },
  { value: 'group', label: 'Group / Community' },
  { value: 'app', label: 'App / Technology' },
]

const supportOptions = [
  { value: 'strong', label: 'Strong - Family/friends are very supportive' },
  { value: 'moderate', label: 'Moderate - Some support available' },
  { value: 'limited', label: 'Limited - Little support from others' },
  { value: 'none', label: 'None - No support system' },
]

const updateField = (field: keyof BehavioralFactors, value: any) => {
  emit('update:formData', { [field]: value })
}
</script>

<template>
  <div>
    <h2 class="text-2xl font-semibold mb-2">Behavioral Factors</h2>
    <p class="text-muted-foreground mb-4">Understanding your habits helps us create better strategies</p>

    <div class="flex items-start gap-2 p-4 mb-8 bg-muted/50 rounded-lg border">
      <AlertCircle class="w-5 h-5 text-muted-foreground mt-0.5 flex-shrink-0" />
      <p class="text-sm text-muted-foreground">
        This step is optional but helps us provide more personalized support.
        Your responses are confidential.
      </p>
    </div>

    <div class="space-y-6">
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div class="space-y-2">
          <Label for="motivationLevel">Current Motivation Level</Label>
          <Select
            :model-value="formData.motivationLevel"
            @update:model-value="updateField('motivationLevel', $event)"
          >
            <SelectTrigger>
              <SelectValue placeholder="Select motivation level" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem v-for="option in motivationOptions" :key="option.value" :value="option.value">
                {{ option.label }}
              </SelectItem>
            </SelectContent>
          </Select>
        </div>

        <div class="space-y-2">
          <Label for="consistencyHistory">Consistency History</Label>
          <Select
            :model-value="formData.consistencyHistory"
            @update:model-value="updateField('consistencyHistory', $event)"
          >
            <SelectTrigger>
              <SelectValue placeholder="How consistent have you been?" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem v-for="option in consistencyOptions" :key="option.value" :value="option.value">
                {{ option.label }}
              </SelectItem>
            </SelectContent>
          </Select>
        </div>

        <div class="space-y-2">
          <Label for="accountabilityPreference">Accountability Preference</Label>
          <Select
            :model-value="formData.accountabilityPreference"
            @update:model-value="updateField('accountabilityPreference', $event)"
          >
            <SelectTrigger>
              <SelectValue placeholder="What keeps you accountable?" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem v-for="option in accountabilityOptions" :key="option.value" :value="option.value">
                {{ option.label }}
              </SelectItem>
            </SelectContent>
          </Select>
        </div>

        <div class="space-y-2">
          <Label for="supportSystem">Support System</Label>
          <Select
            :model-value="formData.supportSystem"
            @update:model-value="updateField('supportSystem', $event)"
          >
            <SelectTrigger>
              <SelectValue placeholder="Select support level" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem v-for="option in supportOptions" :key="option.value" :value="option.value">
                {{ option.label }}
              </SelectItem>
            </SelectContent>
          </Select>
        </div>
      </div>

      <div class="space-y-2">
        <Label for="pastSuccessFailurePatterns">Past Success/Failure Patterns</Label>
        <Textarea
          id="pastSuccessFailurePatterns"
          :model-value="formData.pastSuccessFailurePatterns"
          @update:model-value="updateField('pastSuccessFailurePatterns', $event)"
          placeholder="What has worked or not worked for you in the past?"
          class="min-h-[80px]"
        />
      </div>

      <div class="space-y-2">
        <Label for="relationshipWithFood">Relationship With Food</Label>
        <Textarea
          id="relationshipWithFood"
          :model-value="formData.relationshipWithFood"
          @update:model-value="updateField('relationshipWithFood', $event)"
          placeholder="Describe your relationship with food..."
          class="min-h-[80px]"
        />
      </div>

      <div class="space-y-2">
        <Label for="stressEatingTendencies">Stress Eating Tendencies</Label>
        <Textarea
          id="stressEatingTendencies"
          :model-value="formData.stressEatingTendencies"
          @update:model-value="updateField('stressEatingTendencies', $event)"
          placeholder="Do you tend to eat when stressed? Describe your patterns..."
          class="min-h-[80px]"
        />
      </div>

      <div class="space-y-2">
        <Label for="disorderedEatingHistory">History of Eating Concerns</Label>
        <Textarea
          id="disorderedEatingHistory"
          :model-value="formData.disorderedEatingHistory"
          @update:model-value="updateField('disorderedEatingHistory', $event)"
          placeholder="Any history of disordered eating or eating disorders? (optional)"
          class="min-h-[80px]"
        />
        <p class="text-xs text-muted-foreground">
          This helps us provide appropriate recommendations. If you're currently struggling, please seek professional support.
        </p>
      </div>
    </div>
  </div>
</template>
