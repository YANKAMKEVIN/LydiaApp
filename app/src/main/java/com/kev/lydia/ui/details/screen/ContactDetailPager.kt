package com.kev.lydia.ui.details.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kev.domain.model.Contact
import com.kev.lydia.ui.pageOffsetFor
import com.kev.lydia.ui.theme.contactTints


@Composable
fun ContactDetailPager(
    contacts: List<Contact>,
    startIndex: Int,
    onBack: () -> Unit
) {
    val pagerState = rememberPagerState(initialPage = startIndex, pageCount = { contacts.size })
    val contactColors = contactTints()

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
