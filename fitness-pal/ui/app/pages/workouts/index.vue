<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount, watch } from "vue";
import Button from '~/components/ui/button/Button.vue'
import Input from '~/components/ui/input/Input.vue'
import Card from '~/components/ui/card/Card.vue'
import CardHeader from '~/components/ui/card/CardHeader.vue'
import CardContent from '~/components/ui/card/CardContent.vue'
import WorkoutCategorySummaryRow from '~/components/workouts/WorkoutCategorySummaryRow.vue'
import WorkoutCategorySection from '~/components/workouts/WorkoutCategorySection.vue'
import WorkoutCategoryItemRow from '~/components/workouts/WorkoutCategoryItemRow.vue'
import Tooltip from '~/components/ui/tooltip/Tooltip.vue'
import TooltipContent from '~/components/ui/tooltip/TooltipContent.vue'
import TooltipProvider from '~/components/ui/tooltip/TooltipProvider.vue'
import TooltipTrigger from '~/components/ui/tooltip/TooltipTrigger.vue'
import Badge from '~/components/ui/badge/Badge.vue'
import ToggleGroup from '~/components/ui/toggle-group/ToggleGroup.vue'
import ToggleGroupItem from '~/components/ui/toggle-group/ToggleGroupItem.vue'
import Table from '~/components/ui/table/Table.vue'
import TableHeader from '~/components/ui/table/TableHeader.vue'
import TableBody from '~/components/ui/table/TableBody.vue'
import TableRow from '~/components/ui/table/TableRow.vue'
import TableHead from '~/components/ui/table/TableHead.vue'
import TableCell from '~/components/ui/table/TableCell.vue'
import { Plus, Bell, MoreHorizontal, Search, ChevronDown, Clock, LayoutGrid, Table2 } from 'lucide-vue-next'

const isAddWorkoutModalOpen = ref(false);
const searchQuery = ref("");
const selectedWorkoutId = ref<number | null>(null);
const viewMode = ref<'cards' | 'table'>('cards');

watch(viewMode, (val) => {
    if (!val) {
        viewMode.value = 'cards';
    }
});

// Mock workout data
const workouts = ref([
    {
        id: 1,
        name: "Morning Workout",
        date: "Dec 15, 2025",
        duration: "0:50",
        categories: {
            weightlifting: [
                { id: 1, name: "Bicep Curl (Machine)", sets: 3, reps: 12 },
            ],
            cardio: [
                { id: 1, name: "Treadmill", duration: "12 min" },
            ],
            dynamic: [
                { id: 1, name: "Mountain Climbers", frequency: "3 rounds" },
            ],
        },
    },
    {
        id: 2,
        name: "Evening Push Day",
        date: "Dec 14, 2025",
        duration: "1:15",
        categories: {
            weightlifting: [
                { id: 1, name: "Bench Press", sets: 4, reps: 8 },
                { id: 2, name: "Shoulder Press", sets: 3, reps: 10 },
            ],
            cardio: [
                { id: 1, name: "Assault Bike", duration: "8 min" },
            ],
            dynamic: [
                { id: 1, name: "Medicine Ball Slams", frequency: "4 rounds" },
            ],
        },
    },
    {
        id: 3,
        name: "Leg Day",
        date: "Dec 13, 2025",
        duration: "1:30",
        categories: {
            weightlifting: [
                { id: 1, name: "Squat", sets: 5, reps: 6 },
                { id: 2, name: "Leg Press", sets: 4, reps: 12 },
            ],
            cardio: [
                { id: 1, name: "Row Erg", duration: "10 min" },
            ],
            dynamic: [
                { id: 1, name: "Box Jumps", frequency: "5 rounds" },
            ],
        },
    },
    {
        id: 4,
        name: "Back & Biceps",
        date: "Dec 12, 2025",
        duration: "1:20",
        categories: {
            weightlifting: [
                { id: 1, name: "Deadlift", sets: 4, reps: 6 },
                { id: 2, name: "Pull Ups", sets: 3, reps: 8 },
            ],
            cardio: [
                { id: 1, name: "Bike Sprint", duration: "6 min" },
            ],
            dynamic: [
                { id: 1, name: "Farmer Carry", frequency: "5 rounds" },
            ],
        },
    },
]);

const createWorkout = (name: string) => {
    const newId =
        workouts.value.length > 0
            ? Math.max(...workouts.value.map((w) => w.id)) + 1
            : 1;
    const newWorkout = {
        id: newId,
        name,
        date: new Date().toLocaleDateString("en-US", {
            month: "short",
            day: "numeric",
            year: "numeric",
        }),
        duration: "0:00",
        categories: {
            weightlifting: [],
            cardio: [],
            dynamic: [],
        },
    };
    workouts.value.unshift(newWorkout);
    navigateTo(`/workouts/${newId}`);
};

const filteredWorkouts = computed(() => {
    if (!searchQuery.value) {
        return workouts.value;
    }
    const query = searchQuery.value.toLowerCase();
    return workouts.value.filter(
        (workout) =>
            workout.name.toLowerCase().includes(query) ||
            workout.date.toLowerCase().includes(query),
    );
});

const viewportWidth = ref(0);

const updateViewport = () => {
    viewportWidth.value = window.innerWidth;
};

onMounted(() => {
    updateViewport();
    window.addEventListener("resize", updateViewport);
});

onBeforeUnmount(() => {
    window.removeEventListener("resize", updateViewport);
});

const columnsCount = computed(() => {
    if (viewportWidth.value < 768) {
        return 1;
    }
    if (viewportWidth.value < 1024) {
        return 2;
    }
    if (viewportWidth.value < 1280) {
        return 3;
    }
    return 4;
});

const columnedWorkouts = computed(() => {
    const columns = Array.from({ length: columnsCount.value }, () => [] as typeof workouts.value);
    filteredWorkouts.value.forEach((workout, index) => {
        columns[index % columnsCount.value].push(workout);
    });
    return columns;
});

const toggleWorkout = (id: number) => {
    selectedWorkoutId.value = selectedWorkoutId.value === id ? null : id;
};

const getCategoryCount = (workout: typeof workouts.value[number]) => {
    const categories = workout.categories;
    return (
        categories.weightlifting.length +
        categories.cardio.length +
        categories.dynamic.length
    );
};

const getCategoryPercent = (
    workout: typeof workouts.value[number],
    category: "weightlifting" | "cardio" | "dynamic",
) => {
    const total = getCategoryCount(workout);
    if (total === 0) {
        return 0;
    }
    return Math.round((workout.categories[category].length / total) * 100);
};
</script>

<template>
    <div class="flex-1 flex flex-col h-full">
        <!-- Header -->
        <header class="flex h-16 items-center justify-between border-b px-6 shrink-0">
            <h1 class="text-xl font-semibold">Workouts</h1>
            <div class="flex items-center gap-3">
                <ToggleGroup v-model="viewMode" type="single" variant="outline" size="sm">
                    <ToggleGroupItem value="cards" aria-label="Card view">
                        <LayoutGrid class="h-4 w-4" />
                    </ToggleGroupItem>
                    <ToggleGroupItem value="table" aria-label="Table view">
                        <Table2 class="h-4 w-4" />
                    </ToggleGroupItem>
                </ToggleGroup>

                <Button @click="isAddWorkoutModalOpen = true">
                    <Plus class="h-4 w-4 mr-2" />
                    Add Workout
                </Button>

                <TooltipProvider>
                    <Tooltip>
                        <TooltipTrigger as-child>
                            <Button variant="ghost" size="icon" class="relative">
                                <Bell class="h-5 w-5" />
                                <Badge class="absolute -top-1 -right-1 h-4 w-4 p-0 flex items-center justify-center text-[10px]" variant="destructive">
                                    3
                                </Badge>
                            </Button>
                        </TooltipTrigger>
                        <TooltipContent>
                            <p>Notifications (N)</p>
                        </TooltipContent>
                    </Tooltip>
                </TooltipProvider>
            </div>
        </header>

        <!-- Body -->
        <div class="flex-1 overflow-auto p-6 space-y-6">
            <div class="relative">
                <Search class="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                <Input
                    v-model="searchQuery"
                    placeholder="Search workouts..."
                    class="pl-10 h-11"
                />
            </div>

            <div
                v-if="filteredWorkouts.length === 0"
                class="text-center py-12 text-muted-foreground"
            >
                No workouts found
            </div>

            <Transition
                v-else
                mode="out-in"
                enter-active-class="transition-all duration-300 ease-out"
                enter-from-class="opacity-0 scale-[0.97]"
                enter-to-class="opacity-100 scale-100"
                leave-active-class="transition-all duration-300 ease-in"
                leave-from-class="opacity-100 scale-100"
                leave-to-class="opacity-0 scale-[0.97]"
            >
                <div v-if="viewMode === 'cards'" key="cards" class="flex flex-col md:flex-row gap-6">
                    <div
                        v-for="(column, columnIndex) in columnedWorkouts"
                        :key="`column-${columnIndex}`"
                        class="flex flex-col gap-6 flex-1"
                    >
                        <div
                            v-for="workout in column"
                            :key="workout.id"
                            class="rounded-lg border bg-card text-card-foreground shadow-sm transition-[height] duration-300 cursor-pointer"
                            :class="selectedWorkoutId === workout.id ? 'h-[440px]' : 'h-[260px]'"
                            role="button"
                            tabindex="0"
                            :aria-expanded="selectedWorkoutId === workout.id"
                            @click="toggleWorkout(workout.id)"
                            @keydown.enter.prevent="toggleWorkout(workout.id)"
                            @keydown.space.prevent="toggleWorkout(workout.id)"
                        >
                            <Card class="border-0 shadow-none h-full flex flex-col">
                                <CardHeader class="pb-2">
                                    <div class="flex items-center justify-between">
                                        <div>
                                            <h3 class="text-lg font-semibold">
                                                {{ workout.name }}
                                            </h3>
                                            <div class="flex items-center gap-2 text-sm text-muted-foreground mt-1">
                                                <Badge variant="secondary">{{ workout.date }}</Badge>
                                                <span class="flex items-center gap-1">
                                                    <Clock class="h-3 w-3" />
                                                    {{ workout.duration }}
                                                </span>
                                            </div>
                                        </div>
                                        <div class="flex items-center gap-2">
                                            <Button
                                                variant="ghost"
                                                size="icon"
                                                class="h-8 w-8"
                                                @click.stop
                                            >
                                                <MoreHorizontal class="h-4 w-4" />
                                            </Button>
                                            <ChevronDown
                                                class="h-4 w-4 text-muted-foreground transition-transform duration-200"
                                                :class="selectedWorkoutId === workout.id ? 'rotate-180' : ''"
                                            />
                                        </div>
                                    </div>
                                </CardHeader>

                                <CardContent class="flex flex-1 flex-col">
                                    <div class="space-y-3">
                                        <WorkoutCategorySummaryRow
                                            label="Weightlifting"
                                            :percentage="getCategoryPercent(workout, 'weightlifting')"
                                        />
                                        <WorkoutCategorySummaryRow
                                            label="Cardio"
                                            :percentage="getCategoryPercent(workout, 'cardio')"
                                        />
                                        <WorkoutCategorySummaryRow
                                            label="Dynamic"
                                            :percentage="getCategoryPercent(workout, 'dynamic')"
                                        />
                                    </div>

                                    <div
                                        class="mt-4 overflow-hidden transition-[max-height,opacity] duration-300"
                                        :class="selectedWorkoutId === workout.id ? 'max-h-[320px] opacity-100' : 'max-h-0 opacity-0'"
                                    >
                                        <div class="border-t pt-4 space-y-4">
                                            <WorkoutCategorySection
                                                label="Weightlifting"
                                                :count="workout.categories.weightlifting.length"
                                                empty-text="No weightlifting exercises yet"
                                            >
                                                <WorkoutCategoryItemRow
                                                    v-for="lift in workout.categories.weightlifting"
                                                    :key="`lift-${lift.id}`"
                                                    :name="lift.name"
                                                    :detail="`${lift.sets} sets x ${lift.reps} reps`"
                                                />
                                            </WorkoutCategorySection>

                                            <WorkoutCategorySection
                                                label="Cardio"
                                                :count="workout.categories.cardio.length"
                                                empty-text="No cardio sessions yet"
                                            >
                                                <WorkoutCategoryItemRow
                                                    v-for="cardio in workout.categories.cardio"
                                                    :key="`cardio-${cardio.id}`"
                                                    :name="cardio.name"
                                                    :detail="cardio.duration"
                                                />
                                            </WorkoutCategorySection>

                                            <WorkoutCategorySection
                                                label="Dynamic"
                                                :count="workout.categories.dynamic.length"
                                                empty-text="No dynamic work yet"
                                            >
                                                <WorkoutCategoryItemRow
                                                    v-for="dynamic in workout.categories.dynamic"
                                                    :key="`dynamic-${dynamic.id}`"
                                                    :name="dynamic.name"
                                                    :detail="dynamic.frequency"
                                                />
                                            </WorkoutCategorySection>

                                            <div class="flex justify-end">
                                                <Button size="sm" @click.stop="navigateTo(`/workouts/${workout.id}`)">
                                                    Open workout
                                                </Button>
                                            </div>
                                        </div>
                                    </div>
                                </CardContent>
                            </Card>
                        </div>
                    </div>
                </div>

                <div v-else key="table" class="rounded-lg border">
                    <Table>
                        <TableHeader>
                            <TableRow class="bg-muted/50">
                                <TableHead>Name</TableHead>
                                <TableHead>Date</TableHead>
                                <TableHead>Duration</TableHead>
                                <TableHead class="text-center">Weightlifting</TableHead>
                                <TableHead class="text-center">Cardio</TableHead>
                                <TableHead class="text-center">Dynamic</TableHead>
                                <TableHead class="text-center">Total</TableHead>
                                <TableHead class="w-[50px]" />
                            </TableRow>
                        </TableHeader>
                        <TableBody>
                            <TableRow
                                v-for="workout in filteredWorkouts"
                                :key="workout.id"
                                class="cursor-pointer"
                                @click="navigateTo(`/workouts/${workout.id}`)"
                            >
                                <TableCell class="font-medium">{{ workout.name }}</TableCell>
                                <TableCell>
                                    <Badge variant="secondary">{{ workout.date }}</Badge>
                                </TableCell>
                                <TableCell>
                                    <span class="flex items-center gap-1">
                                        <Clock class="h-3 w-3" />
                                        {{ workout.duration }}
                                    </span>
                                </TableCell>
                                <TableCell class="text-center">
                                    {{ workout.categories.weightlifting.length }}
                                </TableCell>
                                <TableCell class="text-center">
                                    {{ workout.categories.cardio.length }}
                                </TableCell>
                                <TableCell class="text-center">
                                    {{ workout.categories.dynamic.length }}
                                </TableCell>
                                <TableCell class="text-center font-medium">
                                    {{ getCategoryCount(workout) }}
                                </TableCell>
                                <TableCell>
                                    <Button variant="ghost" size="icon" class="h-8 w-8" @click.stop>
                                        <MoreHorizontal class="h-4 w-4" />
                                    </Button>
                                </TableCell>
                            </TableRow>
                        </TableBody>
                    </Table>
                </div>
            </Transition>
        </div>

        <WorkoutAddModal v-model="isAddWorkoutModalOpen" @create="createWorkout" />
    </div>
</template>
