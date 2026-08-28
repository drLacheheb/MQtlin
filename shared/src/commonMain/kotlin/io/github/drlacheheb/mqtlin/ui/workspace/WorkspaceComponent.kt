package io.github.drlacheheb.mqtlin.ui.workspace

import com.arkivanov.decompose.value.Value
import io.github.drlacheheb.mqtlin.domain.model.ConnectionConfig
import io.github.drlacheheb.mqtlin.domain.model.ConnectionState
import io.github.drlacheheb.mqtlin.domain.model.FilterMode
import io.github.drlacheheb.mqtlin.domain.model.TopicNode
import io.github.drlacheheb.mqtlin.domain.model.TopicTree

data class WorkspaceUiState(
    val connectionConfig: ConnectionConfig? = null,
    val connectionState: ConnectionState = ConnectionState.Disconnected,
    val rawTopicTree: TopicTree = TopicTree(),
    val filteredTopicTree: TopicTree = TopicTree(),
    val selectedTopicPath: String? = null,
    val selectedNode: TopicNode? = null,
    val filterQuery: String = "",
    val filterMode: FilterMode = FilterMode.TEXT,
    val latencyMs: Long = 12L,
    val isPublishing: Boolean = false,
    val publishError: String? = null
)

interface WorkspaceComponent {
    val state: Value<WorkspaceUiState>

    fun onTopicSelected(fullPath: String)
    fun onToggleExpand(fullPath: String)
    fun onFilterQueryChanged(query: String)
    fun onFilterModeChanged(mode: FilterMode)
    fun onPublishMessage(
        topic: String,
        payload: String,
        qos: Int,
        isRetained: Boolean,
        userProperties: Map<String, String> = emptyMap()
    )
    fun onDisconnectClicked()
    fun onOpenConnectionManagerClicked()
}
