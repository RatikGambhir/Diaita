<script setup lang="ts">
import { Clock, MoreHorizontal, Trash2 } from 'lucide-vue-next'
import Button from '~/components/ui/button/Button.vue'
import Badge from '~/components/ui/badge/Badge.vue'
import Table from '~/components/ui/table/Table.vue'
import TableHeader from '~/components/ui/table/TableHeader.vue'
import TableBody from '~/components/ui/table/TableBody.vue'
import TableRow from '~/components/ui/table/TableRow.vue'
import TableHead from '~/components/ui/table/TableHead.vue'
import TableCell from '~/components/ui/table/TableCell.vue'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger
} from '~/components/ui/dropdown-menu'
import type { Workout } from '~/types/WorkoutTypes'
import { formatWorkoutDate, formatWorkoutDuration, formatWorkoutVolume } from '~/utils/workouts'

interface Props {
  workouts: Workout[]
}

defineProps<Props>()

const emit = defineEmits<{
  'open-workout': [id: string]
  'delete-workout': [id: string]
}>()
</script>

<template>
  <div class="rounded-lg border">
    <Table>
      <TableHeader>
        <TableRow class="bg-muted/50">
          <TableHead>Name</TableHead>
          <TableHead>Date</TableHead>
          <TableHead>Duration</TableHead>
          <TableHead class="text-center">Lifting</TableHead>
          <TableHead class="text-center">Cardio</TableHead>
          <TableHead class="text-center">Mobility</TableHead>
          <TableHead class="text-center">Volume</TableHead>
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
            <Badge variant="secondary">{{ formatWorkoutDate(workout.performedAt) }}</Badge>
          </TableCell>
          <TableCell>
            <span class="flex items-center gap-1">
              <Clock class="h-3 w-3" />
              {{ formatWorkoutDuration(workout.durationSeconds) }}
            </span>
          </TableCell>
          <TableCell class="text-center">{{ workout.totals.liftingCount }}</TableCell>
          <TableCell class="text-center">{{ workout.totals.cardioCount }}</TableCell>
          <TableCell class="text-center">{{ workout.totals.mobilityCount }}</TableCell>
          <TableCell class="text-center font-medium">
            {{ formatWorkoutVolume(workout.totals.totalVolumeKg) }}
          </TableCell>
          <TableCell>
            <DropdownMenu>
              <DropdownMenuTrigger as-child>
                <Button variant="ghost" size="icon" class="h-8 w-8" @click.stop>
                  <MoreHorizontal class="h-4 w-4" />
                </Button>
              </DropdownMenuTrigger>
              <DropdownMenuContent align="end">
                <DropdownMenuItem @click.stop="emit('open-workout', workout.id)">
                  Open workout
                </DropdownMenuItem>
                <DropdownMenuItem class="text-destructive" @click.stop="emit('delete-workout', workout.id)">
                  <Trash2 class="h-4 w-4 mr-2" />
                  Delete
                </DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>
          </TableCell>
        </TableRow>
      </TableBody>
    </Table>
  </div>
</template>
