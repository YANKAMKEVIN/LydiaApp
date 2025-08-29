package com.kev.lydia.ui

import androidx.compose.runtime.Composable
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.flowOf

/**
 * Convert a list into a LazyPagingItems for preview/testing.
 */
@Composable
fun <T : Any> List<T>.toPagingItems(): LazyPagingItems<T> {
    val flow = flowOf(PagingData.from(this))
    return flow.collectAsLazyPagingItems()
}
