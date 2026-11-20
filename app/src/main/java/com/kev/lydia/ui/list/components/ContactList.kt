package com.kev.lydia.ui.list.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.kev.domain.model.Contact
import com.kev.lydia.ui.list.components.state.EmptySearchScreen
import kotlinx.coroutines.delay

private const val PULL_REFRESH_TIMEOUT_MS = 15_000L

@OptIn(ExperimentalMaterial3Api::class)
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
        var pullRefresh by remember { mutableStateOf(PullRefreshTracker()) }
        val refreshLoading = contacts.loadState.refresh is LoadState.Loading
        LaunchedEffect(refreshLoading) {
            pullRefresh = pullRefresh.onRefreshLoading(refreshLoading)
        }
        LaunchedEffect(pullRefresh.isRefreshing) {
            if (pullRefresh.isRefreshing) {
                delay(PULL_REFRESH_TIMEOUT_MS)
                pullRefresh = pullRefresh.onTimeout()
            }
        }
        PullToRefreshBox(
            isRefreshing = pullRefresh.isRefreshing,
            onRefresh = {
                pullRefresh = pullRefresh.onPull(refreshLoading)
                contacts.refresh()
            },
            modifier = Modifier.fillMaxSize()
        ) {
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
                item(key = "footer") {
                    ContactListFooter(
                        appendState = contacts.loadState.append,
                        onRetry = contacts::retry
                    )
                }
            }
        }
    }

}
