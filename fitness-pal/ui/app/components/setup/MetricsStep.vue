<script setup lang="ts">
import Label from '~/components/ui/label/Label.vue'
import Textarea from '~/components/ui/textarea/Textarea.vue'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '~/components/ui/select'
import { AlertCircle } from 'lucide-vue-next'

interface MetricsTracking {
  preferredProgressMetrics: string[]
  trackingTools: string[]
  checkinFrequency: string
}

interface Props {
  formData: MetricsTracking
  notes: string
}

const props = defineProps<Props>()
const emit = defineEmits<{
  'update:formData': [data: Partial<MetricsTracking>]
  'update:notes': [value: string]
}>()

const progressMetricOptions = [
  'Scale weight',
  'Body measurements',
  'Progress photos',
  'Strength gains',
  'Endurance improvements',
  'Energy levels',
  'Sleep quality',
  'Mood/wellbeing',
  'Clothing fit',
]

const trackingToolOptions = [
  'Apple Watch',
  'Fitbit',
  'Garmin',
  'MyFitnessPal',
  'Cronometer',
  'Strong (workout app)',
  'Strava',
  'Whoop',
  'Oura Ring',
  'None',
]

const frequencyOptions = [
  { value: 'daily', label: 'Daily' },
  { value: 'weekly', label: 'Weekly' },
  { value: 'biweekly', label: 'Every 2 weeks' },
  { value: 'monthly', label: 'Monthly' },
]

const updateField = (field: keyof MetricsTracking, value: any) => {
  emit('update:formData', { [field]: value })
}

const toggleMetric = (metric: string) => {
  const current = props.formData.preferredProgressMetrics
  if (current.includes(metric)) {
    updateField('preferredProgressMetrics', current.filter(m => m !== metric))
  } else {
    updateField('preferredProgressMetrics', [...current, metric])
  }
}

const toggleTool = (tool: string) => {
  const current = props.formData.trackingTools
  if (current.includes(tool)) {
    updateField('trackingTools', current.filter(t => t !== tool))
  } else {
    updateField('trackingTools', [...current, tool])
  }
}
</script>

<template>
  <div>
    <h2 class="text-2xl font-semibold mb-2">Metrics & Tracking</h2>
    <p class="text-muted-foreground mb-4">How would you like to track your progress?</p>

    <div class="flex items-start gap-2 p-4 mb-8 bg-muted/50 rounded-lg border">
      <AlertCircle class="w-5 h-5 text-muted-foreground mt-0.5 flex-shrink-0" />
      <p class="text-sm text-muted-foreground">
        This step is optional. Understanding your tracking preferences helps us provide relevant check-ins.
      </p>
    </div>

    <div class="space-y-6">
      <div class="space-y-2">
        <Label>Preferred Progress Metrics</Label>
        <p class="text-sm text-muted-foreground mb-2">How do you prefer to measure progress?</p>
        <div class="flex flex-wrap gap-2">
          <button
            v-for="metric in progressMetricOptions"
            :key="metric"
            type="button"
            :class="[
              'px-3 py-1.5 rounded-full text-sm border transition-colors',
              formData.preferredProgressMetrics.includes(metric)
                ? 'bg-primary text-primary-foreground border-primary'
                : 'bg-background text-foreground border-border hover:bg-accent'
            ]"
            @click="toggleMetric(metric)"
          >
            {{ metric }}
          </button>
        </div>
      </div>

      <div class="space-y-2">
        <Label>Tracking Tools You Use</Label>
        <p class="text-sm text-muted-foreground mb-2">Select any apps or devices you currently use</p>
        <div class="flex flex-wrap gap-2">
          <button
            v-for="tool in trackingToolOptions"
            :key="tool"
            type="button"
            :class="[
              'px-3 py-1.5 rounded-full text-sm border transition-colors',
              formData.trackingTools.includes(tool)
                ? 'bg-primary text-primary-foreground border-primary'
                : 'bg-background text-foreground border-border hover:bg-accent'
            ]"
            @click="toggleTool(tool)"
          >
            {{ tool }}
          </button>
        </div>
      </div>

      <div class="space-y-2">
        <Label for="checkinFrequency">Preferred Check-in Frequency</Label>
        <Select
          :model-value="formData.checkinFrequency"
          @update:model-value="updateField('checkinFrequency', $event)"
        >
          <SelectTrigger class="w-full md:w-1/2">
            <SelectValue placeholder="How often would you like to check in?" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem v-for="option in frequencyOptions" :key="option.value" :value="option.value">
              {{ option.label }}
            </SelectItem>
          </SelectContent>
        </Select>
      </div>

      <div class="space-y-2 pt-4 border-t">
        <Label for="notes">Additional Notes</Label>
        <Textarea
          id="notes"
          :model-value="notes"
          @update:model-value="emit('update:notes', $event)"
          placeholder="Anything else you'd like us to know about your goals, preferences, or situation..."
          class="min-h-[120px]"
        />
      </div>
    </div>
  </div>
</template>
