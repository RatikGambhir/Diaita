<script setup lang="ts">
import { GripVertical, Trash2 } from 'lucide-vue-next'
import Button from '~/components/ui/button/Button.vue'
import Input from '~/components/ui/input/Input.vue'

/** The shape the editor works in: two free-text metric columns per row, decoded on save. */
export interface WorkoutExerciseDraft {
  id: string
  name: string
  primary: string
  secondary: string
}

interface Props {
  draft: WorkoutExerciseDraft
  primaryLabel: string
  secondaryLabel: string
  editable?: boolean
  draggable?: boolean
  removable?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  editable: false,
  draggable: false,
  removable: false,
})

const emit = defineEmits<{
  'drag-start': [event: DragEvent]
  'drag-end': []
  'update:draft': [draft: WorkoutExerciseDraft]
  'save': []
  'cancel': []
  'remove': []
}>()

const updateField = (field: 'name' | 'primary' | 'secondary', value: string) => {
  emit('update:draft', {
    ...props.draft,
    [field]: value,
  })
}
</script>

<template>
  <div
    class="group select-none rounded-xl p-4 shadow-sm transition-all duration-200 bg-card"
    :draggable="draggable && !editable"
    @dragstart="emit('drag-start', $event)"
    @dragend="emit('drag-end')"
  >
    <div class="flex items-start gap-4">
      <div v-if="!editable" class="mt-1 cursor-grab active:cursor-grabbing">
        <GripVertical class="h-5 w-5 text-muted-foreground" />
      </div>

      <div class="grid flex-1 grid-cols-1 gap-4 md:grid-cols-3">
        <div>
          <p class="text-sm text-muted-foreground">Exercise</p>
          <Input
            v-if="editable"
            :model-value="draft.name"
            placeholder="Exercise name"
            class="mt-1"
            @update:model-value="updateField('name', String($event))"
          />
          <p v-else class="mt-1 text-foreground">{{ draft.name }}</p>
        </div>

        <div>
          <p class="text-sm text-muted-foreground">{{ primaryLabel }}</p>
          <Input
            v-if="editable"
            :model-value="draft.primary"
            :placeholder="primaryLabel"
            class="mt-1"
            @update:model-value="updateField('primary', String($event))"
          />
          <p v-else class="mt-1 text-foreground">{{ draft.primary }}</p>
        </div>

        <div>
          <p class="text-sm text-muted-foreground">{{ secondaryLabel }}</p>
          <Input
            v-if="editable"
            :model-value="draft.secondary"
            :placeholder="secondaryLabel"
            class="mt-1"
            @update:model-value="updateField('secondary', String($event))"
          />
          <p v-else class="mt-1 text-foreground">{{ draft.secondary }}</p>
        </div>
      </div>

      <Button
        v-if="removable && !editable"
        variant="ghost"
        size="icon"
        class="h-8 w-8 opacity-0 transition-opacity group-hover:opacity-100 focus-visible:opacity-100"
        aria-label="Remove exercise"
        @click="emit('remove')"
      >
        <Trash2 class="h-4 w-4 text-destructive" />
      </Button>
    </div>

    <div v-if="editable" class="mt-3 flex justify-end gap-2">
      <Button variant="ghost" size="sm" @click="emit('cancel')">
        Cancel
      </Button>
      <Button size="sm" @click="emit('save')">
        Add
      </Button>
    </div>
  </div>
</template>
