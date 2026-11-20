package com.kev.lydia.ui.list.screen

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.kev.domain.model.Contact
import com.kev.lydia.R
import com.kev.lydia.ui.list.components.ContactList
import com.kev.lydia.ui.list.components.ContactListPlaceholder
import com.kev.lydia.ui.list.components.banner.OfflineBanner
import com.kev.lydia.ui.list.components.state.EmptyContactsScreen
import com.kev.lydia.ui.list.components.state.ErrorScreen
import com.kev.lydia.ui.list.components.top_bar.HomeTopBar
import com.kev.lydia.ui.navigation.LydiaRoutes
import com.kev.lydia.ui.toPagingItems
import java.io.IOException


@Composable
fun HomeRoute(
    navController: NavController,
    isOffline: State<Boolean>,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val contacts = viewModel.contacts.collectAsLazyPagingItems()
    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    var previousOffline by remember { mutableStateOf(isOffline.value) }

    HomeScreen(
        contacts = contacts,
        isOffline = isOffline.value,
        searchQuery = searchQuery,
        isSearching = isSearching,
        onSearchQueryChange = { searchQuery = it },
        onSearchToggle = {
            if (isSearching) searchQuery = ""
            isSearching = !isSearching
        },
        onContactClick = { clickedContact, filteredList ->
            val index = filteredList.indexOf(clickedContact)
            navController.currentBackStackEntry?.savedStateHandle?.set(
                "contacts",
                filteredList
            )
            navController.navigate(LydiaRoutes.detail(index))
        }
    )

    LaunchedEffect(isOffline.value) {
        if (previousOffline && !isOffline.value) {
            contacts.refresh()
        }
        previousOffline = isOffline.value
    }


}

@Composable
fun HomeScreen(
    contacts: LazyPagingItems<Contact>,
    searchQuery: String,
    isSearching: Boolean,
    isOffline: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onSearchToggle: () -> Unit,
    onContactClick: (Contact, List<Contact>) -> Unit
) {

    Scaffold(
        topBar = {
            Column {
                HomeTopBar(
                    searchQuery = searchQuery,
                    onSearchQueryChange = onSearchQueryChange,
                    isSearching = isSearching,
                    onSearchToggle = onSearchToggle
                )
                OfflineBanner(isOffline)
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            when {
                // Contacts are cached: keep showing them while a refresh runs or after it failed
                // (e.g. offline), and only fall back to full-screen states when there is nothing to show.
                contacts.itemCount == 0 && contacts.loadState.refresh is LoadState.Loading -> {
                    ContactListPlaceholder()
                }

                contacts.itemCount == 0 && contacts.loadState.refresh is LoadState.Error -> {
                    val error = contacts.loadState.refresh as LoadState.Error
                    val message = if (error.error is IOException) {
                        stringResource(R.string.error_screen_network_message)
                    } else {
                        error.error.localizedMessage ?: stringResource(R.string.error_screen_unknown_message)
                    }
                    ErrorScreen(message = message) {
                        contacts.refresh()
                    }
                }

                contacts.itemCount == 0 -> EmptyContactsScreen(onRetry = { contacts.refresh() })

                else -> {
                    ContactList(
                        contacts = contacts,
                        searchQuery = searchQuery,
                        onContactClick = onContactClick,
                        onClearSearch = { onSearchQueryChange("") }
                    )
                }
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
            avatarUrl = "https://example.com/avatar.jpg",
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
            avatarUrl = "https://example.com/avatar2.jpg",
            city = "Lyon",
            country = "France",
            age = 28,
            registeredDate = "2021-05-10"
        )
    )

    HomeScreen(
        contacts = fakeContacts.toPagingItems(),
        onContactClick = { _, _ -> },
        searchQuery = "",
        onSearchQueryChange = {},
        isSearching = false,
        onSearchToggle = {},
        isOffline = false
    )
}

