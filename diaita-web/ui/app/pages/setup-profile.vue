<script setup lang="ts">
import { ref, computed } from 'vue'
import { User, Activity, Target, Apple, Dumbbell, FileCheck, ChevronLeft, ChevronRight } from 'lucide-vue-next'
import Button from '~/components/ui/button/Button.vue'
import Badge from '~/components/ui/badge/Badge.vue'
import Dialog from '~/components/ui/dialog/Dialog.vue'
import DialogContent from '~/components/ui/dialog/DialogContent.vue'
import DialogFooter from '~/components/ui/dialog/DialogFooter.vue'
import DialogHeader from '~/components/ui/dialog/DialogHeader.vue'
import DialogTitle from '~/components/ui/dialog/DialogTitle.vue'
import { userApi } from '~/api/user'
import { useUserStore } from '~/stores/useUserStore'
import PersonalInfoStep from '~/components/setup/PersonalInfoStep.vue'
import LifestyleStep from '~/components/setup/LifestyleStep.vue'
import GoalsStep from '~/components/setup/GoalsStep.vue'
import TrainingStep from '~/components/setup/TrainingStep.vue'
import NutritionStep from '~/components/setup/NutritionStep.vue'
import ReviewStep from '~/components/setup/ReviewStep.vue'
import LoadingScreen from '~/components/ui/LoadingScreen.vue'
import { cn } from '~/lib/utils'
import type { RegisterUserProfileRequest } from '~/types/ProfileTypes'

definePageMeta({
  layout: false,
})

type SetupProfileForm = {
  age: number | null
  height: number | null
  weight: number | null
  primaryGoal: string
  timeframe: string
  activityLevel: string
  sleepDuration: number | null
  stressLevel: string
  trainingHistory: string
  trainingAge: string
  equipmentAccess: string
  daysPerWeek: number | null
  timePerSession: number | null
  injuries: string[]
  chronicConditions: string[]
  mobilityRestrictions: string[]
  doctorRestrictions: string
  dietaryRestrictions: string[]
  foodAllergies: string[]
  currentDietPattern: string
  eatingSchedule: string
}

const steps = [
  { id: 1, title: 'Personal Info', description: 'Basic demographics', icon: User, skippable: false },
  { id: 2, title: 'Lifestyle', description: 'Activity & habits', icon: Activity, skippable: false },
  { id: 3, title: 'Goals', description: 'Your objectives', icon: Target, skippable: false },
  { id: 4, title: 'Training', description: 'Exercise background', icon: Dumbbell, skippable: true },
  { id: 5, title: 'Nutrition', description: 'Diet history', icon: Apple, skippable: true },
  { id: 6, title: 'Review', description: 'Final check', icon: FileCheck, skippable: false },
]

const currentStep = ref(1)
const isSubmitting = ref(false)
const isSkipDialogOpen = ref(false)
const toast = useToast()
const router = useRouter()
const userStore = useUserStore()

const form = ref<SetupProfileForm>({
  age: null,
  height: null,
  weight: null,
  primaryGoal: '',
  timeframe: '',
  activityLevel: '',
  sleepDuration: null,
  stressLevel: '',
  trainingHistory: '',
  trainingAge: '',
  equipmentAccess: '',
  daysPerWeek: null,
  timePerSession: null,
  injuries: [],
  chronicConditions: [],
  mobilityRestrictions: [],
  doctorRestrictions: '',
  dietaryRestrictions: [],
  foodAllergies: [],
  currentDietPattern: '',
  eatingSchedule: '',
})

const updateForm = (data: Partial<SetupProfileForm>) => {
  form.value = { ...form.value, ...data }
}

const basicDemographicsForm = computed(() => ({
  age: form.value.age,
  height: form.value.height,
  weight: form.value.weight,
}))

const activityLifestyleForm = computed(() => ({
  activityLevel: form.value.activityLevel,
  sleepDuration: form.value.sleepDuration,
  stressLevel: form.value.stressLevel,
}))

const goalsForm = computed(() => ({
  primaryGoal: form.value.primaryGoal,
  timeframe: form.value.timeframe,
}))

const trainingBackgroundForm = computed(() => ({
  trainingAge: form.value.trainingAge,
  trainingHistory: form.value.trainingHistory,
  equipmentAccess: form.value.equipmentAccess,
  daysPerWeek: form.value.daysPerWeek,
  timePerSession: form.value.timePerSession,
  injuries: form.value.injuries,
  chronicConditions: form.value.chronicConditions,
  mobilityRestrictions: form.value.mobilityRestrictions,
  doctorRestrictions: form.value.doctorRestrictions,
}))

const nutritionHistoryForm = computed(() => ({
  currentDietPattern: form.value.currentDietPattern,
  dietaryRestrictions: form.value.dietaryRestrictions,
  foodAllergies: form.value.foodAllergies,
  eatingSchedule: form.value.eatingSchedule,
}))

const isStep1Valid = computed(() => {
  return form.value.age !== null
    && form.value.age >= 13
    && form.value.age <= 120
    && form.value.height !== null
    && form.value.height > 0
    && form.value.weight !== null
    && form.value.weight > 0
})

const isStep2Valid = computed(() => {
  const sleep = form.value.sleepDuration
  return form.value.activityLevel.trim() !== ''
    && (sleep === null || (sleep >= 0 && sleep <= 24))
})

const isStep3Valid = computed(() => {
  return form.value.primaryGoal.trim() !== ''
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

const normalizeString = (value: string): string | null => {
  const trimmed = value.trim()
  return trimmed === '' ? null : trimmed
}

const normalizeArray = (value: string[]): string[] | null => {
  const normalized = value
    .map(item => item.trim())
    .filter(item => item !== '')
  return normalized.length > 0 ? normalized : null
}

const handleSubmit = async () => {
  const userId = userStore.getUser?.id?.trim()

  if (!userId) {
    toast.add({
      title: 'Missing user session',
      description: 'Please log in again before saving your profile.',
      color: 'error',
    })
    return
  }

  if (!isStep1Valid.value || !isStep2Valid.value || !isStep3Valid.value) {
    toast.add({
      title: 'Missing required fields',
      description: 'Please complete required fields before submitting.',
      color: 'error',
    })
    return
  }

  if (form.value.daysPerWeek !== null && (form.value.daysPerWeek < 0 || form.value.daysPerWeek > 14)) {
    toast.add({
      title: 'Invalid days per week',
      description: 'Days per week must be between 0 and 14.',
      color: 'error',
    })
    return
  }

  if (form.value.timePerSession !== null && (form.value.timePerSession < 0 || form.value.timePerSession > 1440)) {
    toast.add({
      title: 'Invalid time per session',
      description: 'Time per session must be between 0 and 1440 minutes.',
      color: 'error',
    })
    return
  }

  const trainingHistory = normalizeString(form.value.trainingHistory)
  const trainingAge = normalizeString(form.value.trainingAge)

  if (!trainingHistory && !trainingAge) {
    toast.add({
      title: 'Training background required',
      description: 'Please provide either training history or training age.',
      color: 'error',
    })
    return
  }

  const payload: RegisterUserProfileRequest = {
    userId,
    age: form.value.age!,
    height: form.value.height!,
    weight: form.value.weight!,
    primaryGoal: form.value.primaryGoal.trim(),
    timeframe: normalizeString(form.value.timeframe),
    activityLevel: form.value.activityLevel.trim(),
    sleepDuration: form.value.sleepDuration,
    stressLevel: normalizeString(form.value.stressLevel),
    trainingHistory,
    trainingAge,
    equipmentAccess: normalizeString(form.value.equipmentAccess),
    daysPerWeek: form.value.daysPerWeek,
    timePerSession: form.value.timePerSession,
    injuries: normalizeArray(form.value.injuries),
    chronicConditions: normalizeArray(form.value.chronicConditions),
    mobilityRestrictions: normalizeArray(form.value.mobilityRestrictions),
    doctorRestrictions: normalizeString(form.value.doctorRestrictions),
    dietaryRestrictions: normalizeArray(form.value.dietaryRestrictions),
    foodAllergies: normalizeArray(form.value.foodAllergies),
    currentDietPattern: normalizeString(form.value.currentDietPattern),
    eatingSchedule: normalizeString(form.value.eatingSchedule),
  }

  isSubmitting.value = true

  try {
    const response = await userApi.createUserProfile(payload)

    userStore.setProfile(response.profile)
    userStore.setRecommendation(response.recommendation)
    userStore.setProfileStatus('loaded')

    toast.add({
      title: 'Profile setup completed',
      description: 'Your profile has been saved successfully.',
      color: 'success',
    })

    await router.push('/profile')
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

const handleSkipForNow = async () => {
  if (isSubmitting.value) return

  isSkipDialogOpen.value = false
  toast.add({
    title: 'Setup skipped for now',
    description: 'You can complete your profile later from Home.',
    color: 'info',
  })
  await router.push('/')
}
</script>

<template>
  <div class="min-h-screen border bg-background py-12 px-4">
    <div class="max-w-7xl mx-auto">
      <div class="mb-6 flex items-center justify-end">
        <Button
          variant="outline"
          class="gap-2 border-dashed text-muted-foreground hover:text-foreground"
          :disabled="isSubmitting"
          @click="isSkipDialogOpen = true"
        >
          Skip for now
          <ChevronRight class="w-4 h-4" />
        </Button>
      </div>

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
                currentStep === step.id && 'bg-background',
                currentStep !== step.id && 'hover:bg-white'
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
              :form-data="basicDemographicsForm"
              @update:form-data="updateForm"
            />
            <LifestyleStep
              v-if="currentStep === 2"
              :form-data="activityLifestyleForm"
              @update:form-data="updateForm"
            />
            <GoalsStep
              v-if="currentStep === 3"
              :form-data="goalsForm"
              @update:form-data="updateForm"
            />
            <TrainingStep
              v-if="currentStep === 4"
              :form-data="trainingBackgroundForm"
              @update:form-data="updateForm"
            />
            <NutritionStep
              v-if="currentStep === 5"
              :form-data="nutritionHistoryForm"
              @update:form-data="updateForm"
            />
            <ReviewStep
              v-if="currentStep === 6"
              :basic-demographics="basicDemographicsForm"
              :activity-lifestyle="activityLifestyleForm"
              :goals="goalsForm"
              :training-background="trainingBackgroundForm"
              :nutrition-history="nutritionHistoryForm"
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
                  variant="ghost"
                  class="gap-2 text-muted-foreground hover:text-foreground"
                  :disabled="isSubmitting"
                  @click="isSkipDialogOpen = true"
                >
                  Skip for now
                </Button>
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

    <Dialog v-model:open="isSkipDialogOpen">
      <DialogContent class="sm:max-w-md">
        <DialogHeader>
          <DialogTitle>Skip setup for now?</DialogTitle>
        </DialogHeader>

        <p class="py-2 text-sm text-muted-foreground">
          Your setup progress on this screen will not be saved. You can finish profile setup later from Home.
        </p>

        <DialogFooter class="flex gap-2 justify-end">
          <Button variant="outline" @click="isSkipDialogOpen = false">
            Continue Setup
          </Button>
          <Button variant="destructive" @click="handleSkipForNow">
            Skip for now
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  </div>
</template>
