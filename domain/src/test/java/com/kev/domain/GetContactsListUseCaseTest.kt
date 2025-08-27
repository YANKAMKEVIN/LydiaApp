package com.kev.domain

import androidx.paging.PagingData
import androidx.paging.testing.asSnapshot
import com.kev.domain.model.Contact
import com.kev.domain.repository.ContactRepository
import com.kev.domain.usecase.GetContactsListUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetContactListUseCaseTest {
    private val repository = mockk<ContactRepository>()
    private lateinit var useCase: GetContactsListUseCase

    @Before
    fun setup() {
        useCase = GetContactsListUseCase(repository::fetchContacts)
    }

    @Test
    fun `invoke should return contacts from repository`() = runTest {
        // Given
        val dummyContacts = listOf(
            Contact(
                id = "1",
                fullName = "John Doe",
                username = "johndoe",
                gender = "male",
                email = "john@example.com",
                phone = "123456789",
                avatarUrl = "http://example.com/avatar.jpg",
                city = "Paris",
                country = "France",
                age = 30,
                registeredDate = "2020-01-01"
            )
        )
        val dummyFlow = flowOf(PagingData.from(dummyContacts))
        coEvery { repository.fetchContacts() } returns dummyFlow

        // When
        val resultFlow = useCase()

        // Then
        val snapshot = resultFlow.asSnapshot()
        assertEquals(dummyContacts, snapshot)
    }

    @Test
    fun `invoke should return empty list when repository has no contacts`() = runTest {
        // Given
        val dummyFlow = flowOf(PagingData.from(emptyList<Contact>()))
        coEvery { repository.fetchContacts() } returns dummyFlow

        // When
        val resultFlow = useCase()

        // Then
        val snapshot = resultFlow.asSnapshot()
        assertEquals(emptyList<Contact>(), snapshot)
    }

}
