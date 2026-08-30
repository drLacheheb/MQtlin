package io.github.drlacheheb.mqtlin.ui.workspace.inspector.header

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.drlacheheb.mqtlin.domain.model.MqttMessage
import io.github.drlacheheb.mqtlin.domain.model.TopicNode
import io.github.drlacheheb.mqtlin.ui.theme.DarkOnSurfaceVariant
import io.github.drlacheheb.mqtlin.ui.theme.DarkOutlineVariant
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceDim
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinTertiary
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinTertiaryContainer

/**
 * Metadata chips row displaying QoS, Retained status, Size, Timestamp, and live history navigation shortcuts.
 */
@Composable
fun TopicMetadataChips(
    selectedNode: TopicNode,
    currentMessage: MqttMessage?,
    historyCount: Int,
    effectiveHistoryIndex: Int,
    onJumpLive: () -> Unit,
    onDeleteRetainedTopic: ((String) -> Unit)?,
    onOpenPurgeBranchDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
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
