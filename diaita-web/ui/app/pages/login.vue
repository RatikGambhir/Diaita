<script setup lang="ts">
import axios from "axios";
import * as z from "zod";
import { toTypedSchema } from "@vee-validate/zod";
import { Field as FormField, useForm } from "vee-validate";
import { AlertCircle, ChevronLeft, Check, LoaderCircle, LockKeyhole, Mail } from "lucide-vue-next";
import { authApi } from "~/api/auth";
import { useUserStore } from "~/stores/useUserStore";
import { Alert, AlertDescription, AlertTitle } from "~/components/ui/alert";
import Button from "~/components/ui/button/Button.vue";
import Input from "~/components/ui/input/Input.vue";
import FormItem from "~/components/ui/form/FormItem.vue";
import FormControl from "~/components/ui/form/FormControl.vue";
import FormMessage from "~/components/ui/form/FormMessage.vue";

definePageMeta({ layout: false });

const schema = toTypedSchema(z.object({
  email: z.string().email("Enter a valid email"),
  password: z.string().min(8, "Password must be at least 8 characters"),
}));
const { handleSubmit } = useForm({ validationSchema: schema });
const userStore = useUserStore();
const router = useRouter();
const route = useRoute();
const isLoading = ref(false);
const errorMessage = ref("");

const onSubmit = handleSubmit(async (values) => {
  isLoading.value = true;
  errorMessage.value = "";
  try {
    userStore.setSession(await authApi.login(values));
    const profile = await useUserProfile().fetchProfile({ force: true });
    const redirect = typeof route.query.redirect === "string" ? route.query.redirect : null;
    await router.push(redirect || (profile ? "/" : "/setup-profile"));
  } catch (error: unknown) {
    errorMessage.value = axios.isAxiosError(error)
      ? error.response?.data?.message || "Invalid email or password"
      : "Unable to sign in right now";
  } finally {
    isLoading.value = false;
  }
});
</script>

<template>
  <div class="min-h-screen lg:grid lg:grid-cols-[minmax(20rem,.8fr)_minmax(32rem,1.2fr)]">
    <aside class="hidden min-h-screen flex-col bg-foreground p-10 text-background lg:flex xl:p-14">
      <NuxtLink to="/landing" class="focus-ring w-fit rounded-md"><BrandMark inverse /></NuxtLink>
      <div class="mt-auto max-w-md">
        <p class="eyebrow text-background/45">Return to the record</p>
        <p class="display-title mt-5 text-5xl leading-[1.05]">The useful pattern is the one you can see.</p>
        <ul class="mt-8 space-y-3 text-sm text-background/65">
          <li class="flex items-center gap-3"><Check class="h-4 w-4 text-sidebar-primary" />Meals and macros by day</li>
          <li class="flex items-center gap-3"><Check class="h-4 w-4 text-sidebar-primary" />Workouts, volume, and history</li>
          <li class="flex items-center gap-3"><Check class="h-4 w-4 text-sidebar-primary" />Your data on your own service</li>
        </ul>
      </div>
    </aside>
    <main class="flex min-h-screen items-center justify-center bg-background px-5 py-12 sm:p-10">
      <div class="w-full max-w-[28rem] space-y-8">
        <NuxtLink to="/landing" class="inline-flex items-center gap-2 text-sm text-muted-foreground hover:text-foreground">
          <ChevronLeft class="h-4 w-4" /> Back to Diaita
        </NuxtLink>
        <div>
          <p class="eyebrow mb-3">Member access</p>
          <h1 class="display-title text-5xl">Welcome back.</h1>
          <p class="mt-3 leading-7 text-muted-foreground">Sign in to continue your nutrition and training journal.</p>
        </div>
        <Alert v-if="errorMessage" variant="destructive">
          <AlertCircle class="h-4 w-4" />
          <AlertTitle>Sign in failed</AlertTitle>
          <AlertDescription>{{ errorMessage }}</AlertDescription>
        </Alert>
        <form class="space-y-5" novalidate @submit="onSubmit">
          <FormField v-slot="{ componentField }" name="email">
            <FormItem>
              <p class="mb-2 text-sm font-semibold">Email address</p>
              <FormControl>
                <div class="relative">
                  <Mail class="absolute left-3.5 top-3.5 h-4 w-4 text-muted-foreground" />
                  <Input v-bind="componentField" type="email" autocomplete="email" placeholder="you@example.com" class="h-11 pl-10" />
                </div>
              </FormControl>
              <FormMessage />
            </FormItem>
          </FormField>
          <FormField v-slot="{ componentField }" name="password">
            <FormItem>
              <p class="mb-2 text-sm font-semibold">Password</p>
              <FormControl>
                <div class="relative">
                  <LockKeyhole class="absolute left-3.5 top-3.5 h-4 w-4 text-muted-foreground" />
                  <Input v-bind="componentField" type="password" autocomplete="current-password" placeholder="Password" class="h-11 pl-10" />
                </div>
              </FormControl>
              <FormMessage />
            </FormItem>
          </FormField>
          <Button type="submit" size="lg" class="w-full" :disabled="isLoading">
            <LoaderCircle v-if="isLoading" class="mr-2 h-4 w-4 animate-spin" />
            {{ isLoading ? "Signing in…" : "Sign in" }}
          </Button>
        </form>
        <p class="border-t pt-6 text-center text-sm text-muted-foreground">
          New to Diaita?
          <NuxtLink to="/register" class="font-medium text-foreground hover:underline">Create an account</NuxtLink>
        </p>
      </div>
    </main>
  </div>
</template>
