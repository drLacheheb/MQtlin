package io.github.drlacheheb.mqtlin.ui.connection

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.PointerMatcher
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.drlacheheb.mqtlin.domain.model.ConnectionConfig
import io.github.drlacheheb.mqtlin.ui.components.MqtlinContextMenu
import io.github.drlacheheb.mqtlin.ui.components.MqtlinContextMenuDivider
import io.github.drlacheheb.mqtlin.ui.components.MqtlinContextMenuItem
import io.github.drlacheheb.mqtlin.ui.theme.DarkOnSurface
import io.github.drlacheheb.mqtlin.ui.theme.DarkOnSurfaceVariant
import io.github.drlacheheb.mqtlin.ui.theme.DarkOutlineVariant
import io.github.drlacheheb.mqtlin.ui.theme.MonoTopic
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinPrimary
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinSecondary
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinTertiary
import io.github.drlacheheb.mqtlin.ui.theme.UiLabelBold
import io.github.drlacheheb.mqtlin.ui.theme.UiLabelReg

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProfileListItem(
    profile: ConnectionConfig,
    isActive: Boolean,
    badge: String?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onDuplicate: () -> Unit = {},
    onDelete: () -> Unit = {},
) {
    var showContextMenu by remember { mutableStateOf(false) }
    val infiniteTransition = rememberInfiniteTransition()
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.80f,
        targetValue = 1.20f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(1000),
                repeatMode = RepeatMode.Reverse,
            ),
    )

    Surface(
        modifier =
            modifier
                .fillMaxWidth()
                .pointerHoverIcon(PointerIcon.Hand)
                .onClick(matcher = PointerMatcher.mouse(PointerButton.Primary)) {
                    onClick()
                }.onClick(matcher = PointerMatcher.mouse(PointerButton.Secondary)) {
                    onClick()
                    showContextMenu = true
                },
        shape = RoundedCornerShape(2.dp),
        color = if (isActive) MqtlinPrimary.copy(alpha = 0.10f) else Color.Transparent,
        border = if (isActive) BorderStroke(1.dp, MqtlinPrimary.copy(alpha = 0.20f)) else null,
    ) {
        Row(
            modifier =
                Modifier
                    .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Pulse Status Indicator (8px dot)
            Box(
                modifier =
                    Modifier
                        .size(8.dp)
                        .scale(if (isActive) pulseScale else 1f)
                        .background(
                            color = if (isActive) MqtlinSecondary else DarkOutlineVariant,
                            shape = CircleShape,
                        ),
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = profile.name,
                        style = if (isActive) UiLabelBold.copy(fontSize = 14.sp) else UiLabelReg.copy(fontSize = 14.sp),
                        color = DarkOnSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false).padding(end = 4.dp),
                    )

                    if (badge != null) {
                        Surface(
                            shape = RoundedCornerShape(2.dp),
                            color = if (badge == "TLS") MqtlinPrimary.copy(alpha = 0.2f) else MqtlinTertiary.copy(alpha = 0.2f),
                            border =
                                BorderStroke(
                                    1.dp,
                                    if (badge == "TLS") MqtlinPrimary.copy(alpha = 0.3f) else MqtlinTertiary.copy(alpha = 0.3f),
                                ),
                        ) {
                            Text(
                                text = badge,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (badge == "TLS") MqtlinPrimary else MqtlinTertiary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            )
                        }
                    }
                }

                Text(
                    text = "${profile.host}:${profile.port}",
                    style = MonoTopic.copy(fontSize = 12.sp, color = DarkOnSurfaceVariant.copy(alpha = 0.80f)),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            // Right-Click Context Menu Popup
            MqtlinContextMenu(
                expanded = showContextMenu,
                onDismissRequest = { showContextMenu = false },
                width = 160.dp,
            ) {
                MqtlinContextMenuItem(
                    text = "Duplicate",
                    leadingIcon = Icons.Default.ContentCopy,
                    shortcut = "Ctrl+D",
                    onClick = {
                        onDuplicate()
                        showContextMenu = false
                    },
                )

                MqtlinContextMenuDivider()

                MqtlinContextMenuItem(
                    text = "Delete Profile",
                    leadingIcon = Icons.Default.Delete,
                    shortcut = "Del",
                    isDestructive = true,
                    onClick = {
                        onDelete()
                        showContextMenu = false
                    },
                )
            }
        }
    }
}
