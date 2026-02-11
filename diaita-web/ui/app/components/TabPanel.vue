<script setup lang="ts">
import TabsContent from '~/components/ui/tabs/TabsContent.vue'

interface Props {
  value: string
  class?: string
}

const props = withDefaults(defineProps<Props>(), {
  class: ''
})
</script>

<template>
  <TabsContent :value="value" :class="props.class">
    <slot />
  </TabsContent>
</template>
