package com.nnita.kickin.network

data class ApiResponse<T>(
    val get: String,
    val parameters: Map<String, String>,
    val errors: List<Any>,
    val results: Int,
    val paging: Paging,
    val response: T
)

data class Paging(val current: Int, val total: Int)
