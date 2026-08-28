package io.github.drlacheheb.mqtlin.fakes

import io.github.drlacheheb.mqtlin.domain.model.ConnectionConfig
import io.github.drlacheheb.mqtlin.domain.model.ConnectionState
import io.github.drlacheheb.mqtlin.domain.model.MqttMessage
import io.github.drlacheheb.mqtlin.domain.repository.MqttRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeMqttRepository : MqttRepository {

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    override val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private val _incomingMessages = MutableSharedFlow<MqttMessage>(replay = 1, extraBufferCapacity = 64)
    override val incomingMessages: SharedFlow<MqttMessage> = _incomingMessages.asSharedFlow()

    var shouldFailConnection: Boolean = false
    var failureErrorMessage: String = "Connection refused by broker"
    var simulatedDelayMs: Long = 0L

    val subscribedTopics = mutableListOf<String>()
    var lastConnectedConfig: ConnectionConfig? = null

    override suspend fun connect(config: ConnectionConfig) {
        _connectionState.value = ConnectionState.Connecting(config.host, config.port)

        if (simulatedDelayMs > 0) {
            delay(simulatedDelayMs)
        }

        if (shouldFailConnection) {
            _connectionState.value = ConnectionState.Error(
                message = failureErrorMessage,
                cause = null
            )
        } else {
            lastConnectedConfig = config
            _connectionState.value = ConnectionState.Connected(
                host = config.host,
                port = config.port,
                clientId = config.clientId,
                protocolVersion = config.protocolVersion
            )
            subscribe("#", 0)
        }
    }

    override suspend fun disconnect() {
        if (simulatedDelayMs > 0) {
            delay(simulatedDelayMs)
        }
        subscribedTopics.clear()
        _connectionState.value = ConnectionState.Disconnected
    }

    override suspend fun subscribe(topicFilter: String, qos: Int) {
        if (!subscribedTopics.contains(topicFilter)) {
            subscribedTopics.add(topicFilter)
        }
    }

    override suspend fun unsubscribe(topicFilter: String) {
        subscribedTopics.remove(topicFilter)
    }

    suspend fun emitMessage(message: MqttMessage) {
        _incomingMessages.emit(message)
    }

    fun tryEmitMessage(message: MqttMessage): Boolean {
        return _incomingMessages.tryEmit(message)
    }
}
