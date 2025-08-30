package com.kev.lydia.ui.details

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.kev.domain.model.Contact
import com.kev.lydia.ui.pageOffsetFor


@Composable
fun ContactDetailPager(
    contacts: List<Contact>,
    startIndex: Int,
    onBack: () -> Unit
) {
    val pagerState = rememberPagerState(initialPage = startIndex, pageCount = { contacts.size })
    val contactColors = listOf(
        Color(0xFFE0F7FA), Color(0xFFFFF3E0), Color(0xFFE8F5E9),
        Color(0xFFFFEBEE), Color(0xFFF3E5F5)
    )

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { page ->
        val pageOffset = pagerState.pageOffsetFor(page)
        ContactDetailScreen(
            contact = contacts[page],
            onBack = onBack,
            pageOffset = pageOffset,
            bgColor = contactColors[page % contactColors.size]
        )
    }
}
