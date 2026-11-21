package com.kev.domain.usecase

import androidx.paging.PagingData
import com.kev.domain.model.Contact
import kotlinx.coroutines.flow.Flow

/** Paged favourite contacts, read from the local cache only. */
fun interface GetFavoriteContactsUseCase {
    suspend operator fun invoke(): Flow<PagingData<Contact>>
}

/** Ids of the favourite contacts, updated whenever a favourite is added or removed. */
fun interface ObserveFavoriteIdsUseCase {
    operator fun invoke(): Flow<Set<String>>
}

/** Marks the contact [contactId] as favourite or not. */
fun interface SetFavoriteUseCase {
    suspend operator fun invoke(contactId: String, favorite: Boolean)
}
