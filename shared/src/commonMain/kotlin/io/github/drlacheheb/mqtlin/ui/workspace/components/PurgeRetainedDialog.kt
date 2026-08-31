package io.github.drlacheheb.mqtlin.ui.workspace.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import io.github.drlacheheb.mqtlin.ui.theme.DarkBorder
import io.github.drlacheheb.mqtlin.ui.theme.DarkOnSurface
import io.github.drlacheheb.mqtlin.ui.theme.DarkOnSurfaceVariant
import io.github.drlacheheb.mqtlin.ui.theme.DarkOutlineVariant
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceContainer
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceDim
import io.github.drlacheheb.mqtlin.ui.theme.MonoCode
import io.github.drlacheheb.mqtlin.ui.theme.UiLabelBold

@Composable
fun PurgeRetainedDialog(
    branchPath: String,
    retainedTopics: List<String>,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = DarkSurfaceContainer,
            border = BorderStroke(1.dp, DarkBorder),
            modifier = Modifier.width(480.dp),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFF7768E).copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, Color(0xFFF7768E).copy(alpha = 0.30f)),
                        modifier = Modifier.size(36.dp),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = null,
                                tint = Color(0xFFF7768E),
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }

                    Column {
                        Text(
                            text = "Purge Retained Topics",
                            style = UiLabelBold.copy(fontSize = 15.sp, color = DarkOnSurface),
                        )
                        Text(
                            text = "Permanent broker deletion",
                            fontSize = 12.sp,
                            color = DarkOnSurfaceVariant,
                        )
                    }
                }

                Text(
                    text =
                        "Are you sure you want to delete ${retainedTopics.size} retained message(s) under " +
                            "/$branchPath/#? This sends 0-byte retained payloads to remove them permanently from the broker.",
                    fontSize = 13.sp,
                    color = DarkOnSurfaceVariant,
                    lineHeight = 18.sp,
                )

                // List of affected topics
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = DarkSurfaceDim,
                    border = BorderStroke(1.dp, DarkOutlineVariant),
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .heightIn(max = 160.dp),
                ) {
                    Column(
                        modifier =
                            Modifier
                                .verticalScroll(rememberScrollState())
                                .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        retainedTopics.forEach { topic ->
                            Text(
                                text = "• /$topic",
                                style = MonoCode.copy(fontSize = 11.sp, color = DarkOnSurface),
                            )
                        }
                    }
                }

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color.Transparent,
                        modifier =
                            Modifier
                                .pointerHoverIcon(PointerIcon.Hand)
                                .clickable { onDismiss() },
                    ) {
                        Text(
                            text = "Cancel",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = DarkOnSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFFF7768E).copy(alpha = 0.20f),
                        border = BorderStroke(1.dp, Color(0xFFF7768E)),
                        modifier =
                            Modifier
                                .pointerHoverIcon(PointerIcon.Hand)
                                .clickable {
                                    onConfirm()
                                    onDismiss()
                                },
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
                                text = "Purge ${retainedTopics.size} Retained",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF7768E),
                            )
                        }
                    }
                }
            }
        }
    }
}
