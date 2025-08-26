package com.kev.data.datasource.remote

import com.kev.data.model.ContactListResponse
import com.kev.data.network.NetworkResponse

interface ContactDataSource {
    /**
     * Fetches a list of random contacts users all.
     * This method should be implemented to make the appropriate network call
     * and return the results as a [NetworkResponse] wrapped in a [ContactListResponse].
     *
     * @return A [NetworkResponse] containing a [ContactListResponse] with the list of real estate.
     */
    suspend fun fetchContacts(
        page: Int,
        results: Int = 20,
        seed: String = "lydia"
    ): NetworkResponse<ContactListResponse>

}