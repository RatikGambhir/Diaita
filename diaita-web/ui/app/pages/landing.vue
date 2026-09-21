<script setup lang="ts">
import { Apple, ArrowRight, Dumbbell, LockKeyhole, TrendingUp } from "lucide-vue-next";

definePageMeta({ layout: "landing" });

const features = [
  {
    title: "Daily nutrition",
    description: "Log custom foods across breakfast, lunch, dinner, and snacks, then review calories and macro totals for any day.",
    icon: Apple,
  },
  {
    title: "Workout history",
    description: "Record exercises, sets, reps, weight, time, distance, and notes while Diaita calculates recent training volume.",
    icon: Dumbbell,
  },
  {
    title: "Progress at a glance",
    description: "See today's nutrition and your latest workout activity from one focused dashboard.",
    icon: TrendingUp,
  },
  {
    title: "First-party accounts",
    description: "Your account, profile, meals, and workouts are stored by the Kotlin service in its local SQLite database.",
    icon: LockKeyhole,
  },
];
</script>

<template>
  <div class="min-h-screen bg-background text-foreground">
    <header class="border-b bg-background/95 backdrop-blur">
      <nav class="mx-auto flex h-16 max-w-6xl items-center justify-between px-6">
        <NuxtLink to="/landing" class="text-xl font-bold">Diaita</NuxtLink>
        <div class="flex items-center gap-3">
          <NuxtLink to="/login" class="rounded-lg px-4 py-2 text-sm font-medium hover:bg-muted">Sign in</NuxtLink>
          <NuxtLink to="/register" class="rounded-lg bg-primary px-4 py-2 text-sm font-medium text-primary-foreground hover:bg-primary/90">Create account</NuxtLink>
        </div>
      </nav>
    </header>

    <main>
      <section class="mx-auto grid max-w-6xl gap-12 px-6 py-20 lg:grid-cols-[1.05fr_.95fr] lg:items-center lg:py-28">
        <div>
          <p class="mb-4 text-sm font-semibold uppercase tracking-[0.2em] text-primary">Nutrition and workout tracking</p>
          <h1 class="max-w-3xl text-5xl font-bold leading-tight tracking-tight sm:text-6xl">Know what you ate. Know how you trained.</h1>
          <p class="mt-6 max-w-2xl text-lg leading-8 text-muted-foreground">Diaita is a focused health journal for tracking daily nutrients, workout sessions, and the progress those habits create.</p>
          <div class="mt-8 flex flex-wrap gap-3">
            <NuxtLink to="/register" class="inline-flex items-center rounded-lg bg-primary px-5 py-3 font-medium text-primary-foreground hover:bg-primary/90">Start tracking <ArrowRight class="ml-2 h-4 w-4" /></NuxtLink>
            <NuxtLink to="/login" class="rounded-lg border px-5 py-3 font-medium hover:bg-muted">I already have an account</NuxtLink>
          </div>
        </div>

        <div class="rounded-3xl border bg-card p-6 shadow-xl">
          <div class="rounded-2xl bg-muted p-6">
            <p class="text-sm text-muted-foreground">One place for the details that matter</p>
            <div class="mt-5 grid gap-3 sm:grid-cols-2">
              <div class="rounded-xl bg-background p-4"><Apple class="mb-5 h-6 w-6 text-primary" /><p class="font-semibold">Calories and macros</p><p class="mt-1 text-sm text-muted-foreground">Daily meal-by-meal totals</p></div>
              <div class="rounded-xl bg-background p-4"><Dumbbell class="mb-5 h-6 w-6 text-primary" /><p class="font-semibold">Training volume</p><p class="mt-1 text-sm text-muted-foreground">Sessions and exercise metrics</p></div>
            </div>
          </div>
        </div>
      </section>

      <section class="border-y bg-muted/40">
        <div class="mx-auto max-w-6xl px-6 py-20">
          <div class="max-w-2xl"><p class="text-sm font-semibold uppercase tracking-[0.2em] text-primary">Built for consistency</p><h2 class="mt-3 text-3xl font-bold">Everything visible has a job.</h2><p class="mt-3 text-muted-foreground">No placeholder dashboards or invented progress—just your saved data.</p></div>
          <div class="mt-10 grid gap-4 md:grid-cols-2">
            <article v-for="feature in features" :key="feature.title" class="rounded-2xl border bg-card p-6">
              <component :is="feature.icon" class="h-6 w-6 text-primary" />
              <h3 class="mt-5 text-lg font-semibold">{{ feature.title }}</h3>
              <p class="mt-2 leading-7 text-muted-foreground">{{ feature.description }}</p>
            </article>
          </div>
        </div>
      </section>

      <section class="mx-auto max-w-6xl px-6 py-20 text-center">
        <h2 class="text-3xl font-bold">Ready to start your log?</h2>
        <p class="mx-auto mt-3 max-w-xl text-muted-foreground">Create an account and record your first meal or workout in a few minutes.</p>
        <NuxtLink to="/register" class="mt-7 inline-flex items-center rounded-lg bg-primary px-5 py-3 font-medium text-primary-foreground hover:bg-primary/90">Create your account <ArrowRight class="ml-2 h-4 w-4" /></NuxtLink>
      </section>
    </main>

    <footer class="border-t"><div class="mx-auto flex max-w-6xl items-center justify-between px-6 py-8 text-sm text-muted-foreground"><span>Diaita</span><span>Vue · Kotlin · SQLite</span></div></footer>
  </div>
</template>
