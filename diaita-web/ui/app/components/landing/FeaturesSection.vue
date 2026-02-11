<script setup lang="ts">
import { computed, ref } from 'vue'
import { ArrowLeft, ArrowRight, Plus } from 'lucide-vue-next'
import Button from '~/components/ui/button/Button.vue'
import Card from '~/components/ui/card/Card.vue'
import { useScrollAnimation } from '~/composables/useScrollAnimation'

const { sectionRef, isVisible } = useScrollAnimation(0.25)

const currentPage = ref(0)

const featurePages = [
  [
    {
      title: 'Smart Fitness Tracking',
      image: '/assets/ropeworkout-landiing.jpg',
      description: 'Quickly add workouts and excercises, track performance improvements, measure consistency',
    },
    {
      title: 'Nutrition tracking',
      image: '/assets/cuttingfood-landing.png',
      description: 'Search and Add foods from most groceries stores and restaurants, track calroies and macros, and gain insights to eating patterns over time',
    },
    {
      title: 'Workout planning',
      image: '/assets/img.png',
      description: 'Escaping the boring by generating workout plans based on your current starting point and goals, easily share save and share workout plans, and easily find workouts with video instructions',
    },
  ],
  [
    {
      title: 'Meal planning',
      image: '/assets/preportionedhealthyfood.jpg',
      description: 'Get day by day meal plans with prioritized foods, save and share recipes, and get enhance performance metrics with each plan',
    },
    {
      title: 'Smart Watch Integrations',
      image: '/assets/smart-watch-landing.jpg',
      description: 'easily sync with your favorite smart health devices, at no cost!',
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
          From workout plans to nutrition tracking, Diaita helps you build consistent habits with data that actually guides decisions.
        </p>
      </div>

      <div class="grid gap-4 md:gap-6" :class="visibleCards.length === 2 ? 'md:mx-auto md:max-w-5xl md:grid-cols-2' : 'md:grid-cols-3'">
        <Card
          v-for="(feature, index) in visibleCards"
          :key="`${feature.title}-${currentPage}`"
          class="rounded-2xl border-border bg-secondary/30 transition-all duration-700"
          :class="isVisible ? 'translate-y-0 opacity-100' : 'translate-y-10 opacity-0'"
          :style="{ transitionDelay: `${200 + index * 150}ms` }"
        >
          <div class="m-4 h-44 overflow-hidden rounded-xl bg-muted/50 md:h-52">
            <img
              :src="feature.image"
              :alt="feature.title"
              class="h-full w-full object-cover object-center"
            >
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
