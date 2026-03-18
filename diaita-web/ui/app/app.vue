<script setup lang="ts">
import { computed } from "vue"

const route = useRoute()

const pageTransition = computed(() => {
  const layout = route.meta.layout
  if (layout === false || layout === "landing") {
    return false
  }

  return {
    mode: "out-in",
    enterActiveClass: "transition-all duration-300 ease-out",
    enterFromClass: "opacity-0 translate-x-5",
    enterToClass: "opacity-100 translate-x-0",
    leaveActiveClass: "transition-all duration-250 ease-in",
    leaveFromClass: "opacity-100 translate-x-0",
    leaveToClass: "opacity-0 -translate-x-5",
  }
})
</script>

<template>
  <div>
    <NuxtLayout>
      <NuxtPage :transition="pageTransition" />
    </NuxtLayout>
  </div>
</template>
