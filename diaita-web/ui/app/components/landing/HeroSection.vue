<script setup lang="ts">
import { ArrowRight, CheckCircle2, Dumbbell, Lightbulb, Sparkles, Utensils } from 'lucide-vue-next'
import Button from '~/components/ui/button/Button.vue'
import Card from '~/components/ui/card/Card.vue'

const mounted = ref(false)

const bulletPoints = [
  'AI-powered nutrition tracking that adapts to your goals',
  'Personalized workout plans for strength, mobility, and endurance',
  'Daily insight cards for calories, macros, and recovery trends',
]

onMounted(() => {
  requestAnimationFrame(() => {
    mounted.value = true
  })
})

const showClass = (delay: number) => {
  return {
    class: mounted.value ? 'translate-y-0 opacity-100' : 'translate-y-8 opacity-0',
    style: { transitionDelay: `${delay}ms` },
  }
}

const goToLogin = () => {
  navigateTo('/login')
}

const goToFeatures = () => {
  document.getElementById('features')?.scrollIntoView({ behavior: 'smooth', block: 'start' })
}
</script>

<template>
  <section id="hero" class="h-screen snap-start overflow-hidden bg-background">
    <div class="mx-auto grid h-full max-w-7xl grid-cols-1 items-center gap-10 px-6 pt-28 pb-10 lg:grid-cols-2 lg:px-12">
      <div class="space-y-6">
        <div
          class="inline-flex items-center gap-2 rounded-full border border-border bg-card px-4 py-2 text-sm text-muted-foreground transition-all duration-700"
          :class="showClass(0).class"
          :style="showClass(0).style"
        >
          <Sparkles class="h-4 w-4 text-primary" />
          Built for consistent fitness progress
        </div>

        <h1
          class="text-balance text-5xl font-black leading-tight text-foreground transition-all duration-700 md:text-6xl lg:text-7xl"
          :class="showClass(100).class"
          :style="showClass(100).style"
        >
          Your Fitness Tracking,
          <br>
          Meal Plans, and Insights in One Place.
        </h1>

        <p
          class="max-w-xl text-lg text-muted-foreground transition-all duration-700"
          :class="showClass(200).class"
          :style="showClass(200).style"
        >
          Log workouts, track macros, and follow smarter meal plans with a single dashboard designed to help you build momentum every week.
        </p>

        <div
          class="flex flex-wrap items-center gap-3 transition-all duration-700"
          :class="showClass(300).class"
          :style="showClass(300).style"
        >
          <Button variant="outline" class="rounded-full px-6" @click="goToFeatures">
            <Lightbulb class="h-4 w-4" />
            Learn More
          </Button>
          <Button class="rounded-full px-6" @click="goToLogin">
            Get Started
            <ArrowRight class="h-4 w-4" />
          </Button>
        </div>

        <ul
          class="space-y-2 transition-all duration-700"
          :class="showClass(400).class"
          :style="showClass(400).style"
        >
          <li v-for="point in bulletPoints" :key="point" class="flex items-start gap-2 text-sm text-muted-foreground md:text-base">
            <CheckCircle2 class="mt-0.5 h-4 w-4 shrink-0 text-emerald-500" />
            <span>{{ point }}</span>
          </li>
        </ul>
      </div>

      <div
        class="relative transition-all duration-700"
        :class="mounted ? 'scale-100 opacity-100' : 'scale-95 opacity-0'"
        style="transition-delay: 500ms"
      >
        <Card class="relative h-[470px] rounded-3xl border-border bg-card p-6 shadow-xl md:h-[510px]">
          <div class="rounded-2xl border border-border bg-muted/50 p-5">
            <div class="flex items-center justify-between">
              <p class="text-sm text-muted-foreground">Today Overview</p>
              <Sparkles class="h-5 w-5 text-primary" />
            </div>
            <p class="mt-2 text-3xl font-bold text-foreground">1,980 kcal</p>
            <p class="text-sm text-muted-foreground">Remaining target: 320 kcal</p>
          </div>

          <div class="mt-5 grid gap-4 md:grid-cols-2">
            <div class="rounded-xl border border-border bg-card p-4 shadow-sm">
              <div class="mb-2 flex items-center gap-2 text-sm font-semibold text-foreground">
                <Utensils class="h-4 w-4 text-primary" />
                Calorie Tracking
              </div>
              <p class="text-xs text-muted-foreground">Breakfast, lunch, and dinner logged with macro split and hydration score.</p>
            </div>

            <div class="rounded-xl border border-border bg-card p-4 shadow-sm">
              <div class="mb-2 flex items-center gap-2 text-sm font-semibold text-foreground">
                <Dumbbell class="h-4 w-4 text-primary" />
                Workout Plans
              </div>
              <p class="text-xs text-muted-foreground">Push/Pull/Legs progression with volume targets and weekly load trends.</p>
            </div>
          </div>

          <div class="mt-5 rounded-xl border border-border bg-accent/50 p-4">
            <p class="text-sm font-semibold text-foreground">Enhanced Insights</p>
            <p class="mt-1 text-xs text-muted-foreground">Recovery is trending up 14%. Suggested: increase lower-body volume by 1 set this week.</p>
          </div>

          <div class="pointer-events-none absolute -right-3 -top-3 h-20 w-20 rounded-full bg-primary/20 blur-xl" />
          <div class="pointer-events-none absolute -left-4 bottom-6 h-24 w-24 rounded-full bg-accent/50 blur-2xl" />
        </Card>
      </div>
    </div>
  </section>
</template>
