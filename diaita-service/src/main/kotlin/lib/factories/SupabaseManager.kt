package com.diaita.lib.factories

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.rpc
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject


data class Result<T>(val body: T?, val error: Exception?)
data class PaginatedResult<T>(
    val data: List<T>,
    val total: Int,
    val page: Int,
    val pageSize: Int,
    val hasMore: Boolean
)

class SupabaseManager(val client: SupabaseClient) {

    suspend inline fun <reified T : Any> insert(table: String, data: T): Result<T> {
        return try {
            val result = client.postgrest[table].insert(data) {
                select()
            }.decodeSingle<T>()
            Result(result, null)
        } catch (e: Exception) {
            println("Insert error: ${e.message}")
            Result(null, e)
        }
    }

    suspend inline fun <reified T : Any> insertMany(table: String, data: List<T>): Result<List<T>> {
        return try {
            val result = client.postgrest[table].insert(data) {
                select()
            }.decodeList<T>()
            Result(result, null)
        } catch (e: Exception) {
            println("Insert many error: ${e.message}")
            Result(null, e)
        }
    }

    suspend inline fun <reified T : Any> select(
        table: String,
        columns: String = "*"
    ): Result<List<T>> {
        return try {
            val result = client.postgrest[table]
                .select(columns = Columns.raw(columns))
                .decodeList<T>()
            Result(result, null)
        } catch (e: Exception) {
            println("Select error: ${e.message}")
            Result(null, e)
        }
    }

    suspend inline fun <reified T : Any> selectWhere(
        table: String,
        column: String,
        value: Any,
        columns: String = "*"
    ): Result<List<T>> {
        return try {
            val result = client.postgrest[table]
                .select(columns = Columns.raw(columns)) {
                    filter {
                        eq(column, value)
                    }
                    select()
                }
                .decodeList<T>()
            Result(result, null)
        } catch (e: Exception) {
            println("Select where error: ${e.message}")
            Result(null, e)
        }
    }

    suspend inline fun <reified T : Any> selectWithFilters(
        table: String,
        filters: Map<String, Pair<String, Any>>,
        page: Int = 0,
        pageSize: Int = 20,
        columns: String = "*"
    ): Result<PaginatedResult<T>> {
        return try {
            val rows = client.postgrest[table]
                .select(columns = Columns.raw(columns)) {
                    filter {
                        filters.forEach { (column, filterConfig) ->
                            val (operator, value) = filterConfig
                            when (operator) {
                                "eq" -> eq(column, value)
                                "ilike" -> ilike(column, "%$value%")
                            }
                        }
                    }
                }
                .decodeList<T>()

            val normalizedPage = page.coerceAtLeast(0)
            val normalizedPageSize = pageSize.coerceAtLeast(1)
            val offset = normalizedPage * normalizedPageSize
            val data = if (offset >= rows.size) {
                emptyList()
            } else {
                rows.drop(offset).take(normalizedPageSize)
            }

            val total = rows.size
            val hasMore = offset + data.size < total

            Result(
                PaginatedResult(
                    data = data,
                    total = total,
                    page = normalizedPage,
                    pageSize = normalizedPageSize,
                    hasMore = hasMore
                ),
                null
            )
        } catch (e: Exception) {
            println("Select with filters error: ${e.message}")
            Result(null, e)
        }
    }

    /** Selects every row whose [column] matches one of [values]; an empty [values] short-circuits. */
    suspend inline fun <reified T : Any> selectWhereIn(
        table: String,
        column: String,
        values: List<Any>,
        columns: String = "*"
    ): Result<List<T>> {
        if (values.isEmpty()) {
            return Result(emptyList(), null)
        }

        return try {
            val result = client.postgrest[table]
                .select(columns = Columns.raw(columns)) {
                    filter {
                        isIn(column, values)
                    }
                }
                .decodeList<T>()
            Result(result, null)
        } catch (e: Exception) {
            println("Select where in error: ${e.message}")
            Result(null, e)
        }
    }

    /** Selects rows matching every entry of [filters] (all combined with equality). */
    suspend inline fun <reified T : Any> selectWhereAll(
        table: String,
        filters: Map<String, Any>,
        columns: String = "*"
    ): Result<List<T>> {
        return try {
            val result = client.postgrest[table]
                .select(columns = Columns.raw(columns)) {
                    filter {
                        filters.forEach { (column, value) -> eq(column, value) }
                    }
                }
                .decodeList<T>()
            Result(result, null)
        } catch (e: Exception) {
            println("Select where all error: ${e.message}")
            Result(null, e)
        }
    }

    suspend inline fun <reified T : Any> selectSingle(
        table: String,
        column: String,
        value: Any,
        columns: String = "*"
    ): Result<T> {
        return try {
            val result = client.postgrest[table]
                .select(columns = Columns.raw(columns)) {
                    filter {
                        eq(column, value)
                    }
                }
                .decodeSingle<T>()
            Result(result, null)
        } catch (e: Exception) {
            println("Select single error: ${e.message}")
            Result(null, e)
        }
    }

    suspend inline fun <reified T : Any> update(
        table: String,
        data: T,
        column: String,
        value: Any
    ): Result<T> {
        return try {
            val result = client.postgrest[table].update(data) {
                filter {
                    eq(column, value)
                }
                select()
            }.decodeSingle<T>()
            Result(result, null)
        } catch (e: Exception) {
            println("Update error: ${e.message}")
            Result(null, e)
        }
    }



    /** Updates rows matching every entry of [filters] and returns the updated rows. */
    suspend inline fun <reified TData : Any, reified TResult : Any> updateWhereAll(
        table: String,
        data: TData,
        filters: Map<String, Any>
    ): Result<List<TResult>> {
        return try {
            val result = client.postgrest[table].update(data) {
                filter {
                    filters.forEach { (column, value) -> eq(column, value) }
                }
                select()
            }.decodeList<TResult>()
            Result(result, null)
        } catch (e: Exception) {
            println("Update where all error: ${e.message}")
            Result(null, e)
        }
    }

    suspend inline fun <reified T : Any> upsert(
        table: String,
        data: T,
        onConflict: String? = null,
        ignoreDuplicates: Boolean = false
    ): Result<T> {
        return try {
            val result = client.postgrest[table].upsert(data) {
                if (!onConflict.isNullOrBlank()) {
                    this.onConflict = onConflict
                }
                this.ignoreDuplicates = ignoreDuplicates
                select()
            }.decodeSingle<T>()
            Result(result, null)
        } catch (e: Exception) {
            println("Upsert error: ${e.message}")
            Result(null, e)
        }
    }

    /** Upserts a batch of rows in one round trip and returns the stored rows. */
    suspend inline fun <reified TData : Any, reified TResult : Any> upsertMany(
        table: String,
        data: List<TData>,
        onConflict: String? = null
    ): Result<List<TResult>> {
        if (data.isEmpty()) {
            return Result(emptyList(), null)
        }

        return try {
            val result = client.postgrest[table].upsert(data) {
                if (!onConflict.isNullOrBlank()) {
                    this.onConflict = onConflict
                }
                select()
            }.decodeList<TResult>()
            Result(result, null)
        } catch (e: Exception) {
            println("Upsert many error: ${e.message}")
            Result(null, e)
        }
    }

    /** Deletes rows matching every entry of [filters]. */
    suspend fun deleteWhereAll(
        table: String,
        filters: Map<String, Any>
    ): Result<Unit> {
        return try {
            client.postgrest[table].delete {
                filter {
                    filters.forEach { (column, value) -> eq(column, value) }
                }
            }
            Result(Unit, null)
        } catch (e: Exception) {
            println("Delete where all error: ${e.message}")
            Result(null, e)
        }
    }

    /** Deletes rows whose [column] matches one of [values]; an empty [values] short-circuits. */
    suspend fun deleteWhereIn(
        table: String,
        column: String,
        values: List<Any>,
        scopeFilters: Map<String, Any> = emptyMap()
    ): Result<Unit> {
        if (values.isEmpty()) {
            return Result(Unit, null)
        }

        return try {
            client.postgrest[table].delete {
                filter {
                    scopeFilters.forEach { (scopeColumn, scopeValue) -> eq(scopeColumn, scopeValue) }
                    isIn(column, values)
                }
            }
            Result(Unit, null)
        } catch (e: Exception) {
            println("Delete where in error: ${e.message}")
            Result(null, e)
        }
    }

    suspend fun delete(
        table: String,
        column: String,
        value: Any
    ): Result<Unit> {
        return try {
            client.postgrest[table].delete {
                filter {
                    eq(column, value)
                }
                select()
            }
            Result(Unit, null)
        } catch (e: Exception) {
            println("Delete error: ${e.message}")
            Result(null, e)
        }
    }

    suspend fun rpc(
        functionName: String,
        parameters: JsonObject = JsonObject(emptyMap())
    ): Result<JsonElement> {
        return try {
            val result = client.postgrest.rpc(functionName, parameters)
                .decodeAs<JsonElement>()
            Result(result, null)
        } catch (e: Exception) {
            println("RPC error: ${e.message}")
            Result(null, e)
        }
    }

    suspend inline fun <reified T : Any> rpcDecoded(
        functionName: String,
        parameters: JsonObject = buildJsonObject {},
        schemaStr: String = PostgresFactory::PUBLIC_SCHEMA.toString()
    ): Result<T> {
        return try {
            val result = client.postgrest.rpc(functionName, parameters) {
                schema = schemaStr
            }
                .decodeAs<T>() as T
            Result(result, null)
        } catch (e: Exception) {
            println("RPC decoded error: ${e.message}")
            Result(null, e)
        }
    }
}
