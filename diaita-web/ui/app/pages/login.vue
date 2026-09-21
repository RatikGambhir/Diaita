<script setup lang="ts">
import axios from "axios";
import * as z from "zod";
import { toTypedSchema } from "@vee-validate/zod";
import { Field as FormField, useForm } from "vee-validate";
import { AlertCircle, ChevronLeft, LoaderCircle, LockKeyhole, Mail } from "lucide-vue-next";
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
  <div class="min-h-screen lg:grid lg:grid-cols-2">
    <div
      class="hidden bg-cover bg-center lg:block"
      style="background-image: linear-gradient(180deg, transparent, rgb(15 23 42 / .72)), url('/assets/fitness-bento.jpeg')"
    />
    <main class="flex min-h-screen items-center justify-center bg-background p-8">
      <div class="w-full max-w-md space-y-8">
        <NuxtLink to="/landing" class="inline-flex items-center gap-2 text-sm text-muted-foreground hover:text-foreground">
          <ChevronLeft class="h-4 w-4" /> Back to Diaita
        </NuxtLink>
        <div>
          <h1 class="text-3xl font-bold">Welcome back</h1>
          <p class="mt-2 text-muted-foreground">Sign in to continue tracking your nutrition and training.</p>
        </div>
        <Alert v-if="errorMessage" variant="destructive">
          <AlertCircle class="h-4 w-4" />
          <AlertTitle>Sign in failed</AlertTitle>
          <AlertDescription>{{ errorMessage }}</AlertDescription>
        </Alert>
        <form class="space-y-5" @submit="onSubmit">
          <FormField v-slot="{ componentField }" name="email">
            <FormItem>
              <FormControl>
                <div class="relative">
                  <Mail class="absolute left-3 top-3.5 h-4 w-4 text-muted-foreground" />
                  <Input v-bind="componentField" type="email" autocomplete="email" placeholder="you@example.com" class="h-11 pl-10" />
                </div>
              </FormControl>
              <FormMessage />
            </FormItem>
          </FormField>
          <FormField v-slot="{ componentField }" name="password">
            <FormItem>
              <FormControl>
                <div class="relative">
                  <LockKeyhole class="absolute left-3 top-3.5 h-4 w-4 text-muted-foreground" />
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
        <p class="text-center text-sm text-muted-foreground">
          New to Diaita?
          <NuxtLink to="/register" class="font-medium text-foreground hover:underline">Create an account</NuxtLink>
        </p>
      </div>
    </main>
  </div>
</template>
