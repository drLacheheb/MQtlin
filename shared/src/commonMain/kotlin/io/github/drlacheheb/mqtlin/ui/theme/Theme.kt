package io.github.drlacheheb.mqtlin.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = MqtlinPrimary,
    onPrimary = MqtlinOnPrimary,
    primaryContainer = MqtlinPrimaryContainer,
    onPrimaryContainer = MqtlinOnPrimaryContainer,
    secondary = MqtlinSecondary,
    onSecondary = MqtlinOnSecondary,
    secondaryContainer = MqtlinSecondaryContainer,
    tertiary = MqtlinTertiary,
    onTertiary = MqtlinOnTertiary,
    tertiaryContainer = MqtlinTertiaryContainer,
    background = CanvasBackground,
    onBackground = DarkOnSurface,
    surface = ModalSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceContainerHigh,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
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
