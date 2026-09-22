<script setup lang="ts">
import { Dumbbell, Home, LogOut, Settings, UserCircle, Utensils } from "lucide-vue-next"
import { authApi } from "~/api/auth"
import { useUserStore } from "~/stores/useUserStore"

const route = useRoute()
const toast = useToast()
const userStore = useUserStore()

const mainNavItems = [
  { label: "Today", icon: Home, to: "/" },
  { label: "Nutrition", icon: Utensils, to: "/nutrition" },
  { label: "Training", icon: Dumbbell, to: "/workouts" },
  { label: "Plan", icon: UserCircle, to: "/profile" },
  { label: "Settings", icon: Settings, to: "/settings" },
]

const isActive = (to: string) => route.path === to || (to !== "/" && route.path.startsWith(to))

const initials = computed(() => {
  const name = userStore.getUser?.displayName?.trim() || "D"
  return name.split(/\s+/).slice(0, 2).map(part => part[0]?.toUpperCase()).join("")
})

const signOut = async () => {
  try {
    await authApi.logout()
  } catch {
    // The local token is cleared even if the service is unavailable.
  }
  userStore.clearSession()
  toast.add({ title: "Signed out", description: "Your session has ended.", color: "success" })
  await navigateTo("/login")
}
</script>

<template>
  <div class="min-h-screen bg-background lg:grid lg:grid-cols-[232px_minmax(0,1fr)]">
    <aside class="sticky top-0 hidden h-screen flex-col border-r border-sidebar-border bg-sidebar px-4 py-5 text-sidebar-foreground lg:flex">
      <NuxtLink to="/" class="focus-ring mb-10 w-fit rounded-md">
        <BrandMark inverse />
      </NuxtLink>

      <div class="eyebrow px-3 text-sidebar-foreground/45">Your journal</div>
      <nav class="mt-4 space-y-1" aria-label="Primary navigation">
        <NuxtLink
          v-for="item in mainNavItems"
          :key="item.to"
          :to="item.to"
          :aria-current="isActive(item.to) ? 'page' : undefined"
          :class="[
            'focus-ring group flex min-h-11 items-center gap-3 rounded-md px-3 text-sm font-medium transition-colors',
            isActive(item.to)
              ? 'bg-sidebar-accent text-sidebar-foreground'
              : 'text-sidebar-foreground/62 hover:bg-sidebar-accent/60 hover:text-sidebar-foreground',
          ]"
        >
          <span
            :class="[
              'h-5 w-[3px] transition-colors',
              isActive(item.to) ? 'bg-sidebar-primary' : 'bg-transparent group-hover:bg-sidebar-border',
            ]"
          />
          <component :is="item.icon" class="h-[18px] w-[18px] shrink-0" />
          <span>{{ item.label }}</span>
        </NuxtLink>
      </nav>

      <div class="mt-auto border-t border-sidebar-border pt-4">
        <div class="flex items-center gap-3 px-3 py-2">
          <span class="grid h-9 w-9 place-items-center rounded-full bg-sidebar-accent text-xs font-bold text-sidebar-primary">
            {{ initials }}
          </span>
          <div class="min-w-0 flex-1">
            <p class="truncate text-sm font-semibold">{{ userStore.getUser?.displayName || "Diaita member" }}</p>
            <p class="truncate text-xs text-sidebar-foreground/45">{{ userStore.getUser?.email }}</p>
          </div>
        </div>
        <button
          type="button"
          class="focus-ring mt-2 flex min-h-10 w-full items-center gap-3 rounded-md px-3 text-sm text-sidebar-foreground/55 transition-colors hover:bg-sidebar-accent hover:text-sidebar-foreground"
          @click="signOut"
        >
          <LogOut class="h-4 w-4" />
          Sign out
        </button>
      </div>
    </aside>

    <div class="min-w-0">
      <header class="sticky top-0 z-40 flex h-15 items-center justify-between border-b bg-background/95 px-5 backdrop-blur-md lg:hidden">
        <NuxtLink to="/" class="focus-ring rounded-md">
          <BrandMark />
        </NuxtLink>
        <NuxtLink to="/settings" class="focus-ring grid h-9 w-9 place-items-center rounded-full bg-foreground text-xs font-bold text-background" aria-label="Open settings">
          {{ initials }}
        </NuxtLink>
      </header>

      <main class="min-h-screen min-w-0">
        <slot />
      </main>
    </div>

    <nav class="fixed inset-x-3 bottom-3 z-40 grid h-17 grid-cols-5 border border-sidebar-border bg-sidebar px-1.5 text-sidebar-foreground shadow-xl lg:hidden" aria-label="Mobile navigation">
      <NuxtLink
        v-for="item in mainNavItems"
        :key="item.to"
        :to="item.to"
        :aria-current="isActive(item.to) ? 'page' : undefined"
        :class="[
          'focus-ring relative flex flex-col items-center justify-center gap-1 text-[10px] font-semibold transition-colors',
          isActive(item.to) ? 'text-sidebar-primary' : 'text-sidebar-foreground/55',
        ]"
      >
        <span v-if="isActive(item.to)" class="absolute inset-x-5 top-0 h-0.5 bg-sidebar-primary" />
        <component :is="item.icon" class="h-5 w-5" />
        <span>{{ item.label }}</span>
      </NuxtLink>
    </nav>
  </div>
</template>
