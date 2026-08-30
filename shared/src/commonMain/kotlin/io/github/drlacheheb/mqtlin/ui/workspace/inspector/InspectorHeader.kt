package io.github.drlacheheb.mqtlin.ui.workspace.inspector

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.drlacheheb.mqtlin.domain.model.MqttMessage
import io.github.drlacheheb.mqtlin.domain.model.TopicNode
import io.github.drlacheheb.mqtlin.ui.theme.DarkOnSurface
import io.github.drlacheheb.mqtlin.ui.theme.DarkOnSurfaceVariant
import io.github.drlacheheb.mqtlin.ui.theme.DarkOutlineVariant
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceContainerLow
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceDim
import io.github.drlacheheb.mqtlin.ui.theme.MonoCode
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinPrimary
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinSecondary
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinTertiary
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinTertiaryContainer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Top Inspector Breadcrumb Header displaying topic path, QoS, Retained status, size, and timestamp.
 */
@Composable
fun InspectorHeader(
    selectedNode: TopicNode,
    currentMessage: MqttMessage?,
    historyCount: Int,
    effectiveHistoryIndex: Int,
    onJumpLive: () -> Unit,
    onDeleteRetainedTopic: ((String) -> Unit)? = null,
    onOpenPurgeBranchDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()
    var isCopied by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkSurfaceContainerLow)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Topic Path Pill
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .pointerHoverIcon(PointerIcon.Hand)
                        .clickable {
                            clipboardManager.setText(AnnotatedString(selectedNode.fullPath))
                            isCopied = true
                            coroutineScope.launch {
                                delay(1500)
                                isCopied = false
                            }
                        }
                ) {
                    if (isCopied) {
                        Text(
                            text = "Copied!",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = MqtlinSecondary
                        )
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Copied",
                            tint = MqtlinSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Topic Path",
                            tint = DarkOutlineVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Metadata Chips Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MetadataChip(label = "QoS ${currentMessage?.qos ?: 0}")

            if (currentMessage?.isRetained == true) {
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

                if (onDeleteRetainedTopic != null) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFFF7768E).copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, Color(0xFFF7768E).copy(alpha = 0.35f)),
                        modifier = Modifier
                            .pointerHoverIcon(PointerIcon.Hand)
                            .clickable { onDeleteRetainedTopic(selectedNode.fullPath) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Retained Message",
                                tint = Color(0xFFF7768E),
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "Delete Retained",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFFF7768E)
                            )
                        }
                    }
                }
            }

            val retainedDescendants = remember(selectedNode) { selectedNode.collectAllRetainedLeafPaths() }
            if (!selectedNode.isLeaf && retainedDescendants.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFFF7768E).copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, Color(0xFFF7768E).copy(alpha = 0.40f)),
                    modifier = Modifier
                        .pointerHoverIcon(PointerIcon.Hand)
                        .clickable(onClick = onOpenPurgeBranchDialog)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Purge Branch Retained",
                            tint = Color(0xFFF7768E),
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "Purge ${retainedDescendants.size} Retained",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF7768E)
                        )
                    }
                }
            }

            MetadataChip(label = "Size: ${currentMessage?.payload?.size ?: 0} B")

            MetadataChip(
                label = formatInspectorTimestamp(currentMessage?.timestamp ?: System.currentTimeMillis()),
                icon = Icons.Default.Schedule
            )

            if (effectiveHistoryIndex > 0) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFF332014),
                    border = BorderStroke(1.dp, Color(0xFFFF9E64).copy(alpha = 0.5f)),
                    modifier = Modifier
                        .pointerHoverIcon(PointerIcon.Hand)
                        .clickable(onClick = onJumpLive)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FiberManualRecord,
                            contentDescription = null,
                            tint = Color(0xFFFF9E64),
                            modifier = Modifier.size(10.dp)
                        )
                        Text(
                            text = "History (#${historyCount - effectiveHistoryIndex}) — Jump Live",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFFF9E64)
                        )
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(DarkOutlineVariant)
    )
}

@Composable
private fun MetadataChip(
    label: String,
    icon: ImageVector? = null
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

fun formatInspectorTimestamp(epochMs: Long): String {
    val seconds = (epochMs / 1000) % 86400
    val hours = (seconds / 3600) % 24
    val minutes = (seconds / 60) % 60
    val secs = seconds % 60
    val millis = epochMs % 1000
    return "${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}.${millis.toString().padStart(3, '0')}"
}
