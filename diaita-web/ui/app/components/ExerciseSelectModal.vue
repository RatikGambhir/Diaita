<script setup lang="ts">
import { ref, computed } from "vue";
import Dialog from '~/components/ui/dialog/Dialog.vue'
import DialogContent from '~/components/ui/dialog/DialogContent.vue'
import DialogHeader from '~/components/ui/dialog/DialogHeader.vue'
import DialogTitle from '~/components/ui/dialog/DialogTitle.vue'
import Button from '~/components/ui/button/Button.vue'
import Input from '~/components/ui/input/Input.vue'
import { Search, ChevronRight } from 'lucide-vue-next'

const props = defineProps<{
    modelValue: boolean;
}>();

const emit = defineEmits<{
    (e: "update:modelValue", value: boolean): void;
    (e: "select", exercise: { id: number; name: string }): void;
}>();

const searchQuery = ref("");

// Mock exercise library
const exercises = ref([
    { id: 1, name: "Bicep Curl (Machine)", category: "Arms" },
    { id: 2, name: "Bicep Curl (Dumbbell)", category: "Arms" },
    { id: 3, name: "Hammer Curl", category: "Arms" },
    { id: 4, name: "Tricep Extension", category: "Arms" },
    { id: 5, name: "Bench Press", category: "Chest" },
    { id: 6, name: "Incline Bench Press", category: "Chest" },
    { id: 7, name: "Dumbbell Fly", category: "Chest" },
    { id: 8, name: "Push Ups", category: "Chest" },
    { id: 9, name: "Shoulder Press", category: "Shoulders" },
    { id: 10, name: "Lateral Raise", category: "Shoulders" },
    { id: 11, name: "Front Raise", category: "Shoulders" },
    { id: 12, name: "Squat", category: "Legs" },
    { id: 13, name: "Leg Press", category: "Legs" },
    { id: 14, name: "Leg Extension", category: "Legs" },
    { id: 15, name: "Leg Curl", category: "Legs" },
    { id: 16, name: "Deadlift", category: "Back" },
    { id: 17, name: "Pull Ups", category: "Back" },
    { id: 18, name: "Barbell Row", category: "Back" },
    { id: 19, name: "Lat Pulldown", category: "Back" },
]);

const isOpen = computed({
    get: () => props.modelValue,
    set: (value) => emit("update:modelValue", value),
});

const filteredExercises = computed(() => {
    if (!searchQuery.value) {
        return exercises.value;
    }
    const query = searchQuery.value.toLowerCase();
    return exercises.value.filter((exercise) =>
        exercise.name.toLowerCase().includes(query) ||
        exercise.category.toLowerCase().includes(query)
    );
});

const handleSelect = (exercise: { id: number; name: string }) => {
    emit("select", exercise);
    searchQuery.value = "";
    isOpen.value = false;
};
</script>

<template>
    <Dialog v-model:open="isOpen">
        <DialogContent class="sm:max-w-2xl">
            <DialogHeader>
                <DialogTitle>Select Exercise</DialogTitle>
            </DialogHeader>

            <div class="space-y-4 py-4">
                <div class="relative">
                    <Search class="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                    <Input
                        v-model="searchQuery"
                        placeholder="Search exercises..."
                        class="pl-10 h-11"
                    />
                </div>

                <div class="max-h-96 overflow-y-auto space-y-2">
                    <div
                        v-for="exercise in filteredExercises"
                        :key="exercise.id"
                        class="p-4 border border-border rounded-lg hover:bg-muted cursor-pointer transition-colors"
                        @click="handleSelect(exercise)"
                    >
                        <div class="flex items-center justify-between">
                            <div>
                                <p class="font-medium">{{ exercise.name }}</p>
                                <p class="text-sm text-muted-foreground">{{ exercise.category }}</p>
                            </div>
                            <ChevronRight class="h-5 w-5 text-muted-foreground" />
                        </div>
                    </div>

                    <div v-if="filteredExercises.length === 0" class="text-center py-8 text-muted-foreground">
                        No exercises found
                    </div>
                </div>
            </div>
        </DialogContent>
    </Dialog>
</template>
