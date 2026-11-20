package com.kev.lydia.ui.list.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Height of a ContactCard: 64 dp avatar + 2 x 16 dp padding, plus 2 x 4 dp outer padding.
private val SkeletonRowHeight = 104.dp

/**
 * First-load placeholder: skeletons shaped like [ContactCard] (avatar + three text lines) with a
 * shimmer sweeping across them, as many as the screen can hold.
 */
@Composable
fun ContactListPlaceholder() {
    val transition = rememberInfiniteTransition(label = "skeletonShimmer")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerProgress"
    )

    BoxWithConstraints(modifier = Modifier.fillMaxSize().clipToBounds()) {
        val widthPx = constraints.maxWidth.toFloat()
        val base = MaterialTheme.colorScheme.surfaceVariant
        val highlight = MaterialTheme.colorScheme.surfaceContainerLowest
        // The highlight band travels from just off the left edge to just off the right edge.
        val bandStart = -widthPx + 2 * widthPx * progress
        val shimmer = Brush.linearGradient(
            colors = listOf(base, highlight.copy(alpha = 0.9f), base),
            start = Offset(bandStart, 0f),
            end = Offset(bandStart + widthPx, 0f)
        )
        val rows = skeletonRowCount(maxHeight)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            repeat(rows) { SkeletonCard(shimmer) }
        }
    }
}

private fun skeletonRowCount(available: Dp): Int =
    (available / SkeletonRowHeight).toInt().coerceAtLeast(1) + 1

@Composable
private fun SkeletonCard(brush: Brush) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(brush)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SkeletonLine(brush, Modifier.fillMaxWidth(0.55f).height(16.dp))
                SkeletonLine(brush, Modifier.fillMaxWidth(0.4f).height(12.dp))
                SkeletonLine(brush, Modifier.fillMaxWidth(0.7f).height(12.dp))
            }
        }
    }
}

@Composable
private fun SkeletonLine(brush: Brush, modifier: Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(brush)
    )
}
