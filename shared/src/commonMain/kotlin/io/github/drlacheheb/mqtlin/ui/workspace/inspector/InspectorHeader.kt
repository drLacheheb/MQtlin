package io.github.drlacheheb.mqtlin.ui.workspace.inspector

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.drlacheheb.mqtlin.domain.model.MqttMessage
import io.github.drlacheheb.mqtlin.domain.model.TopicNode
import io.github.drlacheheb.mqtlin.ui.theme.DarkOutlineVariant
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceContainerLow
import io.github.drlacheheb.mqtlin.ui.workspace.inspector.header.TopicMetadataChips
import io.github.drlacheheb.mqtlin.ui.workspace.inspector.header.TopicPathPill

/**
 * Top Inspector Breadcrumb Header orchestrating topic path pill and metadata badges.
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
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkSurfaceContainerLow)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 1. Topic Path Pill with Copy Action
        TopicPathPill(fullTopicPath = selectedNode.fullPath)

        // 2. Metadata Chips Row
        TopicMetadataChips(
            selectedNode = selectedNode,
            currentMessage = currentMessage,
            historyCount = historyCount,
            effectiveHistoryIndex = effectiveHistoryIndex,
            onJumpLive = onJumpLive,
            onDeleteRetainedTopic = onDeleteRetainedTopic,
            onOpenPurgeBranchDialog = onOpenPurgeBranchDialog
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(DarkOutlineVariant)
    )
}
