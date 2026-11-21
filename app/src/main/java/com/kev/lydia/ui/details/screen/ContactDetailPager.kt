package com.kev.lydia.ui.details.screen

import android.content.Context
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import com.kev.domain.model.Contact
import com.kev.lydia.ui.details.pagerHintDirection
import com.kev.lydia.ui.pageOffsetFor
import com.kev.lydia.ui.theme.contactTints
import kotlinx.coroutines.delay

private const val UI_HINTS_PREFS = "ui_hints"
private const val KEY_PAGER_HINT_SHOWN = "pager_hint_shown"

@Composable
fun ContactDetailPager(
    contacts: List<Contact>,
    startIndex: Int,
    onBack: () -> Unit,
    favoriteIds: Set<String> = emptySet(),
    onToggleFavorite: (Contact) -> Unit = {},
) {
    val pagerState = rememberPagerState(initialPage = startIndex, pageCount = { contacts.size })
    val contactColors = contactTints()
    val context = LocalContext.current
    val hintDistancePx = with(LocalDensity.current) { 72.dp.toPx() }

    // The first time a contact is opened, nudge the page aside and back so the user sees that the
    // contacts can be swiped through.
    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences(UI_HINTS_PREFS, Context.MODE_PRIVATE)
        val direction = pagerHintDirection(startIndex, contacts.size)
        if (direction != 0 && !prefs.getBoolean(KEY_PAGER_HINT_SHOWN, false)) {
            prefs.edit { putBoolean(KEY_PAGER_HINT_SHOWN, true) }
            delay(600)
            pagerState.animateScrollBy(direction * hintDistancePx, tween(durationMillis = 350))
            pagerState.animateScrollBy(-direction * hintDistancePx, spring())
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { page ->
        val pageOffset = pagerState.pageOffsetFor(page)
        val contact = contacts[page]
        ContactDetailScreen(
            contact = contact,
            isFavorite = contact.id in favoriteIds,
            onToggleFavorite = { onToggleFavorite(contact) },
            position = page + 1,
            total = contacts.size,
            onBack = onBack,
            pageOffset = pageOffset,
            bgColor = contactColors[page % contactColors.size]
        )
    }
}
