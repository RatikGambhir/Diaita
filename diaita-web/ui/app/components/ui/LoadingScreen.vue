<script setup lang="ts">
import { Loader2 } from 'lucide-vue-next'

interface Props {
  show: boolean
  message?: string
}

const props = withDefaults(defineProps<Props>(), {
  message: 'Setting up your profile...',
})

const videoFailed = ref(false)
const animateIn = ref(false)

watch(() => props.show, (visible) => {
  if (visible) {
    nextTick(() => { animateIn.value = true })
  } else {
    animateIn.value = false
  }
})
</script>

<template>
  <Teleport to="body">
    <Transition
      enter-active-class="transition-opacity duration-300 ease-out"
      enter-from-class="opacity-0"
      enter-to-class="opacity-100"
      leave-active-class="transition-opacity duration-300 ease-in"
      leave-from-class="opacity-100"
      leave-to-class="opacity-0"
    >
      <div
        v-if="show"
        class="fixed inset-0 z-50 flex flex-col items-center justify-center bg-background/95 backdrop-blur-sm"
      >
        <p
          class="mb-4 text-xl font-semibold text-foreground transition-all duration-500 ease-out"
          :class="animateIn ? 'opacity-100 translate-y-0' : 'opacity-0 -translate-y-2'"
        >
          We're cooking your profile right now, hang tight!
        </p>

        <div
          class="transition-transform duration-500 ease-out"
          :class="animateIn ? 'scale-100' : 'scale-90'"
        >
          <video
            v-if="!videoFailed"
            autoplay
            loop
            muted
            playsinline
            class="w-64 h-64"
            @error="videoFailed = true"
          >
            <source src="/assets/Loading 48 _ Mortar & Pestle.webm" type="video/webm">
          </video>

          <Loader2
            v-else
            class="w-16 h-16 text-primary animate-spin"
          />
        </div>

        <p
          class="mt-6 text-lg font-medium text-foreground transition-all duration-700 delay-200 ease-out"
          :class="animateIn ? 'opacity-100 translate-y-0' : 'opacity-0 translate-y-2'"
        >
          {{ message }}
        </p>
      </div>
    </Transition>
  </Teleport>
</template>
