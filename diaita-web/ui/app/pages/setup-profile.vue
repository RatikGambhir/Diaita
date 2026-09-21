<script setup lang="ts">
import { ref, computed } from 'vue'
import { User, Activity, Target, Apple, Dumbbell, FileCheck, ChevronLeft, ChevronRight } from 'lucide-vue-next'
import Button from '~/components/ui/button/Button.vue'
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
    trainingHistory: normalizeString(form.value.trainingHistory),
    trainingAge: normalizeString(form.value.trainingAge),
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
  <div class="min-h-screen bg-background px-4 py-5 sm:px-6 lg:py-8">
    <div class="mx-auto max-w-7xl">
      <div class="mb-8 flex items-center justify-between">
        <NuxtLink to="/landing" class="focus-ring rounded-md"><BrandMark /></NuxtLink>
        <Button
          variant="outline"
          class="gap-2 text-muted-foreground hover:text-foreground"
          :disabled="isSubmitting"
          @click="isSkipDialogOpen = true"
        >
          Skip for now
          <ChevronRight class="w-4 h-4" />
        </Button>
      </div>

      <div class="mb-8 max-w-2xl">
        <p class="eyebrow mb-3">Personalize Diaita</p>
        <h1 class="display-title text-4xl sm:text-5xl">Build your health profile.</h1>
        <p class="mt-3 leading-7 text-muted-foreground">A few grounded details help Diaita shape a more useful training recommendation.</p>
      </div>

      <div class="mb-7 h-1 bg-muted lg:hidden"><div class="h-full bg-primary transition-all" :style="{ width: `${(currentStep / steps.length) * 100}%` }" /></div>

      <div class="grid grid-cols-1 gap-6 lg:grid-cols-[17rem_minmax(0,1fr)]">
        <aside class="hidden lg:block">
          <div class="sticky top-8 bg-foreground p-5 text-background">
            <p class="eyebrow mb-5 text-background/40">Setup progress</p>
            <button
              v-for="step in steps"
              :key="step.id"
              :class="cn(
                'group flex w-full items-start gap-3 border-t border-background/15 px-1 py-4 text-left transition-colors last:border-b',
                currentStep === step.id ? 'text-background' : 'text-background/45 hover:text-background/75'
              )"
              @click="currentStep = step.id"
            >
              <span :class="cn('font-mono text-xs', currentStep === step.id && 'text-sidebar-primary')">{{ String(step.id).padStart(2, '0') }}</span>
              <component :is="step.icon" :class="cn('mt-0.5 h-4 w-4', currentStep === step.id && 'text-sidebar-primary')" />
              <div class="flex-1 min-w-0">
                <p class="text-sm font-semibold">{{ step.title }}</p>
                <p class="mt-0.5 text-xs opacity-65">{{ step.description }}</p>
              </div>
            </button>
          </div>
        </aside>

        <main class="min-w-0">
          <div class="mb-5 flex items-center justify-between lg:hidden">
            <div><p class="eyebrow">Step {{ currentStep }} of {{ steps.length }}</p><p class="mt-2 font-semibold">{{ currentStepInfo?.title }}</p></div>
            <component :is="currentStepInfo?.icon" class="h-5 w-5 text-primary" />
          </div>
          <div class="min-h-[600px] rounded-xl border bg-card p-5 sm:p-8 lg:p-10">
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

            <div class="mt-12 flex flex-col gap-4 border-t border-border pt-7 sm:flex-row sm:items-center sm:justify-between">
              <Button
                variant="ghost"
                :disabled="currentStep === 1"
                class="gap-2"
                @click="handleBack"
              >
                <ChevronLeft class="w-4 h-4" />
                Back
              </Button>

              <div class="hidden items-center gap-2 sm:flex">
                <span class="text-sm text-muted-foreground">
                  Step {{ currentStep }} of {{ steps.length }}
                </span>
                <span v-if="isCurrentStepSkippable" class="text-xs text-muted-foreground">
                  Optional
                </span>
              </div>

              <div class="flex flex-wrap items-center justify-end gap-2">
                <Button
                  variant="ghost"
                  class="hidden gap-2 text-muted-foreground hover:text-foreground sm:inline-flex"
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
        </main>
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
