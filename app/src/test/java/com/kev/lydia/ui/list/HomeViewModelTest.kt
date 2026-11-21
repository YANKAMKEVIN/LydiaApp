package com.kev.lydia.ui.list

import androidx.paging.PagingData
import com.kev.domain.model.Contact
import com.kev.domain.usecase.GetContactsListUseCase
import com.kev.lydia.ui.list.screen.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val useCase = GetContactsListUseCase { flowOf(PagingData.empty()) }

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
        assertTrue(HomeViewModel(useCase).openedContacts.isEmpty())
    }

    @Test
    fun `opening a contact keeps the listed contacts and returns its page`() {
        val viewModel = HomeViewModel(useCase)
        val listed = listOf(contact("a"), contact("b"), contact("c"))

        val page = viewModel.openContact(listed[2], listed)

        assertEquals(2, page)
        assertEquals(listed, viewModel.openedContacts)
    }

    @Test
    fun `the opened contacts do not follow later changes of the listed contacts`() {
        val viewModel = HomeViewModel(useCase)
        val listed = mutableListOf(contact("a"), contact("b"))

        viewModel.openContact(listed[0], listed)
        listed.add(contact("c"))

        assertEquals(listOf(contact("a"), contact("b")), viewModel.openedContacts)
    }

    @Test
    fun `opening from a filtered list replaces the previous contacts`() {
        val viewModel = HomeViewModel(useCase)
        val all = listOf(contact("a"), contact("b"), contact("c"))
        viewModel.openContact(all[0], all)

        val filtered = listOf(all[1], all[2])
        val page = viewModel.openContact(all[2], filtered)

        assertEquals(1, page)
        assertEquals(filtered, viewModel.openedContacts)
    }

    @Test
    fun `a contact missing from the list opens the first page`() {
        val viewModel = HomeViewModel(useCase)
        val listed = listOf(contact("a"), contact("b"))

        assertEquals(0, viewModel.openContact(contact("z"), listed))
    }
}
