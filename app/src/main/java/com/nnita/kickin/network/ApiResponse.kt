package com.nnita.kickin.network

import com.google.gson.JsonElement

data class ApiResponse<T>(
    val get: String = "",
    val parameters: JsonElement? = null,
    val errors: JsonElement? = null,
    val results: Int = 0,
    val paging: Paging = Paging(1, 1),
    val response: T
)

data class Paging(val current: Int = 1, val total: Int = 1)
