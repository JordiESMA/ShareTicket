package com.shareticket.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ObsidianColorScheme = darkColorScheme(
    primary = ObsidianPurplePrimary,
    onPrimary = Color.White,
    primaryContainer = ObsidianPurpleContainer,
    onPrimaryContainer = ObsidianOnPurpleContainer,
    secondary = ObsidianCyanSecondary,
    onSecondary = Color.Black,
    secondaryContainer = ObsidianCyanContainer,
    onSecondaryContainer = Color(0xFFBAE6FD),
    background = ObsidianBackground,
    onBackground = ObsidianTextPrimary,
    surface = ObsidianSurface,
    onSurface = ObsidianTextPrimary,
    surfaceVariant = ObsidianSurfaceVariant,
    onSurfaceVariant = ObsidianTextSecondary,
    outline = ObsidianBorder,
    error = ObsidianError
)

@Composable
fun ShareTicketTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ObsidianColorScheme,
        typography = Typography,
        content = content
    )
}
