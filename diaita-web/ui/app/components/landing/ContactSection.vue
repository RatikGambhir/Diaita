<script setup lang="ts">
import { CornerDownRight, Instagram, Linkedin, Mail } from 'lucide-vue-next'
import { useScrollAnimation } from '~/composables/useScrollAnimation'

const { sectionRef, isVisible } = useScrollAnimation(0.25)
</script>

<template>
  <section id="contact" ref="sectionRef" class="relative h-screen snap-start overflow-hidden bg-background">
    <div class="mx-auto grid h-full max-w-7xl grid-cols-1 gap-6 px-6 pt-24 pb-14 lg:grid-cols-2 lg:gap-0 lg:px-12">
      <div class="flex h-full flex-col justify-center lg:pr-10">
        <h2
          class="text-5xl font-black text-foreground transition-all duration-700 md:text-6xl"
          :class="isVisible ? 'translate-x-0 opacity-100' : '-translate-x-8 opacity-0'"
        >
          Stay Connected
        </h2>
        <p
          class="mt-5 max-w-md text-base text-muted-foreground transition-all duration-700 md:text-lg"
          :class="isVisible ? 'translate-x-0 opacity-100' : '-translate-x-8 opacity-0'"
          style="transition-delay: 100ms"
        >
          Tell us your goals and we will help you build a personalized plan for workout routines, meal plans, and performance tracking.
        </p>
        <button
          type="button"
          class="mt-6 inline-flex items-center gap-2 text-base font-medium text-foreground transition hover:text-primary"
          :class="isVisible ? 'translate-x-0 opacity-100' : '-translate-x-8 opacity-0'"
          style="transition-delay: 200ms"
        >
          Get in touch
          <CornerDownRight class="h-4 w-4" />
        </button>

        <div
          class="mt-10 space-y-3 transition-all duration-700"
          :class="isVisible ? 'translate-x-0 opacity-100' : '-translate-x-8 opacity-0'"
          style="transition-delay: 300ms"
        >
          <p class="text-2xl font-bold text-foreground md:text-3xl">hello@diaita.app</p>
          <div class="flex items-center gap-3 text-muted-foreground">
            <a href="#" class="inline-flex h-9 w-9 items-center justify-center rounded-full border border-border transition hover:bg-accent">
              <Mail class="h-4 w-4" />
            </a>
            <a href="#" class="inline-flex h-9 w-9 items-center justify-center rounded-full border border-border transition hover:bg-accent">
              <Instagram class="h-4 w-4" />
            </a>
            <a href="#" class="inline-flex h-9 w-9 items-center justify-center rounded-full border border-border transition hover:bg-accent">
              <Linkedin class="h-4 w-4" />
            </a>
          </div>
        </div>
      </div>

      <div
        class="relative h-full overflow-hidden rounded-3xl bg-muted transition-all duration-700"
        :class="isVisible ? 'scale-100 opacity-100' : 'scale-95 opacity-0'"
      >
        <div class="absolute inset-0 bg-gradient-to-br from-accent/30 via-primary/10 to-secondary/40" />
        <div class="absolute -right-10 top-14 h-52 w-52 rounded-3xl bg-primary/80" />
        <div class="absolute left-10 top-28 h-36 w-44 rounded-2xl bg-card/85" />
        <div class="absolute bottom-20 left-20 h-44 w-56 rounded-2xl bg-accent/70" />
        <div class="absolute bottom-10 right-16 h-24 w-32 rounded-xl bg-card/90" />
      </div>
    </div>

    <div class="absolute inset-x-0 bottom-4 text-center text-xs text-muted-foreground md:text-sm">
      2026 Diaita. All rights reserved.
    </div>
  </section>
</template>
