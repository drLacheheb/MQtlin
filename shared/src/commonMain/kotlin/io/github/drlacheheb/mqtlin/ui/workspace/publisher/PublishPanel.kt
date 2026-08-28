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
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.drlacheheb.mqtlin.ui.components.MqtlinTextField
import io.github.drlacheheb.mqtlin.ui.theme.DarkBackground
import io.github.drlacheheb.mqtlin.ui.theme.DarkOnSurface
import io.github.drlacheheb.mqtlin.ui.theme.DarkOnSurfaceVariant
import io.github.drlacheheb.mqtlin.ui.theme.DarkOutlineVariant
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceContainer
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceContainerHigh
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceDim
import io.github.drlacheheb.mqtlin.ui.theme.HeadlineSm
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinInversePrimary
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinOnPrimaryContainer
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinPrimary
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinPrimaryContainer
import io.github.drlacheheb.mqtlin.ui.theme.UiLabelBold

@Composable
fun PublishPanel(
    selectedTopic: String?,
    modifier: Modifier = Modifier
) {
    var topic by remember(selectedTopic) { mutableStateOf(selectedTopic ?: "/home/living-room/temperature") }
    var payload by remember { mutableStateOf("{\n  \"device_id\": \"LR_TEMP_01\",\n  \"command\": \"CALIBRATE\",\n  \"value\": 1.5\n}") }
    var qos by remember { mutableStateOf(1) }
    var isRetained by remember { mutableStateOf(false) }
    var isMqtt5Expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(DarkSurfaceContainer)
    ) {
        // Header: p-panel_padding border-b border-outline-variant flex items-center gap-2
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = null,
                tint = MqtlinPrimary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Publish Message",
                style = HeadlineSm.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DarkOnSurface)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(DarkOutlineVariant)
        )

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
                            .clickable { }
                            .padding(2.dp)
                    )
                }

                // Textarea Container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(DarkSurfaceDim, RoundedCornerShape(4.dp))
                        .border(1.dp, DarkOutlineVariant, RoundedCornerShape(4.dp))
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
                        cursorBrush = SolidColor(MqtlinPrimary),
                        modifier = Modifier.fillMaxSize()
                    )
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

            // MQTT 5.0 Properties Accordion: border border-outline-variant rounded-lg bg-surface-dim
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isMqtt5Expanded = !isMqtt5Expanded },
                shape = RoundedCornerShape(4.dp),
                color = DarkSurfaceDim,
                border = BorderStroke(1.dp, DarkOutlineVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = DarkOnSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "MQTT 5.0 Properties",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkOnSurfaceVariant
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = DarkOutlineVariant,
                        modifier = Modifier
                            .size(18.dp)
                            .rotate(if (isMqtt5Expanded) 180f else 0f)
                    )
                }
            }
        }

        // Publish Action Button: bg-inverse-primary hover:bg-primary-fixed-dim text-on-primary py-3 rounded-lg (4dp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurfaceContainerHigh)
                .padding(12.dp)
        ) {
            Button(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MqtlinInversePrimary, // #494BD6
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 2.dp,
                    pressedElevation = 0.dp
                )
            ) {
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
