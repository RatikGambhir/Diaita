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
import Badge from '~/components/ui/badge/Badge.vue'
import ToggleGroup from '~/components/ui/toggle-group/ToggleGroup.vue'
import ToggleGroupItem from '~/components/ui/toggle-group/ToggleGroupItem.vue'
import GenericTabGroup from '~/components/GenericTabGroup.vue'
import WorkoutTable from '~/components/workouts/WorkoutTable.vue'
import ExerciseLibraryBrowser from '~/components/workouts/ExerciseLibraryBrowser.vue'
import WorkoutPerformanceSummary from '~/components/workouts/WorkoutPerformanceSummary.vue'
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuTrigger
} from '~/components/ui/dropdown-menu'
import { Plus, MoreHorizontal, Search, ChevronDown, Clock, LayoutGrid, Table2, Trash2, Loader2 } from 'lucide-vue-next'
import { useWorkouts } from '~/composables/useWorkouts'
import type { Workout, WorkoutExerciseCategory } from '~/types/WorkoutTypes'
import {
    categoryPercent,
    exercisesByCategory,
    exerciseSummaryDetail,
    formatWorkoutDate,
    formatWorkoutDuration,
} from '~/utils/workouts'

const isAddWorkoutModalOpen = ref(false);
const activeTab = ref('home');
const searchQuery = ref("");
const selectedWorkoutId = ref<string | null>(null);
const viewMode = ref<'cards' | 'table'>('cards');

const {
    workouts,
    isLoading,
    isMutating,
    loadWorkouts,
    createWorkout,
    deleteWorkout,
} = useWorkouts();

const workoutTabs = [
    { value: "home", label: "Home" },
    { value: "exercises", label: "Exercises" },
    { value: "performance", label: "Performance" },
];

watch(viewMode, (val) => {
    if (!val) {
        viewMode.value = 'cards';
    }
});

onMounted(() => {
    void loadWorkouts();
});

const handleCreate = async (payload: { name: string; performedAt: string; durationSeconds: number | null }) => {
    const workout = await createWorkout(payload);
    if (workout) {
        await navigateTo(`/workouts/${workout.id}`);
    }
};

const handleDelete = async (workoutId: string) => {
    if (selectedWorkoutId.value === workoutId) {
        selectedWorkoutId.value = null;
    }
    await deleteWorkout(workoutId);
};

// Filtering stays client-side so typing never waits on a round trip; the list is already loaded.
const filteredWorkouts = computed(() => {
    const query = searchQuery.value.trim().toLowerCase();
    if (!query) {
        return workouts.value;
    }

    return workouts.value.filter(
        (workout) =>
            workout.name.toLowerCase().includes(query)
            || formatWorkoutDate(workout.performedAt).toLowerCase().includes(query),
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
    const columns = Array.from({ length: columnsCount.value }, () => [] as Workout[]);
    filteredWorkouts.value.forEach((workout, index) => {
        columns[index % columnsCount.value]?.push(workout)
    });
    return columns;
});

const toggleWorkout = (id: string) => {
    selectedWorkoutId.value = selectedWorkoutId.value === id ? null : id;
};

const categorySections: Array<{ category: WorkoutExerciseCategory; label: string; emptyText: string }> = [
    { category: 'lifting', label: 'Lifting', emptyText: 'No lifting exercises yet' },
    { category: 'cardio', label: 'Cardio', emptyText: 'No cardio sessions yet' },
    { category: 'mobility', label: 'Mobility', emptyText: 'No mobility work yet' },
];
</script>

<template>
    <div class="flex-1 flex flex-col h-full">
        <header class="flex h-16 items-center justify-between border-b px-6 shrink-0">
            <h1 class="text-xl font-semibold">Workouts</h1>
        </header>

        <div class="flex-1 overflow-auto p-6">
            <GenericTabGroup
                v-model="activeTab"
                :tabs="workoutTabs"
                tab-trigger-class="text-base px-5 py-2 transition-all duration-300 ease-out hover:text-foreground data-[state=active]:-translate-y-0.5 data-[state=active]:shadow-sm"
            >
                <template #leading>
                    <Transition
                        enter-active-class="transition-all duration-300 ease-out"
                        enter-from-class="opacity-0 -translate-y-1"
                        enter-to-class="opacity-100 translate-y-0"
                        leave-active-class="transition-all duration-200 ease-in"
                        leave-from-class="opacity-100 translate-y-0"
                        leave-to-class="opacity-0 -translate-y-1"
                    >
                        <div v-if="activeTab === 'home'" class="flex flex-1 gap-3">
                            <div class="relative w-[24rem] max-w-full">
                                <Search class="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                                <Input
                                    v-model="searchQuery"
                                    placeholder="Search workouts..."
                                    class="pl-10 h-11"
                                />
                            </div>
                            <div class="flex items-center gap-3">
                                <ToggleGroup v-model="viewMode" type="single" variant="outline" size="sm">
                                    <ToggleGroupItem value="cards" aria-label="Card view">
                                        <LayoutGrid class="h-4 w-4" />
                                    </ToggleGroupItem>
                                    <ToggleGroupItem value="table" aria-label="Table view">
                                        <Table2 class="h-4 w-4" />
                                    </ToggleGroupItem>
                                </ToggleGroup>

                                <Button :disabled="isMutating" @click="isAddWorkoutModalOpen = true">
                                    <Plus class="h-4 w-4 mr-2" />
                                    Add Workout
                                </Button>
                            </div>
                        </div>
                    </Transition>
                </template>
                <Transition
                    mode="out-in"
                    enter-active-class="transition-all duration-300 ease-out"
                    enter-from-class="opacity-0 translate-x-5"
                    enter-to-class="opacity-100 translate-x-0"
                    leave-active-class="transition-all duration-250 ease-in"
                    leave-from-class="opacity-100 translate-x-0"
                    leave-to-class="opacity-0 -translate-x-5"
                >
                    <div :key="activeTab" class="mt-0 space-y-6 overflow-hidden">
                        <template v-if="activeTab === 'home'">
                            <div
                                v-if="isLoading"
                                class="flex items-center justify-center gap-2 py-12 text-muted-foreground"
                            >
                                <Loader2 class="h-4 w-4 animate-spin" />
                                Loading workouts…
                            </div>

                            <div
                                v-else-if="filteredWorkouts.length === 0"
                                class="text-center py-12 text-muted-foreground"
                            >
                                {{ searchQuery ? 'No workouts match your search' : 'No workouts logged yet — add your first one' }}
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
                                            class="cursor-pointer overflow-hidden rounded-lg border bg-card text-card-foreground shadow-sm transition-[height] duration-300"
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
                                                                <Badge variant="secondary">{{ formatWorkoutDate(workout.performedAt) }}</Badge>
                                                                <span class="flex items-center gap-1">
                                                                    <Clock class="h-3 w-3" />
                                                                    {{ formatWorkoutDuration(workout.durationSeconds) }}
                                                                </span>
                                                            </div>
                                                        </div>
                                                        <div class="flex items-center gap-2">
                                                            <DropdownMenu>
                                                                <DropdownMenuTrigger as-child>
                                                                    <Button
                                                                        variant="ghost"
                                                                        size="icon"
                                                                        class="h-8 w-8"
                                                                        @click.stop
                                                                    >
                                                                        <MoreHorizontal class="h-4 w-4" />
                                                                    </Button>
                                                                </DropdownMenuTrigger>
                                                                <DropdownMenuContent align="end">
                                                                    <DropdownMenuItem @click.stop="navigateTo(`/workouts/${workout.id}`)">
                                                                        Open workout
                                                                    </DropdownMenuItem>
                                                                    <DropdownMenuItem
                                                                        class="text-destructive"
                                                                        @click.stop="handleDelete(workout.id)"
                                                                    >
                                                                        <Trash2 class="h-4 w-4 mr-2" />
                                                                        Delete
                                                                    </DropdownMenuItem>
                                                                </DropdownMenuContent>
                                                            </DropdownMenu>
                                                            <ChevronDown
                                                                class="h-4 w-4 text-muted-foreground transition-transform duration-200"
                                                                :class="selectedWorkoutId === workout.id ? 'rotate-180' : ''"
                                                            />
                                                        </div>
                                                    </div>
                                                </CardHeader>

                                                <CardContent class="flex min-h-0 flex-1 flex-col">
                                                    <div class="space-y-3">
                                                        <WorkoutCategorySummaryRow
                                                            v-for="section in categorySections"
                                                            :key="section.category"
                                                            :label="section.label"
                                                            :percentage="categoryPercent(workout, section.category)"
                                                        />
                                                    </div>

                                                    <div
                                                        class="mt-4 min-h-0 overflow-hidden transition-[max-height,opacity] duration-300"
                                                        :class="selectedWorkoutId === workout.id ? 'max-h-[360px] opacity-100' : 'max-h-0 opacity-0'"
                                                    >
                                                        <div class="flex h-full min-h-0 flex-col border-t pt-4">
                                                            <div class="min-h-0 flex-1 space-y-4 overflow-y-auto pr-1">
                                                                <WorkoutCategorySection
                                                                    v-for="section in categorySections"
                                                                    :key="section.category"
                                                                    :label="section.label"
                                                                    :count="exercisesByCategory(workout.exercises, section.category).length"
                                                                    :empty-text="section.emptyText"
                                                                >
                                                                    <WorkoutCategoryItemRow
                                                                        v-for="exercise in exercisesByCategory(workout.exercises, section.category)"
                                                                        :key="exercise.id"
                                                                        :name="exercise.name"
                                                                        :detail="exerciseSummaryDetail(exercise)"
                                                                    />
                                                                </WorkoutCategorySection>
                                                            </div>

                                                            <div class="mt-4 flex justify-end">
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

                                <WorkoutTable
                                    v-else
                                    key="table"
                                    :workouts="filteredWorkouts"
                                    @open-workout="navigateTo(`/workouts/${$event}`)"
                                    @delete-workout="handleDelete"
                                />
                            </Transition>
                        </template>

                        <ExerciseLibraryBrowser v-else-if="activeTab === 'exercises'" />

                        <WorkoutPerformanceSummary v-else />
                    </div>
                </Transition>
            </GenericTabGroup>
        </div>

        <WorkoutAddModal v-model="isAddWorkoutModalOpen" @create="handleCreate" />
    </div>
</template>
