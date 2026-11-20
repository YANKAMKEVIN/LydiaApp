package com.kev.lydia.ui.theme

import androidx.compose.ui.graphics.Color

// Brand palette derived from the launcher icon background (#522CDC), following the Material 3
// tonal roles (primary at tone 40 in light theme, tone 80 in dark theme).
val LydiaBlue = Color(0xFF522CDC)

// Light scheme
val PrimaryLight = LydiaBlue
val OnPrimaryLight = Color(0xFFFFFFFF)
val PrimaryContainerLight = Color(0xFFE6DEFF)
val OnPrimaryContainerLight = Color(0xFF1A0064)
val SecondaryLight = Color(0xFF5E5A71)
val OnSecondaryLight = Color(0xFFFFFFFF)
val SecondaryContainerLight = Color(0xFFE4DEF9)
val OnSecondaryContainerLight = Color(0xFF1B182B)
val TertiaryLight = Color(0xFF7B5266)
val OnTertiaryLight = Color(0xFFFFFFFF)
val TertiaryContainerLight = Color(0xFFFFD8E8)
val OnTertiaryContainerLight = Color(0xFF301122)
val BackgroundLight = Color(0xFFFCF8FF)
val OnBackgroundLight = Color(0xFF1C1B20)
val SurfaceLight = Color(0xFFFCF8FF)
val OnSurfaceLight = Color(0xFF1C1B20)
val SurfaceVariantLight = Color(0xFFE5E0EF)
val OnSurfaceVariantLight = Color(0xFF48454F)
val OutlineLight = Color(0xFF797580)
val OutlineVariantLight = Color(0xFFC9C4D0)
val SurfaceContainerLowestLight = Color(0xFFFFFFFF)
val SurfaceContainerLowLight = Color(0xFFF6F2FA)
val SurfaceContainerLight = Color(0xFFF0ECF5)
val SurfaceContainerHighLight = Color(0xFFEAE6EF)
val SurfaceContainerHighestLight = Color(0xFFE5E1E9)

// Dark scheme
val PrimaryDark = Color(0xFFCABEFF)
val OnPrimaryDark = Color(0xFF2E009F)
val PrimaryContainerDark = Color(0xFF4215CC)
val OnPrimaryContainerDark = Color(0xFFE6DEFF)
val SecondaryDark = Color(0xFFC8C2DC)
val OnSecondaryDark = Color(0xFF302D41)
val SecondaryContainerDark = Color(0xFF474359)
val OnSecondaryContainerDark = Color(0xFFE4DEF9)
val TertiaryDark = Color(0xFFECB8CF)
val OnTertiaryDark = Color(0xFF482537)
val TertiaryContainerDark = Color(0xFF613B4E)
val OnTertiaryContainerDark = Color(0xFFFFD8E8)
val BackgroundDark = Color(0xFF141318)
val OnBackgroundDark = Color(0xFFE5E1E9)
val SurfaceDark = Color(0xFF141318)
val OnSurfaceDark = Color(0xFFE5E1E9)
val SurfaceVariantDark = Color(0xFF48454F)
val OnSurfaceVariantDark = Color(0xFFC9C4D0)
val OutlineDark = Color(0xFF938F9A)
val OutlineVariantDark = Color(0xFF48454F)
val SurfaceContainerLowestDark = Color(0xFF0F0D13)
val SurfaceContainerLowDark = Color(0xFF1C1B20)
val SurfaceContainerDark = Color(0xFF201F25)
val SurfaceContainerHighDark = Color(0xFF2B292F)
val SurfaceContainerHighestDark = Color(0xFF36343A)

// "Back online" banner
val OnlineContainerLight = Color(0xFFC6F0CC)
val OnOnlineContainerLight = Color(0xFF00391A)
val OnlineContainerDark = Color(0xFF1C4D2A)
val OnOnlineContainerDark = Color(0xFFB4F2BE)

// Background tints of the contact detail pages (one per page, cycling). The dark variants keep
// the same hues but are deep enough for light text.
val ContactTintsLight = listOf(
    Color(0xFFE0F7FA), Color(0xFFFFF3E0), Color(0xFFE8F5E9),
    Color(0xFFFFEBEE), Color(0xFFF3E5F5)
)
val ContactTintsDark = listOf(
    Color(0xFF0E3A40), Color(0xFF42300F), Color(0xFF173D1E),
    Color(0xFF4A1C22), Color(0xFF3A1C42)
)
