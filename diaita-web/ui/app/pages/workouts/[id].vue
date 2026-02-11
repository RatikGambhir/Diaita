<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { ArrowLeft, Calendar, Clock, Dumbbell, MoreHorizontal, Target, TrendingUp, Zap } from 'lucide-vue-next'
import Button from '~/components/ui/button/Button.vue'
import WorkoutSummaryCard from '~/components/workouts/WorkoutSummaryCard.vue'
import WorkoutStatCarousel from '~/components/workouts/WorkoutStatCarousel.vue'
import WorkoutExerciseList from '~/components/workouts/WorkoutExerciseList.vue'
import type { WorkoutExercise } from '~/components/workouts/WorkoutExerciseItem.vue'

interface WorkoutDetail {
  id: number
  name: string
  date: string
  duration: string
  summary: {
    progress: string
    intensity: string
    achievement: string
  }
  stats: {
    totalVolume: string
    totalVolumeChange: string
    caloriesBurned: string
    caloriesChange: string
    durationChange: string
  }
  exercises: WorkoutExercise[]
}

const route = useRoute()
const workoutId = computed(() => Number(route.params.id))

const allWorkouts = ref<WorkoutDetail[]>([
  {
    id: 1,
    name: 'Morning Workout',
    date: 'Dec 15, 2025',
    duration: '0:50',
    summary: {
      progress: 'Volume increased 10% since last week.',
      intensity: 'Moderate effort with strong consistency.',
      achievement: 'Completed all scheduled categories.',
    },
    stats: {
      totalVolume: '9,200 lbs',
      totalVolumeChange: '+8%',
      caloriesBurned: '410 kcal',
      caloriesChange: '+10%',
      durationChange: '+3 min',
    },
    exercises: [
      { id: 'lift-1', name: 'Bench Press', category: 'Lifting', col2Value: '4x8', col3Value: '185 lbs' },
      { id: 'lift-2', name: 'Barbell Row', category: 'Lifting', col2Value: '4x8', col3Value: '155 lbs' },
      { id: 'cardio-1', name: 'Running', category: 'Cardio', col2Value: '20 min', col3Value: 'Zone 2' },
      { id: 'mobility-1', name: 'Yoga Flow', category: 'Mobility', col2Value: '12 min', col3Value: 'Hips, Shoulders' },
    ],
  },
  {
    id: 2,
    name: 'Evening Push Day',
    date: 'Dec 14, 2025',
    duration: '1:15',
    summary: {
      progress: 'Hit a rep PR on incline press.',
      intensity: 'High intensity, short rest intervals.',
      achievement: 'Closed all planned sets with clean form.',
    },
    stats: {
      totalVolume: '12,450 lbs',
      totalVolumeChange: '+12%',
      caloriesBurned: '450 kcal',
      caloriesChange: '+15%',
      durationChange: '+5 min',
    },
    exercises: [
      { id: 'lift-3', name: 'Incline Bench Press', category: 'Lifting', col2Value: '5x6', col3Value: '165 lbs' },
      { id: 'lift-4', name: 'Shoulder Press', category: 'Lifting', col2Value: '4x8', col3Value: '75 lbs' },
      { id: 'lift-5', name: 'Cable Fly', category: 'Lifting', col2Value: '3x12', col3Value: '45 lbs' },
      { id: 'cardio-2', name: 'Jump Rope', category: 'Cardio', col2Value: '10 min', col3Value: 'HIIT' },
      { id: 'mobility-2', name: 'Foam Rolling', category: 'Mobility', col2Value: '8 min', col3Value: 'Chest, Lats' },
    ],
  },
  {
    id: 3,
    name: 'Leg Day',
    date: 'Dec 13, 2025',
    duration: '1:30',
    summary: {
      progress: 'Maintained top-end squat numbers.',
      intensity: 'Very high lower-body workload.',
      achievement: 'Finished with strong mobility cooldown.',
    },
    stats: {
      totalVolume: '15,880 lbs',
      totalVolumeChange: '+9%',
      caloriesBurned: '520 kcal',
      caloriesChange: '+12%',
      durationChange: '+6 min',
    },
    exercises: [
      { id: 'lift-6', name: 'Squats', category: 'Lifting', col2Value: '5x5', col3Value: '225 lbs' },
      { id: 'lift-7', name: 'Leg Press', category: 'Lifting', col2Value: '4x10', col3Value: '320 lbs' },
      { id: 'lift-8', name: 'Romanian Deadlift', category: 'Lifting', col2Value: '4x8', col3Value: '185 lbs' },
      { id: 'cardio-3', name: 'Cycling', category: 'Cardio', col2Value: '20 min', col3Value: 'Steady' },
      { id: 'mobility-3', name: 'Dynamic Stretching', category: 'Mobility', col2Value: '10 min', col3Value: 'Hip Flexors' },
    ],
  },
  {
    id: 4,
    name: 'Back & Biceps',
    date: 'Dec 12, 2025',
    duration: '1:20',
    summary: {
      progress: 'Pull volume rose across all major lifts.',
      intensity: 'High effort with controlled tempo.',
      achievement: 'Excellent consistency on all working sets.',
    },
    stats: {
      totalVolume: '11,340 lbs',
      totalVolumeChange: '+7%',
      caloriesBurned: '430 kcal',
      caloriesChange: '+11%',
      durationChange: '+4 min',
    },
    exercises: [
      { id: 'lift-9', name: 'Deadlift', category: 'Lifting', col2Value: '3x5', col3Value: '275 lbs' },
      { id: 'lift-10', name: 'Lat Pulldown', category: 'Lifting', col2Value: '4x10', col3Value: '140 lbs' },
      { id: 'cardio-4', name: 'Row Erg', category: 'Cardio', col2Value: '12 min', col3Value: 'Zone 3' },
      { id: 'mobility-4', name: 'Thoracic Rotations', category: 'Mobility', col2Value: '8 min', col3Value: 'Upper Back' },
    ],
  },
])

const workout = computed(() => {
  return allWorkouts.value.find((item) => item.id === workoutId.value) ?? allWorkouts.value[0]
})

const exercises = ref<WorkoutExercise[]>([])

watch(workout, (newWorkout) => {
  exercises.value = newWorkout.exercises.map((exercise) => ({ ...exercise }))
}, { immediate: true })

const statCards = computed(() => [
  { icon: TrendingUp, label: 'Total Volume', value: workout.value.stats.totalVolume, change: workout.value.stats.totalVolumeChange },
  { icon: Dumbbell, label: 'Exercises', value: String(exercises.value.length), change: '+2' },
  { icon: Zap, label: 'Calories Burned', value: workout.value.stats.caloriesBurned, change: workout.value.stats.caloriesChange },
  { icon: Target, label: 'Duration', value: workout.value.duration, change: workout.value.stats.durationChange },
])
</script>

<template>
  <div class="flex h-full flex-1 flex-col">
    <header class="flex h-16 shrink-0 items-center justify-between border-b px-6">
      <div class="flex items-center gap-3">
        <Button variant="ghost" size="icon" @click="navigateTo('/workouts')">
          <ArrowLeft class="h-5 w-5" />
        </Button>
        <h1 class="text-xl font-semibold text-foreground">{{ workout.name }}</h1>
      </div>
      <Button variant="ghost" size="icon">
        <MoreHorizontal class="h-5 w-5" />
      </Button>
    </header>

    <div class="flex-1 space-y-6 overflow-auto p-6">
      <div class="flex items-center gap-6 text-muted-foreground">
        <div class="flex items-center gap-2">
          <Calendar class="h-5 w-5" />
          <span>{{ workout.date }}</span>
        </div>
        <div class="flex items-center gap-2">
          <Clock class="h-5 w-5" />
          <span>{{ workout.duration }}</span>
        </div>
      </div>

      <WorkoutSummaryCard
        :progress="workout.summary.progress"
        :intensity="workout.summary.intensity"
        :achievement="workout.summary.achievement"
      />

      <section>
        <h2 class="mb-3 text-lg font-semibold text-foreground">Statistics</h2>
        <WorkoutStatCarousel :stats="statCards" />
      </section>

      <WorkoutExerciseList v-model="exercises" />
    </div>
  </div>
</template>
