<script setup lang="ts">
import { computed, watch } from "vue";
import Dialog from '~/components/ui/dialog/Dialog.vue'
import DialogContent from '~/components/ui/dialog/DialogContent.vue'
import DialogHeader from '~/components/ui/dialog/DialogHeader.vue'
import DialogTitle from '~/components/ui/dialog/DialogTitle.vue'
import Input from '~/components/ui/input/Input.vue'
import { Search, ChevronRight, Loader2 } from 'lucide-vue-next'
import { useExerciseSearch } from '~/composables/useExerciseSearch'
import type { ExerciseLibraryEntry } from '~/types/WorkoutTypes'

const props = defineProps<{
    modelValue: boolean;
}>();

const emit = defineEmits<{
    (e: "update:modelValue", value: boolean): void;
    (e: "select", exercise: ExerciseLibraryEntry): void;
}>();

const {
    query,
    results,
    isSearching,
    error,
    hasSearched,
    isQueryTooShort,
    minQueryLength,
    reset,
} = useExerciseSearch();

const isOpen = computed({
    get: () => props.modelValue,
    set: (value) => emit("update:modelValue", value),
});

watch(isOpen, (open) => {
    if (!open) {
        reset();
    }
});

const subtitleFor = (exercise: ExerciseLibraryEntry) =>
    [exercise.exerciseType, exercise.primaryFitnessFocus].filter(Boolean).join(" · ");

const handleSelect = (exercise: ExerciseLibraryEntry) => {
    emit("select", exercise);
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
                        v-model="query"
                        placeholder="Search exercises..."
                        class="pl-10 h-11"
                    />
                </div>

                <div class="max-h-96 overflow-y-auto space-y-2">
                    <div
                        v-if="isSearching"
                        class="flex items-center justify-center gap-2 py-8 text-muted-foreground"
                    >
                        <Loader2 class="h-4 w-4 animate-spin" />
                        Searching…
                    </div>

                    <p v-else-if="error" class="py-8 text-center text-destructive">
                        {{ error }}
                    </p>

                    <p
                        v-else-if="isQueryTooShort"
                        class="py-8 text-center text-muted-foreground"
                    >
                        Type at least {{ minQueryLength }} characters to search.
                    </p>

                    <p
                        v-else-if="!hasSearched"
                        class="py-8 text-center text-muted-foreground"
                    >
                        Search the exercise library to add a movement.
                    </p>

                    <p
                        v-else-if="results.length === 0"
                        class="py-8 text-center text-muted-foreground"
                    >
                        No exercises found
                    </p>

                    <template v-else>
                        <div
                            v-for="exercise in results"
                            :key="exercise.id ?? exercise.exercise"
                            class="p-4 border border-border rounded-lg hover:bg-muted cursor-pointer transition-colors"
                            @click="handleSelect(exercise)"
                        >
                            <div class="flex items-center justify-between gap-4">
                                <div class="min-w-0">
                                    <p class="font-medium truncate">{{ exercise.exercise }}</p>
                                    <p class="text-sm text-muted-foreground truncate">
                                        {{ subtitleFor(exercise) || 'Exercise' }}
                                    </p>
                                </div>
                                <ChevronRight class="h-5 w-5 shrink-0 text-muted-foreground" />
                            </div>
                        </div>
                    </template>
                </div>
            </div>
        </DialogContent>
    </Dialog>
</template>
