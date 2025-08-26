package com.kev.data

import com.kev.data.network.NetworkError
import com.kev.data.network.NetworkResponse
import com.kev.data.network.NetworkUtil
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class NetworkUtilTest {

    // -----------------------------
    // 1) executeApiCall success
    // -----------------------------
    @Test
    fun `executeApiCall returns Success when apiCall succeeds`() = runBlocking {
        val result = NetworkUtil.executeApiCall {
            "Hello"
        }

        assertTrue(result is NetworkResponse.Success)
        assertEquals("Hello", (result as NetworkResponse.Success).data)
    }

    // -----------------------------
    // 2) HttpException mapping
    // -----------------------------
    @Test
    fun `executeApiCall returns Failure with HttpException mapping`() = runBlocking {
        val exception =
            HttpException(Response.error<String>(404, okhttp3.ResponseBody.create(null, "")))
        val result = NetworkUtil.executeApiCall<String> {
            throw exception
        }

        assertTrue(result is NetworkResponse.Failure)
        val error = (result as NetworkResponse.Failure).error
        assertEquals(NetworkError.NotFound::class, error::class)
    }

    // -----------------------------
    // 3) IOException mapping
    // -----------------------------
    @Test
    fun `executeApiCall returns Failure with UnResolveAddress on IOException`() = runBlocking {
        val exception = IOException("No network")
        val result = NetworkUtil.executeApiCall<String> {
            throw exception
        }

        assertTrue(result is NetworkResponse.Failure)
        val error = (result as NetworkResponse.Failure).error
        assertEquals(NetworkError.UnResolveAddress::class, error::class)
        assertEquals("No network", error.message)
    }

    // -----------------------------
    // 4) Generic exception mapping
    // -----------------------------
    @Test
    fun `executeApiCall returns Failure with Unknown on generic Exception`() = runBlocking {
        val exception = RuntimeException("Something went wrong")
        val result = NetworkUtil.executeApiCall<String> {
            throw exception
        }

        assertTrue(result is NetworkResponse.Failure)
        val error = (result as NetworkResponse.Failure).error
        assertEquals(NetworkError.Unknown::class, error::class)
        assertEquals("Something went wrong", error.message)
    }
}
