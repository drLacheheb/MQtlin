package io.github.drlacheheb.mqtlin.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material.icons.filled.FilterNone
import androidx.compose.material.icons.filled.Minimize
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import io.github.drlacheheb.mqtlin.ui.theme.DarkOnSurface
import io.github.drlacheheb.mqtlin.ui.theme.DarkOnSurfaceVariant
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceVariant

data class WindowActions(
    val onMinimize: () -> Unit = {},
    val onMaximizeRestore: () -> Unit = {},
    val onClose: () -> Unit = {},
    val isMaximized: Boolean = false
)

val LocalWindowActions = compositionLocalOf { WindowActions() }

@Composable
fun WindowControls(
    modifier: Modifier = Modifier,
    height: Int = 36
) {
    val actions = LocalWindowActions.current

    Row(
        modifier = modifier.height(height.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Minimize Button (-)
        WindowControlButton(
            onClick = actions.onMinimize,
            hoverBackground = DarkSurfaceVariant
        ) { isHovered ->
            Icon(
                imageVector = Icons.Default.Minimize,
                contentDescription = "Minimize",
                tint = if (isHovered) DarkOnSurface else DarkOnSurfaceVariant,
                modifier = Modifier.size(14.dp)
            )
        }

        // Maximize / Restore Button ([])
        WindowControlButton(
            onClick = actions.onMaximizeRestore,
            hoverBackground = DarkSurfaceVariant
        ) { isHovered ->
            Icon(
                imageVector = if (actions.isMaximized) Icons.Default.FilterNone else Icons.Default.CropSquare,
                contentDescription = if (actions.isMaximized) "Restore" else "Maximize",
                tint = if (isHovered) DarkOnSurface else DarkOnSurfaceVariant,
                modifier = Modifier.size(13.dp)
            )
        }

        // Close Button (x) - Hover Red #E81123
        WindowControlButton(
            onClick = actions.onClose,
            hoverBackground = Color(0xFFE81123)
        ) { isHovered ->
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = if (isHovered) Color.White else DarkOnSurfaceVariant,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
private fun WindowControlButton(
    onClick: () -> Unit,
    hoverBackground: Color,
    content: @Composable (isHovered: Boolean) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(42.dp)
            .pointerHoverIcon(PointerIcon.Hand)
            .background(if (isHovered) hoverBackground else Color.Transparent)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        content(isHovered)
    }
}
