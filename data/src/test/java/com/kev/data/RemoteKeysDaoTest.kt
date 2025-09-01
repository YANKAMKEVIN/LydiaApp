package com.kev.data

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.kev.data.datasource.local.ContactDatabase
import com.kev.data.datasource.local.RemoteKeys
import com.kev.data.datasource.local.RemoteKeysDao
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RemoteKeysDaoTest {

    private lateinit var database: ContactDatabase
    private lateinit var keysDao: RemoteKeysDao

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, ContactDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        keysDao = database.remoteKeysDao
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testInsertAndGetRemoteKeys() = runTest {
        val key = RemoteKeys(repoId = "abc", prevKey = 1, nextKey = 2)
        keysDao.insertAll(listOf(key))

        val retrieved = keysDao.remoteKeysById("abc")
        assertNotNull(retrieved)
        assertEquals(1, retrieved!!.prevKey)
        assertEquals(2, retrieved.nextKey)
    }

    @Test
    fun testClearRemoteKeys() = runTest {
        val key = RemoteKeys(repoId = "xyz", prevKey = 0, nextKey = 1)
        keysDao.insertAll(listOf(key))

        var retrieved = keysDao.remoteKeysById("xyz")
        assertNotNull(retrieved)

        keysDao.clearRemoteKeys()

        retrieved = keysDao.remoteKeysById("xyz")
        assertNull(retrieved)
    }

    @Test
    fun testInsertMultipleAndRetrieve() = runTest {
        val keys = listOf(
            RemoteKeys(repoId = "id1", prevKey = null, nextKey = 2),
            RemoteKeys(repoId = "id2", prevKey = 1, nextKey = 3),
            RemoteKeys(repoId = "id3", prevKey = 2, nextKey = null)
        )
        keysDao.insertAll(keys)

        val retrieved1 = keysDao.remoteKeysById("id1")
        val retrieved2 = keysDao.remoteKeysById("id2")
        val retrieved3 = keysDao.remoteKeysById("id3")

        assertNotNull(retrieved1)
        assertEquals(2, retrieved1!!.nextKey)

        assertNotNull(retrieved2)
        assertEquals(1, retrieved2?.prevKey)
        assertEquals(3, retrieved2?.nextKey)

        assertNotNull(retrieved3)
        assertEquals(2, retrieved3?.prevKey)
        assertNull(retrieved3?.nextKey)
    }

    @Test
    fun testRetrieveNonExistentKeyReturnsNull() = runTest {
        val retrieved = keysDao.remoteKeysById("non-existent-id")
        assertNull(retrieved)
    }



}