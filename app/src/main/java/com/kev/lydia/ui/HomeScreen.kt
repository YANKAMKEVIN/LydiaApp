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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.kev.domain.model.Contact
import com.kev.lydia.ui.details.ContactDetailPager


@Composable
fun HomeRoute(viewModel: HomeViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            val contacts = viewModel.contacts.collectAsLazyPagingItems()
            var searchQuery by remember { mutableStateOf("") }
            var isSearching by remember { mutableStateOf(false) }

            HomeScreen(
                contacts = contacts,
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
                    navController.navigate("detail/$index")
                }
            )
        }

        composable(
            "detail/{startIndex}",
            arguments = listOf(navArgument("startIndex") { type = NavType.IntType })
        ) { backStackEntry ->
            val startIndex = backStackEntry.arguments?.getInt("startIndex") ?: 0
            val contacts: List<Contact> =
                navController.previousBackStackEntry?.savedStateHandle?.get("contacts")
                    ?: emptyList()
            ContactDetailPager(
                contacts = contacts,
                startIndex = startIndex,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun HomeScreen(
    contacts: LazyPagingItems<Contact>,
    searchQuery: String,
    isSearching: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onSearchToggle: () -> Unit,
    onContactClick: (Contact, List<Contact>) -> Unit
) {
    Scaffold(
        topBar = {
            HomeTopBar(
                searchQuery = searchQuery,
                onSearchQueryChange = onSearchQueryChange,
                onFilterClick = { },
                isSearching = isSearching,
                onSearchToggle = onSearchToggle,
                isFilterActive = true

            )
        },
        bottomBar = {}
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

                    val filteredList = contacts.itemSnapshotList.items
                        .filterNotNull()
                        .filter {
                            if (searchQuery.isBlank()) true
                            else it.fullName.contains(searchQuery, ignoreCase = true)
                        }
                    LazyColumn {
                        items(filteredList.size) { contact ->
                            ContactCard(contact = filteredList[contact]) {
                                onContactClick(filteredList[contact], filteredList)
                            }
                        }
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

    /* HomeScreen(
         contacts = fakeContacts.toPagingItems(),
         onContactClick = {},
         userAvatarUrl = "https://example.com/avatar.jpg",
         searchQuery = "",
         onSearchQueryChange = {},
         onFilterClick = {},
         isSearching = false,
         onSearchToggle = {}
     )*/
}

