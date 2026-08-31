package io.github.drlacheheb.mqtlin.ui.workspace.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.drlacheheb.mqtlin.ui.theme.DarkOnSurfaceVariant
import io.github.drlacheheb.mqtlin.ui.theme.DarkOutlineVariant
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceContainerLowest
import io.github.drlacheheb.mqtlin.ui.theme.LabelXs
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinSecondary

@Composable
fun WorkspaceFooter(
    totalTopics: Int,
    totalMessages: Long,
    latencyMs: Long,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition()
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.80f,
        targetValue = 1.25f,
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
                .height(28.dp),
        color = DarkSurfaceContainerLowest,
        border = BorderStroke(1.dp, DarkOutlineVariant),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Left: Version & Connected Status with pulse
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = "Mqtlin v2.4.0",
                    style = LabelXs.copy(fontSize = 11.sp, color = DarkOnSurfaceVariant),
                )

                Box(
                    modifier =
                        Modifier
                            .width(1.dp)
                            .height(12.dp)
                            .background(DarkOutlineVariant),
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier =
                            Modifier
                                .size(6.dp)
                                .scale(pulseScale)
                                .background(MqtlinSecondary, CircleShape),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Connected",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = DarkOnSurfaceVariant,
                    )
                }
            }

            // Right: Live Metrics
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "Connected: ${latencyMs}ms",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = DarkOnSurfaceVariant,
                )

                Box(
                    modifier =
                        Modifier
                            .size(3.dp)
                            .background(DarkOutlineVariant, CircleShape),
                )

                Text(
                    text = "Messages: $totalMessages",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = DarkOnSurfaceVariant,
                )

                Box(
                    modifier =
                        Modifier
                            .size(3.dp)
                            .background(DarkOutlineVariant, CircleShape),
                )

                Text(
                    text = "Topics: $totalTopics",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = MqtlinSecondary,
                )

                Box(
                    modifier =
                        Modifier
                            .size(3.dp)
                            .background(DarkOutlineVariant, CircleShape),
                )

                Text(
                    text = "Mem: 86 MB",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = MqtlinSecondary,
                )
            }
        }
    }
}
