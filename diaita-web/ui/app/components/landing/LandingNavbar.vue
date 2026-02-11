<script setup lang="ts">
import { Menu } from 'lucide-vue-next'
import Button from '~/components/ui/button/Button.vue'
import Sheet from '~/components/ui/sheet/Sheet.vue'
import SheetContent from '~/components/ui/sheet/SheetContent.vue'
import SheetHeader from '~/components/ui/sheet/SheetHeader.vue'
import SheetTitle from '~/components/ui/sheet/SheetTitle.vue'
import SheetTrigger from '~/components/ui/sheet/SheetTrigger.vue'

const scrolled = ref(false)
const mobileOpen = ref(false)

const navItems = [
  { label: 'Features', id: 'features' },
  { label: 'Testimonials', id: 'testimonials' },
  { label: 'Contact', id: 'contact' },
]

let scrollEl: HTMLElement | Window | null = null
let onScroll: (() => void) | null = null

const updateScrolledState = () => {
  if (scrollEl instanceof Window) {
    scrolled.value = scrollEl.scrollY > 10
    return
  }

  scrolled.value = (scrollEl as HTMLElement | null)?.scrollTop ? (scrollEl as HTMLElement).scrollTop > 10 : false
}

const scrollToSection = (id: string) => {
  document.getElementById(id)?.scrollIntoView({ behavior: 'smooth', block: 'start' })
  mobileOpen.value = false
}

const goToLogin = () => {
  navigateTo('/login')
}

onMounted(() => {
  scrollEl = (document.querySelector('.snap-container') as HTMLElement | null) ?? window
  onScroll = () => updateScrolledState()

  if (scrollEl instanceof Window) {
    scrollEl.addEventListener('scroll', onScroll, { passive: true })
  } else {
    scrollEl.addEventListener('scroll', onScroll, { passive: true })
  }

  updateScrolledState()
})

onBeforeUnmount(() => {
  if (!scrollEl || !onScroll) {
    return
  }

  if (scrollEl instanceof Window) {
    scrollEl.removeEventListener('scroll', onScroll)
  } else {
    scrollEl.removeEventListener('scroll', onScroll)
  }
})
</script>

<template>
  <header class="pointer-events-none fixed inset-x-0 top-0 z-50">
    <div class="mx-auto mt-4 w-full max-w-6xl px-4">
      <div
        class="pointer-events-auto flex items-center justify-between rounded-full border px-4 py-3 shadow-lg backdrop-blur-md transition-all duration-300 md:px-6"
        :class="scrolled ? 'border-border bg-card/95' : 'border-border/70 bg-card/80'"
      >
        <button
          type="button"
          class="text-xl font-bold text-foreground transition-opacity hover:opacity-80"
          @click="scrollToSection('hero')"
        >
          Diaita
        </button>

        <nav class="hidden items-center gap-6 md:flex">
          <button
            v-for="item in navItems"
            :key="item.id"
            type="button"
            class="text-sm font-medium text-muted-foreground transition-colors hover:text-foreground"
            @click="scrollToSection(item.id)"
          >
            {{ item.label }}
          </button>
        </nav>

        <div class="hidden md:block">
          <Button class="rounded-full px-6" @click="goToLogin">
            Get Started
          </Button>
        </div>

        <Sheet v-model:open="mobileOpen">
          <SheetTrigger as-child>
            <Button variant="ghost" size="icon" class="md:hidden">
              <Menu class="h-5 w-5" />
            </Button>
          </SheetTrigger>
          <SheetContent side="right" class="w-[280px] bg-card">
            <SheetHeader>
              <SheetTitle>Diaita</SheetTitle>
            </SheetHeader>
            <div class="mt-8 flex flex-col gap-2">
              <Button
                v-for="item in navItems"
                :key="item.id"
                variant="ghost"
                class="justify-start"
                @click="scrollToSection(item.id)"
              >
                {{ item.label }}
              </Button>
              <Button class="mt-2" @click="goToLogin">
                Get Started
              </Button>
            </div>
          </SheetContent>
        </Sheet>
      </div>
    </div>
  </header>
</template>
