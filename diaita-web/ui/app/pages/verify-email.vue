<script setup lang="ts">
import {useUserStore} from "~/stores/useUserStore";
import type {UserSession} from "~/pages/login.vue"
import Button from '~/components/ui/button/Button.vue'

const userStore = useUserStore();

definePageMeta({
    layout: false,
});

const route = useRoute();
const router = useRouter();
const toast = useToast();

const email = ref((route.query.email as string) || "your.email@example.com");

const code = ref(["", "", "", "", "", ""]);
const inputRefs = ref<HTMLInputElement[]>([]);

const resendTimer = ref(25);
const canResend = ref(false);
const timerInterval = ref<NodeJS.Timeout | null>(null);

const startTimer = () => {
    resendTimer.value = 25;
    canResend.value = false;

    if (timerInterval.value) {
        clearInterval(timerInterval.value);
    }

    timerInterval.value = setInterval(() => {
        resendTimer.value--;
        if (resendTimer.value <= 0) {
            canResend.value = true;
            if (timerInterval.value) {
                clearInterval(timerInterval.value);
            }
        }
    }, 1000);
};

const handleInput = (index: number, event: Event) => {
    const target = event.target as HTMLInputElement;
    const value = target.value;

    if (value && !/^\d$/.test(value)) {
        target.value = "";
        return;
    }

    code.value[index] = value;

    if (value && index < 5) {
        inputRefs.value[index + 1]?.focus();
    }
};

const handleKeyDown = (index: number, event: KeyboardEvent) => {
    if (event.key === "Backspace" && !code.value[index] && index > 0) {
        inputRefs.value[index - 1]?.focus();
    }
};

const handlePaste = (event: ClipboardEvent) => {
    event.preventDefault();
    const pastedData = event.clipboardData?.getData("text");

    if (pastedData && /^\d{6}$/.test(pastedData)) {
      code.value = pastedData.split("");
        inputRefs.value[5]?.focus();
    }
};

const isCodeComplete = computed(() => {
    return code.value.every((digit) => digit !== "");
});

async function verifyOtpCode(verificationCode: string): Promise<UserSession> {
  const {
    data: {session, user},
      error,
  } = await supabase.auth.verifyOtp({
    email: email.value,
    token: verificationCode,
    type: "email"
  })

  return {
    user: user,
    error: error,
    session: session,
  }

}

async function onContinue(): Promise<void> {
    if (!isCodeComplete.value) {
        toast.add({
            title: "Incomplete code",
            description: "Please enter all 6 digits",
            color: "error",
        });
        return;
    }

    const verificationCode = code.value.join("");

  const {session, user, error} = await verifyOtpCode(
      verificationCode
  )
    console.log("Verifying code:", verificationCode);

console.log("ERROR: ", error)
  if(error) {
    toast.add({
      title: "Error! Please try again",
      description: "Please enter all 6 digits",
      color: "error",
    });
    return;
  }

  if(session && user) {
    toast.add({
      title: "Logged In! Let's get Fit",
      description: "Lets do this!",
      color: "success",
    });
    userStore.addUserSession(user, session)

    const { fetchProfile } = useUserProfile()
    const profile = await fetchProfile()

    const redirectPath = route.query.redirect as string | undefined

    if (!profile) {
      await router.push({
        path: "/setup-profile",
        query: redirectPath ? { redirect: redirectPath } : {},
      })
    } else if (redirectPath) {
      await router.push(redirectPath)
    } else {
      await router.push({ path: "/" })
    }
  }


}

async function resendCode(): Promise<void> {
    if (!canResend.value) return;

     await supabase.auth.resend({
      type: 'email_change',
      email: email.value
    })
    console.log("Resending code to:", email.value);
    toast.add({
        title: "Code sent",
        description: "A new verification code has been sent to your email",
        color: "primary",
    });

    startTimer();
}

onMounted(() => {
    startTimer();
});

onUnmounted(() => {
    if (timerInterval.value) {
        clearInterval(timerInterval.value);
    }
});
</script>

<template>
    <div
        class="min-h-screen flex items-center justify-center p-8 bg-muted"
    >
        <div class="w-full max-w-md">
            <div class="bg-card rounded-3xl p-12 shadow-sm">
                <div class="text-center mb-12">
                    <h1
                        class="text-4xl font-bold text-foreground mb-4"
                    >
                        Verify Your Email
                    </h1>
                    <p class="text-muted-foreground text-base">
                        We have sent a verification code to
                    </p>
                    <p
                        class="text-foreground font-semibold text-lg mt-1"
                    >
                        {{ email }}
                    </p>
                    <p class="text-muted-foreground text-base mt-3">
                        Please enter the code below.
                    </p>
                </div>

                <div class="mb-8">
                    <label
                        class="block text-foreground text-sm font-medium mb-4"
                    >
                        Verification Code
                    </label>
                    <div class="flex gap-3 justify-center">
                        <input
                            v-for="(digit, index) in code"
                            :key="index"
                            :ref="
                                (el) => {
                                    if (el)
                                        inputRefs[index] =
                                            el as HTMLInputElement;
                                }
                            "
                            v-model="code[index]"
                            type="text"
                            inputmode="numeric"
                            maxlength="1"
                            class="w-14 h-14 text-center text-2xl font-semibold bg-muted border-0 rounded-xl text-foreground focus:ring-2 focus:ring-primary transition-all"
                            @input="handleInput(index, $event)"
                            @keydown="handleKeyDown(index, $event)"
                            @paste="index === 0 ? handlePaste($event) : null"
                        >
                    </div>
                </div>

                <div class="text-center mb-8">
                    <p
                        v-if="!canResend"
                        class="text-muted-foreground text-sm"
                    >
                        Wait for {{ resendTimer }}s to resend
                    </p>
                    <button
                        v-else
                        class="text-foreground font-medium text-sm hover:underline"
                        @click="resendCode"
                    >
                        Resend code
                    </button>
                </div>

                <Button
                    size="lg"
                    class="w-full font-medium mb-6"
                    :disabled="!isCodeComplete"
                    :class="{
                        'opacity-50 cursor-not-allowed': !isCodeComplete,
                    }"
                    @click="onContinue"
                >
                    Continue
                </Button>

                <div class="text-center">
                    <NuxtLink
                        to="/register"
                        class="text-muted-foreground text-sm hover:text-foreground underline"
                    >
                        Change email address
                    </NuxtLink>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
input[type="text"]::-webkit-outer-spin-button,
input[type="text"]::-webkit-inner-spin-button {
    -webkit-appearance: none;
    margin: 0;
}

input[type="text"] {
    -moz-appearance: textfield;
}
</style>
