package com.kev.lydia.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.kev.domain.model.Contact

@Composable
fun ContactList(
    contacts: LazyPagingItems<Contact>,
    searchQuery: String,
    onContactClick: (Contact, List<Contact>) -> Unit,
    onClearSearch: () -> Unit
) {
    val filteredItems = contacts.itemSnapshotList.items.filter {
        it.fullName.contains(searchQuery, ignoreCase = true)
    }

    if (searchQuery.isNotEmpty() && filteredItems.isEmpty()) {
        EmptySearchScreen(onClearSearch = { onClearSearch() })
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(
                count = contacts.itemCount,
                key = contacts.itemKey { it.id }
            ) { index ->
                val contact = contacts[index]
                if (contact != null && contact.fullName.contains(searchQuery, ignoreCase = true)) {
                    ContactCard(contact = contact) {
                        onContactClick(contact, filteredItems)
                    }
                }
            }
        }
    }
}
