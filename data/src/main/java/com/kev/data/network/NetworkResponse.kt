package com.kev.data.network

/**
 * Wrapper for data provided from the server or Mock
 */
sealed class NetworkResponse<out T : Any> {
    /**
     * response with a 2xx status code
     */
    data class Success<out T : Any>(val data: T) : NetworkResponse<T>()

    /**
     * response with a non-2xx status code.
     */
    data class Failure(val error: NetworkError) : NetworkResponse<Nothing>()
}