package com.nutrify.lib.factories

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.rpc
import kotlinx.serialization.json.JsonElement


data class Result<T>(val body: T?, val error: Exception?)

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



    suspend inline fun <reified T : Any> upsert(table: String, data: T): Result<T> {
        return try {
            val result = client.postgrest[table].upsert(data) {
                select()
            }.decodeSingle<T>()
            Result(result, null)
        } catch (e: Exception) {
            println("Upsert error: ${e.message}")
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
        parameters: Map<String, Any> = emptyMap()
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
        parameters: Map<String, Any> = emptyMap()
    ): Result<T> {
        return try {
            val result = client.postgrest.rpc(functionName, parameters)
                .decodeAs<T>()
            Result(result, null)
        } catch (e: Exception) {
            println("RPC decoded error: ${e.message}")
            Result(null, e)
        }
    }
}
