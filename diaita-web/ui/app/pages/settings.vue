<script setup lang="ts">
import { LoaderCircle, Save, UserRound } from "lucide-vue-next";
import { userApi } from "~/api/user";
import { useUserStore } from "~/stores/useUserStore";
import Button from "~/components/ui/button/Button.vue";
import Card from "~/components/ui/card/Card.vue";
import CardContent from "~/components/ui/card/CardContent.vue";
import CardHeader from "~/components/ui/card/CardHeader.vue";
import Input from "~/components/ui/input/Input.vue";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "~/components/ui/select";
import { ACTIVITY_LEVEL_OPTIONS, PRIMARY_GOAL_OPTIONS } from "~/lib/profileOptions";

const userStore = useUserStore();
const toast = useToast();
const isLoading = ref(true);
const isSaving = ref(false);
const form = reactive({ age: 18, height: 170, weight: 70, primaryGoal: "", activityLevel: "" });

onMounted(async () => {
  try {
    const profile = await useUserProfile().fetchProfile({ force: true });
    if (profile) Object.assign(form, {
      age: profile.age,
      height: profile.height,
      weight: profile.weight,
      primaryGoal: profile.primaryGoal,
      activityLevel: profile.activityLevel,
    });
  } finally {
    isLoading.value = false;
  }
});

const save = async () => {
  const profile = userStore.getProfile;
  if (!profile) {
    await navigateTo("/setup-profile");
    return;
  }
  isSaving.value = true;
  try {
    const response = await userApi.createUserProfile({
      ...profile,
      age: Number(form.age),
      height: Number(form.height),
      weight: Number(form.weight),
      primaryGoal: form.primaryGoal.trim(),
      activityLevel: form.activityLevel.trim(),
    });
    userStore.setProfile(response.profile);
    userStore.setRecommendation(response.recommendation);
    toast.add({ title: "Settings saved", description: "Your profile and targets were updated.", color: "success" });
  } catch (error) {
    toast.add({ title: "Unable to save settings", description: error instanceof Error ? error.message : "Please try again.", color: "error" });
  } finally {
    isSaving.value = false;
  }
};
</script>

<template>
  <div class="app-page max-w-5xl space-y-8">
      <AppPageHeader eyebrow="Preferences" title="Settings" description="Keep the identity and health details behind your recommendations accurate." />

      <section>
        <div class="mb-4 flex items-center gap-3"><UserRound class="h-5 w-5 text-primary" /><div><p class="eyebrow">Identity</p><h2 class="display-title mt-1 text-2xl">Account</h2></div></div>
        <div class="grid border-y sm:grid-cols-2 sm:divide-x">
          <div class="py-5 sm:px-6 sm:first:pl-0"><p class="text-xs font-semibold uppercase tracking-[0.1em] text-muted-foreground">Name</p><p class="mt-2 font-medium">{{ userStore.getUser?.displayName }}</p></div>
          <div class="border-t py-5 sm:border-t-0 sm:px-6"><p class="text-xs font-semibold uppercase tracking-[0.1em] text-muted-foreground">Email</p><p class="mt-2 break-all font-medium">{{ userStore.getUser?.email }}</p></div>
        </div>
      </section>

      <Card class="overflow-hidden">
        <CardHeader class="border-b"><p class="eyebrow">Recommendation inputs</p><h2 class="display-title mt-2 text-3xl">Health profile</h2><p class="mt-1 text-sm text-muted-foreground">These values shape your saved nutrition and training recommendation.</p></CardHeader>
        <CardContent class="pt-6">
          <div v-if="isLoading" class="py-10 text-center text-muted-foreground">Loading settings…</div>
          <div v-else-if="!userStore.getProfile" class="py-8 text-center"><p class="mb-4 text-muted-foreground">Complete onboarding before editing your health profile.</p><Button @click="navigateTo('/setup-profile')">Set up profile</Button></div>
          <form v-else class="space-y-5" @submit.prevent="save">
            <div class="grid gap-4 sm:grid-cols-3">
              <label class="space-y-2"><span class="text-sm font-medium">Age</span><Input v-model.number="form.age" type="number" min="13" max="120" /></label>
              <label class="space-y-2"><span class="text-sm font-medium">Height (cm)</span><Input v-model.number="form.height" type="number" min="1" step="0.1" /></label>
              <label class="space-y-2"><span class="text-sm font-medium">Weight (kg)</span><Input v-model.number="form.weight" type="number" min="1" step="0.1" /></label>
            </div>
            <label class="block space-y-2">
              <span class="text-sm font-medium">Primary goal</span>
              <Select v-model="form.primaryGoal">
                <SelectTrigger><SelectValue placeholder="Select your main goal" /></SelectTrigger>
                <SelectContent>
                  <SelectItem v-for="option in PRIMARY_GOAL_OPTIONS" :key="option.value" :value="option.value">
                    {{ option.label }}
                  </SelectItem>
                </SelectContent>
              </Select>
            </label>
            <label class="block space-y-2">
              <span class="text-sm font-medium">Activity level</span>
              <Select v-model="form.activityLevel">
                <SelectTrigger><SelectValue placeholder="Select your activity level" /></SelectTrigger>
                <SelectContent>
                  <SelectItem v-for="option in ACTIVITY_LEVEL_OPTIONS" :key="option.value" :value="option.value">
                    {{ option.label }}
                  </SelectItem>
                </SelectContent>
              </Select>
            </label>
            <div class="flex justify-end border-t pt-5"><Button type="submit" :disabled="isSaving"><LoaderCircle v-if="isSaving" class="h-4 w-4 animate-spin" /><Save v-else class="h-4 w-4" />Save changes</Button></div>
          </form>
        </CardContent>
      </Card>
  </div>
</template>
