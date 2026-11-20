package com.kev.lydia.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kev.lydia.R
import kotlin.math.absoluteValue

/**
 * Round contact picture. A disc with the contact's initials shows while the picture loads and
 * stays when it cannot be loaded (offline, cached contact without image).
 *
 * The size comes from [modifier]. [useCachedPlaceholder] shows the bitmap already in memory (the
 * list's smaller avatar) while a larger one loads.
 */
@Composable
fun ContactAvatar(
    fullName: String,
    avatarUrl: String,
    modifier: Modifier = Modifier,
    useCachedPlaceholder: Boolean = false,
) {
    val context = LocalContext.current
    val request = remember(avatarUrl, useCachedPlaceholder) {
        ImageRequest.Builder(context)
            .data(avatarUrl)
            .crossfade(true)
            // Same key everywhere, so the list and the detail share the bitmap.
            .memoryCacheKey(avatarUrl)
            .apply { if (useCachedPlaceholder) placeholderMemoryCacheKey(avatarUrl) }
            .build()
    }
    val (container, content) = avatarColors(fullName)

    BoxWithConstraints(
        modifier = modifier
            .clip(CircleShape)
            .background(container),
        contentAlignment = Alignment.Center
    ) {
        val fontSize = with(LocalDensity.current) { (maxWidth * 0.38f).toSp() }
        Text(
            text = initialsOf(fullName),
            color = content,
            fontSize = fontSize,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
        AsyncImage(
            model = request,
            contentDescription = stringResource(R.string.contact_avatar_desc),
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )
    }
}

/** A stable container / content colour pair per contact, taken from the theme. */
@Composable
private fun avatarColors(fullName: String): Pair<Color, Color> {
    val scheme = MaterialTheme.colorScheme
    return when (fullName.hashCode().absoluteValue % 3) {
        0 -> scheme.primaryContainer to scheme.onPrimaryContainer
        1 -> scheme.secondaryContainer to scheme.onSecondaryContainer
        else -> scheme.tertiaryContainer to scheme.onTertiaryContainer
    }
}
