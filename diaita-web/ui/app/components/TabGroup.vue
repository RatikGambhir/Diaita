<script setup lang="ts">
import { ref, watch } from 'vue'
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
  tabsListClass?: string
  tabTriggerClass?: string
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: undefined,
  tabsListClass: '',
  tabTriggerClass: ''
})

const emit = defineEmits<{
  'update:modelValue': [value: string]
}>()

// Internal active tab state
const internalActiveTab = ref(props.modelValue || props.tabs[0]?.value || '')

// Watch for external changes to modelValue
watch(() => props.modelValue, (newValue) => {
  if (newValue !== undefined) {
    internalActiveTab.value = newValue
  }
})

// Emit changes when internal state changes
watch(internalActiveTab, (newValue) => {
  emit('update:modelValue', newValue)
})
</script>

<template>
  <Tabs v-model="internalActiveTab" class="w-full">
    <TabsList :class="tabsListClass">
      <TabsTrigger
        v-for="tab in tabs"
        :key="tab.value"
        :value="tab.value"
        :class="tabTriggerClass"
      >
        {{ tab.label }}
      </TabsTrigger>
    </TabsList>
    <slot />
  </Tabs>
</template>
