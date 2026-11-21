package com.kev.lydia.data

import com.kev.data.datasource.local.ContactDatabase
import com.kev.data.datasource.local.FavoriteEntity
import com.kev.data.repository.ContactRepositoryImpl
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ContactRepositoryFavoritesTest {

    private val database = mockk<ContactDatabase>(relaxed = true)
    private val repository = ContactRepositoryImpl(mockk(relaxed = true), database)

    @Test
    fun `starring a contact adds it to the favourites`() = runTest {
        repository.setFavorite("42", favorite = true)

        coVerify { database.favoriteDao.add(FavoriteEntity("42")) }
    }

    @Test
    fun `unstarring a contact removes it from the favourites`() = runTest {
        repository.setFavorite("42", favorite = false)

        coVerify { database.favoriteDao.remove("42") }
    }

    @Test
    fun `favourite ids are exposed as a set`() = runTest {
        every { database.favoriteDao.observeIds() } returns flowOf(listOf("a", "b"))

        assertEquals(setOf("a", "b"), repository.observeFavoriteIds().first())
    }
}
