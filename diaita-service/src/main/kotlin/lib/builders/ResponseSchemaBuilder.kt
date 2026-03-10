package com.diaita.lib.builders

import com.diaita.dto.ResponseSchemaDto
import com.diaita.dto.SchemaPropertyDto

object ResponseSchemaBuilder {

    fun buildRecommendationSchema(): ResponseSchemaDto {
        return ResponseSchemaDto(
            type = "object",
            properties = mapOf(
                "nutrition" to nutritionSchema(),
                "training" to trainingSchema()
            ),
            required = listOf("nutrition", "training")
        )
    }

    private fun nutritionSchema() = SchemaPropertyDto(
        type = "object",
        properties = mapOf(
            "calories" to SchemaPropertyDto(
                type = "object",
                properties = mapOf(
                    "baseline" to SchemaPropertyDto(type = "integer"),
                    "training_day" to SchemaPropertyDto(type = "integer"),
                    "rest_day" to SchemaPropertyDto(type = "integer")
                ),
                required = listOf("baseline", "training_day", "rest_day")
            ),
            "macros" to SchemaPropertyDto(
                type = "object",
                properties = mapOf(
                    "training_day" to macroRatioSchema(),
                    "rest_day" to macroRatioSchema()
                ),
                required = listOf("training_day", "rest_day")
            ),
            "meal_structure" to SchemaPropertyDto(
                type = "object",
                properties = mapOf(
                    "meals" to SchemaPropertyDto(
                        type = "array",
                        items = SchemaPropertyDto(
                            type = "object",
                            properties = mapOf(
                                "name" to SchemaPropertyDto(type = "string"),
                                "time" to SchemaPropertyDto(type = "string"),
                                "calorie_percent" to SchemaPropertyDto(type = "integer")
                            ),
                            required = listOf("name", "time", "calorie_percent")
                        )
                    )
                ),
                required = listOf("meals")
            ),
            "foods" to SchemaPropertyDto(
                type = "object",
                properties = mapOf(
                    "proteins" to stringArraySchema(),
                    "carbs" to stringArraySchema(),
                    "fats" to stringArraySchema(),
                    "vegetables" to stringArraySchema(),
                    "fruits" to stringArraySchema()
                ),
                required = listOf("proteins", "carbs", "fats", "vegetables", "fruits")
            ),
            "checkins" to SchemaPropertyDto(
                type = "object",
                properties = mapOf(
                    "metrics" to stringArraySchema(),
                    "frequency" to SchemaPropertyDto(type = "string")
                ),
                required = listOf("metrics", "frequency")
            ),
            "adjustment_rules" to SchemaPropertyDto(
                type = "object",
                properties = mapOf(
                    "plateau_trigger" to SchemaPropertyDto(type = "string"),
                    "adjustments" to SchemaPropertyDto(
                        type = "array",
                        items = SchemaPropertyDto(
                            type = "object",
                            properties = mapOf(
                                "condition" to SchemaPropertyDto(type = "string"),
                                "action" to SchemaPropertyDto(type = "string")
                            ),
                            required = listOf("condition", "action")
                        )
                    )
                ),
                required = listOf("plateau_trigger", "adjustments")
            )
        ),
        required = listOf("calories", "macros", "meal_structure", "foods", "checkins", "adjustment_rules")
    )

    private fun trainingSchema() = SchemaPropertyDto(
        type = "object",
        properties = mapOf(
            "focus" to SchemaPropertyDto(
                type = "object",
                properties = mapOf(
                    "primary" to SchemaPropertyDto(type = "string"),
                    "secondary" to stringArraySchema()
                ),
                required = listOf("primary", "secondary")
            ),
            "split" to SchemaPropertyDto(
                type = "object",
                properties = mapOf(
                    "days_per_week" to SchemaPropertyDto(type = "integer"),
                    "groupings" to stringArraySchema()
                ),
                required = listOf("days_per_week", "groupings")
            ),
            "exercise_library" to SchemaPropertyDto(
                type = "object",
                properties = mapOf(
                    "chest" to exerciseArraySchema(),
                    "back" to exerciseArraySchema(),
                    "shoulders" to exerciseArraySchema(),
                    "legs" to exerciseArraySchema(),
                    "arms" to exerciseArraySchema(),
                    "core" to exerciseArraySchema()
                ),
                required = listOf("chest", "back", "shoulders", "legs", "arms", "core")
            ),
            "phases" to SchemaPropertyDto(
                type = "array",
                items = SchemaPropertyDto(
                    type = "object",
                    properties = mapOf(
                        "name" to SchemaPropertyDto(type = "string"),
                        "duration" to SchemaPropertyDto(type = "string"),
                        "focus" to SchemaPropertyDto(type = "string")
                    ),
                    required = listOf("name", "duration", "focus")
                )
            ),
            "day_by_day_plan" to SchemaPropertyDto(
                type = "object",
                properties = mapOf(
                    "weeks" to SchemaPropertyDto(type = "array", items = weekSchema())
                ),
                required = listOf("weeks")
            ),
            "progression_rules" to SchemaPropertyDto(
                type = "object",
                properties = mapOf(
                    "load" to SchemaPropertyDto(
                        type = "object",
                        properties = mapOf(
                            "increase" to SchemaPropertyDto(type = "string"),
                            "frequency" to SchemaPropertyDto(type = "string")
                        ),
                        required = listOf("increase", "frequency")
                    ),
                    "performance" to SchemaPropertyDto(
                        type = "object",
                        properties = mapOf(
                            "metrics" to stringArraySchema()
                        ),
                        required = listOf("metrics")
                    )
                ),
                required = listOf("load", "performance")
            )
        ),
        required = listOf("focus", "split", "exercise_library", "phases", "day_by_day_plan", "progression_rules")
    )

    private fun macroRatioSchema() = SchemaPropertyDto(
        type = "object",
        properties = mapOf(
            "protein" to SchemaPropertyDto(type = "integer"),
            "carbs" to SchemaPropertyDto(type = "integer"),
            "fat" to SchemaPropertyDto(type = "integer")
        ),
        required = listOf("protein", "carbs", "fat")
    )

    private fun exerciseArraySchema() = SchemaPropertyDto(
        type = "array",
        items = SchemaPropertyDto(
            type = "object",
            properties = mapOf(
                "name" to SchemaPropertyDto(type = "string"),
                "equipment" to SchemaPropertyDto(type = "string"),
                "type" to SchemaPropertyDto(type = "string")
            ),
            required = listOf("name", "equipment", "type")
        )
    )

    private fun weekSchema() = SchemaPropertyDto(
        type = "object",
        properties = mapOf(
            "week_number" to SchemaPropertyDto(type = "integer"),
            "days" to SchemaPropertyDto(type = "array", items = daySchema())
        ),
        required = listOf("week_number", "days")
    )

    private fun daySchema() = SchemaPropertyDto(
        type = "object",
        properties = mapOf(
            "day" to SchemaPropertyDto(type = "string"),
            "focus" to SchemaPropertyDto(type = "string"),
            "exercises" to SchemaPropertyDto(
                type = "array",
                items = SchemaPropertyDto(
                    type = "object",
                    properties = mapOf(
                        "exercise" to SchemaPropertyDto(type = "string"),
                        "sets" to SchemaPropertyDto(type = "integer"),
                        "reps" to SchemaPropertyDto(type = "string"),
                        "rest_seconds" to SchemaPropertyDto(type = "integer"),
                        "notes" to SchemaPropertyDto(type = "string", nullable = true)
                    ),
                    required = listOf("exercise", "sets", "reps", "rest_seconds")
                )
            )
        ),
        required = listOf("day", "focus", "exercises")
    )

    private fun stringArraySchema() = SchemaPropertyDto(
        type = "array",
        items = SchemaPropertyDto(type = "string")
    )
}
