package com.kev.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.kev.data.datasource.local.ContactDatabase
import com.kev.data.datasource.local.FavoriteEntity
import com.kev.data.datasource.remote.ContactDataSource
import com.kev.data.datasource.remote.ContactRemoteMediator
import com.kev.data.mapper.ContactMapper.toDomain
import com.kev.domain.model.Contact
import com.kev.domain.repository.ContactRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of the [ContactRepository] interface.
 * This class is responsible for processing the data retrieved from the [ContactDataSource]
 * and transforming it into the domain model before passing it to the use cases.
 */
@OptIn(ExperimentalPagingApi::class)
class ContactRepositoryImpl @Inject constructor(
    private val contactRemoteMediator: ContactRemoteMediator,
    private val contactDatabase: ContactDatabase
) : ContactRepository {

    /**
     * Fetches a list of contact from the data source and maps the result into the domain model.
     * The result is emitted as a [Flow] containing a list of [Contact].
     *
     * @return A [Flow] emitting a list of [Contact] containing the list of contacts.
     */
    override suspend fun fetchContacts(): Flow<PagingData<Contact>> =
        Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                prefetchDistance = 10
            ),
            remoteMediator = contactRemoteMediator,
            pagingSourceFactory = { contactDatabase.contactDao.pagingSource() },
        ).flow.map { it.map { entity -> entity.toDomain() } }

    /** Favourites come from the cache only: no RemoteMediator, so no network request. */
    override suspend fun fetchFavoriteContacts(): Flow<PagingData<Contact>> =
        Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { contactDatabase.favoriteDao.favoriteContactsPagingSource() },
        ).flow.map { it.map { entity -> entity.toDomain() } }

    override fun observeFavoriteIds(): Flow<Set<String>> =
        contactDatabase.favoriteDao.observeIds().map { it.toSet() }

    override suspend fun setFavorite(contactId: String, favorite: Boolean) {
        if (favorite) {
            contactDatabase.favoriteDao.add(FavoriteEntity(contactId))
        } else {
            contactDatabase.favoriteDao.remove(contactId)
        }
    }
}
