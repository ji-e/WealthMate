package com.jie.wealthmate.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.time.Clock

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Loggable

object RepositoryLogger {
    private const val TAG = "Repository"

    fun logRequest(
        repositoryName: String,
        methodName: String,
        params: Map<String, Any?>
    ) {
        val paramsStr = params.entries.joinToString(", ") { "${it.key}=${it.value}" }
        println("[$TAG] 🔵 REQUEST -> $repositoryName.$methodName($paramsStr)")
    }

    fun logResponse(
        repositoryName: String,
        methodName: String,
        result: Any?,
        executionTime: Long
    ) {
        val resultStr = when (result) {
            is List<*> -> "List(size=${result.size})"
            is Flow<*> -> "Flow"
            null -> "null"
            else -> result.toString().take(100)
        }
        println("[$TAG] 🟢 RESPONSE <- $repositoryName.$methodName: $resultStr (${executionTime}ms)")
    }

    fun logError(
        repositoryName: String,
        methodName: String,
        error: Throwable
    ) {
        println("[$TAG] 🔴 ERROR <- $repositoryName.$methodName: ${error.message}")
    }
}

suspend inline fun <T> loggedCall(
    repositoryName: String,
    methodName: String,
    params: Map<String, Any?>,
    block: suspend () -> T
): T {
    RepositoryLogger.logRequest(repositoryName, methodName, params)
    val startTime = Clock.System.now().toEpochMilliseconds()

    return try {
        val result = block()
        val executionTime = Clock.System.now().toEpochMilliseconds() - startTime
        RepositoryLogger.logResponse(repositoryName, methodName, result, executionTime)
        result
    } catch (e: Exception) {
        RepositoryLogger.logError(repositoryName, methodName, e)
        throw e
    }
}

inline fun <T> loggedFlow(
    repositoryName: String,
    methodName: String,
    params: Map<String, Any?>,
    crossinline block: () -> Flow<T>
): Flow<T> {
    RepositoryLogger.logRequest(repositoryName, methodName, params)

    return flow {
        try {
            block().collect { value ->
                RepositoryLogger.logResponse(
                    repositoryName,
                    methodName,
                    value,
                    0
                )
                emit(value)
            }
        } catch (e: Exception) {
            RepositoryLogger.logError(repositoryName, methodName, e)
            throw e
        }
    }
}
