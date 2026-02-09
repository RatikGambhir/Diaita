<script setup lang="ts">
import { ArrowLeft, ArrowRight, Dumbbell, Plus, TrendingUp, Utensils } from 'lucide-vue-next'
import Button from '~/components/ui/button/Button.vue'
import Card from '~/components/ui/card/Card.vue'

const { sectionRef, isVisible } = useScrollAnimation(0.25)

const currentPage = ref(0)

const featurePages = [
  [
    {
      title: 'Smart Calorie Tracking',
      icon: Utensils,
      description: 'Scan meals, log macros, and see daily targets update in real time.',
    },
    {
      title: 'Workout Logging',
      icon: Dumbbell,
      description: 'Track sets, reps, intensity, and training consistency from one timeline.',
    },
    {
      title: 'Progress Analytics',
      icon: TrendingUp,
      description: 'Understand trends in weight, performance, and recovery with clear metrics.',
    },
  ],
  [
    {
      title: 'Meal Plan Builder',
      icon: Utensils,
      description: 'Build weekly meal plans around calories, protein goals, and food preferences.',
    },
    {
      title: 'Adaptive Workouts',
      icon: Dumbbell,
      description: 'Plans auto-adjust from your recent sessions, missed days, and recovery load.',
    },
    {
      title: 'Weekly Insights',
      icon: TrendingUp,
      description: 'Get concise action items that help improve nutrition and training outcomes.',
    },
  ],
]

const totalPages = computed(() => featurePages.length)
const visibleCards = computed(() => featurePages[currentPage.value])

const nextPage = () => {
  currentPage.value = (currentPage.value + 1) % totalPages.value
}

const prevPage = () => {
  currentPage.value = (currentPage.value - 1 + totalPages.value) % totalPages.value
}
</script>

<template>
  <section id="features" ref="sectionRef" class="h-screen snap-start overflow-hidden bg-background">
    <div class="mx-auto flex h-full max-w-7xl flex-col justify-center px-6 pt-24 pb-10 lg:px-12">
      <div class="mb-10 flex flex-col gap-4 lg:mb-12 lg:flex-row lg:items-end lg:justify-between">
        <h2
          class="max-w-2xl text-balance text-4xl font-black text-foreground transition-all duration-700 md:text-5xl"
          :class="isVisible ? 'translate-y-0 opacity-100' : 'translate-y-8 opacity-0'"
        >
          Made for Your Fitness Journey
        </h2>
        <p
          class="max-w-md text-sm text-muted-foreground transition-all duration-700 md:text-base"
          :class="isVisible ? 'translate-y-0 opacity-100' : 'translate-y-8 opacity-0'"
          style="transition-delay: 100ms"
        >
          From workout plans to nutrition tracking, Nutrify helps you build consistent habits with data that actually guides decisions.
        </p>
      </div>

      <div class="grid gap-4 md:grid-cols-3 md:gap-6">
        <Card
          v-for="(feature, index) in visibleCards"
          :key="`${feature.title}-${currentPage}`"
          class="rounded-2xl border-border bg-secondary/30 transition-all duration-700"
          :class="isVisible ? 'translate-y-0 opacity-100' : 'translate-y-10 opacity-0'"
          :style="{ transitionDelay: `${200 + index * 150}ms` }"
        >
          <div class="m-4 flex h-44 items-center justify-center rounded-xl bg-muted/50 md:h-52">
            <component :is="feature.icon" class="h-20 w-20 text-primary/90" />
          </div>
          <div class="flex items-center justify-between px-6 pb-6 pt-1">
            <div>
              <p class="text-lg font-bold text-foreground">{{ feature.title }}</p>
              <p class="mt-1 text-xs text-muted-foreground md:text-sm">{{ feature.description }}</p>
            </div>
            <button
              type="button"
              class="ml-3 inline-flex h-10 w-10 shrink-0 items-center justify-center rounded-full border border-border text-foreground transition hover:bg-accent"
            >
              <Plus class="h-4 w-4" />
            </button>
          </div>
        </Card>
      </div>

      <div
        class="mt-6 flex items-center justify-between transition-all duration-700"
        :class="isVisible ? 'opacity-100' : 'opacity-0'"
        style="transition-delay: 650ms"
      >
        <p class="text-sm text-muted-foreground">{{ String(currentPage + 1).padStart(2, '0') }} / {{ String(totalPages).padStart(2, '0') }}</p>
        <div class="flex items-center gap-2">
          <Button variant="outline" size="icon" class="rounded-full" @click="prevPage">
            <ArrowLeft class="h-4 w-4" />
          </Button>
          <Button size="icon" class="rounded-full" @click="nextPage">
            <ArrowRight class="h-4 w-4" />
          </Button>
        </div>
      </div>
    </div>
  </section>
</template>
