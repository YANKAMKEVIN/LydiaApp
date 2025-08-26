package com.kev.data.api

import com.kev.data.model.ContactListResponse
import retrofit2.http.GET
import retrofit2.http.Query


interface ContactApi {
    @GET("api/1.3/")
    suspend fun getContacts(
        @Query(SEED) seed: String = SEED_VALUE,
        @Query(RESULTS) results: Int = RESULTS_VALUE,
        @Query(PAGE) page: Int
    ): ContactListResponse


    companion object {
        private const val SEED = "seed"
        private const val RESULTS = "results"
        private const val PAGE = "page"
        private const val SEED_VALUE = "lydia"
        private const val RESULTS_VALUE = 20
    }
}