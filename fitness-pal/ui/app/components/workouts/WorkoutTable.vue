<script setup lang="ts">
import { Clock, MoreHorizontal } from 'lucide-vue-next'
import Button from '~/components/ui/button/Button.vue'
import Badge from '~/components/ui/badge/Badge.vue'
import Table from '~/components/ui/table/Table.vue'
import TableHeader from '~/components/ui/table/TableHeader.vue'
import TableBody from '~/components/ui/table/TableBody.vue'
import TableRow from '~/components/ui/table/TableRow.vue'
import TableHead from '~/components/ui/table/TableHead.vue'
import TableCell from '~/components/ui/table/TableCell.vue'

interface WorkoutCategory {
  id: number
  name: string
}

interface Workout {
  id: number
  name: string
  date: string
  duration: string
  categories: {
    weightlifting: WorkoutCategory[]
    cardio: WorkoutCategory[]
    dynamic: WorkoutCategory[]
  }
}

interface Props {
  workouts: Workout[]
}

defineProps<Props>()

const emit = defineEmits<{
  'open-workout': [id: number]
}>()

const getCategoryCount = (workout: Workout) => {
  return (
    workout.categories.weightlifting.length
    + workout.categories.cardio.length
    + workout.categories.dynamic.length
  )
}
</script>

<template>
  <div class="rounded-lg border">
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
          v-for="workout in workouts"
          :key="workout.id"
          class="cursor-pointer"
          @click="emit('open-workout', workout.id)"
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
</template>
