package com.diaita.routers

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText

/**
 * Request-handling helpers shared by the routers.
 *
 * Each helper responds to the call itself when validation fails and returns
 * `null`, so route handlers can bail out with `?: return@get`.
 */

/** Responds `200 OK` with [result], or [failureStatus] with [failureMessage] when it is null. */
suspend fun ApplicationCall.respondOrFail(
    result: Any?,
    failureMessage: String,
    failureStatus: HttpStatusCode = HttpStatusCode.InternalServerError
) {
    if (result != null) {
        respond(HttpStatusCode.OK, result)
    } else {
        respondText(failureMessage, status = failureStatus)
    }
}

/** Deserializes the body into [T], responding `400` if it cannot be parsed. */
suspend inline fun <reified T : Any> ApplicationCall.receiveOrBadRequest(
    invalidMessage: String = "Invalid request payload"
): T? = try {
    receive<T>()
} catch (_: Exception) {
    respondText(invalidMessage, status = HttpStatusCode.BadRequest)
    null
}

/** Reads a required path parameter, responding `400` when it is absent or blank. */
suspend fun ApplicationCall.requiredPathParam(
    name: String,
    missingMessage: String = "Missing $name parameter"
): String? {
    val value = parameters[name]
    if (value.isNullOrBlank()) {
        respondText(missingMessage, status = HttpStatusCode.BadRequest)
        return null
    }
    return value
}

/** Reads a required query parameter, responding `400` when it is absent or blank. */
suspend fun ApplicationCall.requiredQueryParam(name: String): String? {
    val value = request.queryParameters[name]?.trim()
    if (value.isNullOrEmpty()) {
        respondText("Invalid request: '$name' query parameter is required", status = HttpStatusCode.BadRequest)
        return null
    }
    return value
}

/** Reads an optional int query parameter, responding `400` when it is unparseable or out of range. */
suspend fun ApplicationCall.intQueryParam(
    name: String,
    defaultValue: Int,
    validRange: IntRange
): Int? {
    val rawValue = request.queryParameters[name] ?: return defaultValue
    val parsedValue = rawValue.toIntOrNull()

    if (parsedValue == null || parsedValue !in validRange) {
        respondText(
            "Invalid request: '$name' must be between ${validRange.first} and ${validRange.last}",
            status = HttpStatusCode.BadRequest
        )
        return null
    }

    return parsedValue
}

/** Reads an optional boolean query parameter, responding `400` on any other value. */
suspend fun ApplicationCall.booleanQueryParam(
    name: String,
    defaultValue: Boolean
): Boolean? {
    val rawValue = request.queryParameters[name] ?: return defaultValue

    return when (rawValue.lowercase()) {
        "true" -> true
        "false" -> false
        else -> {
            respondText(
                "Invalid request: '$name' must be 'true' or 'false'",
                status = HttpStatusCode.BadRequest
            )
            null
        }
    }
}

/** Reads a comma-separated query parameter into a list, or null when absent/empty. */
fun ApplicationCall.csvQueryParam(name: String): List<String>? =
    request.queryParameters[name]
        ?.split(",")
        ?.map(String::trim)
        ?.filter(String::isNotEmpty)
        ?.takeIf { it.isNotEmpty() }
