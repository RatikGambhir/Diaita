<script setup lang="ts">
import { Loader2 } from 'lucide-vue-next'
import Button from '~/components/ui/button/Button.vue'

defineProps<{
    saving?: boolean;
    disabled?: boolean;
}>();

const emit = defineEmits<{
    save: [];
    cancel: [];
}>();
</script>

<template>
    <div class="flex justify-end gap-3">
        <Button
            variant="ghost"
            class="text-muted-foreground"
            :disabled="saving"
            @click="emit('cancel')"
        >
            Cancel
        </Button>
        <Button
            class="bg-[#d96d54] text-white hover:bg-[#c75f49]"
            :disabled="saving || disabled"
            @click="emit('save')"
        >
            <Loader2 v-if="saving" class="mr-2 h-4 w-4 animate-spin" />
            Save changes
        </Button>
    </div>
</template>
