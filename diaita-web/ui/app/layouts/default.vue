<script setup lang="ts">
import { Home, Dumbbell, Utensils, Settings, MessageCircle, LogOut, Search, PanelLeftClose, PanelLeft } from 'lucide-vue-next'
import Button from '~/components/ui/button/Button.vue'
import { supabase } from '~/utils'
import { useUserStore } from '~/stores/useUserStore'

const route = useRoute()
const toast = useToast()
const userStore = useUserStore()

const sidebarCollapsed = ref(false)

const mainNavItems = [
  { label: 'Home', icon: Home, to: '/' },
  { label: 'Workouts', icon: Dumbbell, to: '/workouts' },
  { label: 'Nutrition', icon: Utensils, to: '/nutrition' },
  { label: 'Settings', icon: Settings, to: '/settings' }
]

const secondaryNavItems = [
  { label: 'Feedback', icon: MessageCircle, to: 'https://github.com/nuxt-ui-pro/dashboard', external: true }
]

const toggleSidebar = () => {
  sidebarCollapsed.value = !sidebarCollapsed.value
}

const signOut = async () => {
  const { error } = await supabase.auth.signOut()
  userStore.clearSession()

  if (error) {
    toast.add({
      title: 'Signed out locally',
      description: `Could not fully sign out from Supabase: ${error.message}`,
      color: 'warning'
    })
  } else {
    toast.add({
      title: 'Signed out',
      description: 'Your session has ended.',
      color: 'success'
    })
  }

  await navigateTo('/login')
}

onMounted(async () => {
  const cookie = useCookie('cookie-consent')
  if (cookie.value === 'accepted') {
    return
  }

  toast.add({
    title: 'We use first-party cookies to enhance your experience on our website.',
    description: 'Click Accept to continue.',
    color: 'info'
  })
  cookie.value = 'accepted'
})
</script>

<template>
  <div class="flex h-full bg-sidebar">
    <!-- Sidebar -->
    <aside
      class="flex flex-col h-screen border-r bg-sidebar text-sidebar-foreground transition-all duration-300"
      :class="sidebarCollapsed ? 'w-16' : 'w-64'"
    >
      <!-- Header -->
      <div class="flex h-16 items-center border-b px-4">
        <NuxtLink to="/" class="flex items-center gap-2">
          <span v-if="!sidebarCollapsed" class="text-lg font-bold text-sidebar-foreground">Diaita</span>
        </NuxtLink>
      </div>

      <!-- Search Button -->
      <div class="p-2">
        <Button
          variant="outline"
          class="bg-sidebar hover:bg-background text-sidebar-foreground hover:text-sidebar-accent-foreground"
          :class="sidebarCollapsed ? 'w-full justify-center' : 'w-full justify-start'"
        >
          <Search class="h-4 w-4" />
          <span v-if="!sidebarCollapsed" class="ml-2">Search...</span>
        </Button>
      </div>

      <!-- Main Navigation -->
      <nav class="flex-1 space-y-1 p-2">
        <NuxtLink
          v-for="item in mainNavItems"
          :key="item.to"
          :to="item.to"
          class="flex items-center gap-3 rounded-lg px-3 py-2 font-sans text-sidebar-foreground transition-colors hover:bg-sidebar-accent hover:text-sidebar-accent-foreground"
          :class="{
            'bg-sidebar-primary text-sidebar-primary-foreground': route.path === item.to || (item.to !== '/' && route.path.startsWith(item.to)),
            'justify-center': sidebarCollapsed
          }"
        >
          <component :is="item.icon" class="h-5 w-5 shrink-0" />
          <span v-if="!sidebarCollapsed">{{ item.label }}</span>
        </NuxtLink>
      </nav>

      <!-- Secondary Navigation -->
      <div class="border-t border-sidebar-border p-2">
        <a
          v-for="item in secondaryNavItems"
          :key="item.to"
          :href="item.to"
          target="_blank"
          rel="noopener noreferrer"
          class="flex items-center gap-3 rounded-lg px-3 py-2 font-sans text-sidebar-foreground/80 transition-colors hover:bg-sidebar-accent hover:text-sidebar-accent-foreground"
          :class="{ 'justify-center': sidebarCollapsed }"
        >
          <component :is="item.icon" class="h-5 w-5 shrink-0" />
          <span v-if="!sidebarCollapsed">{{ item.label }}</span>
        </a>
        <button
          type="button"
          class="mt-1 flex w-full items-center gap-3 rounded-lg px-3 py-2 font-sans text-sidebar-foreground/80 transition-colors hover:bg-sidebar-accent hover:text-sidebar-accent-foreground"
          :class="{ 'justify-center': sidebarCollapsed }"
          @click="signOut"
        >
          <LogOut class="h-5 w-5 shrink-0" />
          <span v-if="!sidebarCollapsed">Sign Out</span>
        </button>
      </div>

      <!-- Collapse Toggle -->
      <div class="border-t border-sidebar-border p-2">
        <Button
          variant="ghost"
          size="sm"
          class="font-sans text-sidebar-foreground hover:bg-sidebar-accent hover:text-sidebar-accent-foreground"
          :class="sidebarCollapsed ? 'w-full justify-center' : 'w-full justify-start'"
          @click="toggleSidebar"
        >
          <PanelLeftClose v-if="!sidebarCollapsed" class="h-4 w-4" />
          <PanelLeft v-else class="h-4 w-4" />
          <span v-if="!sidebarCollapsed" class="ml-2">Collapse</span>
        </Button>
      </div>
    </aside>

    <div class="flex-1 pb-3 pr-3 pt-4">
      <!-- Main Content -->
      <main class="relative z-10 h-full overflow-auto rounded-2xl bg-background shadow-lg">
        <slot />
      </main>
    </div>
  </div>
</template>
