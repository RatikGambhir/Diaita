<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { Plus } from 'lucide-vue-next'
import WorkoutExerciseItem, { type WorkoutCategory, type WorkoutExercise } from '~/components/workouts/WorkoutExerciseItem.vue'

const exercises = defineModel<WorkoutExercise[]>({ required: true })

const categoryOrder: WorkoutCategory[] = ['Lifting', 'Cardio', 'Mobility']

const groupedExercises = computed(() => {
  return categoryOrder.map((category) => ({
    category,
    items: exercises.value.filter((exercise) => exercise.category === category),
  }))
})

const getCol2Label = (category: WorkoutCategory) => {
  return category === 'Lifting' ? 'Reps' : 'Duration'
}

const getCol3Label = (category: WorkoutCategory) => {
  if (category === 'Lifting') {
    return 'Weight'
  }
  if (category === 'Cardio') {
    return 'Intensity'
  }
  return 'Target(s)'
}

const creatingCategory = ref<WorkoutCategory | null>(null)
const draftByCategory = reactive<Record<WorkoutCategory, WorkoutExercise>>({
  Lifting: { id: 'draft-lifting', name: '', category: 'Lifting', col2Value: '', col3Value: '' },
  Cardio: { id: 'draft-cardio', name: '', category: 'Cardio', col2Value: '', col3Value: '' },
  Mobility: { id: 'draft-mobility', name: '', category: 'Mobility', col2Value: '', col3Value: '' },
})

const resetDraft = (category: WorkoutCategory) => {
  draftByCategory[category] = {
    id: `draft-${category.toLowerCase()}`,
    name: '',
    category,
    col2Value: '',
    col3Value: '',
  }
}

const startCreating = (category: WorkoutCategory) => {
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

const insertAtCategoryEnd = (item: WorkoutExercise) => {
  const category = item.category
  const categoryIndexes = exercises.value
    .map((exercise, index) => (exercise.category === category ? index : -1))
    .filter((index) => index >= 0)

  if (categoryIndexes.length > 0) {
    exercises.value.splice(categoryIndexes[categoryIndexes.length - 1] + 1, 0, item)
    return
  }

  const currentCategoryIndex = categoryOrder.indexOf(category)
  let insertIndex = exercises.value.length

  for (let i = currentCategoryIndex + 1; i < categoryOrder.length; i += 1) {
    const nextCategoryIndex = exercises.value.findIndex((exercise) => exercise.category === categoryOrder[i])
    if (nextCategoryIndex >= 0) {
      insertIndex = nextCategoryIndex
      break
    }
  }

  exercises.value.splice(insertIndex, 0, item)
}

// TODO: Remove this once we integrate with the backend api, ideally when we save we would re-pull the data
const saveDraft = (category: WorkoutCategory) => {
  const draft = draftByCategory[category]
  if (!draft.name.trim()) {
    return
  }

  insertAtCategoryEnd({
    ...draft,
    id: `${category.toLowerCase()}-${Date.now()}-${Math.random().toString(36).slice(2, 7)}`,
  })
  resetDraft(category)
  creatingCategory.value = null
}

const draggingId = ref<string | null>(null)
const draggingCategory = ref<WorkoutCategory | null>(null)
const activeDropZone = ref<{ category: WorkoutCategory; index: number } | null>(null)

const onDragStart = (id: string, category: WorkoutCategory, event: DragEvent) => {
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

const onDragOverDropZone = (category: WorkoutCategory, index: number, event: DragEvent) => {
  if (!draggingId.value || draggingCategory.value !== category) {
    return
  }
  event.preventDefault()
  activeDropZone.value = { category, index }
  if (event.dataTransfer) {
    event.dataTransfer.dropEffect = 'move'
  }
}

const reorderWithinCategory = (category: WorkoutCategory, sourceId: string, targetIndex: number) => {
  const categoryItems = exercises.value.filter((exercise) => exercise.category === category)
  const sourceIndex = categoryItems.findIndex((exercise) => exercise.id === sourceId)
  if (sourceIndex < 0) {
    return
  }

  const [moved] = categoryItems.splice(sourceIndex, 1)
  const normalizedTarget = Math.max(0, Math.min(targetIndex, categoryItems.length))
  categoryItems.splice(normalizedTarget, 0, moved)

  let pointer = 0
  exercises.value = exercises.value.map((exercise) => {
    if (exercise.category !== category) {
      return exercise
    }
    const replacement = categoryItems[pointer]
    pointer += 1
    return replacement
  })
}

const onDropZoneDrop = (category: WorkoutCategory, index: number, event: DragEvent) => {
  if (!draggingId.value || draggingCategory.value !== category) {
    return
  }
  event.preventDefault()
  reorderWithinCategory(category, draggingId.value, index)
  clearDragState()
}

const isDropZoneActive = (category: WorkoutCategory, index: number) => {
  return activeDropZone.value?.category === category && activeDropZone.value?.index === index
}
</script>

<template>
  <div>
    <h2 class="mb-2 text-lg font-semibold text-foreground">Exercise Plan</h2>
    <p class="mb-4 text-sm text-muted-foreground">Hold and drag to reorder exercises</p>

    <div v-for="group in groupedExercises" :key="group.category" class="mb-6">
      <h3 class="mb-3 font-semibold text-primary">{{ group.category }}</h3>

      <div class="space-y-2">
        <template v-for="(item, index) in group.items" :key="item.id">
          <div
            class="h-1.5 rounded-full transition-all duration-150"
            :class="isDropZoneActive(group.category, index) ? 'bg-primary/30' : 'bg-transparent'"
            @dragover="onDragOverDropZone(group.category, index, $event)"
            @drop="onDropZoneDrop(group.category, index, $event)"
          />

          <WorkoutExerciseItem
            :exercise="item"
            :col2-label="getCol2Label(group.category)"
            :col3-label="getCol3Label(group.category)"
            :draggable="true"
            @drag-start="onDragStart(item.id, group.category, $event)"
            @drag-end="clearDragState"
          />
        </template>

        <div
          class="h-1.5 rounded-full transition-all duration-150"
          :class="isDropZoneActive(group.category, group.items.length) ? 'bg-primary/30' : 'bg-transparent'"
          @dragover="onDragOverDropZone(group.category, group.items.length, $event)"
          @drop="onDropZoneDrop(group.category, group.items.length, $event)"
        />

        <Transition name="morph" mode="out-in">
          <button
            v-if="creatingCategory !== group.category"
            :key="`add-${group.category}`"
            type="button"
            class="flex w-full items-center justify-center gap-2 rounded-xl border border-dashed border-border bg-card py-3 text-sm text-muted-foreground transition-colors hover:border-primary/50 hover:text-primary"
            @click="startCreating(group.category)"
          >
            <Plus class="h-4 w-4" />
            Add exercise
          </button>

          <WorkoutExerciseItem
            v-else
            :key="`draft-${group.category}`"
            :exercise="draftByCategory[group.category]"
            :col2-label="getCol2Label(group.category)"
            :col3-label="getCol3Label(group.category)"
            editable
            @update:exercise="draftByCategory[group.category] = $event"
            @save="saveDraft(group.category)"
            @cancel="cancelCreating"
          />
        </Transition>
      </div>
    </div>
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
