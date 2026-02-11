<script setup lang="ts">
import { computed, ref } from "vue";
import Button from "~/components/ui/button/Button.vue"
import NutritionSummaryCard from "~/components/nutrition/NutritionSummaryCard.vue"
import MacroDistributionCard from "~/components/nutrition/MacroDistributionCard.vue"
import MealCard from "~/components/nutrition/MealCard.vue"
import NutritionResourcesCard from "~/components/nutrition/NutritionResourcesCard.vue"
import { CalendarDays, FileText, Lightbulb, Plus, Sun, Moon } from "lucide-vue-next"
import GenericTabGroup from "~/components/GenericTabGroup.vue"
import GenericTabPanel from "~/components/GenericTabPanel.vue"
import { Popover, PopoverTrigger, PopoverContent } from "~/components/ui/popover"
import { Calendar } from "~/components/ui/calendar"
import FoodsTab from "~/components/nutrition/FoodsTab.vue"
import MealsTab from "~/components/nutrition/MealsTab.vue"

const selectedDate = ref(new Date());
const datePickerOpen = ref(false);

const displayDate = computed(() => {
  return selectedDate.value.toLocaleDateString('en-US', {
    weekday: 'long',
    month: 'long',
    day: 'numeric',
    year: 'numeric'
  });
});

function onDateSelect(date: Date) {
  selectedDate.value = date;
  datePickerOpen.value = false;
}

const activeTab = ref("today");

const summaryCards = [
    { label: "Total Calories", value: "2450", unit: "kcal", valueClass: "text-chart-4", dotClass: "bg-chart-4" },
    { label: "Carbs", value: "271", unit: "g", valueClass: "text-chart-1", dotClass: "bg-chart-1" },
    { label: "Protein", value: "165", unit: "g", valueClass: "text-chart-3", dotClass: "bg-chart-3" },
    { label: "Fat", value: "85", unit: "g", valueClass: "text-chart-2", dotClass: "bg-chart-2" },
];

    const macros = [
        {
            name: "Carbs",
            percent: 43,
            color: "var(--chart-1)",
            labelColor: "text-chart-1",
            labelClass: "-top-6 left-1/2 -translate-x-1/2",
        },
        {
            name: "Protein",
            percent: 26,
            color: "var(--chart-3)",
            labelColor: "text-chart-3",
            labelClass: "left-0 bottom-4 -translate-x-6",
        },
        {
            name: "Fat",
            percent: 30,
            color: "var(--chart-2)",
            labelColor: "text-chart-2",
            labelClass: "right-0 bottom-6 translate-x-6",
        },
    ];

const pieGradient = computed(() => {
    const [carb, protein, fat] = macros;
    const carbStop = carb.percent;
    const proteinStop = carbStop + protein.percent;
    return `conic-gradient(${carb.color} 0 ${carbStop}%, ${protein.color} ${carbStop}% ${proteinStop}%, ${fat.color} ${proteinStop}% 100%)`;
});

const meals = [
    {
        name: "Breakfast",
        icon: Sun,
        totals: { calories: 470, carbs: 66, protein: 32, fat: 12 },
        items: [
            { name: "Oatmeal with Berries", calories: 320, carbs: 54, protein: 12, fat: 8 },
            { name: "Greek Yogurt", calories: 150, carbs: 12, protein: 20, fat: 4 },
        ],
    },
    {
        name: "Lunch",
        icon: Sun,
        totals: { calories: 830, carbs: 80, protein: 56, fat: 30 },
        items: [
            { name: "Grilled Chicken Salad", calories: 450, carbs: 28, protein: 42, fat: 18 },
            { name: "Quinoa Bowl", calories: 380, carbs: 52, protein: 14, fat: 12 },
        ],
    },
    {
        name: "Dinner",
        icon: Moon,
        totals: { calories: 1150, carbs: 125, protein: 77, fat: 43 },
        items: [
            { name: "Grilled Salmon with Vegetables", calories: 620, carbs: 45, protein: 52, fat: 28 },
            { name: "Sweet Potato", calories: 180, carbs: 42, protein: 4, fat: 0 },
            { name: "Mixed Green Salad", calories: 350, carbs: 38, protein: 21, fat: 15 },
        ],
    },
];

const resources = [
            { label: "Meal Plans", value: 12, icon: FileText, iconBg: "bg-primary/10", iconColor: "text-primary" },
            { label: "Add Specific Food", value: 156, icon: Plus, iconBg: "bg-destructive/10", iconColor: "text-destructive" },
            { label: "Recipes", value: 48, icon: FileText, iconBg: "bg-accent", iconColor: "text-accent-foreground" },
            { label: "Nutrition Recommendations", value: 8, icon: Lightbulb, iconBg: "bg-secondary", iconColor: "text-secondary-foreground" },
    ];
</script>

<template>
    <div class="flex-1 flex flex-col h-full bg-background">
        <div class="flex-1 overflow-auto p-6">
            <div class="max-w-6xl mx-auto space-y-6">
                <h1 class="text-2xl font-semibold text-foreground">Nutrition Tracker</h1>
                <GenericTabGroup
                    v-model="activeTab"
                    :tabs="[
                        { value: 'today', label: 'Today' },
                        { value: 'foods', label: 'Foods' },
                        { value: 'meals', label: 'Meals' },
                    ]"
                    tab-trigger-class="text-xl sm:text-2xl"
                >
                    <template #leading>
                        <!-- Date Picker Popover -->
                        <Popover v-model:open="datePickerOpen">
                            <PopoverTrigger as-child>
                                <Button
                                    variant="outline"
                                    class="h-9 justify-start gap-2 text-sm font-normal text-muted-foreground hover:text-foreground"
                                >
                                    <CalendarDays class="h-4 w-4" />
                                    {{ displayDate }}
                                </Button>
                            </PopoverTrigger>
                            <PopoverContent align="start" class="w-auto p-0">
                                <Calendar
                                    :model-value="selectedDate"
                                    @select="onDateSelect"
                                />
                            </PopoverContent>
                        </Popover>
                    </template>

                    <GenericTabPanel value="today" class="mt-0 space-y-6">
                        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
                            <NutritionSummaryCard
                                v-for="card in summaryCards"
                                :key="card.label"
                                :label="card.label"
                                :value="card.value"
                                :unit="card.unit"
                                :value-class="card.valueClass"
                                :dot-class="card.dotClass"
                            />
                        </div>

                        <MacroDistributionCard :gradient="pieGradient" :macros="macros" />

                        <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
                            <MealCard v-for="meal in meals" :key="meal.name" :meal="meal" />
                        </div>

                        <NutritionResourcesCard :resources="resources" />
                    </GenericTabPanel>

                    <GenericTabPanel value="foods" class="mt-0">
                        <FoodsTab />
                    </GenericTabPanel>

                    <GenericTabPanel value="meals" class="mt-0">
                        <MealsTab />
                    </GenericTabPanel>
                </GenericTabGroup>
            </div>
        </div>
    </div>
</template>
