package io.github.drlacheheb.mqtlin.ui.workspace.inspector

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateBefore
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import io.github.drlacheheb.mqtlin.ui.theme.DarkOnSurface
import io.github.drlacheheb.mqtlin.ui.theme.DarkOnSurfaceVariant
import io.github.drlacheheb.mqtlin.ui.theme.DarkOutlineVariant
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceContainer
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceContainerHigh
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinPrimary

/**
 * Bottom history navigation toolbar with message counter, historical message stepping, and live auto-scroll toggle.
 */
@Composable
fun InspectorHistoryToolbar(
    historySize: Int,
    selectedHistoryIndex: Int,
    autoScrollToLatest: Boolean,
    onNavigatePrevious: () -> Unit,
    onNavigateNext: () -> Unit,
    onToggleAutoScroll: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(DarkOutlineVariant)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .background(DarkSurfaceContainer)
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "History: $historySize messages",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = DarkOnSurfaceVariant
                )

                if (historySize > 1) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onNavigatePrevious,
                            enabled = selectedHistoryIndex < historySize - 1,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.NavigateBefore,
                                contentDescription = "Previous message",
                                tint = if (selectedHistoryIndex < historySize - 1) DarkOnSurface else DarkOutlineVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Text(
                            text = "${selectedHistoryIndex + 1}/$historySize",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = if (selectedHistoryIndex == 0) MqtlinPrimary else Color(0xFFFF9E64)
                        )

                        IconButton(
                            onClick = onNavigateNext,
                            enabled = selectedHistoryIndex > 0,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.NavigateNext,
                                contentDescription = "Next message",
                                tint = if (selectedHistoryIndex > 0) DarkOnSurface else DarkOutlineVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Surface(
                shape = RoundedCornerShape(4.dp),
                color = if (autoScrollToLatest) MqtlinPrimary.copy(alpha = 0.15f) else DarkSurfaceContainerHigh,
                border = BorderStroke(1.dp, if (autoScrollToLatest) MqtlinPrimary.copy(alpha = 0.40f) else DarkOutlineVariant),
                modifier = Modifier
                    .pointerHoverIcon(PointerIcon.Hand)
                    .clickable(onClick = onToggleAutoScroll)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Auto Scroll",
                        tint = if (autoScrollToLatest) MqtlinPrimary else DarkOnSurfaceVariant,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = if (autoScrollToLatest) "Live Auto-Scroll" else "Paused",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Medium,
                        color = if (autoScrollToLatest) MqtlinPrimary else DarkOnSurfaceVariant
                    )
                }
            }
        }
    }
}
