package io.github.drlacheheb.mqtlin.ui.workspace.inspector

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.drlacheheb.mqtlin.domain.model.TopicNode
import io.github.drlacheheb.mqtlin.ui.theme.DarkBackground
import io.github.drlacheheb.mqtlin.ui.theme.DarkOnSurface
import io.github.drlacheheb.mqtlin.ui.theme.DarkOnSurfaceVariant
import io.github.drlacheheb.mqtlin.ui.theme.DarkOutline
import io.github.drlacheheb.mqtlin.ui.theme.DarkOutlineVariant
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceBright
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceContainer
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceContainerHigh
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceContainerLow
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceContainerLowest
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceDim
import io.github.drlacheheb.mqtlin.ui.theme.MonoCode
import io.github.drlacheheb.mqtlin.ui.theme.MonoTopic
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinPrimary
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinSecondary
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinTertiary
import io.github.drlacheheb.mqtlin.ui.theme.UiLabelBold
import io.github.drlacheheb.mqtlin.ui.theme.UiLabelReg

enum class InspectorTab { JSON, RAW, HEX, CHART }

@Composable
fun PayloadInspectorPanel(
    selectedNode: TopicNode?,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf(InspectorTab.JSON) }

    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(DarkSurfaceContainerLowest)
    ) {
        if (selectedNode == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DataObject,
                        contentDescription = null,
                        tint = DarkOutlineVariant,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = "Select a topic to inspect payload",
                        fontSize = 13.sp,
                        color = DarkOnSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }
        } else {
            val message = selectedNode.lastMessage
            val payloadText = message?.payloadString ?: ""

            // Breadcrumb Header: p-panel_padding border-b border-outline-variant bg-surface-container-low
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurfaceContainerLow)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Topic Path Pill: bg-surface-dim border border-outline-variant px-3 py-1.5 rounded-lg
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(4.dp),
                    color = DarkSurfaceDim,
                    border = BorderStroke(1.dp, DarkOutlineVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DataObject,
                            contentDescription = null,
                            tint = MqtlinPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "/${selectedNode.fullPath}",
                            style = MonoCode.copy(fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkOnSurface),
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Topic Path",
                            tint = DarkOutlineVariant,
                            modifier = Modifier
                                .size(16.dp)
                                .clickable { }
                        )
                    }
                }

                // Metadata Chips: QoS, Retained, Size, Timestamp
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MetadataChip(label = "QoS ${message?.qos ?: 0}")

                    if (message?.isRetained == true) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MqtlinTertiary.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, MqtlinTertiary.copy(alpha = 0.30f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Save,
                                    contentDescription = null,
                                    tint = MqtlinTertiary,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "Retained",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Medium,
                                    color = MqtlinTertiary
                                )
                            }
                        }
                    }

                    MetadataChip(label = "Size: ${message?.payload?.size ?: 0} B")

                    MetadataChip(
                        label = formatTimestamp(message?.timestamp ?: System.currentTimeMillis()),
                        icon = Icons.Default.Schedule
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(DarkOutlineVariant)
            )

            // Inspector Tabs Bar: bg-surface-container border-b border-outline-variant
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurfaceContainer)
            ) {
                TabItem(
                    label = "JSON",
                    isSelected = activeTab == InspectorTab.JSON,
                    onClick = { activeTab = InspectorTab.JSON }
                )
                TabItem(
                    label = "Raw",
                    isSelected = activeTab == InspectorTab.RAW,
                    onClick = { activeTab = InspectorTab.RAW }
                )
                TabItem(
                    label = "Hex",
                    isSelected = activeTab == InspectorTab.HEX,
                    onClick = { activeTab = InspectorTab.HEX }
                )
                TabItem(
                    label = "Chart",
                    isSelected = activeTab == InspectorTab.CHART,
                    icon = Icons.Default.ShowChart,
                    onClick = { activeTab = InspectorTab.CHART }
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(DarkOutlineVariant)
            )

            // Editor / Viewer Area: bg-surface-container-lowest
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(DarkSurfaceContainerLowest)
            ) {
                val lines = payloadText.lines()
                val lineCount = if (lines.isEmpty()) 1 else lines.size

                // Left Gutter Line Numbers: w-10 bg-surface-dim border-r border-outline-variant
                Column(
                    modifier = Modifier
                        .width(40.dp)
                        .fillMaxHeight()
                        .background(DarkSurfaceDim)
                        .padding(vertical = 12.dp, horizontal = 6.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    for (i in 1..lineCount) {
                        Text(
                            text = i.toString(),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            color = DarkOutline
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(DarkOutlineVariant)
                )

                // Code Area
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = buildHighlightedJson(payloadText),
                        style = MonoCode.copy(fontSize = 13.sp, lineHeight = 20.sp)
                    )
                }
            }

            // History Toolbar: p-2 border-t border-outline-variant bg-surface-container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(DarkOutlineVariant)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurfaceContainer)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {}, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.NavigateBefore,
                            contentDescription = "Previous Message",
                            tint = DarkOutline,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = "Msg ${selectedNode.messageCount} of ${selectedNode.messageCount}",
                        style = MonoTopic.copy(fontSize = 12.sp, color = DarkOnSurfaceVariant),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    IconButton(onClick = {}, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.NavigateNext,
                            contentDescription = "Next Message",
                            tint = DarkOutline,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = DarkSurfaceContainerHigh,
                    border = BorderStroke(1.dp, DarkOutlineVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Auto Scroll",
                            tint = DarkOnSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Auto-Scroll",
                            fontSize = 11.sp,
                            color = DarkOnSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MetadataChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = DarkSurfaceDim,
        border = BorderStroke(1.dp, DarkOutlineVariant)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = DarkOnSurfaceVariant,
                    modifier = Modifier.size(12.dp)
                )
            }
            Text(
                text = label,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = DarkOnSurfaceVariant
            )
        }
    }
}

@Composable
private fun TabItem(
    label: String,
    isSelected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .background(if (isSelected) DarkSurfaceContainerLow else Color.Transparent)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label,
                style = if (isSelected) UiLabelBold.copy(fontSize = 14.sp, color = MqtlinPrimary) else UiLabelReg.copy(fontSize = 14.sp, color = DarkOnSurfaceVariant)
            )
            if (icon != null) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) MqtlinPrimary else DarkOnSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
        if (isSelected) {
            Box(
                modifier = Modifier
                    .padding(top = 6.dp)
                    .width(32.dp)
                    .height(2.dp)
                    .background(MqtlinPrimary)
            )
        }
    }
}

private fun formatTimestamp(epochMs: Long): String {
    val seconds = (epochMs / 1000) % 86400
    val hours = (seconds / 3600) % 24
    val minutes = (seconds / 60) % 60
    val secs = seconds % 60
    val millis = epochMs % 1000
    return "${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}.${millis.toString().padStart(3, '0')}"
}

private fun buildHighlightedJson(text: String): androidx.compose.ui.text.AnnotatedString {
    return buildAnnotatedString {
        var inQuotes = false
        var isKey = false
        val currentToken = StringBuilder()

        val keyColor = Color(0xFFC7C4D7) // .sh-key
        val strColor = Color(0xFF4EDEA3) // .sh-str
        val numColor = Color(0xFFFFB95F) // .sh-num
        val puncColor = Color(0xFF908FA0) // .sh-punc

        for (i in text.indices) {
            val c = text[i]
            when {
                c == '"' -> {
                    if (inQuotes) {
                        currentToken.append(c)
                        val style = if (isKey) keyColor else strColor
                        withStyle(SpanStyle(color = style)) {
                            append(currentToken.toString())
                        }
                        currentToken.clear()
                        inQuotes = false
                        isKey = false
                    } else {
                        inQuotes = true
                        isKey = text.substring(i).contains(':') && !text.substring(0, i).endsWith(":")
                        currentToken.append(c)
                    }
                }
                inQuotes -> currentToken.append(c)
                c in "{}[],:" -> {
                    withStyle(SpanStyle(color = puncColor)) {
                        append(c)
                    }
                }
                c.isDigit() || c == '.' || c == '-' -> {
                    withStyle(SpanStyle(color = numColor)) {
                        append(c)
                    }
                }
                else -> append(c)
            }
        }
    }
}
