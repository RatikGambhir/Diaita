<script setup lang="ts">
import Alert from '~/components/ui/alert/Alert.vue'
import AlertTitle from '~/components/ui/alert/AlertTitle.vue'
import AlertDescription from '~/components/ui/alert/AlertDescription.vue'
import Button from '~/components/ui/button/Button.vue'
import { UserCircle, X } from 'lucide-vue-next'

const router = useRouter()
const userStore = useUserStore()

const DISMISS_COOKIE_NAME = 'profile-setup-dismissed'
const DISMISS_DURATION_DAYS = 7

const isDismissed = ref(false)

onMounted(() => {
  const dismissedUntil = useCookie(DISMISS_COOKIE_NAME).value
  if (dismissedUntil) {
    const dismissedDate = new Date(dismissedUntil as string)
    if (dismissedDate > new Date()) {
      isDismissed.value = true
    }
  }
})

const shouldShowBanner = computed(() => {
  return userStore.getProfileStatus === 'missing' && !isDismissed.value
})

const completeNow = () => {
  router.push('/setup-profile')
}

const remindLater = () => {
  const expiryDate = new Date()
  expiryDate.setDate(expiryDate.getDate() + DISMISS_DURATION_DAYS)

  const cookie = useCookie(DISMISS_COOKIE_NAME, {
    expires: expiryDate
  })
  cookie.value = expiryDate.toISOString()

  isDismissed.value = true
}
</script>

<template>
  <Alert v-if="shouldShowBanner" class="relative border-foreground bg-foreground text-background">
    <UserCircle class="h-5 w-5 text-sidebar-primary" />
    <AlertTitle class="text-background">Complete your profile</AlertTitle>
    <AlertDescription class="text-background/65">
      Add your goals and baseline details to unlock a personalized training recommendation.
    </AlertDescription>
    <div class="mt-4 flex gap-2">
      <Button
        size="sm"
        class="border-sidebar-primary bg-sidebar-primary text-sidebar-primary-foreground hover:bg-sidebar-primary/90"
        @click="completeNow"
      >
        Complete Now
      </Button>
      <Button
        variant="ghost"
        size="sm"
        class="text-background/65 hover:bg-background/10 hover:text-background"
        @click="remindLater"
      >
        Remind Me Later
      </Button>
    </div>
    <Button
      variant="ghost"
      size="icon"
      class="absolute right-2 top-2 h-7 w-7 text-background/55 hover:bg-background/10 hover:text-background"
      aria-label="Dismiss profile reminder"
      @click="remindLater"
    >
      <X class="h-4 w-4" />
    </Button>
  </Alert>
</template>
