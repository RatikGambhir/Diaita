<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import {
  ArrowLeft,
  Calendar,
  Clock,
  Dumbbell,
  Loader2,
  MoreHorizontal,
  Target,
  Timer,
  TrendingUp,
  Trash2,
} from 'lucide-vue-next'
import Button from '~/components/ui/button/Button.vue'
import Input from '~/components/ui/input/Input.vue'
import WorkoutStatCarousel from '~/components/workouts/WorkoutStatCarousel.vue'
import WorkoutExerciseList from '~/components/workouts/WorkoutExerciseList.vue'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger
} from '~/components/ui/dropdown-menu'
import { workoutsApi } from '~/api/workouts'
import { useToast } from '~/composables/useToast'
import { useUserStore } from '~/stores/useUserStore'
import type {
  UpdateWorkoutRequest,
  UpsertWorkoutExercise,
  Workout,
} from '~/types/WorkoutTypes'
import {
  formatWorkoutDate,
  formatWorkoutDuration,
  formatWorkoutVolume,
} from '~/utils/workouts'

const route = useRoute()
const toast = useToast()
const userStore = useUserStore()

const workoutId = computed(() => String(route.params.id))
const currentUserId = computed(() => userStore.getUser?.id?.trim() ?? null)

const workout = ref<Workout | null>(null)
const isLoading = ref(true)
const isSaving = ref(false)
const loadError = ref<string | null>(null)

const isEditingHeader = ref(false)
const headerName = ref('')
const headerDate = ref('')
const headerDurationMinutes = ref('')

const describeError = (error: unknown, fallback: string): string => {
  if (typeof error === 'object' && error !== null && 'response' in error) {
    const response = (error as { response?: { data?: unknown } }).response
    if (typeof response?.data === 'string' && response.data.trim()) {
      return response.data
    }
  }
  return error instanceof Error ? error.message : fallback
}

const applyWorkout = (next: Workout) => {
  workout.value = next
  headerName.value = next.name
  headerDate.value = next.performedAt.slice(0, 10)
  headerDurationMinutes.value = next.durationSeconds
    ? String(Math.round(next.durationSeconds / 60))
    : ''
}

const loadWorkout = async () => {
  const userId = currentUserId.value
  if (!userId) {
    isLoading.value = false
    loadError.value = 'Sign in to view this workout.'
    return
  }

  isLoading.value = true
  loadError.value = null

  try {
    applyWorkout(await workoutsApi.get(workoutId.value, userId))
  } catch (error) {
    workout.value = null
    loadError.value = describeError(error, 'Failed to load this workout.')
  } finally {
    isLoading.value = false
  }
}

watch([workoutId, currentUserId], () => {
  void loadWorkout()
}, { immediate: true })

const saveWorkout = async (payload: Omit<UpdateWorkoutRequest, 'userId'>) => {
  const userId = currentUserId.value
  if (!userId || !workout.value) {
    return
  }

  isSaving.value = true

  try {
    applyWorkout(await workoutsApi.update(workout.value.id, { ...payload, userId }))
  } catch (error) {
    toast.add({
      title: 'Unable to save workout',
      description: describeError(error, 'Failed to save the workout.'),
      color: 'error',
    })
  } finally {
    isSaving.value = false
  }
}

const handleUpsertExercises = async (exercises: UpsertWorkoutExercise[]) => {
  await saveWorkout({ exerciseOps: { upsert: exercises } })
}

const handleRemoveExercise = async (exerciseId: string) => {
  await saveWorkout({ exerciseOps: { deleteIds: [exerciseId] } })
}

const startEditingHeader = () => {
  isEditingHeader.value = true
}

const cancelEditingHeader = () => {
  if (workout.value) {
    applyWorkout(workout.value)
  }
  isEditingHeader.value = false
}

const saveHeader = async () => {
  const name = headerName.value.trim()
  if (!name) {
    return
  }

  const minutes = Number(headerDurationMinutes.value.trim())
  const durationSeconds = Number.isFinite(minutes) && minutes > 0
    ? Math.round(minutes * 60)
    : null

  await saveWorkout({
    name,
    performedAt: headerDate.value || undefined,
    durationSeconds,
  })

  isEditingHeader.value = false
}

const deleteWorkout = async () => {
  const userId = currentUserId.value
  if (!userId || !workout.value) {
    return
  }

  try {
    await workoutsApi.remove(workout.value.id, userId)
    toast.add({ title: 'Workout deleted', color: 'success' })
    await navigateTo('/workouts')
  } catch (error) {
    toast.add({
      title: 'Unable to delete workout',
      description: describeError(error, 'Failed to delete the workout.'),
      color: 'error',
    })
  }
}

const statCards = computed(() => {
  const current = workout.value
  if (!current) {
    return []
  }

  return [
    {
      icon: TrendingUp,
      label: 'Total Volume',
      value: formatWorkoutVolume(current.totals.totalVolumeKg),
      change: `${current.totals.totalSets} sets`,
    },
    {
      icon: Dumbbell,
      label: 'Exercises',
      value: String(current.totals.exerciseCount),
      change: `${current.totals.liftingCount} lifting`,
    },
    {
      icon: Timer,
      label: 'Cardio Time',
      value: formatWorkoutDuration(current.totals.totalCardioSeconds),
      change: `${current.totals.cardioCount} sessions`,
    },
    {
      icon: Target,
      label: 'Duration',
      value: formatWorkoutDuration(current.durationSeconds),
      change: `${current.totals.mobilityCount} mobility`,
    },
  ]
})
</script>

<template>
  <div class="flex h-full flex-1 flex-col">
    <header class="flex h-16 shrink-0 items-center justify-between border-b px-6">
      <div class="flex items-center gap-3">
        <Button variant="ghost" size="icon" @click="navigateTo('/workouts')">
          <ArrowLeft class="h-5 w-5" />
        </Button>
        <h1 class="text-xl font-semibold text-foreground">
          {{ workout?.name ?? 'Workout' }}
        </h1>
        <Loader2 v-if="isSaving" class="h-4 w-4 animate-spin text-muted-foreground" />
      </div>
      <DropdownMenu v-if="workout">
        <DropdownMenuTrigger as-child>
          <Button variant="ghost" size="icon">
            <MoreHorizontal class="h-5 w-5" />
          </Button>
        </DropdownMenuTrigger>
        <DropdownMenuContent align="end">
          <DropdownMenuItem @click="startEditingHeader">Edit details</DropdownMenuItem>
          <DropdownMenuItem class="text-destructive" @click="deleteWorkout">
            <Trash2 class="h-4 w-4 mr-2" />
            Delete workout
          </DropdownMenuItem>
        </DropdownMenuContent>
      </DropdownMenu>
    </header>

    <div class="flex-1 space-y-6 overflow-auto p-6">
      <div
        v-if="isLoading"
        class="flex items-center justify-center gap-2 py-16 text-muted-foreground"
      >
        <Loader2 class="h-4 w-4 animate-spin" />
        Loading workout…
      </div>

      <div v-else-if="!workout" class="space-y-4 py-16 text-center">
        <p class="text-muted-foreground">{{ loadError ?? 'Workout not found.' }}</p>
        <Button variant="outline" @click="navigateTo('/workouts')">Back to workouts</Button>
      </div>

      <template v-else>
        <div v-if="isEditingHeader" class="grid grid-cols-1 gap-3 md:grid-cols-3">
          <Input v-model="headerName" placeholder="Workout name" />
          <Input v-model="headerDate" type="date" />
          <div class="flex gap-2">
            <Input
              v-model="headerDurationMinutes"
              type="number"
              min="0"
              placeholder="Duration (min)"
            />
            <Button :disabled="isSaving" @click="saveHeader">Save</Button>
            <Button variant="ghost" @click="cancelEditingHeader">Cancel</Button>
          </div>
        </div>

        <div v-else class="flex items-center gap-6 text-muted-foreground">
          <div class="flex items-center gap-2">
            <Calendar class="h-5 w-5" />
            <span>{{ formatWorkoutDate(workout.performedAt) }}</span>
          </div>
          <div class="flex items-center gap-2">
            <Clock class="h-5 w-5" />
            <span>{{ formatWorkoutDuration(workout.durationSeconds) }}</span>
          </div>
          <Button variant="ghost" size="sm" @click="startEditingHeader">Edit</Button>
        </div>

        <section>
          <h2 class="mb-3 text-lg font-semibold text-foreground">Statistics</h2>
          <WorkoutStatCarousel :stats="statCards" />
        </section>

        <WorkoutExerciseList
          :exercises="workout.exercises"
          :saving="isSaving"
          @upsert="handleUpsertExercises"
          @remove="handleRemoveExercise"
        />
      </template>
    </div>
  </div>
</template>
