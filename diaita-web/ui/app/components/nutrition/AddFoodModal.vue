<script setup lang="ts">
import { computed, ref, watch } from "vue"
import { Search } from "lucide-vue-next"
import { useNutritionAutocomplete } from "~/composables/useNutritionAutocomplete"
import { useNutritionSearch } from "~/composables/useNutritionSearch"
import Dialog from "~/components/ui/dialog/Dialog.vue"
import DialogContent from "~/components/ui/dialog/DialogContent.vue"
import DialogHeader from "~/components/ui/dialog/DialogHeader.vue"
import DialogTitle from "~/components/ui/dialog/DialogTitle.vue"
import Input from "~/components/ui/input/Input.vue"
import Button from "~/components/ui/button/Button.vue"
import FoodListItem from "~/components/nutrition/FoodListItem.vue"
import Select from "~/components/ui/select/Select.vue"
import SelectContent from "~/components/ui/select/SelectContent.vue"
import SelectItem from "~/components/ui/select/SelectItem.vue"
import SelectTrigger from "~/components/ui/select/SelectTrigger.vue"
import SelectValue from "~/components/ui/select/SelectValue.vue"
import type { NutritionAutocompleteSuggestion, NutritionFood } from "~/types/nutrition"

const props = defineProps<{
    open: boolean;
    mealName: string;
}>();

const emit = defineEmits<{
    (e: "update:open", value: boolean): void;
    (e: "save-foods", ingredients: NutritionFood[]): void;
}>();

const selectedFoods = ref<NutritionFood[]>([]);
const showSuggestions = ref(true);
const hiddenSuggestionsQuery = ref<string | null>(null);

const {
    query: searchQuery,
    filter: searchFilter,
    results: searchResults,
    isSearching,
    error: searchError,
    hasSearched,
    normalizedQuery,
    searchPlaceholder,
    minQueryLength,
    reset: resetSearch,
} = useNutritionSearch();

const {
    suggestions: autocompleteSuggestions,
    isLoading: isAutocompleting,
    error: autocompleteError,
    minQueryLength: autocompleteMinQueryLength,
} = useNutritionAutocomplete(searchQuery, searchFilter);

const isOpen = computed({
    get: () => props.open,
    set: (value) => emit("update:open", value),
});

const resetModalState = () => {
    selectedFoods.value = [];
    showSuggestions.value = true;
    hiddenSuggestionsQuery.value = null;
    resetSearch();
};

watch(() => props.open, (open) => {
    if (!open) {
        resetModalState();
    }
});

watch(searchQuery, (query) => {
    if (hiddenSuggestionsQuery.value === query) {
        return;
    }

    hiddenSuggestionsQuery.value = null;
    showSuggestions.value = true;
});

watch(searchFilter, () => {
    hiddenSuggestionsQuery.value = null;
    showSuggestions.value = true;
});

const isFoodSelected = (food: NutritionFood) => {
    return selectedFoods.value.some(
        (selectedFood) =>
            selectedFood.id === food.id &&
            selectedFood.category === food.category
    );
};

const handleAddFood = (food: NutritionFood) => {
    if (isFoodSelected(food)) {
        return;
    }
    selectedFoods.value.push(food);
};

const handleSuggestionSelect = (suggestion: NutritionAutocompleteSuggestion) => {
    hiddenSuggestionsQuery.value = suggestion.name;
    showSuggestions.value = false;
    searchQuery.value = suggestion.name;
};

const handleRemoveFood = (index: number) => {
    selectedFoods.value.splice(index, 1);
};

const handleCancel = () => {
    isOpen.value = false;
};

const handleSave = () => {
    if (selectedFoods.value.length === 0) {
        return;
    }
    emit("save-foods", [...selectedFoods.value]);
    isOpen.value = false;
};
</script>

<template>
    <Dialog v-model:open="isOpen">
        <DialogContent class="w-[92vw] max-w-3xl h-[68vh] flex flex-col overflow-hidden">
            <DialogHeader>
                <DialogTitle>
                    Add Food to {{ mealName }}
                </DialogTitle>
            </DialogHeader>

            <div class="flex flex-col gap-4 py-2 flex-1 min-h-0">
                <div class="flex items-center gap-2">
                    <div class="relative flex-1">
                        <Search class="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                        <Input
                            v-model="searchQuery"
                            :placeholder="searchPlaceholder"
                            class="pl-10 h-11"
                        />
                    </div>
                    <Select v-model="searchFilter">
                        <SelectTrigger class="w-[150px] h-11">
                            <SelectValue />
                        </SelectTrigger>
                        <SelectContent>
                            <SelectItem value="ingredient">Ingredient</SelectItem>
                            <SelectItem value="product">Product</SelectItem>
                        </SelectContent>
                    </Select>
                </div>

                <div
                    v-if="showSuggestions && normalizedQuery.length >= autocompleteMinQueryLength && (isAutocompleting || autocompleteSuggestions.length > 0 || autocompleteError)"
                    class="rounded-lg border border-border bg-card"
                >
                    <div class="border-b border-border px-3 py-2 text-xs font-medium text-muted-foreground">
                        Suggestions
                    </div>

                    <div v-if="isAutocompleting" class="px-3 py-3 text-sm text-muted-foreground">
                        Looking up suggestions...
                    </div>

                    <div v-else-if="autocompleteError" class="px-3 py-3 text-sm text-destructive">
                        {{ autocompleteError }}
                    </div>

                    <div v-else class="max-h-44 overflow-y-auto">
                        <button
                            v-for="suggestion in autocompleteSuggestions"
                            :key="`${suggestion.category}-${suggestion.id}`"
                            type="button"
                            class="flex w-full flex-col items-start gap-1 px-3 py-3 text-left transition-colors hover:bg-muted"
                            @click="handleSuggestionSelect(suggestion)"
                        >
                            <span class="text-sm font-medium text-foreground">{{ suggestion.name }}</span>
                            <span class="text-xs text-muted-foreground">
                                {{
                                    suggestion.category === "ingredient"
                                        ? suggestion.aisle || suggestion.possibleUnits.slice(0, 3).join(", ") || "Ingredient"
                                        : suggestion.brand || "Product"
                                }}
                            </span>
                        </button>
                    </div>
                </div>

                <div v-if="selectedFoods.length > 0" class="rounded-lg border border-border bg-muted/40 p-3">
                    <div class="mb-2 text-xs font-medium text-muted-foreground">Selected foods ({{ selectedFoods.length }})</div>
                    <div class="max-h-40 overflow-y-auto space-y-2">
                        <FoodListItem
                            v-for="(food, index) in selectedFoods"
                            :key="`${food.category}-${food.id}`"
                            :item="food"
                            action-label="Remove"
                            action-variant="ghost"
                            @action="handleRemoveFood(index)"
                        />
                    </div>
                </div>

                <div class="min-h-0 flex-1 overflow-y-auto space-y-2 pr-1">
                    <div
                        v-if="!normalizedQuery"
                        class="py-8 text-center text-sm text-muted-foreground"
                    >
                        Start typing to search and add foods.
                    </div>

                    <div
                        v-else-if="normalizedQuery.length < minQueryLength"
                        class="py-8 text-center text-sm text-muted-foreground"
                    >
                        Type at least {{ minQueryLength }} characters to search.
                    </div>

                    <div
                        v-else-if="isSearching"
                        class="py-8 text-center text-sm text-muted-foreground"
                    >
                        Searching {{ searchFilter === "ingredient" ? "ingredients" : "products" }}...
                    </div>

                    <div
                        v-else-if="searchError"
                        class="py-8 text-center text-sm text-destructive"
                    >
                        {{ searchError }}
                    </div>

                    <template v-else>
                        <FoodListItem
                            v-for="food in searchResults"
                            :key="`${food.category}-${food.id}`"
                            :item="food"
                            :action-label="isFoodSelected(food) ? 'Added' : 'Add'"
                            action-variant="secondary"
                            :action-disabled="isFoodSelected(food)"
                            @action="handleAddFood(food)"
                        />

                        <div
                            v-if="hasSearched && searchResults.length === 0"
                            class="py-8 text-center text-sm text-muted-foreground"
                        >
                            No matching foods found.
                        </div>
                    </template>
                </div>
            </div>

            <div class="flex items-center justify-end gap-2 border-t border-border pt-4">
                <Button variant="outline" @click="handleCancel">
                    Cancel
                </Button>
                <Button :disabled="selectedFoods.length === 0" @click="handleSave">
                    Save
                </Button>
            </div>
        </DialogContent>
    </Dialog>
</template>
