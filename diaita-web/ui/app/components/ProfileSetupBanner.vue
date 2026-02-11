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
  return !userStore.hasCompletedProfile && !isDismissed.value
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
  <Alert v-if="shouldShowBanner" class="relative bg-primary/10 border-primary/20">
    <UserCircle class="h-5 w-5" />
    <AlertTitle>Complete Your Profile</AlertTitle>
    <AlertDescription>
      Set up your fitness profile to get personalized recommendations and track your progress more effectively.
    </AlertDescription>
    <div class="mt-4 flex gap-2">
      <Button
        size="sm"
        @click="completeNow"
      >
        Complete Now
      </Button>
      <Button
        variant="ghost"
        size="sm"
        @click="remindLater"
      >
        Remind Me Later
      </Button>
    </div>
    <Button
      variant="ghost"
      size="icon"
      class="absolute top-2 right-2 h-6 w-6"
      @click="remindLater"
    >
      <X class="h-4 w-4" />
    </Button>
  </Alert>
</template>
