package com.kev.lydia.ui.list

import androidx.paging.PagingData
import com.kev.domain.model.Contact
import com.kev.domain.usecase.GetContactsListUseCase
import com.kev.domain.usecase.GetFavoriteContactsUseCase
import com.kev.domain.usecase.ObserveFavoriteIdsUseCase
import com.kev.domain.usecase.SetFavoriteUseCase
import com.kev.lydia.ui.list.screen.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val useCase = GetContactsListUseCase { flowOf(PagingData.empty()) }
    private val favoriteContactsUseCase = GetFavoriteContactsUseCase { flowOf(PagingData.empty()) }

    /** Stands for the favourites table: [SetFavoriteUseCase] writes it, the ViewModel observes it. */
    private val storedFavorites = MutableStateFlow(emptySet<String>())
    private val setFavoriteCalls = mutableListOf<Pair<String, Boolean>>()
    private val observeFavoriteIds = ObserveFavoriteIdsUseCase { storedFavorites }
    private val setFavorite = SetFavoriteUseCase { id, favorite ->
        setFavoriteCalls += id to favorite
        storedFavorites.value = if (favorite) storedFavorites.value + id else storedFavorites.value - id
    }

    private fun viewModel() = HomeViewModel(useCase, favoriteContactsUseCase, observeFavoriteIds, setFavorite)

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun contact(id: String) = Contact(
        id = id,
        fullName = "Contact $id",
        username = "user$id",
        gender = "female",
        email = "$id@example.com",
        phone = "0102030405",
        avatarUrl = "https://example.com/$id.jpg",
        city = "Paris",
        country = "France",
        age = 30,
        registeredDate = "2020-01-01T00:00:00.000Z"
    )

    @Test
    fun `no contact is opened at first`() {
        assertTrue(viewModel().openedContacts.isEmpty())
    }

    @Test
    fun `opening a contact keeps the listed contacts and returns its page`() {
        val viewModel = viewModel()
        val listed = listOf(contact("a"), contact("b"), contact("c"))

        val page = viewModel.openContact(listed[2], listed)

        assertEquals(2, page)
        assertEquals(listed, viewModel.openedContacts)
    }

    @Test
    fun `the opened contacts do not follow later changes of the listed contacts`() {
        val viewModel = viewModel()
        val listed = mutableListOf(contact("a"), contact("b"))

        viewModel.openContact(listed[0], listed)
        listed.add(contact("c"))

        assertEquals(listOf(contact("a"), contact("b")), viewModel.openedContacts)
    }

    @Test
    fun `opening from a filtered list replaces the previous contacts`() {
        val viewModel = viewModel()
        val all = listOf(contact("a"), contact("b"), contact("c"))
        viewModel.openContact(all[0], all)

        val filtered = listOf(all[1], all[2])
        val page = viewModel.openContact(all[2], filtered)

        assertEquals(1, page)
        assertEquals(filtered, viewModel.openedContacts)
    }

    @Test
    fun `a contact missing from the list opens the first page`() {
        val viewModel = viewModel()
        val listed = listOf(contact("a"), contact("b"))

        assertEquals(0, viewModel.openContact(contact("z"), listed))
    }

    @Test
    fun `the list shows all the contacts at first`() {
        assertFalse(viewModel().showFavoritesOnly.value)
    }

    @Test
    fun `the favorites filter toggles on and off`() {
        val viewModel = viewModel()

        viewModel.toggleFavoritesOnly()
        assertTrue(viewModel.showFavoritesOnly.value)

        viewModel.toggleFavoritesOnly()
        assertFalse(viewModel.showFavoritesOnly.value)
    }

    @Test
    fun `favourite ids follow the stored favourites`() {
        val viewModel = viewModel()
        assertTrue(viewModel.favoriteIds.value.isEmpty())

        storedFavorites.value = setOf("a", "b")

        assertEquals(setOf("a", "b"), viewModel.favoriteIds.value)
    }

    @Test
    fun `starring a contact that is not a favourite adds it`() {
        val viewModel = viewModel()

        viewModel.toggleFavorite(contact("a"))

        assertEquals(listOf("a" to true), setFavoriteCalls)
        assertEquals(setOf("a"), viewModel.favoriteIds.value)
    }

    @Test
    fun `starring a favourite again removes it`() {
        storedFavorites.value = setOf("a", "b")
        val viewModel = viewModel()

        viewModel.toggleFavorite(contact("a"))

        assertEquals(listOf("a" to false), setFavoriteCalls)
        assertEquals(setOf("b"), viewModel.favoriteIds.value)
    }
}
