<script setup lang="ts">
import Button from '~/components/ui/button/Button.vue'
import Card from '~/components/ui/card/Card.vue'
import CardContent from '~/components/ui/card/CardContent.vue'
import Input from '~/components/ui/input/Input.vue'
import Separator from '~/components/ui/separator/Separator.vue'
import Label from '~/components/ui/label/Label.vue'
import type { PlaceholderFor, SettingsFormDefaults, SettingsFormState } from '~/components/settings/types'

const toast = useToast()

const props = defineProps<{
    formState: SettingsFormState;
    formDefaults: SettingsFormDefaults;
    placeholderFor: PlaceholderFor;
}>();
const accountState = props.formState.account

const onIntegrateAppleHealth = () => {
    toast.add({
        title: 'Apple Health integration',
        description: 'This integration is not available yet.',
        color: 'info',
    })
}
</script>

<template>
    <div class="space-y-6">
        <Card class="shadow-sm">
            <CardContent class="p-6 space-y-6">
                <div class="space-y-1">
                    <h2 class="text-lg font-semibold">Account</h2>
                    <p class="text-sm text-muted-foreground">
                        Update your account details and homepage workout preferences.
                    </p>
                </div>

                <Separator />

                <div class="grid gap-6 md:grid-cols-2">
                    <div class="space-y-2">
                        <Label>First Name</Label>
                        <p class="text-xs text-muted-foreground">Your given name.</p>
                        <Input
                            v-model="accountState.firstName"
                            :placeholder="props.placeholderFor(props.formDefaults.account.firstName, 'e.g., Jane')"
                        />
                    </div>

                    <div class="space-y-2">
                        <Label>Last Name</Label>
                        <p class="text-xs text-muted-foreground">Your family name.</p>
                        <Input
                            v-model="accountState.lastName"
                            :placeholder="props.placeholderFor(props.formDefaults.account.lastName, 'e.g., Doe')"
                        />
                    </div>
                </div>

                <Separator />

                <div class="grid gap-4 md:grid-cols-[220px,1fr]">
                    <div>
                        <Label>Email</Label>
                        <p class="text-xs text-muted-foreground">Used for account access and notifications.</p>
                    </div>
                    <Input
                        v-model="accountState.email"
                        type="email"
                        :placeholder="props.placeholderFor(props.formDefaults.account.email, 'e.g., jane.doe@example.com')"
                    />
                </div>

                <Separator />

                <div class="grid gap-4 md:grid-cols-[220px,1fr]">
                    <div>
                        <Label>Workouts to Track on Homepage</Label>
                        <p class="text-xs text-muted-foreground">How many recent workouts should show on your homepage.</p>
                    </div>
                    <Input
                        v-model="accountState.workoutsToTrackOnHomepage"
                        type="number"
                        min="1"
                        :placeholder="props.placeholderFor(props.formDefaults.account.workoutsToTrackOnHomepage, 'e.g., 6')"
                    />
                </div>
            </CardContent>
        </Card>

        <Card class="shadow-sm">
            <CardContent class="p-6 space-y-4">
                <div class="space-y-1">
                    <h2 class="text-lg font-semibold">Apple Health</h2>
                    <p class="text-sm text-muted-foreground">
                        Connect Apple Health to sync workouts and health metrics.
                    </p>
                </div>

                <Separator />

                <div class="flex items-center justify-between gap-4">
                    <p class="text-sm text-muted-foreground">
                        Integration status: not connected.
                    </p>
                    <Button
                        type="button"
                        class="bg-[#d96d54] text-white hover:bg-[#c75f49]"
                        @click="onIntegrateAppleHealth"
                    >
                        Integrate with Apple Health
                    </Button>
                </div>
            </CardContent>
        </Card>

        <div class="flex justify-end gap-3">
            <Button variant="ghost" class="text-muted-foreground">Cancel</Button>
            <Button class="bg-[#d96d54] text-white hover:bg-[#c75f49]">Save changes</Button>
        </div>
    </div>
</template>
