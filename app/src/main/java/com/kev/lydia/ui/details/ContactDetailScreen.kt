package com.kev.lydia.ui.details

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.kev.domain.model.Contact
import kotlin.math.absoluteValue


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactDetailScreen(contact: Contact, onBack: () -> Unit, pageOffset: Float, bgColor: Color) {
    val avatarScale by animateFloatAsState(targetValue = 1f - 0.2f * pageOffset.absoluteValue)
    val avatarAlpha by animateFloatAsState(targetValue = 1f - 0.6f * pageOffset.absoluteValue)
    val infoOffsetX by animateDpAsState(targetValue = (50 * pageOffset).dp)
    val infoAlpha by animateFloatAsState(targetValue = 1f - 0.5f * pageOffset.absoluteValue)
    val titleScale by animateFloatAsState(targetValue = 1f + 0.2f * (1f - pageOffset.absoluteValue))

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
                onBack = onBack,
                onCall = {},
                onEmail = {},
                onShare = {}
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
                    contentDescription = "Avatar",
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

                // Infos avec slide + fade
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

@Composable
fun ContactInfoRow(icon: ImageVector, info: String, alpha: Float) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .alpha(alpha)
    ) {
        Icon(icon, contentDescription = null, tint = Color(0xFF00796B))
        Spacer(modifier = Modifier.width(8.dp))
        Text(info, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun InfoCard(title: String, value: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2F1)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Medium, color = Color(0xFF00796B))
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
