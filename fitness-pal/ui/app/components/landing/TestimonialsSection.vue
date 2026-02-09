<script setup lang="ts">
import { ArrowRight, Quote } from 'lucide-vue-next'
import Button from '~/components/ui/button/Button.vue'
import Card from '~/components/ui/card/Card.vue'

const { sectionRef, isVisible } = useScrollAnimation(0.25)

const testimonials = [
  {
    description: 'This platform not only helped me with everything in fitness, it also keep me happy and engaged!',
    name: 'Ratik Gambhir',
    image: '/assets/ratik-testimonial.jpg',
  },
  {
    description: 'With Law school and leading many yoga classes per week, this platform saved me so much time and gave me so much insight along the way. don\'t know what I would\'ve done without it!',
    name: 'Amanda Go',
    image: '/assets/amanda-testimonial.jpg',
  },
  {
    description: 'Getting ready for my wedding was a grueling process, but this platform made sure I was consistent and actually gave me good ideas on what to do better!',
    name: 'Tyler Simons',
    image: '/assets/tyler2.jpg',
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

      <div class="mx-auto grid w-full max-w-[1200px] gap-6 sm:grid-cols-2 lg:grid-cols-3">
        <Card
          v-for="(item, index) in testimonials"
          :key="item.name"
          class="rounded-xl border-border bg-card transition-all duration-700 w-full"
          :class="isVisible ? 'translate-y-0 opacity-100' : 'translate-y-10 opacity-0'"
          :style="{ transitionDelay: `${250 + index * 150}ms` }"
        >
          <div class="relative m-3 aspect-[4/4] overflow-hidden rounded-lg bg-muted/50">
            <img
              :src="item.image"
              :alt="item.name"
              class="h-full w-full object-cover object-center"
            >
            <div class="absolute inset-0 flex items-start justify-end bg-black/5 p-3">
              <Quote class="h-6 w-6 text-primary" />
            </div>
          </div>
          <div class="px-4 pt-1">
            <p class="text-sm font-semibold leading-snug text-primary">"{{ item.description }}"</p>
          </div>
          <div class="mt-3 px-4 pb-4">
            <p class="text-sm font-bold text-foreground">{{ item.name }}</p>
          </div>
        </Card>
      </div>
    </div>
  </section>
</template>
