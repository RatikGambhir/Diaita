<script setup lang="ts">
import axios from "axios";
import * as z from "zod";
import { toTypedSchema } from "@vee-validate/zod";
import { Field as FormField, useForm } from "vee-validate";
import { AlertCircle, ChevronLeft, LoaderCircle, LockKeyhole, Mail, User } from "lucide-vue-next";
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
  displayName: z.string().trim().min(2, "Name must be at least 2 characters").max(80),
  email: z.string().email("Enter a valid email"),
  password: z.string()
    .min(8, "Password must be at least 8 characters")
    .regex(/[A-Za-z]/, "Password needs a letter")
    .regex(/[0-9]/, "Password needs a number"),
  confirmPassword: z.string(),
}).refine((value) => value.password === value.confirmPassword, {
  path: ["confirmPassword"],
  message: "Passwords do not match",
}));
const { handleSubmit } = useForm({ validationSchema: schema });
const userStore = useUserStore();
const router = useRouter();
const isLoading = ref(false);
const errorMessage = ref("");

const onSubmit = handleSubmit(async ({ displayName, email, password }) => {
  isLoading.value = true;
  errorMessage.value = "";
  try {
    userStore.setSession(await authApi.register({ displayName, email, password }));
    await router.push("/setup-profile");
  } catch (error: unknown) {
    errorMessage.value = axios.isAxiosError(error)
      ? error.response?.data?.message || "Unable to create your account"
      : "Unable to create your account";
  } finally {
    isLoading.value = false;
  }
});
</script>

<template>
  <div class="min-h-screen lg:grid lg:grid-cols-2">
    <div
      class="hidden bg-cover bg-center lg:block"
      style="background-image: linear-gradient(180deg, transparent, rgb(15 23 42 / .72)), url('/assets/preportionedhealthyfood.jpg')"
    />
    <main class="flex min-h-screen items-center justify-center bg-background p-8">
      <div class="w-full max-w-md space-y-7">
        <NuxtLink to="/landing" class="inline-flex items-center gap-2 text-sm text-muted-foreground hover:text-foreground">
          <ChevronLeft class="h-4 w-4" /> Back to Diaita
        </NuxtLink>
        <div>
          <h1 class="text-3xl font-bold">Create your account</h1>
          <p class="mt-2 text-muted-foreground">Your data stays in the SQLite database you run.</p>
        </div>
        <Alert v-if="errorMessage" variant="destructive">
          <AlertCircle class="h-4 w-4" />
          <AlertTitle>Registration failed</AlertTitle>
          <AlertDescription>{{ errorMessage }}</AlertDescription>
        </Alert>
        <form class="space-y-4" @submit="onSubmit">
          <FormField v-slot="{ componentField }" name="displayName">
            <FormItem><FormControl><div class="relative">
              <User class="absolute left-3 top-3.5 h-4 w-4 text-muted-foreground" />
              <Input v-bind="componentField" autocomplete="name" placeholder="Your name" class="h-11 pl-10" />
            </div></FormControl><FormMessage /></FormItem>
          </FormField>
          <FormField v-slot="{ componentField }" name="email">
            <FormItem><FormControl><div class="relative">
              <Mail class="absolute left-3 top-3.5 h-4 w-4 text-muted-foreground" />
              <Input v-bind="componentField" type="email" autocomplete="email" placeholder="you@example.com" class="h-11 pl-10" />
            </div></FormControl><FormMessage /></FormItem>
          </FormField>
          <FormField v-slot="{ componentField }" name="password">
            <FormItem><FormControl><div class="relative">
              <LockKeyhole class="absolute left-3 top-3.5 h-4 w-4 text-muted-foreground" />
              <Input v-bind="componentField" type="password" autocomplete="new-password" placeholder="Password" class="h-11 pl-10" />
            </div></FormControl><FormMessage /></FormItem>
          </FormField>
          <FormField v-slot="{ componentField }" name="confirmPassword">
            <FormItem><FormControl><div class="relative">
              <LockKeyhole class="absolute left-3 top-3.5 h-4 w-4 text-muted-foreground" />
              <Input v-bind="componentField" type="password" autocomplete="new-password" placeholder="Confirm password" class="h-11 pl-10" />
            </div></FormControl><FormMessage /></FormItem>
          </FormField>
          <Button type="submit" size="lg" class="w-full" :disabled="isLoading">
            <LoaderCircle v-if="isLoading" class="mr-2 h-4 w-4 animate-spin" />
            {{ isLoading ? "Creating account…" : "Create account" }}
          </Button>
        </form>
        <p class="text-center text-sm text-muted-foreground">
          Already have an account?
          <NuxtLink to="/login" class="font-medium text-foreground hover:underline">Sign in</NuxtLink>
        </p>
      </div>
    </main>
  </div>
</template>
