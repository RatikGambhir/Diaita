<script setup lang="ts">
definePageMeta({
  layout: 'landing',
})
</script>

<template>
  <div class="snap-container h-screen snap-y snap-mandatory overflow-y-auto bg-background">
    <LandingNavbar />
    <HeroSection />
    <FeaturesSection />
    <TestimonialsSection />
    <ContactSection />
  </div>
</template>
