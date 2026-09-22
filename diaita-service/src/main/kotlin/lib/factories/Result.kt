package com.diaita.lib.factories

data class Result<T>(val body: T?, val error: Exception?)

data class PaginatedResult<T>(
    val data: List<T>,
    val total: Int,
    val page: Int,
    val pageSize: Int,
    val hasMore: Boolean
)
