package com.kev.lydia.ui.list.components.banner

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kev.lydia.R
import com.kev.lydia.ui.theme.onlineBannerColors
import kotlinx.coroutines.delay

/** What the connectivity strip under the top bar shows. */
enum class ConnectivityBanner { Hidden, Offline, BackOnline }

/** Banner after the connectivity went from [wasOffline] to [isOffline]. */
fun connectivityBannerFor(wasOffline: Boolean, isOffline: Boolean): ConnectivityBanner = when {
    isOffline -> ConnectivityBanner.Offline
    wasOffline -> ConnectivityBanner.BackOnline
    else -> ConnectivityBanner.Hidden
}

private const val BACK_ONLINE_DURATION_MS = 2_500L

/**
 * Strip under the top bar: a steady "offline, showing saved contacts" message while offline, then a
 * short green "Back online" confirmation when the network returns.
 */
@Composable
fun OfflineBanner(isOffline: Boolean) {
    var banner by remember {
        mutableStateOf(if (isOffline) ConnectivityBanner.Offline else ConnectivityBanner.Hidden)
    }
    var wasOffline by remember { mutableStateOf(isOffline) }
    // Last visible content, kept while the strip collapses.
    var shown by remember { mutableStateOf(ConnectivityBanner.Offline) }

    LaunchedEffect(isOffline) {
        banner = connectivityBannerFor(wasOffline, isOffline)
        wasOffline = isOffline
        if (banner == ConnectivityBanner.BackOnline) {
            delay(BACK_ONLINE_DURATION_MS)
            banner = ConnectivityBanner.Hidden
        }
    }
    if (banner != ConnectivityBanner.Hidden) shown = banner

    AnimatedVisibility(
        visible = banner != ConnectivityBanner.Hidden,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
    ) {
        if (shown == ConnectivityBanner.BackOnline) BackOnlineStrip() else OfflineStrip()
    }
}

@Composable
private fun OfflineStrip() {
    Surface(
        color = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.wifi_off),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = stringResource(R.string.offline_banner_title),
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = stringResource(R.string.offline_banner_subtitle),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun BackOnlineStrip() {
    val (container, content) = onlineBannerColors()
    Surface(color = container, contentColor = content, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Wifi, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.online_banner_title),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}
