package io.github.drlacheheb.mqtlin.ui.workspace.publisher

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.drlacheheb.mqtlin.ui.theme.DarkBackground
import io.github.drlacheheb.mqtlin.ui.theme.DarkOnSurface
import io.github.drlacheheb.mqtlin.ui.theme.DarkOnSurfaceVariant
import io.github.drlacheheb.mqtlin.ui.theme.DarkOutlineVariant
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceContainerHigh
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceDim
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinError
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinErrorContainer
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinInversePrimary
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinOnPrimaryContainer
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinPrimary
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinPrimaryContainer
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinSecondary
import io.github.drlacheheb.mqtlin.ui.theme.UiLabelBold

/**
 * Bottom controls bar containing QoS buttons, Retain toggle, and Publish button.
 */
@Composable
fun PublishControlsBar(
    qos: Int,
    onQosChange: (Int) -> Unit,
    isRetained: Boolean,
    onRetainedChange: (Boolean) -> Unit,
    isPublishing: Boolean,
    showSuccessIndicator: Boolean,
    publishError: String?,
    canPublish: Boolean,
    onPublishClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // QoS & Retain Settings Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // QoS Segmented Pill Group
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = "QoS",
                    style = UiLabelBold.copy(fontSize = 12.sp, color = DarkOnSurfaceVariant),
                )
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(32.dp)
                            .background(DarkSurfaceDim, RoundedCornerShape(4.dp))
                            .border(1.dp, DarkOutlineVariant, RoundedCornerShape(4.dp)),
                ) {
                    for (q in 0..2) {
                        val isSelected = qos == q
                        Box(
                            modifier =
                                Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .pointerHoverIcon(PointerIcon.Hand)
                                    .clickable { onQosChange(q) }
                                    .background(if (isSelected) MqtlinPrimaryContainer else Color.Transparent),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = q.toString(),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MqtlinOnPrimaryContainer else DarkOnSurfaceVariant,
                            )
                        }
                    }
                }
            }

            // Retain Switch
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = "Retain",
                    style = UiLabelBold.copy(fontSize = 12.sp, color = DarkOnSurfaceVariant),
                )
                Row(
                    modifier =
                        Modifier
                            .height(32.dp)
                            .pointerHoverIcon(PointerIcon.Hand)
                            .clickable { onRetainedChange(!isRetained) },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier =
                            Modifier
                                .width(36.dp)
                                .height(18.dp)
                                .background(
                                    if (isRetained) MqtlinPrimary else DarkOutlineVariant,
                                    CircleShape,
                                ).padding(2.dp),
                    ) {
                        Box(
                            modifier =
                                Modifier
                                    .size(14.dp)
                                    .align(if (isRetained) Alignment.CenterEnd else Alignment.CenterStart)
                                    .background(if (isRetained) DarkBackground else DarkOnSurface, CircleShape),
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isRetained) "ON" else "OFF",
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Medium,
                        color = if (isRetained) DarkOnSurface else DarkOnSurfaceVariant,
                    )
                }
            }
        }

        // Action Button & Feedback Area
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(DarkSurfaceContainerHigh)
                    .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            if (publishError != null) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .background(MqtlinErrorContainer.copy(alpha = 0.25f))
                            .border(BorderStroke(1.dp, MqtlinErrorContainer.copy(alpha = 0.60f)))
                            .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = null,
                        tint = MqtlinError,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = publishError,
                        fontSize = 11.sp,
                        color = MqtlinError,
                    )
                }
            }

            Button(
                onClick = onPublishClick,
                enabled = !isPublishing && canPublish,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                        .pointerHoverIcon(if (!isPublishing && canPublish) PointerIcon.Hand else PointerIcon.Default),
                shape = RoundedCornerShape(4.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = if (showSuccessIndicator) MqtlinSecondary else MqtlinInversePrimary,
                        contentColor = if (showSuccessIndicator) Color(0xFF003824) else Color.White,
                        disabledContainerColor = MqtlinInversePrimary.copy(alpha = 0.5f),
                        disabledContentColor = Color.White.copy(alpha = 0.5f),
                    ),
                elevation =
                    ButtonDefaults.buttonElevation(
                        defaultElevation = 2.dp,
                        pressedElevation = 0.dp,
                    ),
            ) {
                if (isPublishing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = Color.White,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Publishing...", style = UiLabelBold.copy(fontSize = 14.sp, color = Color.White))
                } else if (showSuccessIndicator) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color(0xFF003824),
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Published!",
                        style = UiLabelBold.copy(fontSize = 14.sp, color = Color(0xFF003824)),
                    )
                } else {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Publish Message",
                        style = UiLabelBold.copy(fontSize = 14.sp, color = Color.White),
                    )
                }
            }
        }
    }
}
