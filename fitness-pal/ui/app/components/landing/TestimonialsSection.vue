<script setup lang="ts">
import { ArrowRight, Quote, UserRound } from 'lucide-vue-next'
import Button from '~/components/ui/button/Button.vue'
import Card from '~/components/ui/card/Card.vue'

const { sectionRef, isVisible } = useScrollAnimation(0.25)

const testimonials = [
  {
    quote: 'Nutrify helped me lose 30 pounds in 3 months with a plan I could actually stick to.',
    name: 'Sarah M.',
    role: 'Fitness Enthusiast',
  },
  {
    quote: 'The macro tracking is the best I have ever used. My clients finally stay consistent.',
    name: 'James K.',
    role: 'Personal Trainer',
  },
  {
    quote: 'Finally an app that makes calorie counting effortless and keeps me motivated each week.',
    name: 'Maria L.',
    role: 'Nutrition Coach',
  },
  {
    quote: 'I have tried everything, and Nutrify is the one that stuck because insights are actionable.',
    name: 'David R.',
    role: 'Marathon Runner',
  },
]
</script>

<template>
  <section id="testimonials" ref="sectionRef" class="h-screen snap-start overflow-hidden bg-background">
    <div class="mx-auto flex h-full max-w-7xl flex-col justify-center px-6 pt-24 pb-10 lg:px-12">
      <div class="mb-8 md:mb-10">
        <h2
          class="text-balance text-4xl font-bold text-foreground transition-all duration-700 md:text-5xl"
          :class="isVisible ? 'translate-y-0 opacity-100' : 'translate-y-8 opacity-0'"
        >
          Trusted by Fitness Enthusiasts
        </h2>
        <p
          class="mt-3 max-w-2xl text-sm text-muted-foreground transition-all duration-700 md:text-lg"
          :class="isVisible ? 'translate-y-0 opacity-100' : 'translate-y-8 opacity-0'"
          style="transition-delay: 100ms"
        >
          Thousands of members use Nutrify to improve workouts, nutrition, and long-term consistency.
        </p>
        <Button
          variant="outline"
          class="mt-5 rounded-full"
          :class="isVisible ? 'translate-y-0 opacity-100' : 'translate-y-8 opacity-0'"
          style="transition-delay: 200ms"
        >
          Read our success stories
          <ArrowRight class="h-4 w-4" />
        </Button>
      </div>

      <div class="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <Card
          v-for="(item, index) in testimonials"
          :key="item.name"
          class="rounded-xl border-border bg-card transition-all duration-700"
          :class="isVisible ? 'translate-y-0 opacity-100' : 'translate-y-10 opacity-0'"
          :style="{ transitionDelay: `${250 + index * 150}ms` }"
        >
          <div class="m-3 aspect-[3/4] rounded-lg bg-gradient-to-br from-muted to-accent/40 p-4">
            <div class="flex h-full items-start justify-between">
              <UserRound class="h-6 w-6 text-muted-foreground" />
              <Quote class="h-6 w-6 text-primary" />
            </div>
          </div>
          <div class="px-4 pt-1">
            <p class="text-sm font-semibold leading-snug text-primary">"{{ item.quote }}"</p>
          </div>
          <div class="mt-3 px-4 pb-4">
            <p class="text-sm font-bold text-foreground">{{ item.name }}</p>
            <p class="text-xs text-muted-foreground">{{ item.role }}</p>
          </div>
        </Card>
      </div>
    </div>
  </section>
</template>
