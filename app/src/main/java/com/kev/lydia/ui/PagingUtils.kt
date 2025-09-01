package com.kev.lydia.ui

import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.kev.domain.model.Contact
import kotlinx.coroutines.flow.flowOf
import kotlin.collections.filterNotNull

/**
 * Convert a list into a LazyPagingItems for preview/testing.
 */
@Composable
fun <T : Any> List<T>.toPagingItems(): LazyPagingItems<T> {
    val flow = flowOf(PagingData.from(this))
    return flow.collectAsLazyPagingItems()
}

fun PagerState.pageOffsetFor(page: Int): Float {
    return (currentPage - page) + currentPageOffsetFraction
}

fun List<Contact?>.filterByQuery(query: String): List<Contact> =
    this.filterNotNull().filter { it.fullName.contains(query, ignoreCase = true) }
