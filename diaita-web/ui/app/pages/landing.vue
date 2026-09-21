<script setup lang="ts">
import { Apple, ArrowRight, Check, Dumbbell, LockKeyhole, TrendingUp } from "lucide-vue-next";

definePageMeta({ layout: "landing" });

const features = [
  { title: "Daily nutrition", description: "Log custom foods across breakfast, lunch, dinner, and snacks, then review calories and macro totals for any day.", icon: Apple },
  { title: "Workout history", description: "Record exercises, sets, reps, weight, time, distance, and notes while Diaita calculates recent training volume.", icon: Dumbbell },
  { title: "Progress at a glance", description: "See today’s nutrition and your latest workout activity from one focused home view.", icon: TrendingUp },
  { title: "Private by construction", description: "Your account, profile, meals, and workouts stay in the SQLite database run by your own Kotlin service.", icon: LockKeyhole },
];
</script>

<template>
  <div class="min-h-screen bg-background text-foreground">
    <header class="sticky top-0 z-40 border-b bg-background/95 backdrop-blur-md">
      <nav class="mx-auto flex h-17 max-w-7xl items-center justify-between px-5 sm:px-8">
        <NuxtLink to="/landing" class="focus-ring rounded-md"><BrandMark /></NuxtLink>
        <div class="flex items-center gap-1 sm:gap-3">
          <NuxtLink to="/login" class="focus-ring rounded-md px-3 py-2 text-sm font-semibold hover:bg-muted sm:px-4">Sign in</NuxtLink>
          <NuxtLink to="/register" class="focus-ring rounded-md border border-primary bg-primary px-3 py-2 text-sm font-semibold text-primary-foreground shadow-xs hover:bg-primary/90 sm:px-4">Create account</NuxtLink>
        </div>
      </nav>
    </header>

    <main>
      <section class="mx-auto grid max-w-7xl gap-14 px-5 py-16 sm:px-8 sm:py-24 lg:grid-cols-[1.1fr_.9fr] lg:items-end lg:py-28">
        <div>
          <p class="eyebrow mb-5 text-primary">A private performance journal</p>
          <h1 class="display-title max-w-4xl text-6xl leading-[0.94] sm:text-7xl lg:text-[5.5rem]">A clearer record of how you live.</h1>
          <p class="mt-7 max-w-2xl text-lg leading-8 text-muted-foreground sm:text-xl">Diaita brings meals, macros, training sessions, and your plan into one deliberate daily practice—without the noise of a social fitness app.</p>
          <div class="mt-9 flex flex-col gap-3 sm:flex-row">
            <NuxtLink to="/register" class="focus-ring inline-flex min-h-12 items-center justify-center gap-2 rounded-md border border-primary bg-primary px-5 font-semibold text-primary-foreground shadow-xs hover:bg-primary/90">Start your journal <ArrowRight class="h-4 w-4" /></NuxtLink>
            <NuxtLink to="/login" class="focus-ring inline-flex min-h-12 items-center justify-center rounded-md border bg-card px-5 font-semibold hover:bg-muted">Continue where you left off</NuxtLink>
          </div>
        </div>

        <div class="overflow-hidden rounded-xl bg-foreground text-background shadow-xl">
          <div class="flex items-start justify-between border-b border-background/15 px-6 py-5">
            <div><p class="eyebrow text-background/45">Sample day</p><p class="mt-2 font-semibold">Tuesday, September 22</p></div>
            <span class="font-mono text-xs text-background/45">DAY 143</span>
          </div>
          <div class="px-6 py-7">
            <div class="flex items-end justify-between border-b border-background/15 pb-7">
              <div><p class="font-mono text-5xl font-semibold tracking-[-0.06em]">2,140</p><p class="mt-1 text-xs text-background/45">kilocalories</p></div>
              <div class="text-right"><p class="font-mono text-2xl font-semibold text-chart-3">148g</p><p class="text-xs text-background/45">protein</p></div>
            </div>
            <div class="space-y-4 py-6">
              <div class="grid grid-cols-[5rem_1fr_2rem] items-center gap-3 text-xs"><span class="text-background/55">Carbs</span><span class="h-2 bg-chart-1" style="width: 74%" /><span class="font-mono">214</span></div>
              <div class="grid grid-cols-[5rem_1fr_2rem] items-center gap-3 text-xs"><span class="text-background/55">Protein</span><span class="h-2 bg-chart-3" style="width: 62%" /><span class="font-mono">148</span></div>
              <div class="grid grid-cols-[5rem_1fr_2rem] items-center gap-3 text-xs"><span class="text-background/55">Fat</span><span class="h-2 bg-chart-2" style="width: 48%" /><span class="font-mono">67</span></div>
            </div>
            <div class="grid grid-cols-[2.5rem_1fr_auto] items-center gap-3 border-t border-background/15 pt-5">
              <span class="grid h-10 w-10 place-items-center bg-background/10"><Dumbbell class="h-5 w-5 text-sidebar-primary" /></span>
              <div><p class="text-sm font-semibold">Upper body · strength</p><p class="mt-0.5 text-xs text-background/45">52 min · 6 exercises</p></div>
              <Check class="h-5 w-5 text-chart-1" />
            </div>
          </div>
        </div>
      </section>

      <section class="border-y bg-card">
        <div class="mx-auto max-w-7xl px-5 py-20 sm:px-8 lg:py-28">
          <div class="grid gap-10 lg:grid-cols-[.75fr_1.25fr]">
            <div class="max-w-lg">
              <p class="eyebrow text-primary">Built for the habit</p>
              <h2 class="display-title mt-4 text-4xl leading-tight sm:text-5xl">Less dashboard. More useful record.</h2>
              <p class="mt-5 leading-7 text-muted-foreground">Every screen is organized around the next sensible action: log the meal, capture the set, understand the week.</p>
            </div>
            <div class="border-t">
              <article v-for="(feature, index) in features" :key="feature.title" class="grid gap-4 border-b py-6 sm:grid-cols-[3rem_3rem_1fr] sm:items-start">
                <span class="font-mono text-xs text-muted-foreground">{{ String(index + 1).padStart(2, '0') }}</span>
                <component :is="feature.icon" class="h-5 w-5 text-primary" />
                <div>
                  <h3 class="text-lg font-semibold">{{ feature.title }}</h3>
                  <p class="mt-2 max-w-2xl leading-7 text-muted-foreground">{{ feature.description }}</p>
                </div>
              </article>
            </div>
          </div>
        </div>
      </section>

      <section class="mx-auto max-w-7xl px-5 py-20 sm:px-8 lg:py-28">
        <div class="grid overflow-hidden rounded-xl bg-primary text-primary-foreground lg:grid-cols-[1fr_auto] lg:items-end">
          <div class="p-8 sm:p-12">
            <p class="eyebrow text-primary-foreground/60">Begin today</p>
            <h2 class="display-title mt-4 max-w-3xl text-4xl leading-tight sm:text-5xl">One honest entry is enough to start.</h2>
            <p class="mt-4 max-w-xl leading-7 text-primary-foreground/75">Create an account, record a meal or session, and let the journal become more useful with every day you return.</p>
          </div>
          <NuxtLink to="/register" class="focus-ring m-8 inline-flex min-h-12 items-center justify-center gap-2 rounded-md bg-primary-foreground px-5 font-semibold text-primary hover:bg-primary-foreground/90 sm:m-12">Create your account <ArrowRight class="h-4 w-4" /></NuxtLink>
        </div>
      </section>
    </main>

    <footer class="border-t"><div class="mx-auto flex max-w-7xl flex-col gap-3 px-5 py-8 text-sm text-muted-foreground sm:flex-row sm:items-center sm:justify-between sm:px-8"><BrandMark /><span>Private, focused, and self-hosted.</span></div></footer>
  </div>
</template>
