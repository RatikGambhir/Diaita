<script setup lang="ts">
import { ref } from "vue"
import TabGroup from '~/components/TabGroup.vue'
import TabPanel from '~/components/TabPanel.vue'
import Table from '~/components/ui/table/Table.vue'
import TableBody from '~/components/ui/table/TableBody.vue'
import TableCell from '~/components/ui/table/TableCell.vue'
import TableHead from '~/components/ui/table/TableHead.vue'
import TableHeader from '~/components/ui/table/TableHeader.vue'
import TableRow from '~/components/ui/table/TableRow.vue'

const selectedTab = ref("nutrition")

const tabs = [
    { value: "nutrition", label: "Nutrition" },
    { value: "workout", label: "Workout" },
]

const nutritionData = ref([
    {
        mealType: "Breakfast",
        food: ["Oatmeal", "Berries", "Protein Powder"],
        calories: 400,
        protein: 30,
        carbs: 45,
        fats: 10,
    },
    {
        mealType: "Lunch",
        food: ["Chicken Salad", "Mixed Greens", "Olive Oil Dressing"],
        calories: 500,
        protein: 40,
        carbs: 20,
        fats: 25,
    },
    {
        mealType: "Dinner",
        food: ["Salmon", "Asparagus", "Quinoa"],
        calories: 600,
        protein: 50,
        carbs: 30,
        fats: 30,
    },
    {
        mealType: "Snack",
        food: ["Greek Yogurt", "Almonds"],
        calories: 200,
        protein: 20,
        carbs: 10,
        fats: 10,
    },
])

const workoutData = ref([
    {
        workoutType: "Strength Training",
        duration: "60 min",
        caloriesBurned: 350,
        workouts: ["Bench Press", "Squats", "Deadlifts"],
    },
    {
        workoutType: "Cardio",
        duration: "45 min",
        caloriesBurned: 400,
        workouts: ["Running", "Elliptical"],
    },
    {
        workoutType: "Yoga",
        duration: "30 min",
        caloriesBurned: 150,
        workouts: ["Vinyasa Flow", "Warrior Pose"],
    },
])
</script>

<template>
    <div>
        <TabGroup v-model="selectedTab" :tabs="tabs" tabs-list-class="mb-4">
            <TabPanel value="nutrition">
                <div class="rounded-lg border">
                    <Table>
                        <TableHeader>
                            <TableRow class="bg-muted/50">
                                <TableHead>Meal Type</TableHead>
                                <TableHead>Food</TableHead>
                                <TableHead>Calories</TableHead>
                                <TableHead>Protein (g)</TableHead>
                                <TableHead>Carbs (g)</TableHead>
                                <TableHead>Fats (g)</TableHead>
                            </TableRow>
                        </TableHeader>
                        <TableBody>
                            <TableRow v-for="item in nutritionData" :key="item.mealType">
                                <TableCell>{{ item.mealType }}</TableCell>
                                <TableCell>{{ item.food.join(", ") }}</TableCell>
                                <TableCell>{{ item.calories }}</TableCell>
                                <TableCell>{{ item.protein }}</TableCell>
                                <TableCell>{{ item.carbs }}</TableCell>
                                <TableCell>{{ item.fats }}</TableCell>
                            </TableRow>
                        </TableBody>
                    </Table>
                </div>
            </TabPanel>

            <TabPanel value="workout">
                <div class="rounded-lg border">
                    <Table>
                        <TableHeader>
                            <TableRow class="bg-muted/50">
                                <TableHead>Workout Type</TableHead>
                                <TableHead>Duration</TableHead>
                                <TableHead>Calories Burned</TableHead>
                                <TableHead>Workouts</TableHead>
                            </TableRow>
                        </TableHeader>
                        <TableBody>
                            <TableRow v-for="item in workoutData" :key="item.workoutType">
                                <TableCell>{{ item.workoutType }}</TableCell>
                                <TableCell>{{ item.duration }}</TableCell>
                                <TableCell>{{ item.caloriesBurned }}</TableCell>
                                <TableCell>{{ item.workouts.join(", ") }}</TableCell>
                            </TableRow>
                        </TableBody>
                    </Table>
                </div>
            </TabPanel>
        </TabGroup>
    </div>
</template>
