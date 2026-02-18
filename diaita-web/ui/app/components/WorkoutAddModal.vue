<script setup lang="ts">
import { ref } from "vue";
import Dialog from '~/components/ui/dialog/Dialog.vue'
import DialogContent from '~/components/ui/dialog/DialogContent.vue'
import DialogHeader from '~/components/ui/dialog/DialogHeader.vue'
import DialogTitle from '~/components/ui/dialog/DialogTitle.vue'
import DialogFooter from '~/components/ui/dialog/DialogFooter.vue'
import Button from '~/components/ui/button/Button.vue'
import Input from '~/components/ui/input/Input.vue'

const props = defineProps<{
    modelValue: boolean;
}>();

const emit = defineEmits<{
    (e: "update:modelValue", value: boolean): void;
    (e: "create", name: string): void;
}>();

const workoutName = ref("");

const isOpen = computed({
    get: () => props.modelValue,
    set: (value) => {
        if (!value) {
            // Clear input when modal is closed
            workoutName.value = "";
        }
        emit("update:modelValue", value);
    },
});

const handleCreate = () => {
    if (workoutName.value.trim()) {
        emit("create", workoutName.value);
        workoutName.value = "";
        isOpen.value = false;
    }
};

const handleCancel = () => {
    workoutName.value = "";
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
                <div>
                    <label class="block text-sm font-medium mb-2">
                        Workout Name
                    </label>
                    <Input
                        v-model="workoutName"
                        placeholder="Enter workout name"
                        class="h-11"
                        @keyup.enter="handleCreate"
                    />
                </div>
            </div>

            <DialogFooter class="flex gap-3 justify-end">
                <Button
                    variant="outline"
                    @click="handleCancel"
                >
                    Cancel
                </Button>
                <Button @click="handleCreate">
                    Create Workout
                </Button>
            </DialogFooter>
        </DialogContent>
    </Dialog>
</template>
