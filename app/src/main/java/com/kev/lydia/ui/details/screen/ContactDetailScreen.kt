package com.kev.lydia.ui.details.screen

import android.content.Intent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.kev.domain.model.Contact
import com.kev.lydia.R
import com.kev.lydia.ui.details.components.ContactInfoRow
import com.kev.lydia.ui.details.components.InfoCard
import com.kev.lydia.ui.details.top_bar.ContactDetailTopBar
import kotlin.math.absoluteValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactDetailScreen(contact: Contact, onBack: () -> Unit, pageOffset: Float, bgColor: Color) {
    val context = LocalContext.current

    val avatarScale by animateFloatAsState(targetValue = 1f - 0.2f * pageOffset.absoluteValue)
    val avatarAlpha by animateFloatAsState(targetValue = 1f - 0.6f * pageOffset.absoluteValue)
    val infoOffsetX by animateDpAsState(targetValue = (50 * pageOffset).dp)

    val animatedBg = Brush.verticalGradient(
        colors = listOf(
            bgColor.copy(alpha = 0.8f + 0.2f * (1f - pageOffset.absoluteValue)),
            Color.White
        )
    )

    Scaffold(
        topBar = {
            ContactDetailTopBar(
                contact = contact,
                pageOffset = pageOffset,
                onBack = onBack,
                onCall = {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = "tel:${contact.phone}".toUri()
                    }
                    context.startActivity(intent)
                },
                onEmail = {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = "mailto:${contact.email}".toUri()
                        putExtra(
                            Intent.EXTRA_SUBJECT,
                            context.getString(R.string.email_subject, contact.fullName)
                        )
                        putExtra(Intent.EXTRA_TEXT, context.getString(R.string.email_body))
                    }
                    context.startActivity(intent)
                },
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
                AsyncImage(
                    model = contact.avatarUrl,
                    contentDescription = stringResource(R.string.contact_avatar_desc),
                    modifier = Modifier
                        .size((150 * avatarScale).dp)
                        .scale(avatarScale)
                        .clip(CircleShape)
                        .alpha(avatarAlpha)
                        .shadow(10.dp, CircleShape)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = contact.fullName,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .alpha(avatarAlpha)
                        .scale(1f - 0.15f * pageOffset.absoluteValue)
                )

                Spacer(modifier = Modifier.height(24.dp))

                val infoList = listOf(
                    Pair(Icons.Filled.Email, contact.email),
                    Pair(Icons.Filled.Phone, contact.phone),
                    Pair(Icons.Filled.LocationOn, "${contact.city}, ${contact.country}")
                )
                Column(
                    modifier = Modifier
                        .offset(x = infoOffsetX)
                ) {
                    infoList.forEachIndexed { index, (icon, info) ->
                        val alpha by animateFloatAsState(
                            targetValue = if (pageOffset.absoluteValue < 0.5f) 1f else 0f
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        ContactInfoRow(icon, info, alpha)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                InfoCard(title = "Age", value = contact.age.toString())
                InfoCard(title = "Registered", value = contact.registeredDate)
            }
        }
    }
}


