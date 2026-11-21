package com.kev.lydia.ui.details.screen

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.kev.domain.model.Contact
import com.kev.lydia.R
import com.kev.lydia.ui.components.ContactAvatar
import com.kev.lydia.ui.components.sharedAvatar
import com.kev.lydia.ui.details.RegisteredDate
import com.kev.lydia.ui.details.components.ContactInfoRow
import com.kev.lydia.ui.details.components.InfoCard
import com.kev.lydia.ui.details.top_bar.ContactDetailTopBar
import java.time.LocalDate
import java.util.Locale
import kotlin.math.absoluteValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactDetailScreen(
    contact: Contact,
    position: Int,
    total: Int,
    onBack: () -> Unit,
    pageOffset: Float,
    bgColor: Color,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit = {},
) {
    val context = LocalContext.current

    val avatarScale by animateFloatAsState(targetValue = 1f - 0.2f * pageOffset.absoluteValue)
    val avatarAlpha by animateFloatAsState(targetValue = 1f - 0.6f * pageOffset.absoluteValue)
    val infoOffsetX by animateDpAsState(targetValue = (50 * pageOffset).dp)

    val animatedBg = Brush.verticalGradient(
        colors = listOf(
            bgColor.copy(alpha = 0.8f + 0.2f * (1f - pageOffset.absoluteValue)),
            MaterialTheme.colorScheme.surface
        )
    )

    val dial = {
        context.startActivitySafely(Intent(Intent.ACTION_DIAL, "tel:${contact.phone}".toUri()))
    }
    val writeEmail = {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = "mailto:${contact.email}".toUri()
            putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.email_subject, contact.fullName))
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.email_body))
        }
        context.startActivitySafely(intent)
    }
    val openMap = {
        val place = Uri.encode("${contact.city}, ${contact.country}")
        context.startActivitySafely(Intent(Intent.ACTION_VIEW, "geo:0,0?q=$place".toUri()))
    }

    Scaffold(
        topBar = {
            ContactDetailTopBar(
                contact = contact,
                position = position,
                total = total,
                pageOffset = pageOffset,
                onBack = onBack,
                isFavorite = isFavorite,
                onToggleFavorite = onToggleFavorite,
                onCall = dial,
                onEmail = writeEmail,
                onShare = {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, "Contact: ${contact.fullName}")
                        putExtra(
                            Intent.EXTRA_TEXT,
                            context.getString(
                                R.string.contact_share_text,
                                contact.fullName,
                                contact.phone,
                                contact.email
                            )
                        )
                    }
                    context.startActivity(
                        Intent.createChooser(
                            shareIntent,
                            context.getString(R.string.contact_share_title)
                        )
                    )
                }
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    animatedBg
                )
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ContactAvatar(
                    fullName = contact.fullName,
                    avatarUrl = contact.avatarUrl,
                    useCachedPlaceholder = true,
                    modifier = Modifier
                        .sharedAvatar(contact.id)
                        .size((150 * avatarScale).dp)
                        .scale(avatarScale)
                        .alpha(avatarAlpha)
                        // Shadow and ring outside the avatar's own circle clip, so both show.
                        .shadow(10.dp, CircleShape)
                        .border(3.dp, MaterialTheme.colorScheme.surface, CircleShape)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = contact.fullName,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .alpha(avatarAlpha)
                        .scale(1f - 0.15f * pageOffset.absoluteValue)
                )

                if (contact.username.isNotBlank()) {
                    Text(
                        text = stringResource(R.string.contact_username, contact.username),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.alpha(avatarAlpha)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                val infoList = listOf(
                    InfoLine(Icons.Filled.Email, contact.email, stringResource(R.string.intent_email_action), writeEmail),
                    InfoLine(Icons.Filled.Phone, contact.phone, stringResource(R.string.intent_call_action), dial),
                    InfoLine(
                        Icons.Filled.LocationOn,
                        "${contact.city}, ${contact.country}",
                        stringResource(R.string.intent_map_action),
                        openMap
                    )
                )
                // The three lines grouped in one card.
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(x = infoOffsetX)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        infoList.forEach { line ->
                            val alpha by animateFloatAsState(
                                targetValue = if (pageOffset.absoluteValue < 0.5f) 1f else 0f
                            )
                            ContactInfoRow(line.icon, line.text, alpha, line.actionLabel, line.onClick)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                val registered = remember(contact.registeredDate) {
                    RegisteredDate.parse(contact.registeredDate)
                }
                // Age and registration side by side, same height.
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                ) {
                    InfoCard(
                        title = stringResource(R.string.contact_info_age),
                        value = contact.age.toString(),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )
                    InfoCard(
                        title = stringResource(R.string.contact_info_registered),
                        value = registered?.let { RegisteredDate.format(it, Locale.getDefault()) }
                            ?: contact.registeredDate,
                        supporting = registered?.let { registeredAgo(it) },
                        modifier = Modifier
                            .weight(2f)
                            .fillMaxHeight()
                    )
                }
            }
        }
    }
}

/** "11 years ago", or "Less than a year ago" for a recent registration. */
@Composable
private fun registeredAgo(date: LocalDate): String {
    val years = RegisteredDate.yearsSince(date, LocalDate.now())
    return if (years == 0) {
        stringResource(R.string.contact_registered_recently)
    } else {
        pluralStringResource(R.plurals.contact_registered_years_ago, years, years)
    }
}

private class InfoLine(
    val icon: ImageVector,
    val text: String,
    val actionLabel: String,
    val onClick: () -> Unit
)

/** Starts [intent], or tells the user when no installed app can handle it. */
private fun Context.startActivitySafely(intent: Intent) {
    try {
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(this, getString(R.string.intent_no_app), Toast.LENGTH_SHORT).show()
    }
}
