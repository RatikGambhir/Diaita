<script setup lang="ts">
import { computed, ref } from "vue";
import Dialog from '~/components/ui/dialog/Dialog.vue'
import DialogContent from '~/components/ui/dialog/DialogContent.vue'
import DialogHeader from '~/components/ui/dialog/DialogHeader.vue'
import DialogTitle from '~/components/ui/dialog/DialogTitle.vue'
import DialogFooter from '~/components/ui/dialog/DialogFooter.vue'
import Button from '~/components/ui/button/Button.vue'
import Input from '~/components/ui/input/Input.vue'
import Label from '~/components/ui/label/Label.vue'

const props = defineProps<{
    modelValue: boolean;
}>();

const emit = defineEmits<{
    (e: "update:modelValue", value: boolean): void;
    (e: "create", payload: { name: string; performedAt: string; durationSeconds: number | null }): void;
}>();

const todayIsoDate = () => new Date().toISOString().slice(0, 10);

const workoutName = ref("");
const performedOn = ref(todayIsoDate());
const durationMinutes = ref("");

const resetForm = () => {
    workoutName.value = "";
    performedOn.value = todayIsoDate();
    durationMinutes.value = "";
};

const isOpen = computed({
    get: () => props.modelValue,
    set: (value) => {
        if (!value) {
            resetForm();
        }
        emit("update:modelValue", value);
    },
});

const canCreate = computed(() => workoutName.value.trim().length > 0);

const handleCreate = () => {
    if (!canCreate.value) {
        return;
    }

    const minutes = Number(durationMinutes.value.trim());
    const durationSeconds = Number.isFinite(minutes) && minutes > 0
        ? Math.round(minutes * 60)
        : null;

    emit("create", {
        name: workoutName.value.trim(),
        performedAt: performedOn.value || todayIsoDate(),
        durationSeconds,
    });

    isOpen.value = false;
};

const handleCancel = () => {
    isOpen.value = false;
};
</script>

<template>
    <Dialog v-model:open="isOpen">
        <DialogContent class="sm:max-w-md">
            <DialogHeader>
                <DialogTitle>New Workout</DialogTitle>
            </DialogHeader>

            <div class="space-y-4 py-4">
                <div class="space-y-2">
                    <Label for="workout-name">Workout Name</Label>
                    <Input
                        id="workout-name"
                        v-model="workoutName"
                        placeholder="Enter workout name"
                        class="h-11"
                        @keyup.enter="handleCreate"
                    />
                </div>

                <div class="grid grid-cols-2 gap-3">
                    <div class="space-y-2">
                        <Label for="workout-date">Date</Label>
                        <Input id="workout-date" v-model="performedOn" type="date" class="h-11" />
                    </div>
                    <div class="space-y-2">
                        <Label for="workout-duration">Duration (min)</Label>
                        <Input
                            id="workout-duration"
                            v-model="durationMinutes"
                            type="number"
                            min="0"
                            placeholder="60"
                            class="h-11"
                            @keyup.enter="handleCreate"
                        />
                    </div>
                </div>
            </div>

            <DialogFooter class="flex gap-3 justify-end">
                <Button variant="outline" @click="handleCancel">
                    Cancel
                </Button>
                <Button :disabled="!canCreate" @click="handleCreate">
                    Create Workout
                </Button>
            </DialogFooter>
        </DialogContent>
    </Dialog>
</template>
