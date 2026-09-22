<script setup lang="ts">
import { CheckCircle2, CircleAlert, Info, TriangleAlert, X } from "lucide-vue-next"

const { toasts, remove } = useToast()

const iconClass = (color: string | undefined) => {
  if (color === "error") return "text-destructive"
  if (color === "info") return "text-muted-foreground"
  return "text-primary"
}
</script>

<template>
  <div
    aria-label="Notifications"
    class="pointer-events-none fixed inset-x-4 top-4 z-[100] flex flex-col items-end gap-3 sm:left-auto sm:w-full sm:max-w-sm"
  >
    <TransitionGroup name="toast">
      <section
        v-for="toast in toasts"
        :key="toast.id"
        :role="toast.color === 'error' ? 'alert' : 'status'"
        aria-atomic="true"
        class="pointer-events-auto relative w-full rounded-lg border bg-card p-4 pr-12 text-card-foreground shadow-lg"
      >
        <div class="flex items-start gap-3">
          <CheckCircle2 v-if="toast.color === 'success'" class="mt-0.5 h-5 w-5 shrink-0" :class="iconClass(toast.color)" />
          <CircleAlert v-else-if="toast.color === 'error'" class="mt-0.5 h-5 w-5 shrink-0" :class="iconClass(toast.color)" />
          <TriangleAlert v-else-if="toast.color === 'warning'" class="mt-0.5 h-5 w-5 shrink-0" :class="iconClass(toast.color)" />
          <Info v-else class="mt-0.5 h-5 w-5 shrink-0" :class="iconClass(toast.color)" />
          <div class="min-w-0">
            <p class="text-sm font-semibold">{{ toast.title }}</p>
            <p v-if="toast.description" class="mt-1 text-sm leading-5 text-muted-foreground">
              {{ toast.description }}
            </p>
          </div>
        </div>
        <button
          type="button"
          class="absolute right-1 top-1 flex h-10 w-10 items-center justify-center rounded-md text-muted-foreground transition-colors hover:bg-muted hover:text-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
          :aria-label="`Dismiss ${toast.title}`"
          @click="remove(toast.id)"
        >
          <X class="h-4 w-4" />
        </button>
      </section>
    </TransitionGroup>
  </div>
</template>

<style scoped>
.toast-enter-active,
.toast-leave-active {
  transition:
    opacity 180ms cubic-bezier(0.23, 1, 0.32, 1),
    transform 180ms cubic-bezier(0.23, 1, 0.32, 1);
}

.toast-enter-from,
.toast-leave-to {
  opacity: 0;
  transform: translateY(-8px) scale(0.98);
}

@media (prefers-reduced-motion: reduce) {
  .toast-enter-active,
  .toast-leave-active {
    transition: opacity 120ms linear;
  }

  .toast-enter-from,
  .toast-leave-to {
    transform: none;
  }
}
</style>
