package com.kev.lydia.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.kev.domain.model.Contact

@Composable
fun HomeRoute(
    viewModel: HomeViewModel = hiltViewModel(),
    onContactClick: (Contact) -> Unit
) {
    val contacts = viewModel.contacts.collectAsLazyPagingItems()
    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }

    HomeScreen(
        contacts = contacts,
        onContactClick = onContactClick,
        userAvatarUrl = "https://example.com/avatar.jpg",
        searchQuery = searchQuery,
        isSearching = isSearching,
        onSearchQueryChange = { searchQuery = it },
        onFilterClick = { /* open filter bottom sheet */ },
        onSearchToggle = {
            if (isSearching) searchQuery = ""
            isSearching = !isSearching
        }
    )
}

@Composable
fun HomeScreen(
    contacts: LazyPagingItems<Contact>,
    onContactClick: (Contact) -> Unit,
    userAvatarUrl: String,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit,
    isSearching: Boolean,
    onSearchToggle: () -> Unit
) {
    Scaffold(
        topBar = {
            HomeTopBar(
                userAvatarUrl = userAvatarUrl,
                searchQuery = searchQuery,
                onSearchQueryChange = onSearchQueryChange,
                onFilterClick = onFilterClick,
                isSearching = isSearching,
                onSearchToggle = onSearchToggle,
                isFilterActive = true

            )
        },
        bottomBar = {
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            when {
                contacts.loadState.refresh is LoadState.Loading -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(5) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFFE0E0E0))
                            )
                        }
                    }
                }

                contacts.loadState.refresh is LoadState.Error -> {
                    val error = contacts.loadState.refresh as LoadState.Error
                    ErrorScreen(message = error.error.localizedMessage ?: "Unknown Error") {
                        contacts.refresh()
                    }
                }

                contacts.itemCount == 0 -> EmptyScreen()

                else -> {
                    if (searchQuery.isBlank()) {
                        ContactList(
                            contacts = contacts,
                            onContactClick = onContactClick
                        )
                    } else {
                        val filtered = contacts.itemSnapshotList.items
                            .filterNotNull()
                            .filter { it.fullName.contains(searchQuery, ignoreCase = true) }

                        ContactListStatic(
                            contacts = filtered,
                            onContactClick = onContactClick
                        )
                    }
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
        onContactClick = {},
        userAvatarUrl = "https://example.com/avatar.jpg",
        searchQuery = "",
        onSearchQueryChange = {},
        onFilterClick = {},
        isSearching = false,
        onSearchToggle = {}
    )
}

