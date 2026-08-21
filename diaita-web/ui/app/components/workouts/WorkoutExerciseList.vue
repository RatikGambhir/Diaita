<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { Plus, Search } from 'lucide-vue-next'
import Button from '~/components/ui/button/Button.vue'
import WorkoutExerciseItem, { type WorkoutExerciseDraft } from '~/components/workouts/WorkoutExerciseItem.vue'
import ExerciseSelectModal from '~/components/ExerciseSelectModal.vue'
import type {
  ExerciseLibraryEntry,
  UpsertWorkoutExercise,
  WorkoutExercise,
  WorkoutExerciseCategory,
} from '~/types/WorkoutTypes'
import {
  WORKOUT_CATEGORY_LABELS,
  exercisePrimaryMetric,
  exerciseSecondaryMetric,
  toUpsertExercise,
} from '~/utils/workouts'

const props = defineProps<{
  exercises: WorkoutExercise[]
  saving?: boolean
}>()

/**
 * The list never mutates its own copy. Every change is emitted so the page can persist it and then
 * re-render from what the service actually stored.
 */
const emit = defineEmits<{
  'upsert': [exercises: UpsertWorkoutExercise[]]
  'remove': [exerciseId: string]
}>()

const categoryOrder: WorkoutExerciseCategory[] = ['lifting', 'cardio', 'mobility']

const groupedExercises = computed(() =>
  categoryOrder.map((category) => ({
    category,
    label: WORKOUT_CATEGORY_LABELS[category],
    items: props.exercises
      .filter((exercise) => exercise.category === category)
      .sort((a, b) => a.position - b.position),
  })),
)

const getPrimaryLabel = (category: WorkoutExerciseCategory) =>
  category === 'lifting' ? 'Sets x Reps' : 'Duration (min)'

const getSecondaryLabel = (category: WorkoutExerciseCategory) => {
  if (category === 'lifting') {
    return 'Weight (kg)'
  }
  if (category === 'cardio') {
    return 'Intensity'
  }
  return 'Target(s)'
}

const toDraft = (exercise: WorkoutExercise): WorkoutExerciseDraft => ({
  id: exercise.id,
  name: exercise.name,
  primary: exercisePrimaryMetric(exercise),
  secondary: exerciseSecondaryMetric(exercise),
})

const emptyDraft = (category: WorkoutExerciseCategory): WorkoutExerciseDraft => ({
  id: `draft-${category}`,
  name: '',
  primary: '',
  secondary: '',
})

const creatingCategory = ref<WorkoutExerciseCategory | null>(null)
const pickerCategory = ref<WorkoutExerciseCategory | null>(null)
const isPickerOpen = ref(false)
const draftByCategory = reactive<Record<WorkoutExerciseCategory, WorkoutExerciseDraft>>({
  lifting: emptyDraft('lifting'),
  cardio: emptyDraft('cardio'),
  mobility: emptyDraft('mobility'),
})
const draftExerciseId = reactive<Record<WorkoutExerciseCategory, number | null>>({
  lifting: null,
  cardio: null,
  mobility: null,
})

const resetDraft = (category: WorkoutExerciseCategory) => {
  draftByCategory[category] = emptyDraft(category)
  draftExerciseId[category] = null
}

const startCreating = (category: WorkoutExerciseCategory) => {
  resetDraft(category)
  creatingCategory.value = category
}

const cancelCreating = () => {
  if (!creatingCategory.value) {
    return
  }
  resetDraft(creatingCategory.value)
  creatingCategory.value = null
}

const openPicker = (category: WorkoutExerciseCategory) => {
  pickerCategory.value = category
  isPickerOpen.value = true
}

const handlePickerSelect = (entry: ExerciseLibraryEntry) => {
  const category = pickerCategory.value
  if (!category) {
    return
  }

  startCreating(category)
  draftByCategory[category] = { ...draftByCategory[category], name: entry.exercise }
  draftExerciseId[category] = entry.id
}

const nextPosition = () =>
  props.exercises.reduce((highest, exercise) => Math.max(highest, exercise.position), -1) + 1

const saveDraft = (category: WorkoutExerciseCategory) => {
  const draft = draftByCategory[category]
  if (!draft.name.trim()) {
    return
  }

  const template: WorkoutExercise = {
    id: draft.id,
    exerciseId: draftExerciseId[category],
    name: draft.name,
    category,
    position: nextPosition(),
    sets: null,
    reps: null,
    weightKg: null,
    durationSeconds: null,
    intensity: null,
    target: null,
    notes: null,
  }

  emit('upsert', [toUpsertExercise(template, draft)])
  resetDraft(category)
  creatingCategory.value = null
}

const updateExistingDraft = (exercise: WorkoutExercise, draft: WorkoutExerciseDraft) => {
  emit('upsert', [toUpsertExercise(exercise, draft)])
}

const editingId = ref<string | null>(null)
const editingDraft = ref<WorkoutExerciseDraft | null>(null)

const startEditing = (exercise: WorkoutExercise) => {
  editingId.value = exercise.id
  editingDraft.value = toDraft(exercise)
}

const cancelEditing = () => {
  editingId.value = null
  editingDraft.value = null
}

const commitEditing = (exercise: WorkoutExercise) => {
  if (editingDraft.value) {
    updateExistingDraft(exercise, editingDraft.value)
  }
  cancelEditing()
}

const draggingId = ref<string | null>(null)
const draggingCategory = ref<WorkoutExerciseCategory | null>(null)
const activeDropZone = ref<{ category: WorkoutExerciseCategory; index: number } | null>(null)

const onDragStart = (id: string, category: WorkoutExerciseCategory, event: DragEvent) => {
  draggingId.value = id
  draggingCategory.value = category
  if (event.dataTransfer) {
    event.dataTransfer.effectAllowed = 'move'
    event.dataTransfer.setData('text/plain', id)
  }
}

const clearDragState = () => {
  draggingId.value = null
  draggingCategory.value = null
  activeDropZone.value = null
}

const onDragOverDropZone = (category: WorkoutExerciseCategory, index: number, event: DragEvent) => {
  if (!draggingId.value || draggingCategory.value !== category) {
    return
  }
  event.preventDefault()
  activeDropZone.value = { category, index }
  if (event.dataTransfer) {
    event.dataTransfer.dropEffect = 'move'
  }
}

/**
 * Reordering rewrites the positions of the affected category and persists all of them, so the order
 * survives a reload rather than living only in local state.
 */
const reorderWithinCategory = (
  category: WorkoutExerciseCategory,
  sourceId: string,
  targetIndex: number,
) => {
  const categoryItems = props.exercises
    .filter((exercise) => exercise.category === category)
    .sort((a, b) => a.position - b.position)

  const sourceIndex = categoryItems.findIndex((exercise) => exercise.id === sourceId)
  if (sourceIndex < 0) {
    return
  }

  const [moved] = categoryItems.splice(sourceIndex, 1)
  if (!moved) {
    return
  }

  const normalizedTarget = Math.max(0, Math.min(targetIndex, categoryItems.length))
  categoryItems.splice(normalizedTarget, 0, moved)

  const slots = props.exercises
    .filter((exercise) => exercise.category === category)
    .map((exercise) => exercise.position)
    .sort((a, b) => a - b)

  const repositioned = categoryItems
    .map((exercise, index) => ({ exercise, position: slots[index] ?? exercise.position }))
    .filter(({ exercise, position }) => exercise.position !== position)
    .map(({ exercise, position }) =>
      toUpsertExercise({ ...exercise, position }, toDraft(exercise)),
    )

  if (repositioned.length > 0) {
    emit('upsert', repositioned)
  }
}

const onDropZoneDrop = (category: WorkoutExerciseCategory, index: number, event: DragEvent) => {
  if (!draggingId.value || draggingCategory.value !== category) {
    return
  }
  event.preventDefault()
  reorderWithinCategory(category, draggingId.value, index)
  clearDragState()
}

const isDropZoneActive = (category: WorkoutExerciseCategory, index: number) =>
  activeDropZone.value?.category === category && activeDropZone.value?.index === index
</script>

<template>
  <div>
    <div>
      <h2 class="mb-2 text-lg font-semibold text-foreground">Exercise Plan</h2>
      <p class="mb-4 text-sm text-muted-foreground">
        Hold and drag to reorder exercises. Click an exercise to edit it.
      </p>
    </div>
    <div class="grid grid-cols-1 gap-4 lg:grid-cols-3">
      <div v-for="group in groupedExercises" :key="group.category" class="mb-6 min-w-0">
        <div class="flex min-w-0 flex-col">
          <h3 class="mb-3 font-semibold text-primary">{{ group.label }}</h3>

          <div class="space-y-2">
            <template v-for="(item, index) in group.items" :key="item.id">
              <div
                class="h-1.5 rounded-full transition-all duration-150"
                :class="isDropZoneActive(group.category, index) ? 'bg-primary/30' : 'bg-transparent'"
                @dragover="onDragOverDropZone(group.category, index, $event)"
                @drop="onDropZoneDrop(group.category, index, $event)"
              />

              <WorkoutExerciseItem
                v-if="editingId === item.id && editingDraft"
                :draft="editingDraft"
                :primary-label="getPrimaryLabel(group.category)"
                :secondary-label="getSecondaryLabel(group.category)"
                editable
                @update:draft="editingDraft = $event"
                @save="commitEditing(item)"
                @cancel="cancelEditing"
              />

              <WorkoutExerciseItem
                v-else
                :draft="toDraft(item)"
                :primary-label="getPrimaryLabel(group.category)"
                :secondary-label="getSecondaryLabel(group.category)"
                :draggable="true"
                removable
                @drag-start="onDragStart(item.id, group.category, $event)"
                @drag-end="clearDragState"
                @remove="emit('remove', item.id)"
                @click="startEditing(item)"
              />
            </template>

            <div
              class="h-1.5 rounded-full transition-all duration-150"
              :class="isDropZoneActive(group.category, group.items.length) ? 'bg-primary/30' : 'bg-transparent'"
              @dragover="onDragOverDropZone(group.category, group.items.length, $event)"
              @drop="onDropZoneDrop(group.category, group.items.length, $event)"
            />

            <Transition name="morph" mode="out-in">
              <div
                v-if="creatingCategory !== group.category"
                :key="`add-${group.category}`"
                class="flex gap-2"
              >
                <button
                  type="button"
                  class="flex flex-1 items-center justify-center gap-2 rounded-xl border border-dashed border-border bg-card py-3 text-sm text-muted-foreground transition-colors hover:border-primary/50 hover:text-primary"
                  :disabled="saving"
                  @click="startCreating(group.category)"
                >
                  <Plus class="h-4 w-4" />
                  Add exercise
                </button>
                <Button
                  variant="outline"
                  size="icon"
                  class="h-auto"
                  :disabled="saving"
                  aria-label="Search exercise library"
                  @click="openPicker(group.category)"
                >
                  <Search class="h-4 w-4" />
                </Button>
              </div>

              <WorkoutExerciseItem
                v-else
                :key="`draft-${group.category}`"
                :draft="draftByCategory[group.category]"
                :primary-label="getPrimaryLabel(group.category)"
                :secondary-label="getSecondaryLabel(group.category)"
                editable
                @update:draft="draftByCategory[group.category] = $event"
                @save="saveDraft(group.category)"
                @cancel="cancelCreating"
              />
            </Transition>
          </div>
        </div>
      </div>
    </div>

    <ExerciseSelectModal v-model="isPickerOpen" @select="handlePickerSelect" />
  </div>
</template>

<style scoped>
.morph-enter-active,
.morph-leave-active {
  transition: all 220ms ease;
}

.morph-enter-from,
.morph-leave-to {
  opacity: 0;
  transform: translateY(8px) scale(0.98);
}

.morph-enter-to,
.morph-leave-from {
  opacity: 1;
  transform: translateY(0) scale(1);
}
</style>
