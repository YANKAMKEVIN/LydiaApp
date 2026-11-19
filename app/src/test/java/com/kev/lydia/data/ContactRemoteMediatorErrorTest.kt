package com.kev.lydia.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.RemoteMediator.MediatorResult
import com.kev.data.datasource.local.ContactDatabase
import com.kev.data.datasource.local.ContactEntity
import com.kev.data.datasource.local.RemoteKeys
import com.kev.data.datasource.remote.ContactDataSource
import com.kev.data.datasource.remote.ContactRemoteMediator
import com.kev.data.network.NetworkError
import com.kev.data.network.NetworkResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

/**
 * Error paths of [ContactRemoteMediator]: a failed page must surface as [MediatorResult.Error]
 * so the list can show its "Retry" footer. (The Room-backed mediator tests live in :data and
 * need Robolectric; these paths never touch the database, so they run as plain JVM tests.)
 */
@OptIn(ExperimentalPagingApi::class)
class ContactRemoteMediatorErrorTest {

    private val dataSource = mockk<ContactDataSource>()
    private val database = mockk<ContactDatabase>(relaxed = true)
    private val mediator = ContactRemoteMediator(dataSource, database)

    private val contact = ContactEntity(
        id = "42", fullName = "Jane Doe", gender = "female", email = "jane@example.com",
        phone = "0600000000", avatarUrl = "", city = "Lyon", country = "France", age = 30,
        registeredDate = "2020-01-01", username = "jane"
    )

    private val stateWithOnePage = PagingState<Int, ContactEntity>(
        pages = listOf(PagingSource.LoadResult.Page<Int, ContactEntity>(listOf(contact), prevKey = null, nextKey = null)),
        anchorPosition = 0,
        config = PagingConfig(pageSize = 20),
        leadingPlaceholderCount = 0
    )

    private fun givenNextPageIs2() {
        coEvery { database.remoteKeysDao.remoteKeysById("42") } returns
            RemoteKeys(repoId = "42", prevKey = null, nextKey = 2)
    }

    @Test
    fun `append returns Error when the request throws`() = runTest {
        givenNextPageIs2()
        coEvery { dataSource.fetchContacts(page = 2, results = any(), seed = any()) } throws
            IllegalStateException("boom")

        val result = mediator.load(LoadType.APPEND, stateWithOnePage)

        assertTrue("was $result", result is MediatorResult.Error)
    }

    @Test
    fun `append returns Error when offline`() = runTest {
        givenNextPageIs2()
        coEvery { dataSource.fetchContacts(page = 2, results = any(), seed = any()) } returns
            NetworkResponse.Failure(NetworkError.UnResolveAddress("Unable to resolve host"))

        val result = mediator.load(LoadType.APPEND, stateWithOnePage)

        assertTrue("was $result", result is MediatorResult.Error)
        assertTrue((result as MediatorResult.Error).throwable is IOException)
    }

    @Test
    fun `refresh returns Error when the server fails`() = runTest {
        coEvery { dataSource.fetchContacts(page = 1, results = any(), seed = any()) } returns
            NetworkResponse.Failure(NetworkError.InternalServerError("Internal Server Error"))

        val result = mediator.load(LoadType.REFRESH, stateWithOnePage)

        assertTrue("was $result", result is MediatorResult.Error)
    }
}
