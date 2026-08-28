package io.github.drlacheheb.mqtlin.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = MqtlinPrimary,
    onPrimary = MqtlinOnPrimary,
    primaryContainer = MqtlinPrimaryContainer,
    onPrimaryContainer = MqtlinOnPrimaryContainer,
    inversePrimary = MqtlinInversePrimary,
    secondary = MqtlinSecondary,
    onSecondary = MqtlinOnSecondary,
    secondaryContainer = MqtlinSecondaryContainer,
    tertiary = MqtlinTertiary,
    onTertiary = MqtlinOnTertiary,
    tertiaryContainer = MqtlinTertiaryContainer,
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceDim = DarkSurfaceDim,
    surfaceBright = DarkSurfaceBright,
    surfaceContainerLowest = DarkSurfaceContainerLowest,
    surfaceContainerLow = DarkSurfaceContainerLow,
    surfaceContainer = DarkSurfaceContainer,
    surfaceContainerHigh = DarkSurfaceContainerHigh,
    surfaceContainerHighest = DarkSurfaceContainerHighest,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    inverseSurface = DarkInverseSurface,
    outline = DarkOutlineMuted,
    outlineVariant = DarkOutlineVariant,
    error = MqtlinError,
    onError = MqtlinOnError,
    errorContainer = MqtlinErrorContainer,
    onErrorContainer = MqtlinOnErrorContainer
)

@Composable
fun MqtlinTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = MqtlinTypography,
        content = content
    )
}
