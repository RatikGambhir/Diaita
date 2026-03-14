package com.diaita.lib.factories

class SqlFactory {
    companion object {
        private val sqlFiles: Map<String, String?> by lazy {
            mapOf(
                "nutritionMealUpsert" to loadSql("/sql/nutrition_meal_upsert.sql"),
                "recommendations" to loadSql("/sql/recommendations.sql"),
                "upsertFullProfile" to loadSql("/sql/upsert_full_profile.sql"),
            )
        }

        fun getSql(name: String): String? {
            return sqlFiles[name]?.takeIf { it.isNotBlank() }
        }

        private fun loadSql(path: String): String? {
            return try {
                object {}.javaClass.getResource(path)?.readText()
            } catch (e: Exception) {
                null
            }
        }
    }

    private val sqlFiles: Map<String, String?> by lazy {
        mapOf(
            "nutritionMealUpsert" to loadSql("/sql/nutrition_meal_upsert.sql"),
            "recommendations" to loadSql("/sql/recommendations.sql"),
            "upsertFullProfile" to loadSql("/sql/upsert_full_profile.sql"),
        )
    }

    private fun loadSql(path: String): String? {
        return try {
            object {}.javaClass.getResource(path)?.readText()
        } catch (e: Exception) {
            null
        }
    }

    fun get(name: String): String? =
        sqlFiles[name]?.takeIf { it.isNotBlank() }
            ?: throw IllegalArgumentException("SQL '$name' is blank or missing")
}
