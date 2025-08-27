package com.kev.lydia.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.kev.domain.model.Contact

// ------------------------- HomeScreen -------------------------

@Composable
fun HomeRoute(
    viewModel: HomeViewModel = hiltViewModel(),
    onContactClick: (Contact) -> Unit
) {
    val contacts = viewModel.contacts.collectAsLazyPagingItems()

    HomeScreen(
        contacts = contacts,
        onContactClick = onContactClick
    )
}

@Composable
fun HomeScreen(
    onContactClick: (Contact) -> Unit,
    contacts: LazyPagingItems<Contact>
) {

    Scaffold(
        topBar = { HomeTopBar() }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when {
                contacts.loadState.refresh is LoadState.Loading -> {
                    LoaderScreen()
                }

                contacts.loadState.refresh is LoadState.Error -> {
                    val error = contacts.loadState.refresh as LoadState.Error
                    ErrorScreen(message = error.error.localizedMessage ?: "Unknown Error") {
                        contacts.refresh()
                    }
                }

                contacts.itemCount == 0 -> EmptyScreen()
                else -> ContactList(contacts = contacts, onContactClick = onContactClick)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    val fakeContacts = listOf(
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
        ),
        Contact(
            id = "2",
            fullName = "Jane Doe",
            username = "janedoe",
            gender = "female",
            email = "jane@example.com",
            phone = "987654321",
            avatarUrl = "http://example.com/avatar2.jpg",
            city = "Lyon",
            country = "France",
            age = 28,
            registeredDate = "2021-05-10"
        )
    )

    HomeScreen(
        contacts = fakeContacts.toPagingItems(),
        onContactClick = {}
    )
}

