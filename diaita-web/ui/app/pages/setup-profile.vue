<script setup lang="ts">
import { ref, computed } from 'vue'
import { User, Activity, Target, Apple, Dumbbell, Heart, Brain, BarChart3, FileCheck, ChevronLeft, ChevronRight } from 'lucide-vue-next'
import Button from '~/components/ui/button/Button.vue'
import Badge from '~/components/ui/badge/Badge.vue'
import { userApi, type RegisterUserProfileRequest } from '~/api/user'
import { useUserStore } from '~/stores/useUserStore'
import PersonalInfoStep from '~/components/setup/PersonalInfoStep.vue'
import LifestyleStep from '~/components/setup/LifestyleStep.vue'
import GoalsStep from '~/components/setup/GoalsStep.vue'
import TrainingStep from '~/components/setup/TrainingStep.vue'
import MedicalStep from '~/components/setup/MedicalStep.vue'
import NutritionStep from '~/components/setup/NutritionStep.vue'
import BehavioralStep from '~/components/setup/BehavioralStep.vue'
import MetricsStep from '~/components/setup/MetricsStep.vue'
import ReviewStep from '~/components/setup/ReviewStep.vue'
import LoadingScreen from '~/components/ui/LoadingScreen.vue'
import { cn } from '~/lib/utils'

definePageMeta({
  layout: false,
})

// Types matching backend entities
interface BasicDemographics {
  age: number | null
  sex: string
  gender: string
  height: number | null
  weight: number | null
  bodyFatPercentage: number | null
  leanMass: number | null
  biologicalConsiderations: string
  menstrualCycleInfo: string
}

interface ActivityLifestyle {
  activityLevel: string
  dailyStepCount: number | null
  jobType: string
  commuteTime: string
  sleepDuration: number | null
  sleepQuality: string
  stressLevel: string
  recoveryCapacity: string
}

interface GoalsPriorities {
  primaryGoal: string
  secondaryGoals: string[]
  timeframe: string
  targetWeight: number | null
  performanceMetric: string
  aestheticGoals: string
  healthGoals: string[]
}

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

interface MedicalHistory {
  injuries: string[]
  chronicConditions: string[]
  painPatterns: string
  mobilityRestrictions: string[]
  medications: string[]
  doctorRestrictions: string
}

interface NutritionHistory {
  currentDietPattern: string
  calorieTrackingExperience: boolean | null
  macronutrientPreferences: string
  foodAllergies: string[]
  dietaryRestrictions: string[]
  culturalFoodPreferences: string
  cookingSkillLevel: string
  foodBudget: string
  eatingSchedule: string
  snackingHabits: string
  alcoholIntake: string
  supplementUse: string[]
}

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

interface MetricsTracking {
  preferredProgressMetrics: string[]
  trackingTools: string[]
  checkinFrequency: string
}

const steps = [
  { id: 1, title: 'Personal Info', description: 'Basic demographics', icon: User, required: true },
  { id: 2, title: 'Lifestyle', description: 'Activity & habits', icon: Activity, required: true },
  { id: 3, title: 'Goals', description: 'Your objectives', icon: Target, required: true },
  { id: 4, title: 'Training', description: 'Exercise background', icon: Dumbbell, required: false },
  { id: 5, title: 'Nutrition', description: 'Diet history', icon: Apple, required: false },
  { id: 6, title: 'Medical', description: 'Health considerations (optional)', icon: Heart, required: false, skippable: true },
  { id: 7, title: 'Behavioral', description: 'Habits & mindset (optional)', icon: Brain, required: false, skippable: true },
  { id: 8, title: 'Metrics', description: 'Progress tracking (optional)', icon: BarChart3, required: false, skippable: true },
  { id: 9, title: 'Review', description: 'Final check', icon: FileCheck, required: true },
]

const currentStep = ref(1)
const isSubmitting = ref(false)
const toast = useToast()
const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// Form data matching backend entities
const basicDemographics = ref<BasicDemographics>({
  age: null,
  sex: '',
  gender: '',
  height: null,
  weight: null,
  bodyFatPercentage: null,
  leanMass: null,
  biologicalConsiderations: '',
  menstrualCycleInfo: '',
})

const activityLifestyle = ref<ActivityLifestyle>({
  activityLevel: '',
  dailyStepCount: null,
  jobType: '',
  commuteTime: '',
  sleepDuration: null,
  sleepQuality: '',
  stressLevel: '',
  recoveryCapacity: '',
})

const goals = ref<GoalsPriorities>({
  primaryGoal: '',
  secondaryGoals: [],
  timeframe: '',
  targetWeight: null,
  performanceMetric: '',
  aestheticGoals: '',
  healthGoals: [],
})

const trainingBackground = ref<TrainingBackground>({
  trainingAge: '',
  trainingHistory: '',
  currentWorkoutRoutine: '',
  exercisePreferences: [],
  exerciseDislikes: [],
  equipmentAccess: '',
  timePerSession: null,
  daysPerWeek: null,
})

const medicalHistory = ref<MedicalHistory>({
  injuries: [],
  chronicConditions: [],
  painPatterns: '',
  mobilityRestrictions: [],
  medications: [],
  doctorRestrictions: '',
})

const nutritionHistory = ref<NutritionHistory>({
  currentDietPattern: '',
  calorieTrackingExperience: null,
  macronutrientPreferences: '',
  foodAllergies: [],
  dietaryRestrictions: [],
  culturalFoodPreferences: '',
  cookingSkillLevel: '',
  foodBudget: '',
  eatingSchedule: '',
  snackingHabits: '',
  alcoholIntake: '',
  supplementUse: [],
})

const behavioralFactors = ref<BehavioralFactors>({
  motivationLevel: '',
  consistencyHistory: '',
  accountabilityPreference: '',
  pastSuccessFailurePatterns: '',
  relationshipWithFood: '',
  disorderedEatingHistory: '',
  stressEatingTendencies: '',
  supportSystem: '',
})

const metricsTracking = ref<MetricsTracking>({
  preferredProgressMetrics: [],
  trackingTools: [],
  checkinFrequency: '',
})

const notes = ref('')

// Validation for required fields
const isStep1Valid = computed(() => {
  return basicDemographics.value.age !== null &&
    basicDemographics.value.age > 0 &&
    basicDemographics.value.height !== null &&
    basicDemographics.value.height > 0 &&
    basicDemographics.value.weight !== null &&
    basicDemographics.value.weight > 0
})

const isStep2Valid = computed(() => {
  return activityLifestyle.value.activityLevel !== ''
})

const isStep3Valid = computed(() => {
  return goals.value.primaryGoal !== ''
})

const handleNext = () => {
  if (currentStep.value < steps.length) {
    currentStep.value++
  }
}

const handleBack = () => {
  if (currentStep.value > 1) {
    currentStep.value--
  }
}

const handleSubmit = async () => {
  const currentUser = userStore.getUser as { id?: string } | null
  const userId = currentUser?.id

  // if (!userId) {
  //   toast.add({
  //     title: 'Missing user session',
  //     description: 'Please log in again before saving your profile.',
  //     color: 'error',
  //   })
  //   return
  // }

  const payload: RegisterUserProfileRequest = {
    id: crypto.randomUUID(),
    userId: "39183c95-ccbb-4cd0-9a6a-328327a39d1c",
    basicDemographics: {
      age: basicDemographics.value.age!,
      sex: basicDemographics.value.sex || null,
      gender: basicDemographics.value.gender || null,
      height: basicDemographics.value.height!,
      weight: basicDemographics.value.weight!,
      bodyFatPercentage: basicDemographics.value.bodyFatPercentage,
      leanMass: basicDemographics.value.leanMass,
      biologicalConsiderations: basicDemographics.value.biologicalConsiderations || null,
      menstrualCycleInfo: basicDemographics.value.menstrualCycleInfo || null,
    },
    activityLifestyle: {
      activityLevel: activityLifestyle.value.activityLevel,
      dailyStepCount: activityLifestyle.value.dailyStepCount,
      jobType: activityLifestyle.value.jobType || null,
      commuteTime: activityLifestyle.value.commuteTime || null,
      sleepDuration: activityLifestyle.value.sleepDuration,
      sleepQuality: activityLifestyle.value.sleepQuality || null,
      stressLevel: activityLifestyle.value.stressLevel || null,
      recoveryCapacity: activityLifestyle.value.recoveryCapacity || null,
    },
    goals: {
      primaryGoal: goals.value.primaryGoal,
      secondaryGoals: goals.value.secondaryGoals.length > 0 ? goals.value.secondaryGoals : null,
      timeframe: goals.value.timeframe || null,
      targetWeight: goals.value.targetWeight,
      performanceMetric: goals.value.performanceMetric || null,
      aestheticGoals: goals.value.aestheticGoals || null,
      healthGoals: goals.value.healthGoals.length > 0 ? goals.value.healthGoals : null,
    },
    trainingBackground: {
      trainingAge: trainingBackground.value.trainingAge || null,
      trainingHistory: trainingBackground.value.trainingHistory || null,
      currentWorkoutRoutine: trainingBackground.value.currentWorkoutRoutine || null,
      exercisePreferences: trainingBackground.value.exercisePreferences.length > 0 ? trainingBackground.value.exercisePreferences : null,
      exerciseDislikes: trainingBackground.value.exerciseDislikes.length > 0 ? trainingBackground.value.exerciseDislikes : null,
      equipmentAccess: trainingBackground.value.equipmentAccess || null,
      timePerSession: trainingBackground.value.timePerSession,
      daysPerWeek: trainingBackground.value.daysPerWeek,
    },
    medicalHistory: {
      injuries: medicalHistory.value.injuries.length > 0 ? medicalHistory.value.injuries : null,
      chronicConditions: medicalHistory.value.chronicConditions.length > 0 ? medicalHistory.value.chronicConditions : null,
      painPatterns: medicalHistory.value.painPatterns || null,
      mobilityRestrictions: medicalHistory.value.mobilityRestrictions.length > 0 ? medicalHistory.value.mobilityRestrictions : null,
      medications: medicalHistory.value.medications.length > 0 ? medicalHistory.value.medications : null,
      doctorRestrictions: medicalHistory.value.doctorRestrictions || null,
    },
    nutritionHistory: {
      currentDietPattern: nutritionHistory.value.currentDietPattern || null,
      calorieTrackingExperience: nutritionHistory.value.calorieTrackingExperience,
      macronutrientPreferences: nutritionHistory.value.macronutrientPreferences || null,
      foodAllergies: nutritionHistory.value.foodAllergies.length > 0 ? nutritionHistory.value.foodAllergies : null,
      dietaryRestrictions: nutritionHistory.value.dietaryRestrictions.length > 0 ? nutritionHistory.value.dietaryRestrictions : null,
      culturalFoodPreferences: nutritionHistory.value.culturalFoodPreferences || null,
      cookingSkillLevel: nutritionHistory.value.cookingSkillLevel || null,
      foodBudget: nutritionHistory.value.foodBudget || null,
      eatingSchedule: nutritionHistory.value.eatingSchedule || null,
      snackingHabits: nutritionHistory.value.snackingHabits || null,
      alcoholIntake: nutritionHistory.value.alcoholIntake || null,
      supplementUse: nutritionHistory.value.supplementUse.length > 0 ? nutritionHistory.value.supplementUse : null,
    },
    behavioralFactors: {
      motivationLevel: behavioralFactors.value.motivationLevel || null,
      consistencyHistory: behavioralFactors.value.consistencyHistory || null,
      accountabilityPreference: behavioralFactors.value.accountabilityPreference || null,
      pastSuccessFailurePatterns: behavioralFactors.value.pastSuccessFailurePatterns || null,
      relationshipWithFood: behavioralFactors.value.relationshipWithFood || null,
      disorderedEatingHistory: behavioralFactors.value.disorderedEatingHistory || null,
      stressEatingTendencies: behavioralFactors.value.stressEatingTendencies || null,
      supportSystem: behavioralFactors.value.supportSystem || null,
    },
    metricsTracking: {
      preferredProgressMetrics: metricsTracking.value.preferredProgressMetrics.length > 0 ? metricsTracking.value.preferredProgressMetrics : null,
      trackingTools: metricsTracking.value.trackingTools.length > 0 ? metricsTracking.value.trackingTools : null,
      checkinFrequency: metricsTracking.value.checkinFrequency || null,
    },
    notes: notes.value || null,
  }
  const hasTrainingData = Object.values(payload.trainingBackground ?? {}).some((value) => {
    if (Array.isArray(value)) return value.length > 0
    return value !== null && value !== ''
  })
  const hasMedicalData = Object.values(payload.medicalHistory ?? {}).some((value) => {
    if (Array.isArray(value)) return value.length > 0
    return value !== null && value !== ''
  })
  const hasNutritionData = Object.values(payload.nutritionHistory ?? {}).some((value) => {
    if (Array.isArray(value)) return value.length > 0
    return value !== null && value !== ''
  })
  const hasBehavioralData = Object.values(payload.behavioralFactors ?? {}).some((value) => {
    if (Array.isArray(value)) return value.length > 0
    return value !== null && value !== ''
  })
  const hasMetricsData = Object.values(payload.metricsTracking ?? {}).some((value) => {
    if (Array.isArray(value)) return value.length > 0
    return value !== null && value !== ''
  })

  payload.trainingBackground = hasTrainingData ? payload.trainingBackground : null
  payload.medicalHistory = hasMedicalData ? payload.medicalHistory : null
  payload.nutritionHistory = hasNutritionData ? payload.nutritionHistory : null
  payload.behavioralFactors = hasBehavioralData ? payload.behavioralFactors : null
  payload.metricsTracking = hasMetricsData ? payload.metricsTracking : null
  isSubmitting.value = true
  try {
    await userApi.createUserProfile(payload)

    userStore.setProfile(payload as any)

    toast.add({
      title: 'Profile setup completed',
      description: 'Your profile has been saved successfully.',
      color: 'success',
    })

    const redirectPath = route.query.redirect as string | undefined
    await router.push(redirectPath || '/')
  } catch (error) {
    console.error('Error submitting profile:', error)
    toast.add({
      title: 'Could not save profile',
      description: 'Please try again.',
      color: 'error',
    })
  } finally {
    isSubmitting.value = false
  }
}

const isNextDisabled = computed(() => {
  if (currentStep.value === 1) return !isStep1Valid.value
  if (currentStep.value === 2) return !isStep2Valid.value
  if (currentStep.value === 3) return !isStep3Valid.value
  return false
})

const currentStepInfo = computed(() => steps.find(s => s.id === currentStep.value))
const isCurrentStepSkippable = computed(() => currentStepInfo.value?.skippable ?? false)

const handleSkip = () => {
  if (currentStep.value < steps.length) {
    currentStep.value++
  }
}
</script>

<template>
  <div class="min-h-screen border bg-background py-12 px-4">
    <div class="max-w-7xl mx-auto">
      <div class="text-center mb-12">
        <Badge variant="outline" class="mb-4 px-4 py-1 bg-primary">
          Diaita
        </Badge>
        <h1 class="text-4xl font-bold mb-3">Account Setup</h1>
        <p class="text-muted-foreground">
          Complete the steps below to set up your personalized fitness profile
        </p>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-4 gap-8">
        <div class="lg:col-span-1">
          <div class="bg-card rounded-lg border shadow-sm p-6 space-y-2">
            <button
              v-for="step in steps"
              :key="step.id"
              :class="cn(
                'w-full flex items-start gap-3 p-3 rounded-lg text-left transition-colors',
                currentStep === step.id && 'bg-accent',
                currentStep !== step.id && 'hover:bg-accent/50'
              )"
              @click="currentStep = step.id"
            >
              <div
                :class="cn(
                  'flex items-center justify-center w-10 h-10 rounded-full flex-shrink-0',
                  currentStep === step.id && 'bg-primary text-primary-foreground',
                  currentStep !== step.id && 'bg-muted text-muted-foreground'
                )"
              >
                <component :is="step.icon" class="w-5 h-5" />
              </div>
              <div class="flex-1 min-w-0">
                <p
                  :class="cn(
                    'font-medium text-sm',
                    currentStep === step.id && 'text-foreground',
                    currentStep !== step.id && 'text-muted-foreground'
                  )"
                >
                  {{ step.title }}
                </p>
                <p class="text-xs text-muted-foreground">
                  {{ step.description }}
                </p>
              </div>
            </button>
          </div>
        </div>

        <div class="lg:col-span-3">
          <div class="bg-card rounded-lg border shadow-sm p-8 min-h-[600px]">
            <PersonalInfoStep
              v-if="currentStep === 1"
              :form-data="basicDemographics"
              @update:form-data="(data) => basicDemographics = { ...basicDemographics, ...data }"
            />
            <LifestyleStep
              v-if="currentStep === 2"
              :form-data="activityLifestyle"
              @update:form-data="(data) => activityLifestyle = { ...activityLifestyle, ...data }"
            />
            <GoalsStep
              v-if="currentStep === 3"
              :form-data="goals"
              @update:form-data="(data) => goals = { ...goals, ...data }"
            />
            <TrainingStep
              v-if="currentStep === 4"
              :form-data="trainingBackground"
              @update:form-data="(data) => trainingBackground = { ...trainingBackground, ...data }"
            />
            <NutritionStep
              v-if="currentStep === 5"
              :form-data="nutritionHistory"
              @update:form-data="(data) => nutritionHistory = { ...nutritionHistory, ...data }"
            />
            <MedicalStep
              v-if="currentStep === 6"
              :form-data="medicalHistory"
              @update:form-data="(data) => medicalHistory = { ...medicalHistory, ...data }"
            />
            <BehavioralStep
              v-if="currentStep === 7"
              :form-data="behavioralFactors"
              @update:form-data="(data) => behavioralFactors = { ...behavioralFactors, ...data }"
            />
            <MetricsStep
              v-if="currentStep === 8"
              :form-data="metricsTracking"
              :notes="notes"
              @update:form-data="(data) => metricsTracking = { ...metricsTracking, ...data }"
              @update:notes="(val) => notes = val"
            />
            <ReviewStep
              v-if="currentStep === 9"
              :basic-demographics="basicDemographics"
              :activity-lifestyle="activityLifestyle"
              :goals="goals"
              :training-background="trainingBackground"
              :medical-history="medicalHistory"
              :nutrition-history="nutritionHistory"
              :behavioral-factors="behavioralFactors"
              :metrics-tracking="metricsTracking"
              :notes="notes"
            />

            <div class="flex items-center justify-between mt-12 pt-8 border-t border-border">
              <Button
                variant="ghost"
                :disabled="currentStep === 1"
                class="gap-2"
                @click="handleBack"
              >
                <ChevronLeft class="w-4 h-4" />
                Back
              </Button>

              <div class="flex items-center gap-2">
                <span class="text-sm text-muted-foreground">
                  Step {{ currentStep }} of {{ steps.length }}
                </span>
                <span v-if="isCurrentStepSkippable" class="text-xs text-muted-foreground bg-muted px-2 py-0.5 rounded">
                  Optional
                </span>
              </div>

              <div class="flex items-center gap-2">
                <Button
                  v-if="isCurrentStepSkippable"
                  variant="outline"
                  class="gap-2"
                  @click="handleSkip"
                >
                  Skip
                  <ChevronRight class="w-4 h-4" />
                </Button>
                <Button
                  v-if="currentStep < steps.length"
                  class="gap-2"
                  :disabled="isNextDisabled"
                  @click="handleNext"
                >
                  Next Step
                  <ChevronRight class="w-4 h-4" />
                </Button>
                <Button
                  v-else
                  class="gap-2"
                  :disabled="isSubmitting"
                  @click="handleSubmit"
                >
                  Complete Setup
                  <ChevronRight class="w-4 h-4" />
                </Button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <LoadingScreen
        v-if="isSubmitting"
      :show="isSubmitting"
      message="Setting up your profile..."
    />
  </div>
</template>
