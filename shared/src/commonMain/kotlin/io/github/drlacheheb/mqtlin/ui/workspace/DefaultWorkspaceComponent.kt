package io.github.drlacheheb.mqtlin.ui.workspace

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.arkivanov.essenty.lifecycle.subscribe
import io.github.drlacheheb.mqtlin.domain.model.ConnectionConfig
import io.github.drlacheheb.mqtlin.domain.model.ConnectionState
import io.github.drlacheheb.mqtlin.domain.model.FilterMode
import io.github.drlacheheb.mqtlin.domain.repository.MqttRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class DefaultWorkspaceComponent(
    componentContext: ComponentContext,
    private val config: ConnectionConfig,
    private val mqttRepository: MqttRepository,
    private val onDisconnect: () -> Unit,
    private val onOpenConnectionManager: () -> Unit,
    mainContext: CoroutineContext = Dispatchers.Main
) : WorkspaceComponent, ComponentContext by componentContext {

    private val scope = CoroutineScope(SupervisorJob() + mainContext)

    private val _state = MutableValue(
        WorkspaceUiState(
            connectionConfig = config,
            connectionState = ConnectionState.Connected(
                host = config.host,
                port = config.port,
                clientId = config.clientId,
                protocolVersion = config.protocolVersion
            )
        )
    )
    override val state: Value<WorkspaceUiState> = _state

    init {
        lifecycle.subscribe(
            onDestroy = {
                scope.cancel()
            }
        )

        mqttRepository.connectionState
            .onEach { connState ->
                _state.update { it.copy(connectionState = connState) }
                if (connState is ConnectionState.Disconnected) {
                    onDisconnect()
                }
            }
            .launchIn(scope)

        mqttRepository.incomingMessages
            .onEach { message ->
                _state.update { current ->
                    val newRawTree = current.rawTopicTree.insert(message)
                    val newFilteredTree = newRawTree.filter(current.filterQuery, current.filterMode)
                    val updatedSelectedNode = current.selectedTopicPath?.let { newRawTree.findNode(it) }

                    current.copy(
                        rawTopicTree = newRawTree,
                        filteredTopicTree = newFilteredTree,
                        selectedNode = updatedSelectedNode ?: current.selectedNode
                    )
                }
            }
            .launchIn(scope)
    }

    override fun onTopicSelected(fullPath: String) {
        _state.update { current ->
            val node = current.rawTopicTree.findNode(fullPath)
            current.copy(
                selectedTopicPath = fullPath,
                selectedNode = node
            )
        }
    }

    override fun onToggleExpand(fullPath: String) {
        _state.update { current ->
            val newRawTree = current.rawTopicTree.toggleExpanded(fullPath)
            val newFilteredTree = newRawTree.filter(current.filterQuery, current.filterMode)
            current.copy(
                rawTopicTree = newRawTree,
                filteredTopicTree = newFilteredTree
            )
        }
    }

    override fun onFilterQueryChanged(query: String) {
        _state.update { current ->
            val newFiltered = current.rawTopicTree.filter(query, current.filterMode)
            current.copy(
                filterQuery = query,
                filteredTopicTree = newFiltered
            )
        }
    }

    override fun onFilterModeChanged(mode: FilterMode) {
        _state.update { current ->
            val newFiltered = current.rawTopicTree.filter(current.filterQuery, mode)
            current.copy(
                filterMode = mode,
                filteredTopicTree = newFiltered
            )
        }
    }

    override fun onDisconnectClicked() {
        scope.launch {
            mqttRepository.disconnect()
        }
    }

    override fun onOpenConnectionManagerClicked() {
        onOpenConnectionManager()
    }
}

