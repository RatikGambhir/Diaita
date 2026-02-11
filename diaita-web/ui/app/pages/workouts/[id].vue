<script setup lang="ts">
import { ref, computed } from "vue";
import Button from '~/components/ui/button/Button.vue'
import Card from '~/components/ui/card/Card.vue'
import CardContent from '~/components/ui/card/CardContent.vue'
import { ArrowLeft, MoreHorizontal, Calendar, Clock, LineChart, Trash } from 'lucide-vue-next'

const route = useRoute();
const workoutId = Number(route.params.id);

const isAddExerciseModalOpen = ref(false);

const allWorkouts = ref([
    {
        id: 1,
        name: "Morning Workout",
        date: "Dec 15, 2025",
        duration: "0:50",
        exercises: [
            {
                id: 1,
                name: "Bicep Curl (Machine)",
                sets: [{ set: 1, previous: null, lbs: 50, reps: 12 }],
            },
        ],
    },
    {
        id: 2,
        name: "Evening Push Day",
        date: "Dec 14, 2025",
        duration: "1:15",
        exercises: [
            {
                id: 1,
                name: "Bench Press",
                sets: [
                    { set: 1, previous: null, lbs: 135, reps: 10 },
                    { set: 2, previous: 135, lbs: 155, reps: 8 },
                    { set: 3, previous: 155, lbs: 155, reps: 8 },
                ],
            },
            {
                id: 2,
                name: "Shoulder Press",
                sets: [
                    { set: 1, previous: null, lbs: 65, reps: 10 },
                    { set: 2, previous: 65, lbs: 65, reps: 10 },
                ],
            },
        ],
    },
    {
        id: 3,
        name: "Leg Day",
        date: "Dec 13, 2025",
        duration: "1:30",
        exercises: [
            {
                id: 1,
                name: "Squat",
                sets: [
                    { set: 1, previous: null, lbs: 185, reps: 10 },
                    { set: 2, previous: 185, lbs: 205, reps: 8 },
                    { set: 3, previous: 205, lbs: 225, reps: 6 },
                ],
            },
            {
                id: 2,
                name: "Leg Press",
                sets: [
                    { set: 1, previous: null, lbs: 270, reps: 12 },
                    { set: 2, previous: 270, lbs: 270, reps: 12 },
                ],
            },
        ],
    },
    {
        id: 4,
        name: "Back & Biceps",
        date: "Dec 12, 2025",
        duration: "1:20",
        exercises: [
            {
                id: 1,
                name: "Deadlift",
                sets: [
                    { set: 1, previous: null, lbs: 225, reps: 8 },
                    { set: 2, previous: 225, lbs: 245, reps: 6 },
                ],
            },
            {
                id: 2,
                name: "Pull Ups",
                sets: [
                    { set: 1, previous: null, lbs: 0, reps: 10 },
                    { set: 2, previous: 0, lbs: 0, reps: 8 },
                    { set: 3, previous: 0, lbs: 0, reps: 6 },
                ],
            },
        ],
    },
]);

const workout = computed(() => {
    return allWorkouts.value.find((w) => w.id === workoutId);
});

const addSet = (exerciseId: number) => {
    const exercise = workout.value?.exercises.find((e) => e.id === exerciseId);
    if (exercise) {
        const lastSet = exercise.sets[exercise.sets.length - 1];
        exercise.sets.push({
            set: exercise.sets.length + 1,
            previous: lastSet.lbs,
            lbs: lastSet.lbs,
            reps: lastSet.reps,
        });
    }
};

const deleteSet = (exerciseId: number, setIndex: number) => {
    const exercise = workout.value?.exercises.find((e) => e.id === exerciseId);
    if (exercise && exercise.sets.length > 1) {
        exercise.sets.splice(setIndex, 1);
        // Renumber sets
        exercise.sets.forEach((set, index) => {
            set.set = index + 1;
        });
    }
};

const addExercise = (exercise: { id: number; name: string }) => {
    if (workout.value) {
        const newExerciseId =
            workout.value.exercises.length > 0
                ? Math.max(...workout.value.exercises.map((e) => e.id)) + 1
                : 1;
        workout.value.exercises.push({
            id: newExerciseId,
            name: exercise.name,
            sets: [{ set: 1, previous: null, lbs: 0, reps: 0 }],
        });
    }
};
</script>

<template>
    <div class="flex-1 flex flex-col h-full">
        <!-- Header -->
        <header class="flex h-16 items-center justify-between border-b px-6 shrink-0">
            <div class="flex items-center gap-3">
                <Button
                    variant="ghost"
                    size="icon"
                    @click="navigateTo('/workouts')"
                >
                    <ArrowLeft class="h-5 w-5" />
                </Button>
                <h1 class="text-xl font-semibold">{{ workout?.name || 'Workout' }}</h1>
            </div>
            <Button variant="ghost" size="icon">
                <MoreHorizontal class="h-5 w-5" />
            </Button>
        </header>

        <!-- Body -->
        <div class="flex-1 overflow-auto p-6 space-y-6">
            <!-- Workout Header Info -->
            <div class="flex items-center gap-6 text-muted-foreground">
                <div class="flex items-center gap-2">
                    <Calendar class="h-5 w-5" />
                    <span>{{ workout?.date }}</span>
                </div>
                <div class="flex items-center gap-2">
                    <Clock class="h-5 w-5" />
                    <span>{{ workout?.duration }}</span>
                </div>
            </div>

            <!-- Exercises List -->
            <div class="space-y-6">
                <div
                    v-for="exercise in workout?.exercises"
                    :key="exercise.id"
                    class="space-y-4"
                >
                    <div class="flex items-center justify-between">
                        <h3 class="text-xl font-semibold text-primary">
                            {{ exercise.name }}
                        </h3>
                        <div class="flex items-center gap-2">
                            <Button
                                variant="ghost"
                                size="icon"
                                class="h-8 w-8"
                            >
                                <LineChart class="h-4 w-4" />
                            </Button>
                            <Button
                                variant="ghost"
                                size="icon"
                                class="h-8 w-8"
                            >
                                <MoreHorizontal class="h-4 w-4" />
                            </Button>
                        </div>
                    </div>

                    <!-- Sets Table -->
                    <Card>
                        <CardContent class="p-0">
                            <div class="overflow-x-auto">
                                <table class="w-full">
                                    <thead
                                        class="bg-muted border-b border-border"
                                    >
                                        <tr>
                                            <th
                                                class="px-4 py-3 text-left text-sm font-semibold"
                                            >
                                                Set
                                            </th>
                                            <th
                                                class="px-4 py-3 text-left text-sm font-semibold"
                                            >
                                                Previous
                                            </th>
                                            <th
                                                class="px-4 py-3 text-left text-sm font-semibold"
                                            >
                                                lbs
                                            </th>
                                            <th
                                                class="px-4 py-3 text-left text-sm font-semibold"
                                            >
                                                Reps
                                            </th>
                                            <th
                                                class="px-4 py-3 text-center text-sm font-semibold"
                                            ></th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <tr
                                            v-for="(
                                                set, index
                                            ) in exercise.sets"
                                            :key="index"
                                            class="border-b border-border hover:bg-muted"
                                        >
                                            <td class="px-4 py-3">
                                                <div
                                                    class="bg-secondary rounded-full w-8 h-8 flex items-center justify-center text-sm font-medium"
                                                >
                                                    {{ set.set }}
                                                </div>
                                            </td>
                                            <td class="px-4 py-3 text-muted-foreground">
                                                {{
                                                    set.previous !== null
                                                        ? set.previous
                                                        : "—"
                                                }}
                                            </td>
                                            <td class="px-4 py-3">
                                                <input
                                                    v-model.number="set.lbs"
                                                    type="number"
                                                    class="w-20 px-2 py-1 border border-border rounded focus:outline-none focus:ring-2 focus:ring-ring bg-background"
                                                />
                                            </td>
                                            <td class="px-4 py-3">
                                                <input
                                                    v-model.number="set.reps"
                                                    type="number"
                                                    class="w-20 px-2 py-1 border border-border rounded focus:outline-none focus:ring-2 focus:ring-ring bg-background"
                                                />
                                            </td>
                                            <td class="px-4 py-3 text-center">
                                                <Button
                                                    v-if="
                                                        exercise.sets.length > 1
                                                    "
                                                    variant="ghost"
                                                    size="icon"
                                                    class="h-8 w-8 text-destructive hover:text-destructive hover:bg-destructive/10"
                                                    @click="
                                                        deleteSet(
                                                            exercise.id,
                                                            index,
                                                        )
                                                    "
                                                >
                                                    <Trash class="h-4 w-4" />
                                                </Button>
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>

                            <div class="p-4 border-t border-border">
                                <Button
                                    variant="outline"
                                    class="w-full"
                                    @click="addSet(exercise.id)"
                                >
                                    + Add Set (2:00)
                                </Button>
                            </div>
                        </CardContent>
                    </Card>
                </div>
            </div>

            <!-- Add Exercises Button -->
            <Button
                variant="outline"
                size="lg"
                class="w-full"
                @click="isAddExerciseModalOpen = true"
            >
                Add Exercises
            </Button>

            <!-- Cancel Workout Button -->
            <Button
                variant="outline"
                size="lg"
                class="w-full text-destructive border-destructive/50 hover:bg-destructive/10 hover:text-destructive"
            >
                Cancel Workout
            </Button>
        </div>

        <ExerciseSelectModal
            v-model="isAddExerciseModalOpen"
            @select="addExercise"
        />
    </div>
</template>
