package com.kev.lydia.ui.list.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.kev.domain.model.Contact
import com.kev.lydia.ui.list.components.state.EmptySearchScreen
import com.kev.lydia.ui.list.components.state.LoaderScreen

@Composable
fun ContactList(
    contacts: LazyPagingItems<Contact>,
    searchQuery: String,
    onContactClick: (Contact, List<Contact>) -> Unit,
    onClearSearch: () -> Unit
) {
    val filteredItems =
        contacts.itemSnapshotList.items.filter {
            it.fullName.contains(searchQuery, ignoreCase = true)
        }

    if (searchQuery.isNotEmpty()) {
        if (filteredItems.isEmpty()) {
            EmptySearchScreen(onClearSearch = { onClearSearch() })
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(filteredItems.size) { index ->
                    val contact = filteredItems[index]
                    ContactCard(contact = contact) {
                        onContactClick(contact, filteredItems)
                    }
                }
            }
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(
                count = contacts.itemCount,
                key = contacts.itemKey { it.id }
            ) { index ->
                contacts[index]?.let { contact ->
                    ContactCard(contact = contact) {
                        onContactClick(contact, contacts.itemSnapshotList.items)
                    }
                }
            }
            item {
                if (contacts.loadState.append is LoadState.Loading) {
                    LoaderScreen()
                }
            }
        }
    }

}
