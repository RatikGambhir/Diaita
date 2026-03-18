package com.diaita.dto

enum class UserSettingsPage(val queryValue: String) {
    BASIC_DEMOGRAPHICS("basic-demographics"),
    ACTIVITY_LIFESTYLE("activity-lifestyle"),
    GOALS_PRIORITIES("goals-priorities"),
    TRAINING_BACKGROUND("training-background"),
    NUTRITION_HISTORY("nutrition-history");

    companion object {
        fun fromQuery(value: String?): UserSettingsPage? {
            if (value.isNullOrBlank()) return null
            return entries.firstOrNull { it.queryValue == value.lowercase() }
        }
    }
}

enum class UserSettingsAction(val queryValue: String) {
    GET("get"),
    UPDATE("update"),
    DELETE("delete");

    companion object {
        fun fromQuery(value: String?): UserSettingsAction? {
            if (value.isNullOrBlank()) return null
            return entries.firstOrNull { it.queryValue == value.lowercase() }
        }
    }
}
