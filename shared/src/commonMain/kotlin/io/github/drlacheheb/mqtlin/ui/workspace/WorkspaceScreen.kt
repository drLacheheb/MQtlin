package io.github.drlacheheb.mqtlin.ui.workspace

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import io.github.drlacheheb.mqtlin.ui.theme.DarkOutlineVariant
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceDim
import io.github.drlacheheb.mqtlin.ui.workspace.components.TopNavBar
import io.github.drlacheheb.mqtlin.ui.workspace.components.WorkspaceFooter
import io.github.drlacheheb.mqtlin.ui.workspace.inspector.PayloadInspectorPanel
import io.github.drlacheheb.mqtlin.ui.workspace.publisher.PublishPanel
import io.github.drlacheheb.mqtlin.ui.workspace.tree.TopicTreePanel

@Composable
fun WorkspaceScreen(
    component: WorkspaceComponent,
    modifier: Modifier = Modifier
) {
    val state by component.state.subscribeAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkSurfaceDim)
    ) {
        // 1. Top Navigation Bar (56dp)
        TopNavBar(
            config = state.connectionConfig,
            onOpenConnectionManager = component::onOpenConnectionManagerClicked
        )

        // 2. Main 3-Column Workspace (340dp / 1fr / 380dp)
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            // Left Column: Topic Tree (340dp)
            TopicTreePanel(
                topicTree = state.filteredTopicTree,
                selectedTopicPath = state.selectedTopicPath,
                filterQuery = state.filterQuery,
                filterMode = state.filterMode,
                onTopicSelected = component::onTopicSelected,
                onToggleExpand = component::onToggleExpand,
                onFilterQueryChanged = component::onFilterQueryChanged,
                onFilterModeChanged = component::onFilterModeChanged,
                modifier = Modifier.width(340.dp)
            )

            // Divider
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(DarkOutlineVariant)
            )

            // Center Column: Payload Inspector (1fr)
            PayloadInspectorPanel(
                selectedNode = state.selectedNode,
                modifier = Modifier.weight(1f)
            )

            // Divider
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(DarkOutlineVariant)
            )

            // Right Column: Publisher (380dp)
            PublishPanel(
                selectedTopic = state.selectedTopicPath,
                isPublishing = state.isPublishing,
                publishError = state.publishError,
                onPublishMessage = { topic, payload, qos, isRetained ->
                    component.onPublishMessage(topic, payload, qos, isRetained)
                },
                modifier = Modifier.width(380.dp)
            )
        }

        // 3. Footer Status Bar (32dp)
        WorkspaceFooter(
            totalTopics = state.rawTopicTree.totalTopicCount,
            totalMessages = state.rawTopicTree.totalMessageCount,
            latencyMs = state.latencyMs
        )
    }
}
