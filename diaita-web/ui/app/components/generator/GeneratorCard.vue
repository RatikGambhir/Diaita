<script setup lang="ts">
import { ref } from 'vue'
import { Dumbbell, UtensilsCrossed, Sparkles } from 'lucide-vue-next'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '~/components/ui/tabs'
import WorkoutForm from './WorkoutForm.vue'
import MealPlanForm from './MealPlanForm.vue'

const activeTab = ref('workout')
const isLoading = ref(false)

const handleWorkoutSubmit = (data: any) => {
  isLoading.value = true
  // Simulate API call
  setTimeout(() => {
    isLoading.value = false
    console.log('Workout form submitted:', data)
  }, 2000)
}

const handleMealPlanSubmit = (data: any) => {
  isLoading.value = true
  // Simulate API call
  setTimeout(() => {
    isLoading.value = false
    console.log('Meal plan form submitted:', data)
  }, 2000)
}
</script>

<template>
  <div class="bg-card rounded-2xl shadow-sm overflow-hidden">
    <Tabs v-model="activeTab" class="w-full">
      <div class="border-b border-border">
        <TabsList class="w-full h-auto p-0 bg-transparent rounded-none grid grid-cols-2">
          <TabsTrigger
            value="workout"
            class="relative h-14 rounded-none border-0 bg-transparent data-[state=active]:bg-orange-50 dark:data-[state=active]:bg-orange-950/20 data-[state=active]:text-orange-600 dark:data-[state=active]:text-orange-400 data-[state=active]:shadow-none text-muted-foreground font-medium transition-colors"
          >
            <Dumbbell class="w-4 h-4 mr-2" />
            Generate Workout
            <span class="absolute bottom-0 left-0 right-0 h-0.5 bg-orange-500 scale-x-0 data-[state=active]:scale-x-100 transition-transform origin-left" />
          </TabsTrigger>
          <TabsTrigger
            value="meal"
            class="relative h-14 rounded-none border-0 bg-transparent data-[state=active]:bg-emerald-50 dark:data-[state=active]:bg-emerald-950/20 data-[state=active]:text-emerald-600 dark:data-[state=active]:text-emerald-400 data-[state=active]:shadow-none text-muted-foreground font-medium transition-colors"
          >
            <UtensilsCrossed class="w-4 h-4 mr-2" />
            Generate Meal Plan
            <span class="absolute bottom-0 left-0 right-0 h-0.5 bg-emerald-500 scale-x-0 data-[state=active]:scale-x-100 transition-transform origin-left" />
          </TabsTrigger>
        </TabsList>
      </div>

      <div class="p-6 md:p-8">
        <TabsContent value="workout" class="m-0 focus-visible:outline-none focus-visible:ring-0">
          <div class="flex items-center gap-3 mb-6">
            <div class="w-12 h-12 rounded-xl bg-orange-100 dark:bg-orange-900/30 flex items-center justify-center">
              <Dumbbell class="w-6 h-6 text-orange-600 dark:text-orange-400" />
            </div>
            <div>
              <h3 class="text-lg font-semibold text-foreground">Generate Workout</h3>
              <p class="text-sm text-muted-foreground">Customize your perfect training session</p>
            </div>
          </div>

          <WorkoutForm :loading="isLoading" @submit="handleWorkoutSubmit" />
        </TabsContent>

        <TabsContent value="meal" class="m-0 focus-visible:outline-none focus-visible:ring-0">
          <div class="flex items-center gap-3 mb-6">
            <div class="w-12 h-12 rounded-xl bg-emerald-100 dark:bg-emerald-900/30 flex items-center justify-center">
              <UtensilsCrossed class="w-6 h-6 text-emerald-600 dark:text-emerald-400" />
            </div>
            <div>
              <h3 class="text-lg font-semibold text-foreground">Generate Meal Plan</h3>
              <p class="text-sm text-muted-foreground">Create your personalized nutrition plan</p>
            </div>
          </div>

          <MealPlanForm :loading="isLoading" @submit="handleMealPlanSubmit" />
        </TabsContent>
      </div>
    </Tabs>
  </div>
</template>

<style scoped>
/* Fix for the active indicator line */
[data-state="active"] .absolute.bottom-0 {
  transform: scaleX(1);
}

/* Tab trigger active underline */
:deep([data-state="active"]) > span:last-child {
  transform: scaleX(1);
}
</style>
