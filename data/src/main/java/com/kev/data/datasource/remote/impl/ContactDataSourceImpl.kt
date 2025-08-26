package com.kev.data.datasource.remote.impl

import com.kev.data.api.ContactApi
import com.kev.data.datasource.remote.ContactDataSource
import com.kev.data.model.ContactListResponse
import com.kev.data.network.NetworkResponse
import com.kev.data.network.NetworkUtil
import javax.inject.Inject

/**
 * Implementation of the [ContactDataSource] interface.
 * This class is responsible for fetching user data from the [com.kev.data.api.ContactApi]
 * and wrapping the responses in a [NetworkResponse].
 *
 * @param contactApi The API service used to fetch data.
 */
class ContactDataSourceImpl @Inject constructor(
    private val contactApi: ContactApi
) : ContactDataSource {

    /**
     * Fetches a list of random contacts users all.
     * This method wraps the API response in a [NetworkResponse] to handle success or failure.
     *
     * @return A [NetworkResponse] wrapping a [com.kev.data.model.ContactListResponse] containing the list of contacts.
     */
    override suspend fun fetchContacts(
        page: Int,
        results: Int,
        seed: String
    ): NetworkResponse<ContactListResponse> =
        NetworkUtil.executeApiCall {
            contactApi.getContacts(
                page = page,
                results = results,
                seed = seed
            )
        }

}