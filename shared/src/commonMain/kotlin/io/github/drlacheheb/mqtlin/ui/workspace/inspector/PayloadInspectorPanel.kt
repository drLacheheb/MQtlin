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
import androidx.compose.material.icons.automirrored.filled.NavigateBefore
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
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
import io.github.drlacheheb.mqtlin.ui.components.MqtlinSymbols
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
import io.github.drlacheheb.mqtlin.ui.util.highlightJson
import io.github.drlacheheb.mqtlin.ui.workspace.components.PurgeRetainedDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class InspectorTab { JSON, HEX, DIFF, CHART }

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
    var isCopied by remember { mutableStateOf(false) }
    var showPurgeBranchDialog by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()

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
            val historyList = selectedNode.history.ifEmpty {
                selectedNode.lastMessage?.let { listOf(it) } ?: emptyList()
            }

            // If auto-scroll is enabled, keep viewing the latest message
            val effectiveIndex = if (autoScrollToLatest) 0 else selectedHistoryIndex.coerceIn(0, (historyList.size - 1).coerceAtLeast(0))
            val currentMessage = historyList.getOrNull(effectiveIndex) ?: selectedNode.lastMessage
            val previousMessage = historyList.getOrNull(effectiveIndex + 1)
            val isViewingHistoricalMessage = effectiveIndex > 0

            val rawPayloadText = currentMessage?.payloadString ?: ""
            val isJson = remember(rawPayloadText) { JsonUtils.isValidJson(rawPayloadText) }
            val formattedJsonText = remember(rawPayloadText, isJson) {
                if (isJson) JsonUtils.format(rawPayloadText) else rawPayloadText
            }

            // Display text based on active tab
            val displayText = when (activeTab) {
                InspectorTab.JSON -> formattedJsonText
                InspectorTab.HEX -> HexUtils.formatHexDump(currentMessage?.payload ?: ByteArray(0))
                InspectorTab.DIFF -> ""
                InspectorTab.CHART -> "Real-time topic chart visualization"
            }

            // Breadcrumb Header
            Column(
                modifier = Modifier
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

                // Metadata Chips: QoS, Retained, Size, Timestamp + History Indicator
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

                        // 1-Click Delete Retained Topic Button
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

                    // Recursive Purge Branch Button for Folders/Branches
                    val retainedDescendants = remember(selectedNode) { selectedNode.collectAllRetainedLeafPaths() }
                    if (!selectedNode.isLeaf && retainedDescendants.isNotEmpty() && onDeleteRetainedBranch != null) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFF7768E).copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, Color(0xFFF7768E).copy(alpha = 0.40f)),
                            modifier = Modifier
                                .pointerHoverIcon(PointerIcon.Hand)
                                .clickable { showPurgeBranchDialog = true }
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
                        label = formatTimestamp(currentMessage?.timestamp ?: System.currentTimeMillis()),
                        icon = Icons.Default.Schedule
                    )

                    if (isViewingHistoricalMessage) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFF332014),
                            border = BorderStroke(1.dp, Color(0xFFFF9E64).copy(alpha = 0.5f)),
                            modifier = Modifier
                                .pointerHoverIcon(PointerIcon.Hand)
                                .clickable {
                                    selectedHistoryIndex = 0
                                    autoScrollToLatest = true
                                }
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
                                    text = "History (#${historyList.size - effectiveIndex}) — Jump Live",
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

            if (!selectedNode.isLeaf && currentMessage == null) {
                val retainedDescendants = remember(selectedNode) { selectedNode.collectAllRetainedLeafPaths() }
                BranchOverviewView(
                    node = selectedNode,
                    retainedDescendantPaths = retainedDescendants,
                    onPurgeClicked = { showPurgeBranchDialog = true },
                    modifier = Modifier.weight(1f)
                )
            } else {
                // Inspector Tabs Bar
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
                        label = "Diff",
                        isSelected = activeTab == InspectorTab.DIFF,
                        icon = Icons.Default.CompareArrows,
                        onClick = { activeTab = InspectorTab.DIFF }
                    )
                    TabItem(
                        label = "Hex",
                        isSelected = activeTab == InspectorTab.HEX,
                        onClick = { activeTab = InspectorTab.HEX }
                    )
                    TabItem(
                        label = "Chart",
                        isSelected = activeTab == InspectorTab.CHART,
                        icon = Icons.AutoMirrored.Filled.ShowChart,
                        onClick = { activeTab = InspectorTab.CHART }
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(DarkOutlineVariant)
                )

                // Main Content Area
                when (activeTab) {
                    InspectorTab.DIFF -> {
                        DiffView(
                            oldText = previousMessage?.payloadString ?: "",
                            newText = currentMessage?.payloadString ?: "",
                            oldLabel = if (previousMessage != null) "Msg #${historyList.size - effectiveIndex - 1} (${formatTimestamp(previousMessage.timestamp)})" else "No Earlier Msg",
                            newLabel = "Msg #${historyList.size - effectiveIndex} (${formatTimestamp(currentMessage?.timestamp ?: 0L)})",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    InspectorTab.HEX -> {
                        HexViewer(
                            payload = currentMessage?.payload ?: ByteArray(0),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    InspectorTab.JSON -> {
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
                            // Line Numbers Gutter
                            Column(
                                modifier = Modifier
                                    .width(44.dp)
                                    .fillMaxHeight()
                                    .background(DarkSurfaceDim)
                                    .verticalScroll(verticalScrollState)
                                    .padding(vertical = 16.dp, horizontal = 4.dp),
                                horizontalAlignment = Alignment.End
                            ) {
                                for (i in 1..lineCount) {
                                    Text(
                                        text = "$i",
                                        style = MonoCode.copy(fontSize = 13.sp, lineHeight = 20.sp, color = DarkOutlineVariant)
                                    )
                                }
                            }

                            // Gutter Divider
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .fillMaxHeight()
                                    .background(DarkOutlineVariant)
                            )

                            // Code Area
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .verticalScroll(verticalScrollState)
                                    .horizontalScroll(horizontalScrollState)
                                    .padding(16.dp)
                            ) {
                                if (isJson) {
                                    Text(
                                        text = highlightJson(displayText),
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
                            text = "History: ${historyList.size} messages",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = DarkOnSurfaceVariant
                        )

                        if (historyList.size > 1) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = {
                                        autoScrollToLatest = false
                                        selectedHistoryIndex = (selectedHistoryIndex + 1).coerceAtMost(historyList.size - 1)
                                    },
                                    enabled = selectedHistoryIndex < historyList.size - 1,
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.NavigateBefore,
                                        contentDescription = "Previous message",
                                        tint = if (selectedHistoryIndex < historyList.size - 1) DarkOnSurface else DarkOutlineVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                Text(
                                    text = "${selectedHistoryIndex + 1}/${historyList.size}",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (selectedHistoryIndex == 0) MqtlinPrimary else Color(0xFFFF9E64)
                                )

                                IconButton(
                                    onClick = {
                                        selectedHistoryIndex = (selectedHistoryIndex - 1).coerceAtLeast(0)
                                        if (selectedHistoryIndex == 0) autoScrollToLatest = true
                                    },
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
                            .clickable {
                                autoScrollToLatest = !autoScrollToLatest
                                if (autoScrollToLatest) selectedHistoryIndex = 0
                            }
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
private fun BranchOverviewView(
    node: TopicNode,
    retainedDescendantPaths: List<String>,
    onPurgeClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkSurfaceContainerLowest)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Branch Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MqtlinPrimary.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, MqtlinPrimary.copy(alpha = 0.30f)),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = MqtlinSymbols.FolderOpen,
                        contentDescription = null,
                        tint = MqtlinPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "/${node.fullPath}/#",
                    style = UiLabelBold.copy(fontSize = 16.sp, color = DarkOnSurface)
                )
                Text(
                    text = "${node.children.size} direct subtopics • ${retainedDescendantPaths.size} retained messages",
                    fontSize = 12.sp,
                    color = DarkOnSurfaceVariant
                )
            }
        }

        // Action Card / Retained Banner
        if (retainedDescendantPaths.isNotEmpty()) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color(0xFFF7768E).copy(alpha = 0.10f),
                border = BorderStroke(1.dp, Color(0xFFF7768E).copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = null,
                                tint = Color(0xFFF7768E),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Retained Messages Stored on Broker",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF7768E)
                            )
                        }
                        Text(
                            text = "There are ${retainedDescendantPaths.size} retained topic(s) under this branch.",
                            fontSize = 12.sp,
                            color = DarkOnSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFFF7768E).copy(alpha = 0.25f),
                        border = BorderStroke(1.dp, Color(0xFFF7768E)),
                        modifier = Modifier
                            .pointerHoverIcon(PointerIcon.Hand)
                            .clickable { onPurgeClicked() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = null,
                                tint = Color(0xFFF7768E),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Purge All ${retainedDescendantPaths.size} Retained",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF7768E)
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
            color = DarkOnSurfaceVariant
        )

        // Subtopics List
        Surface(
            shape = RoundedCornerShape(4.dp),
            color = DarkSurfaceDim,
            border = BorderStroke(1.dp, DarkOutlineVariant),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                node.children.forEach { child ->
                    val childRetained = child.collectAllRetainedLeafPaths().size
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = if (child.isLeaf) Icons.Default.DataObject else MqtlinSymbols.Folder,
                                contentDescription = null,
                                tint = if (child.isLeaf) MqtlinTertiary else MqtlinPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = child.segment,
                                style = MonoCode.copy(fontSize = 12.sp, color = DarkOnSurface)
                            )
                        }

                        if (childRetained > 0) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MqtlinTertiaryContainer.copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, MqtlinTertiary.copy(alpha = 0.30f))
                            ) {
                                Text(
                                    text = "$childRetained retained",
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = MqtlinTertiary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
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
            .pointerHoverIcon(PointerIcon.Hand)
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
