package com.kev.data

import androidx.paging.PagingSource
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.kev.data.datasource.local.ContactDao
import com.kev.data.datasource.local.ContactDatabase
import com.kev.data.datasource.local.ContactEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ContactDaoTest {

    private lateinit var database: ContactDatabase
    private lateinit var contactDao: ContactDao

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, ContactDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        contactDao = database.contactDao
    }

    @After
    fun teardown() {
        database.close()
    }

    private suspend fun ContactDao.loadPagingData(): List<ContactEntity> {
        val result = pagingSource().load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 10, placeholdersEnabled = false)
        )
        return (result as PagingSource.LoadResult.Page).data
    }

    @Test
    fun testUpsertAndRetrieveById() = runTest {
        val entity = ContactEntity(
            id = "123",
            fullName = "Alice Dupont",
            email = "alice@example.com",
            phone = "0123456789",
            avatarUrl = "https://example.com/a.jpg",
            city = "Paris",
            country = "France",
            age = 25,
            registeredDate = "2022-01-01",
            username = "alice",
            gender = "female"
        )
        contactDao.upsertAll(listOf(entity))

        val data = contactDao.loadPagingData()

        assertEquals(1, data.size)
        val retrieved = data[0]
        assertEquals("123", retrieved.id)
        assertEquals("Alice Dupont", retrieved.fullName)
        assertEquals("alice@example.com", retrieved.email)
    }

    @Test
    fun testClearAll() = runTest {
        val entities = listOf(
            ContactEntity(
                id = "1",
                fullName = "User1",
                email = "u1@example.com",
                phone = "000",
                avatarUrl = "url",
                city = "City",
                country = "Country",
                age = 25,
                registeredDate = "2022-01-01",
                username = "user1",
                gender = "male"
            ),

            ContactEntity(
                id = "2",
                fullName = "User2",
                email = "u2@example.com",
                phone = "000",
                avatarUrl = "url",
                city = "City",
                country = "Country",
                age = 25,
                registeredDate = "2022-01-01",
                username = "user2",
                gender = "female"
            ),
        )
        contactDao.upsertAll(entities)

        var data = contactDao.loadPagingData()

        assertEquals(2, data.size)

        contactDao.clearAll()

        data = contactDao.loadPagingData()

        assertEquals(0, data.size)
    }

    @Test
    fun testUpsertUpdatesExistingContact() = runTest {
        val entity = ContactEntity(
            id = "123",
            fullName = "Alice Dupont",
            email = "alice@example.com",
            phone = "0123456789",
            avatarUrl = "url1",
            city = "Paris",
            country = "France",
            age = 25,
            registeredDate = "2022-01-01",
            username = "alice",
            gender = "female"
        )
        contactDao.upsertAll(listOf(entity))

        val updatedEntity = entity.copy(fullName = "Alice D.", avatarUrl = "url2")
        contactDao.upsertAll(listOf(updatedEntity))

        val data = contactDao.loadPagingData()
        assertEquals(1, data.size)
        assertEquals("Alice D.", data[0].fullName)
        assertEquals("url2", data[0].avatarUrl)
    }

    @Test
    fun testPagingSourceEmpty() = runTest {
        val data = contactDao.loadPagingData()
        assertEquals(0, data.size)
    }


}