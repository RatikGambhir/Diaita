<script setup lang="ts">
definePageMeta({
  layout: 'landing',
})

const router = useRouter();

const handleGetStarted = async () => {
  await router.push("/login")
}
</script>

<template>
  <div class="relative min-h-screen w-full overflow-hidden bg-background from-muted via-background to-muted">
    <HeroSection />
    <FeaturesSection />
    <BenefitsSection />
    <FeedbackSection />
  </div>
</template>
