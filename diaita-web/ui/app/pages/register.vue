<script setup lang="ts">
import * as z from "zod";
import { toTypedSchema } from '@vee-validate/zod'
import { useForm, Field as FormField } from 'vee-validate'
import Button from '~/components/ui/button/Button.vue'
import Input from '~/components/ui/input/Input.vue'
import FormItem from '~/components/ui/form/FormItem.vue'
import FormControl from '~/components/ui/form/FormControl.vue'
import FormMessage from '~/components/ui/form/FormMessage.vue'
import { ChevronLeft, Mail } from 'lucide-vue-next'

definePageMeta({
    layout: false,
});

const toast = useToast();

const schema = toTypedSchema(z.object({
    email: z.string().email("Invalid email"),
}));

const { handleSubmit } = useForm({
    validationSchema: schema,
});

const router = useRouter();

const onSubmit = handleSubmit((values) => {
    console.log("Register submitted", values);
    toast.add({
        title: "Success",
        description: "Creating your account...",
        color: "primary",
    });

    router.push({
        path: "/verify-email",
        query: { email: values.email },
    });
});
</script>

<template>
    <div class="min-h-screen flex">
        <div
            class="hidden lg:flex lg:w-1/2 relative overflow-hidden bg-gradient-to-br from-primary/80 via-primary to-primary/90"
        >
            <div class="absolute bottom-12 left-12 max-w-lg z-10">
                <p class="text-white text-2xl font-medium mb-4">
                    "This platform has helped me to save time and serve my
                    clients faster than ever before."
                </p>
                <p class="text-white/80 text-sm">~ Ali Hassan</p>
            </div>
        </div>

        <div
            class="w-full lg:w-1/2 flex items-center justify-center p-8 bg-background"
        >
            <div class="w-full max-w-md">
                <div class="mb-16">
                    <NuxtLink to="/" class="flex items-center gap-2">
                        <div
                            class="w-8 h-8 bg-primary rounded"
                        />
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
                            Create Your Account
                        </h1>
                        <p class="text-muted-foreground">
                            Start your journey with efferd today.
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
                            Enter your email address to create an account
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

                    <div class="text-center">
                        <p class="text-sm text-muted-foreground">
                            Already have an account?
                            <NuxtLink
                                to="/login"
                                class="font-medium text-foreground hover:underline"
                            >
                                Sign in
                            </NuxtLink>
                        </p>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>
