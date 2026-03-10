<script setup lang="ts">
import ReviewCard from '~/components/setup/ReviewCard.vue'

interface Props {
  basicDemographics: Record<string, any>
  activityLifestyle: Record<string, any>
  goals: Record<string, any>
  trainingBackground: Record<string, any>
  nutritionHistory: Record<string, any>
}

const props = defineProps<Props>()

// Field configurations for each section
const personalInfoFields = [
  { key: 'age', label: 'Age', suffix: 'years' },
  { key: 'height', label: 'Height', suffix: 'in' },
  { key: 'weight', label: 'Weight', suffix: 'lbs' },
  { key: 'sex', label: 'Sex' },
  { key: 'gender', label: 'Gender' },
  { key: 'bodyFatPercentage', label: 'Body Fat %' },
  { key: 'leanMass', label: 'Lean Mass', suffix: 'lbs' },
]

const lifestyleFields = [
  { key: 'activityLevel', label: 'Activity Level' },
  { key: 'dailyStepCount', label: 'Daily Steps' },
  { key: 'jobType', label: 'Job Type' },
  { key: 'sleepDuration', label: 'Sleep Duration', suffix: 'hrs' },
  { key: 'sleepQuality', label: 'Sleep Quality' },
  { key: 'stressLevel', label: 'Stress Level' },
  { key: 'recoveryCapacity', label: 'Recovery' },
]

const goalsFields = [
  { key: 'primaryGoal', label: 'Primary Goal' },
  { key: 'timeframe', label: 'Timeframe' },
  { key: 'targetWeight', label: 'Target Weight', suffix: 'lbs' },
  { key: 'performanceMetric', label: 'Performance Metric' },
  { key: 'secondaryGoals', label: 'Secondary Goals', type: 'badges' as const },
  { key: 'healthGoals', label: 'Health Goals', type: 'badges' as const, badgeVariant: 'outline' as const },
]

const trainingFields = [
  { key: 'trainingAge', label: 'Experience' },
  { key: 'equipmentAccess', label: 'Equipment' },
  { key: 'daysPerWeek', label: 'Days/Week' },
  { key: 'timePerSession', label: 'Time/Session', suffix: 'min' },
  { key: 'exercisePreferences', label: 'Preferred Exercises', type: 'badges' as const },
  { key: 'exerciseDislikes', label: 'Dislikes', type: 'badges' as const, badgeVariant: 'outline' as const },
]

const nutritionFields = [
  { key: 'currentDietPattern', label: 'Diet Pattern' },
  { key: 'cookingSkillLevel', label: 'Cooking Skill' },
  { key: 'foodBudget', label: 'Budget' },
  { key: 'alcoholIntake', label: 'Alcohol' },
  { key: 'calorieTrackingExperience', label: 'Tracked Calories Before', type: 'boolean' as const },
  { key: 'foodAllergies', label: 'Allergies', type: 'badges' as const, badgeVariant: 'destructive' as const },
  { key: 'dietaryRestrictions', label: 'Dietary Restrictions', type: 'badges' as const },
  { key: 'supplementUse', label: 'Supplements', type: 'badges' as const, badgeVariant: 'outline' as const },
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
      <!-- TODO: Re-add the deferred medical, behavioral, metrics, and notes review sections when those features return. -->
    </div>
  </div>
</template>
