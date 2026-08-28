package io.github.drlacheheb.mqtlin.ui.workspace.inspector

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import io.github.drlacheheb.mqtlin.domain.util.HexUtils
import io.github.drlacheheb.mqtlin.domain.util.JsonUtils
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
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinTertiaryContainer
import io.github.drlacheheb.mqtlin.ui.theme.SyntaxKey
import io.github.drlacheheb.mqtlin.ui.theme.SyntaxNumber
import io.github.drlacheheb.mqtlin.ui.theme.SyntaxPunctuation
import io.github.drlacheheb.mqtlin.ui.theme.SyntaxString
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
            .background(DarkSurfaceContainerLowest) // HTML line 302: bg-surface-container-lowest (#0E0E11)
    ) {
        if (selectedNode == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DataObject,
                        contentDescription = null,
                        tint = DarkOutlineVariant,
                        modifier = Modifier.size(36.dp)
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
            val rawPayloadText = message?.payloadString ?: ""
            val formattedJsonText = remember(rawPayloadText) { JsonUtils.format(rawPayloadText) }

            // Display text based on active tab
            val displayText = when (activeTab) {
                InspectorTab.JSON -> formattedJsonText
                InspectorTab.RAW -> rawPayloadText
                InspectorTab.HEX -> HexUtils.formatHexDump(message?.payload ?: ByteArray(0))
                InspectorTab.CHART -> "Real-time topic chart visualization"
            }

            // Breadcrumb Header: p-panel_padding border-b border-outline-variant bg-surface-container-low (#1B1B1E)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurfaceContainerLow)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Topic Path Pill: bg-surface-dim (#131316) border border-outline-variant px-3 py-1.5 rounded-lg
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
                            color = MqtlinTertiaryContainer.copy(alpha = 0.15f),
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
                    .height(40.dp)
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

            // Editor / Viewer Area with Synchronized Scrolling
            val verticalScrollState = rememberScrollState()
            val horizontalScrollState = rememberScrollState()
            val lines = displayText.lines()
            val lineCount = if (lines.isEmpty()) 1 else lines.size

            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(DarkSurfaceContainerLowest)
            ) {
                // Line Numbers Gutter: bg-surface-dim (#131316) text-outline (#908FA0)
                Column(
                    modifier = Modifier
                        .width(44.dp)
                        .fillMaxHeight()
                        .background(DarkSurfaceDim)
                        .verticalScroll(verticalScrollState)
                        .padding(top = 16.dp, bottom = 16.dp, end = 8.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    for (i in 1..lineCount) {
                        Text(
                            text = i.toString(),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            color = DarkOutline,
                            lineHeight = 20.sp
                        )
                    }
                }

                // Gutter Right Divider
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(DarkOutlineVariant)
                )

                // Code Area: scrolls vertically & horizontally
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .verticalScroll(verticalScrollState)
                        .horizontalScroll(horizontalScrollState)
                        .padding(16.dp)
                ) {
                    if (activeTab == InspectorTab.JSON) {
                        Text(
                            text = buildHighlightedJson(displayText),
                            style = MonoCode.copy(fontSize = 13.sp, lineHeight = 20.sp)
                        )
                    } else {
                        Text(
                            text = displayText,
                            style = MonoCode.copy(fontSize = 13.sp, lineHeight = 20.sp, color = DarkOnSurface)
                        )
                    }
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
            .fillMaxHeight()
            .width(IntrinsicSize.Max)
            .clickable(onClick = onClick)
            .background(if (isSelected) DarkSurfaceContainerLow else Color.Transparent),
        verticalArrangement = Arrangement.Bottom
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = if (isSelected) UiLabelBold.copy(fontSize = 14.sp, color = MqtlinPrimary) else UiLabelReg.copy(fontSize = 14.sp, color = DarkOnSurfaceVariant),
                maxLines = 1
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
                    .fillMaxWidth()
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
        var cursor = 0
        val len = text.length

        while (cursor < len) {
            val ch = text[cursor]

            if (ch == '"') {
                val start = cursor
                cursor++
                while (cursor < len && text[cursor] != '"') {
                    if (text[cursor] == '\\' && cursor + 1 < len) cursor++
                    cursor++
                }
                if (cursor < len) cursor++ // consume closing quote
                val strVal = text.substring(start, cursor)

                // Check if key (followed by colon)
                var lookAhead = cursor
                while (lookAhead < len && text[lookAhead].isWhitespace()) lookAhead++
                val isKey = lookAhead < len && text[lookAhead] == ':'

                withStyle(SpanStyle(color = if (isKey) SyntaxKey else SyntaxString)) {
                    append(strVal)
                }
            } else if (ch in "{}[],:") {
                withStyle(SpanStyle(color = SyntaxPunctuation)) {
                    append(ch)
                }
                cursor++
            } else if (ch.isDigit() || ch == '-' || text.startsWith("true", cursor) || text.startsWith("false", cursor) || text.startsWith("null", cursor)) {
                val start = cursor
                if (text.startsWith("true", cursor)) {
                    cursor += 4
                } else if (text.startsWith("false", cursor)) {
                    cursor += 5
                } else if (text.startsWith("null", cursor)) {
                    cursor += 4
                } else {
                    while (cursor < len && (text[cursor].isDigit() || text[cursor] in ".eE+-")) cursor++
                }
                val numVal = text.substring(start, cursor)
                withStyle(SpanStyle(color = SyntaxNumber)) {
                    append(numVal)
                }
            } else {
                append(ch)
                cursor++
            }
        }
    }
}
