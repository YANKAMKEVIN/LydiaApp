package com.kev.data.datasource.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.kev.data.datasource.local.ContactDatabase
import com.kev.data.datasource.local.ContactEntity
import com.kev.data.datasource.local.RemoteKeys
import com.kev.data.mapper.ContactMapper.toContactEntity
import com.kev.data.network.NetworkError
import com.kev.data.network.NetworkResponse
import java.io.IOException
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@OptIn(ExperimentalPagingApi::class)
class ContactRemoteMediator @Inject constructor(
    private val contactDataSource: ContactDataSource,
    private val contactDatabase: ContactDatabase
) : RemoteMediator<Int, ContactEntity>() {

    private val contactDao = contactDatabase.contactDao
    private val keysDao = contactDatabase.remoteKeysDao

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ContactEntity>
    ): MediatorResult {

        val page = when (loadType) {
            LoadType.REFRESH -> 1
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                val lastItem = state.lastItemOrNull() ?: return MediatorResult.Success(
                    endOfPaginationReached = true
                )
                val remoteKeys = keysDao.remoteKeysById(lastItem.id)
                remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = true)
            }
        }

        return try {
            val networkResult = contactDataSource.fetchContacts(page = page)

            when (networkResult) {
                is NetworkResponse.Success -> {
                    val contacts = networkResult.data.results
                    val infoPage = networkResult.data.info.page
                    contactDatabase.withTransaction {
                        if (loadType == LoadType.REFRESH && contacts.isNotEmpty()) {
                            contactDao.clearAll()
                            keysDao.clearRemoteKeys()
                        }

                        val keys = contacts.map { dto ->
                            RemoteKeys(
                                repoId = dto.login.uuid,
                                prevKey = if (infoPage > 1) infoPage - 1 else null,
                                nextKey = if (contacts.isEmpty()) null else infoPage + 1
                            )
                        }
                        keysDao.insertAll(keys)
                        val contactEntities = contacts.map { it.toContactEntity() }
                        contactDatabase.contactDao.upsertAll(contactEntities)
                    }
                    MediatorResult.Success(endOfPaginationReached = contacts.isEmpty())
                }

                // Failures are reported as errors (not as a successful empty load) so Paging exposes a
                // LoadState.Error the UI can show with a retry action. Cached contacts stay visible.
                is NetworkResponse.Failure -> {
                    when (val error = networkResult.error) {
                        is NetworkError.UnResolveAddress ->
                            MediatorResult.Error(IOException(error.message))

                        else ->
                            MediatorResult.Error(Exception("Network error: ${error.message}"))
                    }
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}