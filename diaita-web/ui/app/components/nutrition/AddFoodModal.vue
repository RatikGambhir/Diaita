<script setup lang="ts">
import { computed, ref } from "vue"
import { Search } from "lucide-vue-next"
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

type Ingredient = {
    name: string;
    calories: number;
    carbs: number;
    protein: number;
    fat: number;
    servingSize: string;
};

type SearchFilter = "ingredient" | "product";

const props = defineProps<{
    open: boolean;
    mealName: string;
}>();

const emit = defineEmits<{
    (e: "update:open", value: boolean): void;
    (e: "save-foods", ingredients: Ingredient[]): void;
}>();

const searchQuery = ref("");
const selectedIngredients = ref<Ingredient[]>([]);
const searchFilter = ref<SearchFilter>("ingredient");

const ingredientsList: Ingredient[] = [
    { name: "Chicken Breast", calories: 165, carbs: 0, protein: 31, fat: 3.6, servingSize: "100g" },
    { name: "Brown Rice", calories: 216, carbs: 45, protein: 5, fat: 1.8, servingSize: "1 cup" },
    { name: "Broccoli", calories: 55, carbs: 11, protein: 3.7, fat: 0.6, servingSize: "1 cup" },
    { name: "Salmon", calories: 208, carbs: 0, protein: 20, fat: 13, servingSize: "100g" },
    { name: "Egg", calories: 78, carbs: 0.6, protein: 6, fat: 5, servingSize: "1 large" },
    { name: "Avocado", calories: 160, carbs: 9, protein: 2, fat: 15, servingSize: "100g" },
    { name: "Sweet Potato", calories: 103, carbs: 24, protein: 2.3, fat: 0.2, servingSize: "1 medium" },
    { name: "Greek Yogurt", calories: 100, carbs: 6, protein: 17, fat: 0.4, servingSize: "170g" },
    { name: "Tofu", calories: 94, carbs: 2.3, protein: 10, fat: 5.9, servingSize: "100g" },
    { name: "Almonds", calories: 164, carbs: 6, protein: 6, fat: 14, servingSize: "28g" },
    { name: "Spinach", calories: 23, carbs: 3.6, protein: 2.9, fat: 0.4, servingSize: "100g" },
    { name: "Quinoa", calories: 222, carbs: 39, protein: 8, fat: 3.6, servingSize: "1 cup" },
    { name: "Banana", calories: 105, carbs: 27, protein: 1.3, fat: 0.3, servingSize: "1 medium" },
    { name: "Black Beans", calories: 227, carbs: 41, protein: 15, fat: 0.9, servingSize: "1 cup" },
    { name: "Olive Oil", calories: 119, carbs: 0, protein: 0, fat: 13.5, servingSize: "1 tbsp" },
    { name: "Cottage Cheese", calories: 103, carbs: 4.3, protein: 11, fat: 4.3, servingSize: "100g" },
    { name: "Whole Wheat Bread", calories: 81, carbs: 13.8, protein: 4, fat: 1.1, servingSize: "1 slice" },
    { name: "Apple", calories: 95, carbs: 25, protein: 0.5, fat: 0.3, servingSize: "1 medium" },
    { name: "Tuna", calories: 132, carbs: 0, protein: 28, fat: 1.3, servingSize: "100g" },
    { name: "Peanut Butter", calories: 188, carbs: 6.9, protein: 8, fat: 16, servingSize: "2 tbsp" },
];

const isOpen = computed({
    get: () => props.open,
    set: (value) => {
        if (!value) {
            searchQuery.value = "";
            selectedIngredients.value = [];
            searchFilter.value = "ingredient";
        }
        emit("update:open", value);
    },
});

const searchPlaceholder = computed(() => {
    return searchFilter.value === "ingredient" ? "Search ingredients..." : "Search products...";
});

const filteredIngredients = computed(() => {
    const query = searchQuery.value.trim().toLowerCase();
    if (!query) {
        return ingredientsList;
    }

    return ingredientsList.filter((ingredient) =>
        ingredient.name.toLowerCase().includes(query)
    );
});

const isIngredientSelected = (ingredient: Ingredient) => {
    return selectedIngredients.value.some(
        (selectedIngredient) =>
            selectedIngredient.name === ingredient.name &&
            selectedIngredient.servingSize === ingredient.servingSize
    );
};

const handleAddFood = (ingredient: Ingredient) => {
    if (isIngredientSelected(ingredient)) {
        return;
    }
    selectedIngredients.value.push(ingredient);
};

const handleRemoveFood = (index: number) => {
    selectedIngredients.value.splice(index, 1);
};

const handleCancel = () => {
    isOpen.value = false;
};

const handleSave = () => {
    if (selectedIngredients.value.length === 0) {
        return;
    }
    emit("save-foods", [...selectedIngredients.value]);
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

                <div v-if="selectedIngredients.length > 0" class="rounded-lg border border-border bg-muted/40 p-3">
                    <div class="mb-2 text-xs font-medium text-muted-foreground">Selected foods</div>
                    <div class="max-h-40 overflow-y-auto space-y-2">
                        <FoodListItem
                            v-for="(ingredient, index) in selectedIngredients"
                            :key="`${ingredient.name}-${ingredient.servingSize}`"
                            :item="ingredient"
                            action-label="Remove"
                            action-variant="ghost"
                            @action="handleRemoveFood(index)"
                        />
                    </div>
                </div>

                <div class="min-h-0 flex-1 overflow-y-auto space-y-2 pr-1">
                    <FoodListItem
                        v-for="ingredient in filteredIngredients"
                        :key="ingredient.name"
                        :item="ingredient"
                        :action-label="isIngredientSelected(ingredient) ? 'Added' : 'Add'"
                        action-variant="secondary"
                        :action-disabled="isIngredientSelected(ingredient)"
                        @action="handleAddFood(ingredient)"
                    />

                    <div
                        v-if="filteredIngredients.length === 0"
                        class="py-8 text-center text-sm text-muted-foreground"
                    >
                        No ingredients found
                    </div>
                </div>
            </div>

            <div class="flex items-center justify-end gap-2 border-t border-border pt-4">
                <Button variant="outline" @click="handleCancel">
                    Cancel
                </Button>
                <Button :disabled="selectedIngredients.length === 0" @click="handleSave">
                    Save
                </Button>
            </div>
        </DialogContent>
    </Dialog>
</template>
