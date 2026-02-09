import { useIntersectionObserver } from '@vueuse/core'
import { ref } from 'vue'

export function useScrollAnimation(threshold = 0.2) {
  const sectionRef = ref<HTMLElement | null>(null)
  const isVisible = ref(false)

  useIntersectionObserver(
    sectionRef,
    ([{ isIntersecting }]) => {
      if (isIntersecting) {
        isVisible.value = true
      }
    },
    { threshold },
  )

  return { sectionRef, isVisible }
}
