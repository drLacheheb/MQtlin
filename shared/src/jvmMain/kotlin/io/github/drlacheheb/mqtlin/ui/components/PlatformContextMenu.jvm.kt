package io.github.drlacheheb.mqtlin.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ContextMenuRepresentation
import androidx.compose.foundation.ContextMenuState
import androidx.compose.foundation.LocalContextMenuRepresentation
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceContainerLow
import kotlin.math.roundToInt

/**
 * Custom Desktop ContextMenuRepresentation rendering all platform text field context menus
 * with MQtlin's dark, compact, pixel-perfect styling positioned precisely at cursor coordinates.
 */
object MqtlinContextMenuRepresentation : ContextMenuRepresentation {

    @Composable
    override fun Representation(state: ContextMenuState, items: () -> List<androidx.compose.foundation.ContextMenuItem>) {
        val status = state.status
        if (status is ContextMenuState.Status.Open) {
            val positionProvider = remember(status.rect) {
                object : PopupPositionProvider {
                    override fun calculatePosition(
                        anchorBounds: IntRect,
                        windowSize: IntSize,
                        layoutDirection: LayoutDirection,
                        popupContentSize: IntSize
                    ): IntOffset {
                        val margin = 6 // Safety margin in pixels from window edge for shadows

                        val cursorX = anchorBounds.left + status.rect.left.roundToInt() + 2
                        val cursorY = anchorBounds.top + status.rect.bottom.roundToInt() + 2

                        // Horizontal: Open right of cursor, or flip left if near right edge
                        val x = if (cursorX + popupContentSize.width <= windowSize.width - margin) {
                            cursorX.coerceAtLeast(margin)
                        } else {
                            (cursorX - popupContentSize.width - 4)
                                .coerceIn(margin, (windowSize.width - popupContentSize.width - margin).coerceAtLeast(margin))
                        }

                        // Vertical: Open below cursor, or flip upward if near bottom edge
                        val y = if (cursorY + popupContentSize.height <= windowSize.height - margin) {
                            cursorY.coerceAtLeast(margin)
                        } else {
                            (anchorBounds.top + status.rect.top.roundToInt() - popupContentSize.height - 2)
                                .coerceIn(margin, (windowSize.height - popupContentSize.height - margin).coerceAtLeast(margin))
                        }
                        return IntOffset(x, y)
                    }
                }
            }

            Popup(
                popupPositionProvider = positionProvider,
                onDismissRequest = { state.status = ContextMenuState.Status.Closed },
                properties = PopupProperties(focusable = false)
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = DarkSurfaceContainerLow,
                    border = BorderStroke(1.dp, ContextMenuBorderColor),
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .width(180.dp)
                        .clip(RoundedCornerShape(6.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                    ) {
                        items().forEach { item ->
                            val label = item.label
                            val (icon, shortcut) = when (label.lowercase().trim()) {
                                "cut" -> Pair(Icons.Default.ContentCut, "Ctrl+X")
                                "copy" -> Pair(Icons.Default.ContentCopy, "Ctrl+C")
                                "paste" -> Pair(Icons.Default.ContentPaste, "Ctrl+V")
                                "select all" -> Pair(Icons.Default.SelectAll, "Ctrl+A")
                                else -> Pair(Icons.Default.Edit, null)
                            }

                            MqtlinContextMenuItem(
                                text = label,
                                leadingIcon = icon,
                                shortcut = shortcut,
                                onClick = {
                                    state.status = ContextMenuState.Status.Closed
                                    item.onClick()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
actual fun ProvidePlatformContextMenu(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalContextMenuRepresentation provides MqtlinContextMenuRepresentation
    ) {
        content()
    }
}
