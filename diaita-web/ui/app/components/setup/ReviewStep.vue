<script setup lang="ts">
import ReviewCard from '~/components/setup/ReviewCard.vue'

interface Props {
  basicDemographics: Record<string, unknown>
  activityLifestyle: Record<string, unknown>
  goals: Record<string, unknown>
  trainingBackground: Record<string, unknown>
  nutritionHistory: Record<string, unknown>
}

defineProps<Props>()

const personalInfoFields = [
  { key: 'age', label: 'Age', suffix: 'years' },
  { key: 'height', label: 'Height', suffix: 'cm' },
  { key: 'weight', label: 'Weight', suffix: 'kg' },
]

const lifestyleFields = [
  { key: 'activityLevel', label: 'Activity Level' },
  { key: 'sleepDuration', label: 'Sleep Duration', suffix: 'hrs' },
  { key: 'stressLevel', label: 'Stress Level' },
]

const goalsFields = [
  { key: 'primaryGoal', label: 'Primary Goal' },
  { key: 'timeframe', label: 'Timeframe' },
]

const trainingFields = [
  { key: 'trainingAge', label: 'Experience' },
  { key: 'trainingHistory', label: 'Training History' },
  { key: 'equipmentAccess', label: 'Equipment' },
  { key: 'daysPerWeek', label: 'Days/Week' },
  { key: 'timePerSession', label: 'Time/Session', suffix: 'min' },
  { key: 'injuries', label: 'Injuries', type: 'badges' as const },
  { key: 'chronicConditions', label: 'Chronic Conditions', type: 'badges' as const },
  { key: 'mobilityRestrictions', label: 'Mobility Restrictions', type: 'badges' as const },
  { key: 'doctorRestrictions', label: 'Doctor Restrictions' },
]

const nutritionFields = [
  { key: 'currentDietPattern', label: 'Diet Pattern' },
  { key: 'eatingSchedule', label: 'Eating Schedule' },
  { key: 'foodAllergies', label: 'Allergies', type: 'badges' as const, badgeVariant: 'destructive' as const },
  { key: 'dietaryRestrictions', label: 'Dietary Restrictions', type: 'badges' as const },
]
</script>

<template>
  <div>
    <h2 class="text-2xl font-semibold mb-2">Review Your Profile</h2>
    <p class="text-muted-foreground mb-8">Please review your information before submitting</p>

    <div class="space-y-6">
      <ReviewCard
        title="Personal Information"
        :data="basicDemographics"
        :fields="personalInfoFields"
        show-empty
      />

      <ReviewCard
        title="Lifestyle & Activity"
        :data="activityLifestyle"
        :fields="lifestyleFields"
        show-empty
      />

      <ReviewCard
        title="Goals & Priorities"
        :data="goals"
        :fields="goalsFields"
        show-empty
      />

      <ReviewCard
        title="Training Background"
        :data="trainingBackground"
        :fields="trainingFields"
      />

      <ReviewCard
        title="Nutrition"
        :data="nutritionHistory"
        :fields="nutritionFields"
      />
    </div>
  </div>
</template>
