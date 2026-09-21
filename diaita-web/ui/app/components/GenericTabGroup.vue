<script setup lang="ts">
import { ref, watch } from 'vue'
import { cn } from '~/lib/utils'
import Tabs from '~/components/ui/tabs/Tabs.vue'
import TabsList from '~/components/ui/tabs/TabsList.vue'
import TabsTrigger from '~/components/ui/tabs/TabsTrigger.vue'

export interface Tab {
  value: string
  label: string
}

interface Props {
  tabs: Tab[]
  modelValue?: string
  class?: string
  headerClass?: string
  tabsListClass?: string
  tabTriggerClass?: string
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: undefined,
  class: '',
  headerClass: '',
  tabsListClass: '',
  tabTriggerClass: ''
})

const emit = defineEmits<{
  'update:modelValue': [value: string]
}>()

const internalActiveTab = ref(props.modelValue || props.tabs[0]?.value || '')

watch(() => props.modelValue, (newValue) => {
  if (newValue !== undefined) {
    internalActiveTab.value = newValue
  }
})

watch(internalActiveTab, (newValue) => {
  emit('update:modelValue', newValue)
})
</script>

<template>
  <Tabs v-model="internalActiveTab" :class="cn('w-full space-y-7', props.class)">
    <div :class="cn('flex flex-col gap-4 sm:flex-row sm:items-center', props.headerClass)">
      <slot name="leading" />
      <TabsList
        :class="cn(
          'inline-flex h-auto items-center justify-start gap-6 border-b bg-transparent p-0 sm:ml-auto',
          tabsListClass
        )"
      >
        <TabsTrigger
          v-for="tab in tabs"
          :key="tab.value"
          :value="tab.value"
          :class="cn(
            'relative rounded-none border-b-2 border-transparent bg-transparent px-0 pb-3 pt-1 text-sm font-semibold text-muted-foreground shadow-none transition-colors data-[state=active]:border-primary data-[state=active]:bg-transparent data-[state=active]:text-foreground data-[state=active]:shadow-none',
            tabTriggerClass
          )"
        >
          {{ tab.label }}
        </TabsTrigger>
      </TabsList>
    </div>
    <slot />
  </Tabs>
</template>
