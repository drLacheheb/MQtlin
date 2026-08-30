package io.github.drlacheheb.mqtlin.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import io.github.drlacheheb.mqtlin.ui.theme.DarkOnSurface
import io.github.drlacheheb.mqtlin.ui.theme.DarkOutline
import io.github.drlacheheb.mqtlin.ui.theme.DarkOutlineVariant
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceBright
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceContainerLow

val ContextMenuBorderColor = Color(0xFF2E2E35)
val ContextMenuDestructiveColor = Color(0xFFF7768E)

/**
 * Compact Desktop Context Menu Popup with 0px internal padding.
 * Eliminates hardcoded Material 3 padding and mobile touch constraints.
 */
@Composable
fun MqtlinContextMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    width: Dp = 200.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    if (expanded) {
        Popup(
            onDismissRequest = onDismissRequest,
            properties = PopupProperties(focusable = true)
        ) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = DarkSurfaceContainerLow,
                border = BorderStroke(1.dp, ContextMenuBorderColor),
                shadowElevation = 8.dp,
                modifier = modifier
                    .width(width)
                    .clip(RoundedCornerShape(6.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                ) {
                    content()
                }
            }
        }
    }
}

/**
 * Compact Desktop Context Menu Item Row (26dp height, hover highlight).
 */
@Composable
fun MqtlinContextMenuItem(
    text: String,
    leadingIcon: ImageVector,
    shortcut: String? = null,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    val textColor = when {
        isDestructive -> ContextMenuDestructiveColor
        isHovered -> Color.White
        else -> DarkOnSurface
    }

    val iconColor = when {
        isDestructive -> ContextMenuDestructiveColor
        isHovered -> Color.White
        else -> DarkOutline
    }

    val backgroundColor = when {
        isHovered && isDestructive -> ContextMenuDestructiveColor.copy(alpha = 0.15f)
        isHovered -> DarkSurfaceBright
        else -> Color.Transparent
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(26.dp)
            .background(backgroundColor)
            .pointerHoverIcon(PointerIcon.Hand)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = leadingIcon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            color = textColor,
            modifier = Modifier.weight(1f)
        )
        if (shortcut != null) {
            Text(
                text = shortcut,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                color = if (isHovered) DarkOnSurface.copy(alpha = 0.8f) else DarkOutlineVariant
            )
        }
    }
}

@Composable
fun MqtlinContextMenuDivider() {
    HorizontalDivider(
        color = ContextMenuBorderColor,
        thickness = 1.dp,
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
    )
}
