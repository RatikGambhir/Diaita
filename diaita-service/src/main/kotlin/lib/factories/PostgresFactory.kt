package com.diaita.lib.factories

object PostgresFactory {

    // ── Schemas ──
    const val NUTRITION_SCHEMA = "nutrition"
    const val PUBLIC_SCHEMA = "public"

    // ── Tables ──
    const val MEALS_TABLE = "meals"
    const val MEAL_ITEMS_TABLE = "meal_items"
    const val USER_PROFILE_TABLE = "user_profile"
    const val BASIC_DEMOGRAPHICS_TABLE = "basic_demographics"
    const val ACTIVITY_LIFESTYLE_TABLE = "activity_lifestyle"
    const val GOALS_PRIORITIES_TABLE = "goals_priorities"
    const val TRAINING_BACKGROUND_TABLE = "training_background"
    const val NUTRITION_HISTORY_TABLE = "nutrition_history"
    const val EXERCISES_TABLE = "exercises"
    const val WORKOUTS_TABLE = "workouts"
    const val WORKOUT_EXERCISES_TABLE = "workout_exercises"

    // ── Columns ──
    const val USER_ID_COLUMN = "user_id"
    const val ID_COLUMN = "id"
    const val WORKOUT_ID_COLUMN = "workout_id"

    // ── RPC Functions ──
    const val UPSERT_FULL_PROFILE_RPC = "upsert_full_profile"
    const val GET_FULL_PROFILE_RPC = "get_full_profile"
    const val UPSERT_MEALS_RPC = "nutrition_upsert_meals"
}
