<script setup lang="ts">
import Card from '~/components/ui/card/Card.vue'
import CardHeader from '~/components/ui/card/CardHeader.vue'
import CardContent from '~/components/ui/card/CardContent.vue'
import Input from '~/components/ui/input/Input.vue'
import Badge from '~/components/ui/badge/Badge.vue'
import { Loader2, Search } from 'lucide-vue-next'
import { useExerciseSearch } from '~/composables/useExerciseSearch'

const {
  query,
  results,
  isSearching,
  error,
  hasSearched,
  isQueryTooShort,
  minQueryLength,
} = useExerciseSearch(50)
</script>

<template>
  <Card>
    <CardHeader>
      <h2 class="text-lg font-semibold">Exercises</h2>
      <p class="text-sm text-muted-foreground">
        Browse the exercise library to see equipment, mechanics, and the focus each movement trains.
      </p>
    </CardHeader>
    <CardContent class="space-y-4">
      <div class="relative max-w-lg">
        <Search class="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
        <Input v-model="query" placeholder="Search exercises..." class="pl-10 h-11" />
      </div>

      <div
        v-if="isSearching"
        class="flex items-center justify-center gap-2 py-10 text-muted-foreground"
      >
        <Loader2 class="h-4 w-4 animate-spin" />
        Searching…
      </div>

      <p v-else-if="error" class="py-10 text-center text-destructive">{{ error }}</p>

      <p v-else-if="isQueryTooShort" class="py-10 text-center text-muted-foreground">
        Type at least {{ minQueryLength }} characters to search.
      </p>

      <p v-else-if="!hasSearched" class="py-10 text-center text-muted-foreground">
        Start typing to search the exercise library.
      </p>

      <p v-else-if="results.length === 0" class="py-10 text-center text-muted-foreground">
        No exercises match that search.
      </p>

      <ul v-else class="grid grid-cols-1 gap-3 md:grid-cols-2">
        <li
          v-for="exercise in results"
          :key="exercise.id ?? exercise.exercise"
          class="rounded-lg border border-border p-4"
        >
          <div class="flex items-start justify-between gap-3">
            <p class="font-medium">{{ exercise.exercise }}</p>
            <Badge v-if="exercise.exerciseType" variant="secondary">
              {{ exercise.exerciseType }}
            </Badge>
          </div>
          <p v-if="exercise.primaryFitnessFocus" class="mt-1 text-sm text-muted-foreground">
            Focus: {{ exercise.primaryFitnessFocus }}
          </p>
          <p v-if="exercise.description" class="mt-1 text-sm text-muted-foreground">
            {{ exercise.description }}
          </p>
        </li>
      </ul>
    </CardContent>
  </Card>
</template>
