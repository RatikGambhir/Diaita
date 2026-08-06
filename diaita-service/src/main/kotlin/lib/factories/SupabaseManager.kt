package com.diaita.lib.factories

import com.diaita.lib.logging.loggerFor
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

/**
 * Thin wrapper over Supabase PostgREST that turns thrown exceptions into a
 * [Result], so callers can branch on `body`/`error` instead of using try/catch.
 */
class SupabaseManager(val client: SupabaseClient) {

    suspend inline fun <reified T : Any> insert(table: String, data: T): Result<T> =
        query("insert into '$table'") {
            client.postgrest[table].insert(data) { select() }.decodeSingle<T>()
        }

    suspend inline fun <reified T : Any> insertMany(table: String, data: List<T>): Result<List<T>> =
        query("insert many into '$table'") {
            client.postgrest[table].insert(data) { select() }.decodeList<T>()
        }

    suspend inline fun <reified T : Any> select(
        table: String,
        columns: String = "*"
    ): Result<List<T>> = query("select from '$table'") {
        client.postgrest[table]
            .select(columns = Columns.raw(columns))
            .decodeList<T>()
    }

    suspend inline fun <reified T : Any> selectWhere(
        table: String,
        column: String,
        value: Any,
        columns: String = "*"
    ): Result<List<T>> = query("select from '$table' where $column") {
        client.postgrest[table]
            .select(columns = Columns.raw(columns)) {
                filter { eq(column, value) }
                select()
            }
            .decodeList<T>()
    }

    /**
     * Applies [filters] as `column to (operator to value)`, where operator is
     * `eq` or `ilike`, then pages the result set in memory.
     */
    suspend inline fun <reified T : Any> selectWithFilters(
        table: String,
        filters: Map<String, Pair<String, Any>>,
        page: Int = 0,
        pageSize: Int = 20,
        columns: String = "*"
    ): Result<PaginatedResult<T>> = query("filtered select from '$table'") {
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

        rows.paginate(page, pageSize)
    }

    suspend inline fun <reified T : Any> selectSingle(
        table: String,
        column: String,
        value: Any,
        columns: String = "*"
    ): Result<T> = query("select single from '$table' where $column") {
        client.postgrest[table]
            .select(columns = Columns.raw(columns)) {
                filter { eq(column, value) }
            }
            .decodeSingle<T>()
    }

    suspend inline fun <reified T : Any> update(
        table: String,
        data: T,
        column: String,
        value: Any
    ): Result<T> = query("update '$table' where $column") {
        client.postgrest[table].update(data) {
            filter { eq(column, value) }
            select()
        }.decodeSingle<T>()
    }

    suspend inline fun <reified T : Any> upsert(
        table: String,
        data: T,
        onConflict: String? = null,
        ignoreDuplicates: Boolean = false
    ): Result<T> = query("upsert into '$table'") {
        client.postgrest[table].upsert(data) {
            if (!onConflict.isNullOrBlank()) {
                this.onConflict = onConflict
            }
            this.ignoreDuplicates = ignoreDuplicates
            select()
        }.decodeSingle<T>()
    }

    suspend fun delete(
        table: String,
        column: String,
        value: Any
    ): Result<Unit> = query("delete from '$table' where $column") {
        client.postgrest[table].delete {
            filter { eq(column, value) }
            select()
        }
        Unit
    }

    suspend fun rpc(
        functionName: String,
        parameters: JsonObject = JsonObject(emptyMap())
    ): Result<JsonElement> = query("rpc '$functionName'") {
        client.postgrest.rpc(functionName, parameters).decodeAs<JsonElement>()
    }

    suspend inline fun <reified T : Any> rpcDecoded(
        functionName: String,
        parameters: JsonObject = buildJsonObject {},
        schemaStr: String = PostgresFactory.PUBLIC_SCHEMA
    ): Result<T> = query("rpc '$functionName' on schema '$schemaStr'") {
        client.postgrest.rpc(functionName, parameters) { schema = schemaStr }
            .decodeAs<T>()
    }

    /**
     * Runs [block] and wraps the outcome in a [Result], logging any failure with
     * the [description] of the query that produced it.
     */
    @PublishedApi
    internal inline fun <T> query(description: String, block: () -> T): Result<T> =
        try {
            Result(block(), null)
        } catch (e: Exception) {
            log.error("Supabase {} failed: {}", description, e.message, e)
            Result(null, e)
        }

    @PublishedApi
    internal fun <T> List<T>.paginate(page: Int, pageSize: Int): PaginatedResult<T> {
        val normalizedPage = page.coerceAtLeast(0)
        val normalizedPageSize = pageSize.coerceAtLeast(1)
        val offset = normalizedPage * normalizedPageSize
        val pageRows = if (offset >= size) emptyList() else drop(offset).take(normalizedPageSize)

        return PaginatedResult(
            data = pageRows,
            total = size,
            page = normalizedPage,
            pageSize = normalizedPageSize,
            hasMore = offset + pageRows.size < size
        )
    }

    companion object {
        @PublishedApi
        internal val log = loggerFor(SupabaseManager::class.java)
    }
}
