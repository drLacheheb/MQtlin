package io.github.drlacheheb.mqtlin.ui.workspace.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.drlacheheb.mqtlin.domain.model.ConnectionConfig
import io.github.drlacheheb.mqtlin.ui.components.WindowControls
import io.github.drlacheheb.mqtlin.ui.theme.DarkOnSurface
import io.github.drlacheheb.mqtlin.ui.theme.DarkOnSurfaceVariant
import io.github.drlacheheb.mqtlin.ui.theme.DarkOutlineVariant
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceContainer
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinPrimary
import io.github.drlacheheb.mqtlin.ui.theme.UiLabelBold

@Composable
fun TopNavBar(
    config: ConnectionConfig?,
    onOpenConnectionManager: () -> Unit,
    onOpenSettings: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Header: h-10.5 (42px) bg-surface-container border-b border-outline-variant
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(42.dp)
            .background(DarkSurfaceContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Connected Profile status: Name | server:port
            val profileName = config?.name ?: "Connected Broker"
            val profileAddress = if (config != null) "${config.host}:${config.port}" else "127.0.0.1:1883"

            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(start = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = profileName,
                    style = UiLabelBold.copy(fontSize = 13.sp, color = DarkOnSurface)
                )

                Text(
                    text = "|",
                    fontSize = 13.sp,
                    color = DarkOutlineVariant
                )

                Text(
                    text = profileAddress,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    color = DarkOnSurfaceVariant
                )

                if (config?.transport?.name == "TLS" || config?.transport?.name == "WSS") {
                    Surface(
                        shape = RoundedCornerShape(2.dp),
                        color = MqtlinPrimary.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, MqtlinPrimary.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = config.transport.name,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MqtlinPrimary,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
            }

            // Right: Switch Profile Icon (⇄) + Settings Icon (⚙) + Window Controls
            Row(
                modifier = Modifier.fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.padding(end = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Switch Connection Icon Button (⇄)
                    IconButton(
                        onClick = onOpenConnectionManager,
                        modifier = Modifier
                            .size(28.dp)
                            .pointerHoverIcon(PointerIcon.Hand)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = "Switch Profile",
                            tint = DarkOnSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Settings Button (⚙)
                    IconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier
                            .size(28.dp)
                            .pointerHoverIcon(PointerIcon.Hand)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Application Settings",
                            tint = DarkOnSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Vertical Divider
                    Box(
                        modifier = Modifier
                            .padding(start = 2.dp)
                            .width(1.dp)
                            .height(18.dp)
                            .background(DarkOutlineVariant)
                    )
                }

                // Window Controls (- [] ✕)
                WindowControls(height = 42)
            }
        }

        // Bottom Border of Header
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(1.dp)
                .background(DarkOutlineVariant)
        )
    }
}
