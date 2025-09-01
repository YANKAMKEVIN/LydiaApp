package com.kev.data

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.Room
import androidx.room.withTransaction
import androidx.test.core.app.ApplicationProvider
import com.kev.data.datasource.local.ContactDatabase
import com.kev.data.datasource.local.ContactEntity
import com.kev.data.datasource.local.RemoteKeys
import com.kev.data.datasource.remote.ContactDataSource
import com.kev.data.datasource.remote.ContactRemoteMediator
import com.kev.data.mapper.ContactMapper.toContactEntity
import com.kev.data.model.ContactDto
import com.kev.data.model.ContactListResponse
import com.kev.data.model.CoordinatesDto
import com.kev.data.model.DobDto
import com.kev.data.model.IdDto
import com.kev.data.model.InfoDto
import com.kev.data.model.LocationDto
import com.kev.data.model.LoginDto
import com.kev.data.model.NameDto
import com.kev.data.model.PictureDto
import com.kev.data.model.RegisteredDto
import com.kev.data.model.StreetDto
import com.kev.data.model.TimezoneDto
import com.kev.data.network.NetworkError
import com.kev.data.network.NetworkResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.IOException

@OptIn(ExperimentalPagingApi::class, ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class ContactRemoteMediatorTest {

    private lateinit var database: ContactDatabase
    private lateinit var mediator: ContactRemoteMediator
    private lateinit var mockRemote: ContactDataSource
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(ctx, ContactDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        mockRemote = mockk()

        mediator = ContactRemoteMediator(
            contactDataSource = mockRemote,
            contactDatabase = database
        )

        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        database.close()
        Dispatchers.resetMain()
    }

    private fun makeContactDto(id: String, seed: Int): ContactDto {
        return ContactDto(
            login = LoginDto(
                uuid = id,
                username = "user$seed",
                password = "password$seed",
                salt = "salt$seed",
                md5 = "md5$seed",
                sha1 = "sha1$seed",
                sha256 = "sha256$seed"
            ),
            gender = "male",
            name = NameDto(title = "Mr", first = "First$seed", last = "Last$seed"),
            location = LocationDto(
                street = StreetDto(number = 1, name = "Street"),
                city = "City$seed",
                state = "State",
                country = "Country",
                postcode = "00000",
                coordinates = CoordinatesDto("0.0", "0.0"),
                timezone = TimezoneDto("+1:00", "Region")
            ),
            email = "email$seed@example.com",
            phone = "000$seed",
            cell = "111$seed",
            picture = PictureDto(
                large = "https://example.com/$seed.jpg",
                medium = "https://example.com/med/$seed.jpg",
                thumbnail = "https://example.com/thumb/$seed.jpg"
            ),
            dob = DobDto(date = "1990-01-01T00:00:00.000Z", age = 30),
            registered = RegisteredDto(date = "2020-01-01T00:00:00.000Z", age = 2),
            nat = "US",
            id = IdDto(name = "SSN", value = "123456789"),
        )
    }

    private fun makeContactListResponse(page: Int, count: Int): ContactListResponse {
        val dtos = (1..count).map { idx ->
            makeContactDto(id = "id_${page}_$idx", seed = (page * 100 + idx))
        }
        val info = InfoDto(seed = "seed", results = count, page = page, version = "1.3")
        return ContactListResponse(results = dtos, info = info)
    }

    // -----------------------------------------------------------
    // 1) TEST : LoadType.REFRESH
    // -----------------------------------------------------------
    @Test
    fun `load refresh success inserts contacts and remoteKeys`() = runTest {
        val responsePage1 = makeContactListResponse(page = 1, count = 2)
        coEvery {
            mockRemote.fetchContacts(
                page = 1,
                results = any(),
                seed = any()
            )
        } returns NetworkResponse.Success(responsePage1)

        val pagingState = PagingState<Int, ContactEntity>(
            pages = emptyList(),
            anchorPosition = null,
            config = PagingConfig(pageSize = 2),
            leadingPlaceholderCount = 0
        )

        val result = mediator.load(loadType = LoadType.REFRESH, state = pagingState)

        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(result is RemoteMediator.MediatorResult.Success)
        val successResult = result as RemoteMediator.MediatorResult.Success
        assertFalse(successResult.endOfPaginationReached)

        val keys =
            database.remoteKeysDao.remoteKeysById("id_1_1")
        assertNotNull(keys)
        assertNull(keys!!.prevKey)
        assertEquals(2, keys.nextKey)

        val keys2 = database.remoteKeysDao.remoteKeysById("id_1_2")
        assertNotNull(keys2)
        assertNull(keys2!!.prevKey)
        assertEquals(2, keys2.nextKey)

        val allContacts = database.contactDao.pagingSource().load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 10, placeholdersEnabled = false)
        ) as PagingSource.LoadResult.Page
        assertEquals(2, allContacts.data.size)

        val mappedFirst = responsePage1.results[0].toContactEntity()
        assertEquals(mappedFirst.id, allContacts.data[0].id)
        assertEquals(mappedFirst.fullName, allContacts.data[0].fullName)
    }

    // -----------------------------------------------------------
    // 2) TEST : LoadType.APPEND
    // -----------------------------------------------------------
    @Test
    fun `load append success appends new data`() = runTest {
        val page1 = makeContactListResponse(page = 1, count = 2)

        database.withTransaction {
            // Crée RemoteKeys pour page 1
            val keys1 = page1.results.map { dto ->
                RemoteKeys(
                    repoId = dto.login.uuid,
                    prevKey = null,
                    nextKey = 2
                )
            }
            database.remoteKeysDao.insertAll(keys1)
            val entities1 = page1.results.map { it.toContactEntity() }
            database.contactDao.upsertAll(entities1)
        }

        val page2 = makeContactListResponse(page = 2, count = 3)
        coEvery {
            mockRemote.fetchContacts(
                page = 2,
                results = any(),
                seed = any()
            )
        } returns NetworkResponse.Success(page2)

        val loadedPage1 = database.contactDao.pagingSource().load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 10, placeholdersEnabled = false)
        ) as PagingSource.LoadResult.Page
        val lastEntityPage1 = loadedPage1.data.last()

        val pagingState = PagingState<Int, ContactEntity>(
            pages = listOf(loadedPage1),
            anchorPosition = loadedPage1.data.size - 1,
            config = PagingConfig(pageSize = 10),
            leadingPlaceholderCount = 0
        )

        val result = mediator.load(loadType = LoadType.APPEND, state = pagingState)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)

        page2.results.forEach { dto ->
            val remoteKey = database.remoteKeysDao.remoteKeysById(dto.login.uuid)
            assertNotNull(remoteKey)
            assertEquals(1, remoteKey!!.prevKey)
            assertEquals(3, remoteKey.nextKey)
        }

        val loadedAll = database.contactDao.pagingSource().load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 10, placeholdersEnabled = false)
        ) as PagingSource.LoadResult.Page
        assertEquals(5, loadedAll.data.size)
    }

    // -----------------------------------------------------------
    // 3) TEST : LoadType.APPEND fin de pagination (page vide)
    // -----------------------------------------------------------
    @Test
    fun `load append returns endOfPagination when no data`() = runTest {
        val page1 = makeContactListResponse(page = 1, count = 2)
        database.withTransaction {
            val keys1 = page1.results.map { dto ->
                RemoteKeys(repoId = dto.login.uuid, prevKey = null, nextKey = 2)
            }
            database.remoteKeysDao.insertAll(keys1)
            val entities1 = page1.results.map { it.toContactEntity() }
            database.contactDao.upsertAll(entities1)
        }

        val emptyResponse = ContactListResponse(
            results = emptyList(),
            info = InfoDto(page = 2, results = 0, seed = "lydia", version = "1.3")
        )
        coEvery {
            mockRemote.fetchContacts(
                page = 2,
                results = any(),
                seed = any()
            )
        } returns NetworkResponse.Success(emptyResponse)

        val loadedPage1 = database.contactDao.pagingSource().load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 10, placeholdersEnabled = false)
        ) as PagingSource.LoadResult.Page
        val pagingState = PagingState<Int, ContactEntity>(
            pages = listOf(loadedPage1),
            anchorPosition = loadedPage1.data.lastIndex,
            config = PagingConfig(pageSize = 10),
            leadingPlaceholderCount = 0
        )

        val result = mediator.load(loadType = LoadType.APPEND, state = pagingState)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)

        run {
            page1.results.forEach { dto ->
                val rk = database.remoteKeysDao.remoteKeysById(dto.login.uuid)
                assertNotNull(rk)
                assertEquals(
                    2,
                    rk!!.nextKey
                )
            }
        }
    }

    // -----------------------------------------------------------
    // 4) TEST : Network error while refresh
    // -----------------------------------------------------------
    @Test
    fun `load refresh error returns MediatorResultError`() = runTest {
        coEvery {
            mockRemote.fetchContacts(
                page = 1,
                results = any(),
                seed = any()
            )
        } returns NetworkResponse.Failure(
            NetworkError.Unknown("Network down")
        )

        val pagingState = PagingState<Int, ContactEntity>(
            pages = emptyList(),
            anchorPosition = null,
            config = PagingConfig(pageSize = 10),
            leadingPlaceholderCount = 0
        )

        val result = mediator.load(loadType = LoadType.REFRESH, state = pagingState)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(result is RemoteMediator.MediatorResult.Error)
    }

    // -----------------------------------------------------------
    // 5) TEST : Exception IOException when APPEND send Error
    // -----------------------------------------------------------
    @Test
    fun `load append exception returns MediatorResultError`() = runTest {
        val page1 = makeContactListResponse(page = 1, count = 1)
        database.withTransaction {
            val keys1 = page1.results.map { dto ->
                RemoteKeys(repoId = dto.login.uuid, prevKey = null, nextKey = 2)
            }
            database.remoteKeysDao.insertAll(keys1)
            val entities1 = page1.results.map { it.toContactEntity() }
            database.contactDao.upsertAll(entities1)
        }

        coEvery {
            mockRemote.fetchContacts(
                page = 2,
                results = any(),
                seed = any()
            )
        } throws IOException("Network IO")

        val loadedPage1 = database.contactDao.pagingSource().load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 10, placeholdersEnabled = false)
        ) as PagingSource.LoadResult.Page
        val pagingState = PagingState(
            pages = listOf(loadedPage1),
            anchorPosition = loadedPage1.data.lastIndex,
            config = PagingConfig(pageSize = 10),
            leadingPlaceholderCount = 0
        )

        val result = mediator.load(loadType = LoadType.APPEND, state = pagingState)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(result is RemoteMediator.MediatorResult.Error)
    }
}