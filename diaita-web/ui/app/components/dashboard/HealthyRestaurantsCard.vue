<script setup lang="ts">
import { ref } from 'vue'
import { ChevronLeft, ChevronRight, UtensilsCrossed, MapPin } from 'lucide-vue-next'
import Button from '~/components/ui/button/Button.vue'
import RestaurantPill from './RestaurantPill.vue'

interface Restaurant {
  id: number
  name: string
  distance: string
  position: { top: string; left: string }
}

const restaurants = ref<Restaurant[]>([
  { id: 1, name: 'Green Bowl Cafe', distance: '0.2 mi', position: { top: '42%', left: '38%' } },
  { id: 2, name: 'Fresh & Fit Kitchen', distance: '0.6 mi', position: { top: '32%', left: '52%' } },
  { id: 3, name: 'Protein Bar & Grill', distance: '0.8 mi', position: { top: '45%', left: '78%' } },
  { id: 4, name: 'Vitality Juice Bar', distance: '0.4 mi', position: { top: '50%', left: '70%' } },
  { id: 5, name: 'Clean Eats Bistro', distance: '1.1 mi', position: { top: '38%', left: '80%' } }
])

const currentPage = ref(0)

const nextPage = () => {
  currentPage.value = (currentPage.value + 1) % 2
}

const prevPage = () => {
  currentPage.value = currentPage.value === 0 ? 1 : 0
}
</script>

<template>
  <div class="bg-card rounded-2xl shadow-sm overflow-hidden">
    <div class="flex items-center justify-between px-5 py-4 md:px-6">
      <div class="flex items-center gap-3">
        <div class="w-10 h-10 rounded-xl bg-emerald-100 dark:bg-emerald-900/30 flex items-center justify-center">
          <UtensilsCrossed class="w-5 h-5 text-emerald-600 dark:text-emerald-400" />
        </div>
        <div>
          <h3 class="text-base font-semibold text-foreground">Find Restaurants Near You</h3>
          <p class="text-sm text-muted-foreground">{{ restaurants.length }} locations in your area</p>
        </div>
      </div>

      <div class="flex items-center gap-2">
        <Button
          variant="ghost"
          size="icon"
          class="h-9 w-9 rounded-full bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 dark:hover:bg-slate-700"
          @click="prevPage"
        >
          <ChevronLeft class="h-4 w-4" />
        </Button>
        <Button
          variant="ghost"
          size="icon"
          class="h-9 w-9 rounded-full bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 dark:hover:bg-slate-700"
          @click="nextPage"
        >
          <ChevronRight class="h-4 w-4" />
        </Button>
      </div>
    </div>

    <div class="relative h-[320px] md:h-[400px] bg-slate-50 dark:bg-slate-900/50 mx-4 md:mx-5 rounded-xl overflow-hidden">
      <div class="absolute inset-0 grid-background opacity-50" />

      <div
        v-for="restaurant in restaurants"
        :key="restaurant.id"
        class="absolute transform -translate-x-1/2 -translate-y-full"
        :style="{ top: restaurant.position.top, left: restaurant.position.left }"
      >
        <div class="relative">
          <div class="w-8 h-10 bg-emerald-500 rounded-t-full rounded-b-lg flex items-center justify-center shadow-md">
            <MapPin class="w-4 h-4 text-white fill-white" />
          </div>
          <div class="absolute -bottom-1 left-1/2 transform -translate-x-1/2 w-2 h-2 bg-emerald-500 rotate-45" />
        </div>
      </div>

      <div
        class="absolute transform -translate-x-1/2 -translate-y-1/2"
        style="top: 52%; left: 55%"
      >
        <div class="flex flex-col items-center">
          <div class="px-2 py-0.5 bg-slate-700 dark:bg-slate-600 text-white text-xs rounded mb-1 font-medium">
            You
          </div>
          <div class="w-3 h-3 bg-blue-500 rounded-full border-2 border-white shadow-md" />
        </div>
      </div>
    </div>

    <div class="p-4 md:p-5">
      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-3">
        <RestaurantPill
          v-for="restaurant in restaurants.slice(0, 3)"
          :key="restaurant.id"
          :name="restaurant.name"
          :distance="restaurant.distance"
        />
      </div>
      <div class="grid grid-cols-1 sm:grid-cols-2 gap-3 mt-3">
        <RestaurantPill
          v-for="restaurant in restaurants.slice(3, 5)"
          :key="restaurant.id"
          :name="restaurant.name"
          :distance="restaurant.distance"
        />
      </div>
    </div>
  </div>
</template>

<style scoped>
.grid-background {
  background-image:
    linear-gradient(to right, var(--border) 1px, transparent 1px),
    linear-gradient(to bottom, var(--border) 1px, transparent 1px);
  background-size: 40px 40px;
}
</style>
