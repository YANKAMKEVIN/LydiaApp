package com.kev.lydia.ui.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier

/** The [SharedTransitionScope] around the navigation graph, or null outside of it (previews). */
@OptIn(ExperimentalSharedTransitionApi::class)
val LocalSharedTransitionScope = staticCompositionLocalOf<SharedTransitionScope?> { null }

/** The animated scope of the current navigation destination, or null outside of the graph. */
val LocalNavAnimatedVisibilityScope = staticCompositionLocalOf<AnimatedVisibilityScope?> { null }

/** Key matching a contact's avatar in the list and in the detail. */
fun avatarSharedKey(contactId: String): String = "contact-avatar-$contactId"

/**
 * Lets the contact's avatar fly between the list card and the detail page when navigating.
 * Does nothing outside of the navigation graph.
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Modifier.sharedAvatar(contactId: String): Modifier {
    val sharedScope = LocalSharedTransitionScope.current ?: return this
    val visibilityScope = LocalNavAnimatedVisibilityScope.current ?: return this
    return with(sharedScope) {
        this@sharedAvatar.sharedElement(
            sharedContentState = rememberSharedContentState(key = avatarSharedKey(contactId)),
            animatedVisibilityScope = visibilityScope
        )
    }
}
