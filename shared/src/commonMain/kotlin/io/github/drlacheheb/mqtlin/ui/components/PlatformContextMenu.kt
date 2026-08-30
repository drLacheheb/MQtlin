package io.github.drlacheheb.mqtlin.ui.components

import androidx.compose.runtime.Composable

/**
 * Provides ambient platform context menu styling across all text fields and selection containers.
 */
@Composable
expect fun ProvidePlatformContextMenu(content: @Composable () -> Unit)
