<script setup lang="ts">
import type { WorkoutCard } from "~/types";
import Card from '~/components/ui/card/Card.vue'
import Badge from '~/components/ui/badge/Badge.vue'
import Button from '~/components/ui/button/Button.vue'
import Separator from '~/components/ui/separator/Separator.vue'
import { Clock, Calendar, ChevronDown, Dumbbell, Copy } from 'lucide-vue-next'

interface Props {
    workout: WorkoutCard;
}

const props = defineProps<Props>();

const emit = defineEmits<{
    toggle: [id: number];
    viewDetails: [id: number];
    duplicate: [id: number];
}>();

const handleCardClick = () => {
    emit("toggle", props.workout.id);
};

const handleViewDetails = () => {
    emit("viewDetails", props.workout.id);
};

const handleDuplicate = () => {
    emit("duplicate", props.workout.id);
};
</script>

<template>
    <Card
        role="button"
        :aria-expanded="workout.isExpanded"
        :aria-label="`${workout.name} workout card`"
        tabindex="0"
        :class="[
            'cursor-pointer transition-all duration-300 overflow-hidden',
            workout.isExpanded
                ? 'ring-2 ring-primary shadow-lg shadow-primary/20'
                : 'hover:shadow-lg hover:-translate-y-1',
        ]"
        @click="handleCardClick"
        @keydown.enter="handleCardClick"
        @keydown.space.prevent="handleCardClick"
    >
        <div class="p-5">
            <div class="mb-4">
                <Badge variant="default" class="bg-primary hover:bg-primary/90">
                    {{ workout.duration }}
                </Badge>
            </div>

            <h3 class="text-xl font-semibold mb-3">
                {{ workout.name }}
            </h3>

            <div class="flex items-center gap-2 text-muted-foreground text-sm mb-2">
                <Clock class="h-4 w-4" />
                <span>{{ workout.startTime }} — {{ workout.endTime }}</span>
            </div>

            <div class="flex items-center gap-2 text-muted-foreground text-sm">
                <Calendar class="h-4 w-4" />
                <span>{{ workout.date }}</span>
            </div>

            <div class="flex justify-end mt-4">
                <ChevronDown
                    :class="[
                        'h-5 w-5 text-muted-foreground transition-transform duration-300',
                        workout.isExpanded ? 'rotate-180' : '',
                    ]"
                />
            </div>
        </div>

        <Transition
            enter-active-class="transition-all duration-300 ease-out"
            enter-from-class="max-h-0 opacity-0"
            enter-to-class="max-h-96 opacity-100"
            leave-active-class="transition-all duration-300 ease-in"
            leave-from-class="max-h-96 opacity-100"
            leave-to-class="max-h-0 opacity-0"
        >
            <div v-if="workout.isExpanded" class="overflow-hidden">
                <div class="px-5 pb-5">
                    <Separator class="mb-4" />

                    <h4 class="font-medium mb-3">Exercises</h4>

                    <p
                        v-if="workout.exercises.length === 0"
                        class="text-muted-foreground text-sm italic py-2"
                    >
                        No exercises added yet
                    </p>

                    <div v-else class="space-y-2">
                        <div
                            v-for="exercise in workout.exercises"
                            :key="exercise.id"
                            class="flex items-center justify-between py-2 px-3 bg-muted rounded-lg"
                        >
                            <div class="flex items-center gap-3">
                                <Dumbbell class="h-4 w-4 text-primary" />
                                <span class="text-sm font-medium">{{
                                    exercise.name
                                }}</span>
                            </div>
                            <Badge variant="secondary">
                                {{ exercise.sets.length }}
                                {{ exercise.sets.length === 1 ? "set" : "sets" }}
                            </Badge>
                        </div>
                    </div>

                    <!-- Action Buttons -->
                    <div class="flex gap-3 mt-4">
                        <Button
                            size="sm"
                            class="flex-1"
                            @click.stop="handleViewDetails"
                        >
                            View Details
                        </Button>
                        <Button
                            variant="outline"
                            size="sm"
                            @click.stop="handleDuplicate"
                        >
                            <Copy class="h-4 w-4 mr-2" />
                            Duplicate
                        </Button>
                    </div>
                </div>
            </div>
        </Transition>
    </Card>
</template>
