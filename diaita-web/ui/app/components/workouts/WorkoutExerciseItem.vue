<script setup lang="ts">
import { GripVertical } from 'lucide-vue-next'
import Button from '~/components/ui/button/Button.vue'
import Input from '~/components/ui/input/Input.vue'

export type WorkoutCategory = 'Lifting' | 'Cardio' | 'Mobility'

export interface WorkoutExercise {
  id: string
  name: string
  category: WorkoutCategory
  col2Value: string
  col3Value: string
}

interface Props {
  exercise: WorkoutExercise
  col2Label: string
  col3Label: string
  editable?: boolean
  draggable?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  editable: false,
  draggable: false,
})

const emit = defineEmits<{
  'drag-start': [event: DragEvent]
  'drag-end': []
  'update:exercise': [exercise: WorkoutExercise]
  'save': []
  'cancel': []
}>()

const updateField = (field: 'name' | 'col2Value' | 'col3Value', value: string) => {
  emit('update:exercise', {
    ...props.exercise,
    [field]: value,
  })
}
</script>

<template>
  <div
    class="select-none rounded-xl p-4 shadow-sm transition-all duration-200 bg-card"
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
            :model-value="exercise.name"
            placeholder="Exercise name"
            class="mt-1"
            @update:model-value="updateField('name', String($event))"
          />
          <p v-else class="mt-1 text-foreground">{{ exercise.name }}</p>
        </div>

        <div>
          <p class="text-sm text-muted-foreground">{{ col2Label }}</p>
          <Input
            v-if="editable"
            :model-value="exercise.col2Value"
            :placeholder="col2Label"
            class="mt-1"
            @update:model-value="updateField('col2Value', String($event))"
          />
          <p v-else class="mt-1 text-foreground">{{ exercise.col2Value }}</p>
        </div>

        <div>
          <p class="text-sm text-muted-foreground">{{ col3Label }}</p>
          <Input
            v-if="editable"
            :model-value="exercise.col3Value"
            :placeholder="col3Label"
            class="mt-1"
            @update:model-value="updateField('col3Value', String($event))"
          />
          <p v-else class="mt-1 text-foreground">{{ exercise.col3Value }}</p>
        </div>
      </div>
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
