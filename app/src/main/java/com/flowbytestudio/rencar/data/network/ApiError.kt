package com.flowbytestudio.rencar.data.network

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import retrofit2.HttpException

@Serializable
private data class ErrorBody(val message: String? = null)

private val errorJson = Json { ignoreUnknownKeys = true }

fun HttpException.backendMessage(): String? {
    val body = response()?.errorBody()?.string() ?: return null
    return runCatching { errorJson.decodeFromString<ErrorBody>(body).message }.getOrNull()
}
