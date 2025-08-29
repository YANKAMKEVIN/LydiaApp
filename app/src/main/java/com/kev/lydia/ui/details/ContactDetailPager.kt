package com.kev.lydia.ui.details

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kev.domain.model.Contact

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
        ContactDetailScreen(
            contact = contacts[page],
            onBack = onBack
        )
    }
}