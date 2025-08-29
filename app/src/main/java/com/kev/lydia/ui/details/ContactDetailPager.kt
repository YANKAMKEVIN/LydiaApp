package com.kev.lydia.ui.details

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kev.domain.model.Contact
import com.kev.lydia.ui.ContactDetailScreen

@Composable
fun ContactDetailPager(
    contacts: List<Contact>,
    startIndex: Int,
    onBack: () -> Unit
) {
    val pagerState = rememberPagerState(initialPage = startIndex, pageCount = { contacts.size })

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { page ->
        val pageOffset = calculateCurrentOffsetForPage(page, pagerState)
        ContactDetailScreen(
            contact = contacts[page],
            onBack = onBack,
            pageOffset = pageOffset
        )
    }
}

@Composable
fun calculateCurrentOffsetForPage(page: Int, pagerState: PagerState): Float {
    return (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
}