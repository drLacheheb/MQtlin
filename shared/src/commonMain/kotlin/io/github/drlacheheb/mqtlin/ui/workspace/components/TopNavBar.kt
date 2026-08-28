package io.github.drlacheheb.mqtlin.ui.workspace.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.drlacheheb.mqtlin.domain.model.ConnectionConfig
import io.github.drlacheheb.mqtlin.ui.theme.DarkOnSurface
import io.github.drlacheheb.mqtlin.ui.theme.DarkOnSurfaceVariant
import io.github.drlacheheb.mqtlin.ui.theme.DarkOutline
import io.github.drlacheheb.mqtlin.ui.theme.DarkOutlineMuted
import io.github.drlacheheb.mqtlin.ui.theme.DarkOutlineVariant
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceBright
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceContainer
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceContainerHigh
import io.github.drlacheheb.mqtlin.ui.theme.HeadlineSm
import io.github.drlacheheb.mqtlin.ui.theme.MonoCode
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinPrimary
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinSecondary
import io.github.drlacheheb.mqtlin.ui.theme.UiLabelBold
import io.github.drlacheheb.mqtlin.ui.theme.UiLabelReg

@Composable
fun TopNavBar(
    config: ConnectionConfig?,
    latencyMs: Long,
    onOpenConnectionManager: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition()
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.80f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Header: h-14 (56px) bg-surface-container border-b border-outline-variant
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(DarkSurfaceContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Brand + Connection Tabs
            Row(
                modifier = Modifier.fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mqtlin",
                    style = HeadlineSm.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MqtlinPrimary),
                    modifier = Modifier.padding(end = 24.dp)
                )

                // Navigation Tabs Container (flex h-full items-end gap-2)
                Row(
                    modifier = Modifier.fillMaxHeight(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Active Tab: Local Mosquitto:1883 (width tightly bound to content)
                    val tabTitle = if (config != null) "${config.name}:${config.port}" else "Local Mosquitto:1883"
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(IntrinsicSize.Max)
                            .clickable { },
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .scale(pulseScale)
                                    .background(MqtlinSecondary, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = tabTitle,
                                style = UiLabelBold.copy(fontSize = 14.sp, color = MqtlinPrimary),
                                maxLines = 1
                            )
                        }
                        // Bottom Border 2px matching tab width exactly
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(2.dp)
                                .background(MqtlinPrimary)
                        )
                    }

                    // Inactive Tab: AWS IoT Staging
                    Row(
                        modifier = Modifier
                            .fillMaxHeight()
                            .clickable(onClick = onOpenConnectionManager)
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cloud,
                            contentDescription = null,
                            tint = DarkOutlineMuted,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AWS IoT Staging",
                            style = UiLabelReg.copy(fontSize = 14.sp, color = DarkOnSurfaceVariant),
                            maxLines = 1
                        )
                    }
                }
            }

            // Right: Latency Pill + Navigation Links + Trailing Actions
            Row(
                modifier = Modifier.fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Latency Pill (bg-surface-container-high px-3 py-1 rounded-full border border-outline-variant)
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = DarkSurfaceContainerHigh,
                    border = BorderStroke(1.dp, DarkOutlineVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = "Latency",
                            tint = MqtlinSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${latencyMs}ms",
                            style = MonoCode.copy(fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MqtlinSecondary)
                        )
                    }
                }

                // Nav Links (Connections, Brokers, Settings, Logs)
                Row(
                    modifier = Modifier.fillMaxHeight(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Connections (Active) with tight intrinsic width
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(IntrinsicSize.Max)
                            .clickable(onClick = onOpenConnectionManager),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Connections",
                                style = UiLabelBold.copy(fontSize = 14.sp, color = MqtlinPrimary),
                                maxLines = 1
                            )
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(2.dp)
                                .background(MqtlinPrimary)
                        )
                    }

                    // Brokers, Settings, Logs (Inactive)
                    listOf("Brokers", "Settings", "Logs").forEach { item ->
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .clickable { }
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = item,
                                style = UiLabelBold.copy(fontSize = 14.sp, color = DarkOnSurfaceVariant),
                                maxLines = 1
                            )
                        }
                    }
                }

                // Divider (border-l border-outline-variant pl-4)
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(24.dp)
                        .background(DarkOutlineVariant)
                )

                // Trailing Action Icons (account_tree, notifications, settings)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = {}, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.AccountTree,
                            contentDescription = "Topic Tree",
                            tint = DarkOnSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = {}, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = DarkOnSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = {}, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = DarkOnSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Bottom Border of the Header
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(1.dp)
                .background(DarkOutlineVariant)
        )
    }
}
