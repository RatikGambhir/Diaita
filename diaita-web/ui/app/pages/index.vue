<script setup lang="ts">
import type { Range } from '~/types'
import { useUserStore } from "~/stores/useUserStore"
import Button from '~/components/ui/button/Button.vue'
import { Bell, Plus, Send, UserPlus } from 'lucide-vue-next'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger
} from '~/components/ui/dropdown-menu'
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger
} from '~/components/ui/tooltip'
import DashboardStatsGrid from '~/components/dashboard/DashboardStatsGrid.vue'
import DashboardVisitorsChart from '~/components/dashboard/DashboardVisitorsChart.vue'
import StatsRow from '~/components/dashboard/StatsRow.vue'
import HealthyRestaurantsCard from '~/components/dashboard/HealthyRestaurantsCard.vue'
import GeneratorCard from '~/components/generator/GeneratorCard.vue'

const items = [
  { label: 'New mail', icon: Send, to: '/inbox' },
  { label: 'New customer', icon: UserPlus, to: '/customers' }
]

//TODO: Prefetch user data
const userStore = useUserStore()
const user = userStore.user
const session = userStore.session

// Load user profile if not already loaded
const { fetchProfile } = useUserProfile()
onMounted(async () => {
  if (!userStore.getProfile) {
    await fetchProfile()
  }
})

const range = shallowRef<Range>({
  start: new Date(Date.now() - 13 * 24 * 60 * 60 * 1000),
  end: new Date()
})
</script>


<template>
  <div class="flex-1 flex flex-col overflow-hidden">
    <!-- Header -->
    <header class="border-b bg-background">
      <div class="flex h-16 items-center justify-between px-6">
        <h1 class="text-xl font-semibold">Home</h1>

        <div class="flex items-center gap-3">
          <TooltipProvider>
            <Tooltip>
              <TooltipTrigger as-child>
                <Button variant="ghost" size="icon" class="relative">
                  <Bell class="h-5 w-5" />
                  <span class="absolute -top-1 -right-1 h-3 w-3 bg-destructive rounded-full" />
                </Button>
              </TooltipTrigger>
              <TooltipContent>
                <p>Notifications (N)</p>
              </TooltipContent>
            </Tooltip>
          </TooltipProvider>

          <DropdownMenu>
            <DropdownMenuTrigger as-child>
              <Button size="icon" class="rounded-full">
                <Plus class="h-4 w-4" />
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end">
              <DropdownMenuItem v-for="item in items" :key="item.label" as-child>
                <NuxtLink :to="item.to" class="flex items-center gap-2">
                  <component :is="item.icon" class="h-4 w-4" />
                  {{ item.label }}
                </NuxtLink>
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        </div>
      </div>

      <!-- Toolbar -->
      <div class="flex items-center gap-4 px-6 py-3 border-t">
        <HomeDateRangePicker v-model="range" />
      </div>
    </header>

    <!-- Body -->
    <div class="flex-1 overflow-auto p-6">
      <div class="mb-6">
        <ProfileSetupBanner />
      </div>
      <DashboardStatsGrid />
      <DashboardVisitorsChart />

      <!-- Stats Row (Calories, Steps, Sleep) -->
      <div class="mt-6">
        <StatsRow />
      </div>

      <!-- Healthy Restaurants Card -->
      <div class="mt-6">
        <HealthyRestaurantsCard />
      </div>

      <!-- Generator Card (Workout/Meal Plan) -->
      <div class="mt-6">
        <GeneratorCard />
      </div>
    </div>
  </div>
</template>
