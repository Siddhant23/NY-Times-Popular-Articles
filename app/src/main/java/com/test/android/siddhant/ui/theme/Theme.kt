package com.test.android.siddhant.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.foundation.isSystemInDarkTheme

private val LightColorScheme =
    lightColorScheme(
        primary = Green40,
        secondary = Pink40,
        tertiary = GreenGrey40,
    )

private val DarkColorScheme =
    darkColorScheme(
        primary = Green80,
        secondary = Pink80,
        tertiary = GreenGrey80,
    )

@Composable
fun NYTimesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
