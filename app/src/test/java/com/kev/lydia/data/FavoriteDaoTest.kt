package com.kev.lydia.data

import android.content.Context
import androidx.paging.PagingSource
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kev.data.datasource.local.ContactDatabase
import com.kev.data.datasource.local.ContactEntity
import com.kev.data.datasource.local.FavoriteEntity
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Room favourites, on a real SQLite in memory (see [JdbcSQLiteOpenHelperFactory]). */
class FavoriteDaoTest {

    private val database: ContactDatabase =
        Room.inMemoryDatabaseBuilder(mockk<Context>(relaxed = true), ContactDatabase::class.java)
            .openHelperFactory(JdbcSQLiteOpenHelperFactory())
            .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
            .allowMainThreadQueries()
            .build()
    private val favoriteDao = database.favoriteDao
    private val contactDao = database.contactDao

    @After
    fun tearDown() {
        database.close()
    }

    private fun contact(id: String) = ContactEntity(
        id = id, fullName = "Contact $id", gender = "female", email = "$id@example.com",
        phone = "0600000000", avatarUrl = "", city = "Lyon", country = "France", age = 30,
        registeredDate = "2020-01-01", username = "user$id"
    )

    private suspend fun loadFavoriteContacts(): List<ContactEntity> {
        val result = favoriteDao.favoriteContactsPagingSource().load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 20, placeholdersEnabled = false)
        )
        return (result as PagingSource.LoadResult.Page).data
    }

    @Test
    fun `there is no favourite at first`() = runTest {
        assertTrue(favoriteDao.observeIds().first().isEmpty())
        assertTrue(loadFavoriteContacts().isEmpty())
    }

    @Test
    fun `added favourites are observed and removed ones disappear`() = runTest {
        favoriteDao.add(FavoriteEntity("a"))
        favoriteDao.add(FavoriteEntity("b"))
        assertEquals(setOf("a", "b"), favoriteDao.observeIds().first().toSet())

        favoriteDao.remove("a")
        assertEquals(listOf("b"), favoriteDao.observeIds().first())
    }

    @Test
    fun `adding a favourite twice keeps a single row`() = runTest {
        favoriteDao.add(FavoriteEntity("a"))
        favoriteDao.add(FavoriteEntity("a"))

        assertEquals(listOf("a"), favoriteDao.observeIds().first())
    }

    @Test
    fun `removing a contact that is not a favourite does nothing`() = runTest {
        favoriteDao.add(FavoriteEntity("a"))
        favoriteDao.remove("z")

        assertEquals(listOf("a"), favoriteDao.observeIds().first())
    }

    @Test
    fun `the favourite contacts are the cached favourites in list order`() = runTest {
        contactDao.upsertAll(listOf(contact("1"), contact("2"), contact("3"), contact("4")))
        favoriteDao.add(FavoriteEntity("3"))
        favoriteDao.add(FavoriteEntity("1"))
        favoriteDao.add(FavoriteEntity("not-cached"))

        assertEquals(listOf("1", "3"), loadFavoriteContacts().map { it.id })
    }

    @Test
    fun `favourites survive the refresh that clears and reloads the contacts`() = runTest {
        contactDao.upsertAll(listOf(contact("1"), contact("2")))
        favoriteDao.add(FavoriteEntity("2"))

        // What ContactRemoteMediator does on REFRESH: the API is seeded, the ids come back.
        contactDao.clearAll()
        assertTrue(loadFavoriteContacts().isEmpty())
        contactDao.upsertAll(listOf(contact("1"), contact("2")))

        assertEquals(listOf("2"), favoriteDao.observeIds().first())
        assertEquals(listOf("2"), loadFavoriteContacts().map { it.id })
    }
}
