package com.kev.domain.repository

import androidx.paging.PagingData
import com.kev.domain.model.Contact
import kotlinx.coroutines.flow.Flow

/**
 * Interface defining the repository methods for fetching real estate data.
 * This interface abstracts the data layer and is used by use cases to interact
 * with the data source(s) without knowing the details of the data retrieval.
 */
interface ContactRepository {

    /**
     * Fetches a list of all real estate.
     *
     * @return A [Flow] emitting a list of [Contact] containing the list of real estate.
     */
    suspend fun fetchContacts(): Flow<PagingData<Contact>>

}