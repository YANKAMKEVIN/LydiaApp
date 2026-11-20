package com.kev.lydia.ui.details.components

import android.content.ClipData
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kev.lydia.R
import kotlinx.coroutines.launch

/**
 * One line of contact information. A tap runs [onClick] (dial, write, open the map), a long press
 * copies [info] to the clipboard with a haptic tick.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContactInfoRow(
    icon: ImageVector,
    info: String,
    alpha: Float,
    onClickLabel: String,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val clipboard = LocalClipboard.current
    val haptics = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val copiedMessage = stringResource(R.string.contact_info_copied)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .alpha(alpha)
            .clip(RoundedCornerShape(12.dp))
            .combinedClickable(
                onClickLabel = onClickLabel,
                onLongClickLabel = stringResource(R.string.contact_info_copy_action),
                onLongClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    scope.launch {
                        clipboard.setClipEntry(ClipEntry(ClipData.newPlainText(info, info)))
                    }
                    // Android 13+ already confirms clipboard copies with its own overlay.
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                        Toast.makeText(context, copiedMessage, Toast.LENGTH_SHORT).show()
                    }
                },
                onClick = onClick
            )
            .padding(horizontal = 8.dp, vertical = 10.dp)
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(12.dp))
        Text(info, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
    }
}
