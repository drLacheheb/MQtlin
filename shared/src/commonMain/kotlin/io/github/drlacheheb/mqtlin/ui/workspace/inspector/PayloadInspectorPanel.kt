package io.github.drlacheheb.mqtlin.ui.workspace.inspector

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.drlacheheb.mqtlin.domain.model.TopicNode
import io.github.drlacheheb.mqtlin.domain.util.JsonUtils
import io.github.drlacheheb.mqtlin.ui.theme.DarkOnSurfaceVariant
import io.github.drlacheheb.mqtlin.ui.theme.DarkOutlineVariant
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceContainerLowest
import io.github.drlacheheb.mqtlin.ui.workspace.components.PurgeRetainedDialog

/**
 * Main Payload Inspector Panel orchestrating topic metadata, message history navigation,
 * and view representations (JSON code viewer, Hex viewer, Diff comparison, and time-series charting).
 */
@Composable
fun PayloadInspectorPanel(
    selectedNode: TopicNode?,
    onDeleteRetainedTopic: ((String) -> Unit)? = null,
    onDeleteRetainedBranch: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf(InspectorTab.JSON) }
    var selectedHistoryIndex by remember(selectedNode?.fullPath) { mutableStateOf(0) }
    var autoScrollToLatest by remember { mutableStateOf(true) }
    var showPurgeBranchDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(DarkSurfaceContainerLowest)
    ) {
        if (selectedNode == null) {
            InspectorEmptyState(modifier = Modifier.fillMaxSize())
        } else {
            val historyList = selectedNode.history.ifEmpty {
                selectedNode.lastMessage?.let { listOf(it) } ?: emptyList()
            }

            val effectiveIndex = if (autoScrollToLatest) 0 else selectedHistoryIndex.coerceIn(0, (historyList.size - 1).coerceAtLeast(0))
            val currentMessage = historyList.getOrNull(effectiveIndex) ?: selectedNode.lastMessage
            val previousMessage = historyList.getOrNull(effectiveIndex + 1)

            val rawPayloadText = currentMessage?.payloadString ?: ""
            val isJson = remember(rawPayloadText) { JsonUtils.isValidJson(rawPayloadText) }
            val formattedJsonText = remember(rawPayloadText, isJson) {
                if (isJson) JsonUtils.format(rawPayloadText) else rawPayloadText
            }

            // 1. Breadcrumb & Metadata Header
            InspectorHeader(
                selectedNode = selectedNode,
                currentMessage = currentMessage,
                historyCount = historyList.size,
                effectiveHistoryIndex = effectiveIndex,
                onJumpLive = {
                    selectedHistoryIndex = 0
                    autoScrollToLatest = true
                },
                onDeleteRetainedTopic = onDeleteRetainedTopic,
                onOpenPurgeBranchDialog = { showPurgeBranchDialog = true }
            )

            // 2. Folder Branch Overview (when node has no payload)
            if (!selectedNode.isLeaf && currentMessage == null) {
                val retainedDescendants = remember(selectedNode) { selectedNode.collectAllRetainedLeafPaths() }
                BranchOverviewView(
                    node = selectedNode,
                    retainedDescendantPaths = retainedDescendants,
                    onPurgeClicked = { showPurgeBranchDialog = true },
                    modifier = Modifier.weight(1f)
                )
            } else {
                // 3. Tab Navigation Bar
                InspectorTabsBar(
                    activeTab = activeTab,
                    onTabSelected = { activeTab = it }
                )

                // 4. Active View Content
                when (activeTab) {
                    InspectorTab.JSON -> {
                        PayloadCodeViewer(
                            text = formattedJsonText,
                            isJson = isJson,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    InspectorTab.HEX -> {
                        HexViewer(
                            payload = currentMessage?.payload ?: ByteArray(0),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    InspectorTab.DIFF -> {
                        DiffView(
                            oldText = previousMessage?.payloadString ?: "",
                            newText = currentMessage?.payloadString ?: "",
                            oldLabel = if (previousMessage != null) "Msg #${historyList.size - effectiveIndex - 1} (${formatInspectorTimestamp(previousMessage.timestamp)})" else "No Earlier Msg",
                            newLabel = "Msg #${historyList.size - effectiveIndex} (${formatInspectorTimestamp(currentMessage?.timestamp ?: 0L)})",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    InspectorTab.CHART -> {
                        TopicChartView(
                            history = historyList,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        )
                    }
                }

                // 5. Bottom History Toolbar
                InspectorHistoryToolbar(
                    historySize = historyList.size,
                    selectedHistoryIndex = selectedHistoryIndex,
                    autoScrollToLatest = autoScrollToLatest,
                    onNavigatePrevious = {
                        autoScrollToLatest = false
                        selectedHistoryIndex = (selectedHistoryIndex + 1).coerceAtMost(historyList.size - 1)
                    },
                    onNavigateNext = {
                        selectedHistoryIndex = (selectedHistoryIndex - 1).coerceAtLeast(0)
                        if (selectedHistoryIndex == 0) autoScrollToLatest = true
                    },
                    onToggleAutoScroll = {
                        autoScrollToLatest = !autoScrollToLatest
                        if (autoScrollToLatest) selectedHistoryIndex = 0
                    }
                )
            }

            // Confirmation Modal for Branch Purging
            if (showPurgeBranchDialog && onDeleteRetainedBranch != null) {
                val retainedDescendants = remember(selectedNode) { selectedNode.collectAllRetainedLeafPaths() }
                PurgeRetainedDialog(
                    branchPath = selectedNode.fullPath,
                    retainedTopics = retainedDescendants,
                    onConfirm = { onDeleteRetainedBranch(selectedNode.fullPath) },
                    onDismiss = { showPurgeBranchDialog = false }
                )
            }
        }
    }
}

@Composable
private fun InspectorEmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
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
}
