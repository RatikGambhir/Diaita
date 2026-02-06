<script setup lang="ts">
import { computed } from 'vue'
import Card from '~/components/ui/card/Card.vue'
import CardContent from '~/components/ui/card/CardContent.vue'
import Badge from '~/components/ui/badge/Badge.vue'

interface FieldConfig {
  key: string
  label: string
  type?: 'text' | 'number' | 'boolean' | 'array' | 'badges'
  suffix?: string
  badgeVariant?: 'default' | 'secondary' | 'destructive' | 'outline'
}

interface Props {
  title: string
  data: Record<string, any>
  fields: FieldConfig[]
  showEmpty?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  showEmpty: false,
})

const formatValue = (value: any, field: FieldConfig): string => {
  if (value === null || value === undefined || value === '') return '—'
  if (field.type === 'boolean') return value ? 'Yes' : 'No'
  if (field.type === 'array' && Array.isArray(value)) {
    return value.length > 0 ? value.join(', ') : '—'
  }
  const suffix = field.suffix ? ` ${field.suffix}` : ''
  return String(value) + suffix
}

const hasData = (field: FieldConfig): boolean => {
  const value = props.data[field.key]
  if (value === null || value === undefined || value === '') return false
  if (Array.isArray(value) && value.length === 0) return false
  return true
}

const visibleFields = computed(() => {
  if (props.showEmpty) return props.fields
  return props.fields.filter(field => hasData(field))
})

const gridFields = computed(() => visibleFields.value.filter(f => f.type !== 'badges'))
const badgeFields = computed(() => visibleFields.value.filter(f => f.type === 'badges'))

const hasAnyData = computed(() => visibleFields.value.length > 0)
</script>

<template>
  <Card v-if="hasAnyData || showEmpty">
    <CardContent class="pt-6">
      <h3 class="text-lg font-semibold mb-4">{{ title }}</h3>

      <!-- Grid fields -->
      <div v-if="gridFields.length > 0" class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4 text-sm">
        <div v-for="field in gridFields" :key="field.key">
          <p class="text-muted-foreground">{{ field.label }}</p>
          <p class="text-foreground font-medium">{{ formatValue(data[field.key], field) }}</p>
        </div>
      </div>

      <!-- Badge fields -->
      <div v-for="field in badgeFields" :key="field.key" class="mt-4">
        <p class="text-muted-foreground text-sm mb-2">{{ field.label }}</p>
        <div class="flex flex-wrap gap-2">
          <Badge
            v-for="item in (data[field.key] || [])"
            :key="item"
            :variant="field.badgeVariant || 'secondary'"
          >
            {{ item }}
          </Badge>
        </div>
      </div>
    </CardContent>
  </Card>
</template>
