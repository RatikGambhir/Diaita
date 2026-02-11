<script setup lang="ts">
import * as z from "zod";
import { toTypedSchema } from '@vee-validate/zod'
import { useForm, Field as FormField } from 'vee-validate'
import { supabase } from "~/utils";
import { useUserStore } from "~/stores/useUserStore";
import type {AuthError} from "@supabase/supabase-js";
import Button from '~/components/ui/button/Button.vue'
import Input from '~/components/ui/input/Input.vue'
import FormItem from '~/components/ui/form/FormItem.vue'
import FormControl from '~/components/ui/form/FormControl.vue'
import FormMessage from '~/components/ui/form/FormMessage.vue'
import { ChevronLeft, Mail } from 'lucide-vue-next'

definePageMeta({
    layout: false,
});

export interface UserSession {
  error: AuthError | null,
  user: object | null,
  session: object | null
}

const userStore = useUserStore();

const toast = useToast();

const schema = toTypedSchema(z.object({
    email: z.string().email("Invalid email"),
}));

const { handleSubmit } = useForm({
  validationSchema: schema,
});

const router = useRouter();
const route = useRoute();

async function login(email: string): Promise<UserSession> {
    const { data, error } = await supabase.auth.signInWithOtp({
        email: email,
    });
    if (error) {
        return {
          error: error,
          user: null,
          session: null
        }
    }
        toast.add({
            title: "Success",
            description: "Continuing with email login...",
            color: "primary",
        });
        return {
          user: data.user,
          session: data.session,
          error: error
        }

}

const onSubmit = handleSubmit(async (values) => {
    const { email } = values;
    const {user, session, error} = await login(email)
    if(error) {
      toast.add({
        title: "Uh oh, something went wrong with your email, please try again",
        description: error.message,
        color: "error",
      });
    } else {
      userStore.addUserSession(user ?? {}, session ?? {});

      const redirectPath = route.query.redirect as string | undefined

      await router.push({
        path: "/verify-email",
        query: {
          email: values.email,
          ...(redirectPath && { redirect: redirectPath }),
        },
      });
    }
});
</script>

<template>
    <div class="min-h-screen flex">
        <div
            class="hidden lg:flex lg:w-1/2 relative overflow-hidden bg-cover bg-center bg-no-repeat"
            style="background-image: url('/assets/fitness-bento.jpeg')"
        >
            <div class="absolute bottom-12 left-12 max-w-lg z-10">
                <p class="text-white text-2xl font-medium mb-4">
                    "This platform has helped me to save time and serve my
                    clients faster than ever before."
                </p>
                <p class="text-white/80 text-sm">Amanda Go - Yoga Instructor</p>
            </div>
        </div>

        <div
            class="w-full lg:w-1/2 flex items-center justify-center p-8 bg-background"
        >
            <div class="w-full max-w-md">
                <div class="mb-16">
                    <NuxtLink to="/" class="flex items-center gap-2">
                        <span
                            class="text-xl font-bold text-foreground"
                            >Diaita</span
                        >
                    </NuxtLink>
                </div>

                <NuxtLink
                    to="/"
                    class="inline-flex items-center gap-2 text-sm text-muted-foreground hover:text-foreground mb-12"
                >
                    <ChevronLeft class="w-4 h-4" />
                    Home
                </NuxtLink>

                <div class="space-y-8">
                    <div>
                        <h1
                            class="text-3xl font-bold text-foreground mb-2"
                        >
                            Sign In or Join Now!
                        </h1>
                        <p class="text-muted-foreground">
                            login or create your efferd account.
                        </p>
                    </div>

                    <div class="relative">
                        <div class="absolute inset-0 flex items-center">
                            <div
                                class="w-full border-t border-border"
                            />
                        </div>
                        <div class="relative flex justify-center text-sm">
                            <span
                                class="px-2 bg-background text-muted-foreground"
                                >OR</span
                            >
                        </div>
                    </div>

                    <div class="space-y-4">
                        <p class="text-sm text-muted-foreground">
                            Enter your email address to sign in or create an
                            account
                        </p>

                        <form class="space-y-4" @submit="onSubmit">
                            <FormField v-slot="{ componentField }" name="email">
                                <FormItem>
                                    <FormControl>
                                        <div class="relative">
                                            <Mail class="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                                            <Input
                                                v-bind="componentField"
                                                type="email"
                                                placeholder="your.email@example.com"
                                                class="h-11 pl-10 w-full"
                                            />
                                        </div>
                                    </FormControl>
                                    <FormMessage />
                                </FormItem>
                            </FormField>

                            <Button
                                type="submit"
                                size="lg"
                                class="w-full font-medium"
                            >
                                Continue With Email
                            </Button>
                        </form>

                        <p
                            class="text-xs text-muted-foreground text-center"
                        >
                            By clicking continue, you agree to our
                            <NuxtLink
                                to="/terms"
                                class="underline hover:text-foreground"
                                >Terms of Service</NuxtLink
                            >
                            and
                            <NuxtLink
                                to="/privacy"
                                class="underline hover:text-foreground"
                                >Privacy Policy</NuxtLink
                            >.
                        </p>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>
