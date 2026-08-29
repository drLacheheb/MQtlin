package io.github.drlacheheb.mqtlin.ui.workspace.publisher

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.drlacheheb.mqtlin.domain.util.JsonUtils
import io.github.drlacheheb.mqtlin.ui.components.MqtlinTextField
import io.github.drlacheheb.mqtlin.ui.util.JsonVisualTransformation
import io.github.drlacheheb.mqtlin.ui.theme.DarkBackground
import io.github.drlacheheb.mqtlin.ui.theme.DarkOnSurface
import io.github.drlacheheb.mqtlin.ui.theme.DarkOnSurfaceVariant
import io.github.drlacheheb.mqtlin.ui.theme.DarkOutlineVariant
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceContainer
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceContainerHigh
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceDim
import io.github.drlacheheb.mqtlin.ui.theme.HeadlineSm
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinError
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinErrorContainer
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinInversePrimary
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinOnPrimaryContainer
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinPrimary
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinPrimaryContainer
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinSecondary
import io.github.drlacheheb.mqtlin.ui.theme.UiLabelBold
import kotlinx.coroutines.delay

@Composable
fun PublishPanel(
    selectedTopic: String?,
    isPublishing: Boolean = false,
    publishError: String? = null,
    onPublishMessage: (topic: String, payload: String, qos: Int, isRetained: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var topic by remember { mutableStateOf(selectedTopic ?: "home/living-room/temperature") }
    var payload by remember { mutableStateOf("{\n  \"device_id\": \"LR_TEMP_01\",\n  \"command\": \"CALIBRATE\",\n  \"value\": 1.5\n}") }
    var qos by remember { mutableStateOf(1) }
    var isRetained by remember { mutableStateOf(false) }
    var showSuccessIndicator by remember { mutableStateOf(false) }

    // Synchronize topic when selectedTopic from tree changes
    LaunchedEffect(selectedTopic) {
        if (!selectedTopic.isNullOrBlank()) {
            topic = selectedTopic
        }
    }

    // Auto-dismiss success indicator after 2.5 seconds
    LaunchedEffect(showSuccessIndicator) {
        if (showSuccessIndicator) {
            delay(2500)
            showSuccessIndicator = false
        }
    }

    // Real-time JSON validation
    val jsonSyntaxError = remember(payload) { JsonUtils.validate(payload) }

    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(DarkSurfaceContainer)
    ) {
        // Form Content: p-panel_padding flex flex-col gap-4
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Topic Field: font-ui-label-bold text-[12px] text-on-surface-variant
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text = "Topic", style = UiLabelBold.copy(fontSize = 12.sp, color = DarkOnSurfaceVariant))
                MqtlinTextField(
                    value = topic,
                    onValueChange = { topic = it },
                    height = 36.dp,
                    isMonospace = true,
                    backgroundColor = DarkSurfaceDim
                )
            }

            // Payload Field: Payload (JSON) + Format button
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Payload (JSON)",
                        style = UiLabelBold.copy(fontSize = 12.sp, color = DarkOnSurfaceVariant)
                    )
                    Text(
                        text = "Format",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MqtlinPrimary,
                        modifier = Modifier
                            .pointerHoverIcon(PointerIcon.Hand)
                            .clickable {
                                payload = JsonUtils.format(payload)
                            }
                            .padding(2.dp)
                    )
                }

                // Textarea Container (with red error border if syntax error)
                val borderColor = if (jsonSyntaxError != null) MqtlinErrorContainer else DarkOutlineVariant
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(DarkSurfaceDim, RoundedCornerShape(4.dp))
                        .border(1.dp, borderColor, RoundedCornerShape(4.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(10.dp)
                    ) {
                        BasicTextField(
                            value = payload,
                            onValueChange = { payload = it },
                            textStyle = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp,
                                color = DarkOnSurface,
                                lineHeight = 18.sp
                            ),
                            visualTransformation = remember { JsonVisualTransformation() },
                            cursorBrush = SolidColor(MqtlinPrimary),
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerHoverIcon(PointerIcon.Text)
                        )
                    }

                    // Validation Error Banner (HTML line 393)
                    if (jsonSyntaxError != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MqtlinErrorContainer.copy(alpha = 0.20f))
                                .border(BorderStroke(1.dp, MqtlinErrorContainer.copy(alpha = 0.50f)))
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = "Syntax Error",
                                tint = MqtlinError,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Syntax Error: $jsonSyntaxError",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = MqtlinError,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            // Publish Settings: QoS & Retain in 2-column grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // QoS Segmented Pill Group
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "QoS",
                        style = UiLabelBold.copy(fontSize = 12.sp, color = DarkOnSurfaceVariant)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(32.dp)
                            .background(DarkSurfaceDim, RoundedCornerShape(4.dp))
                            .border(1.dp, DarkOutlineVariant, RoundedCornerShape(4.dp))
                    ) {
                        for (q in 0..2) {
                            val isSelected = qos == q
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .pointerHoverIcon(PointerIcon.Hand)
                                    .clickable { qos = q }
                                    .background(if (isSelected) MqtlinPrimaryContainer else Color.Transparent),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = q.toString(),
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MqtlinOnPrimaryContainer else DarkOnSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Retain Switch
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Retain",
                        style = UiLabelBold.copy(fontSize = 12.sp, color = DarkOnSurfaceVariant)
                    )
                    Row(
                        modifier = Modifier
                            .height(32.dp)
                            .pointerHoverIcon(PointerIcon.Hand)
                            .clickable { isRetained = !isRetained },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .width(36.dp)
                                .height(18.dp)
                                .background(
                                    if (isRetained) MqtlinPrimary else DarkOutlineVariant,
                                    CircleShape
                                )
                                .padding(2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .align(if (isRetained) Alignment.CenterEnd else Alignment.CenterStart)
                                    .background(if (isRetained) DarkBackground else DarkOnSurface, CircleShape)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isRetained) "ON" else "OFF",
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium,
                            color = if (isRetained) DarkOnSurface else DarkOnSurfaceVariant
                        )
                    }
                }
            }
        }

        // Publish Action Button Area
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurfaceContainerHigh)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Error banner if publish failed
            if (publishError != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MqtlinErrorContainer.copy(alpha = 0.25f))
                        .border(BorderStroke(1.dp, MqtlinErrorContainer.copy(alpha = 0.60f)))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = null,
                        tint = MqtlinError,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = publishError,
                        fontSize = 11.sp,
                        color = MqtlinError
                    )
                }
            }

            Button(
                onClick = {
                    val sanitizedTopic = topic.trim().removePrefix("/")
                    onPublishMessage(sanitizedTopic, payload, qos, isRetained)
                    showSuccessIndicator = true
                },
                enabled = !isPublishing && topic.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
                    .pointerHoverIcon(if (!isPublishing && topic.isNotBlank()) PointerIcon.Hand else PointerIcon.Default),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (showSuccessIndicator) MqtlinSecondary else MqtlinInversePrimary,
                    contentColor = if (showSuccessIndicator) Color(0xFF003824) else Color.White,
                    disabledContainerColor = MqtlinInversePrimary.copy(alpha = 0.5f),
                    disabledContentColor = Color.White.copy(alpha = 0.5f)
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 2.dp,
                    pressedElevation = 0.dp
                )
            ) {
                if (isPublishing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Publishing...", style = UiLabelBold.copy(fontSize = 14.sp, color = Color.White))
                } else if (showSuccessIndicator) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color(0xFF003824),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Published!",
                        style = UiLabelBold.copy(fontSize = 14.sp, color = Color(0xFF003824))
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Publish Message",
                        style = UiLabelBold.copy(fontSize = 14.sp, color = Color.White)
                    )
                }
            }
        }
    }
}
