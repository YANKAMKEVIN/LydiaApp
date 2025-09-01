package com.kev.lydia.ui.details.top_bar

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.kev.domain.model.Contact
import com.kev.lydia.R
import kotlin.math.absoluteValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactDetailTopBar(
    contact: Contact,
    pageOffset: Float,
    onBack: () -> Unit,
    onCall: () -> Unit,
    onEmail: () -> Unit,
    onShare: () -> Unit
) {
    val avatarAlpha by animateFloatAsState(targetValue = 1f - 0.6f * pageOffset.absoluteValue)
    val avatarScale by animateFloatAsState(targetValue = 1f - 0.15f * pageOffset.absoluteValue)
    val titleAlpha by animateFloatAsState(targetValue = 1f - 0.5f * pageOffset.absoluteValue)
    val iconScale by animateFloatAsState(targetValue = 1f - 0.1f * pageOffset.absoluteValue)
    val iconAlpha by animateFloatAsState(targetValue = 1f - 0.5f * pageOffset.absoluteValue)
    val context = LocalContext.current

    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = rememberAsyncImagePainter(contact.avatarUrl),
                    contentDescription = context.getString(R.string.contact_avatar_desc),
                    modifier = Modifier
                        .size((36 * avatarScale).dp)
                        .clip(CircleShape)
                        .alpha(avatarAlpha)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = context.getString(R.string.details_top_bar_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.alpha(titleAlpha)
                )
            }
        },
        navigationIcon = {
            IconButton(
                onClick = onBack, modifier = Modifier
                    .scale(iconScale)
                    .alpha(iconAlpha)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = context.getString(R.string.intent_back_action),
                    tint = Color(0xFF00796B)
                )
            }
        },
        actions = {
            IconButton(
                onClick = onCall,
                modifier = Modifier
                    .scale(iconScale)
                    .alpha(iconAlpha)
            ) {
                Icon(
                    Icons.Filled.Phone,
                    contentDescription = context.getString(R.string.intent_call_action),
                    tint = Color(0xFF00796B)
                )
            }
            IconButton(
                onClick = onEmail,
                modifier = Modifier
                    .scale(iconScale)
                    .alpha(iconAlpha)
            ) {
                Icon(
                    Icons.Filled.Email,
                    contentDescription = context.getString(R.string.intent_email_action),
                    tint = Color(0xFF00796B)
                )
            }
            IconButton(
                onClick = onShare,
                modifier = Modifier
                    .scale(iconScale)
                    .alpha(iconAlpha)
            ) {
                Icon(
                    Icons.Filled.Share,
                    contentDescription = context.getString(R.string.intent_share_action),
                    tint = Color(0xFF00796B)
                )
            }
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = Color.Black,
        )
    )
}


@Preview
@Composable
fun ContactDetailTopBarPreview() {
    val contact = Contact(
        id = "1",
        fullName = "John Doe",
        gender = "Male",
        email = "john.c.breckinridge@altostrat.com",
        phone = "123-456-7890",
        avatarUrl = "https://example.com/avatar.jpg",
        city = "New York",
        country = "USA",
        age = 30,
        registeredDate = "2023-01-01",
        username = "username"
    )
    ContactDetailTopBar(
        contact = contact,
        pageOffset = 0f,
        onBack = {},
        onCall = {},
        onEmail = {},
        onShare = {}
    )
}