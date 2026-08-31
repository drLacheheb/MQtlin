package io.github.drlacheheb.mqtlin.ui.workspace.inspector

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
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
import io.github.drlacheheb.mqtlin.domain.model.TopicNode
import io.github.drlacheheb.mqtlin.ui.components.MqtlinSymbols
import io.github.drlacheheb.mqtlin.ui.theme.DarkOnSurface
import io.github.drlacheheb.mqtlin.ui.theme.DarkOnSurfaceVariant
import io.github.drlacheheb.mqtlin.ui.theme.DarkOutlineVariant
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceContainerLowest
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceDim
import io.github.drlacheheb.mqtlin.ui.theme.MonoCode
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinPrimary
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinTertiary
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinTertiaryContainer
import io.github.drlacheheb.mqtlin.ui.theme.UiLabelBold

/**
 * Overview screen for folder/branch nodes that do not contain a leaf message payload.
 */
@Composable
fun BranchOverviewView(
    node: TopicNode,
    retainedDescendantPaths: List<String>,
    onPurgeClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(DarkSurfaceContainerLowest)
                .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Branch Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MqtlinPrimary.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, MqtlinPrimary.copy(alpha = 0.30f)),
                modifier = Modifier.size(44.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = MqtlinSymbols.FolderOpen,
                        contentDescription = null,
                        tint = MqtlinPrimary,
                        modifier = Modifier.size(24.dp),
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "/${node.fullPath}/#",
                    style = UiLabelBold.copy(fontSize = 16.sp, color = DarkOnSurface),
                )
                Text(
                    text = "${node.children.size} direct subtopics • ${retainedDescendantPaths.size} retained messages",
                    fontSize = 12.sp,
                    color = DarkOnSurfaceVariant,
                )
            }
        }

        // Action Card / Retained Banner
        if (retainedDescendantPaths.isNotEmpty()) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color(0xFFF7768E).copy(alpha = 0.10f),
                border = BorderStroke(1.dp, Color(0xFFF7768E).copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = null,
                                tint = Color(0xFFF7768E),
                                modifier = Modifier.size(16.dp),
                            )
                            Text(
                                text = "Retained Messages Stored on Broker",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF7768E),
                            )
                        }
                        Text(
                            text = "There are ${retainedDescendantPaths.size} retained topic(s) under this branch.",
                            fontSize = 12.sp,
                            color = DarkOnSurfaceVariant,
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFFF7768E).copy(alpha = 0.25f),
                        border = BorderStroke(1.dp, Color(0xFFF7768E)),
                        modifier =
                            Modifier
                                .pointerHoverIcon(PointerIcon.Hand)
                                .clickable { onPurgeClicked() },
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = null,
                                tint = Color(0xFFF7768E),
                                modifier = Modifier.size(14.dp),
                            )
                            Text(
                                text = "Purge All ${retainedDescendantPaths.size} Retained",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF7768E),
                            )
                        }
                    }
                }
            }
        }

        // Subtopics List Header
        Text(
            text = "DIRECT SUBTOPICS (${node.children.size})",
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = DarkOnSurfaceVariant,
        )

        // Subtopics List
        Surface(
            shape = RoundedCornerShape(4.dp),
            color = DarkSurfaceDim,
            border = BorderStroke(1.dp, DarkOutlineVariant),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
        ) {
            Column(
                modifier =
                    Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                node.children.forEach { child ->
                    val childRetained = child.collectAllRetainedLeafPaths().size
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Icon(
                                imageVector = if (child.isLeaf) Icons.Default.DataObject else MqtlinSymbols.Folder,
                                contentDescription = null,
                                tint = if (child.isLeaf) MqtlinTertiary else MqtlinPrimary,
                                modifier = Modifier.size(14.dp),
                            )
                            Text(
                                text = child.segment,
                                style = MonoCode.copy(fontSize = 12.sp, color = DarkOnSurface),
                            )
                        }

                        if (childRetained > 0) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MqtlinTertiaryContainer.copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, MqtlinTertiary.copy(alpha = 0.30f)),
                            ) {
                                Text(
                                    text = "$childRetained retained",
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = MqtlinTertiary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
