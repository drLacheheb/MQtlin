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

    private val _incomingMessages = MutableSharedFlow<MqttMessage>(extraBufferCapacity = 64)
    override val incomingMessages: SharedFlow<MqttMessage> = _incomingMessages.asSharedFlow()

    val publishedMessages = mutableListOf<MqttMessage>()
    val subscriptions = mutableListOf<String>()

    var simulatedDelayMs: Long = 0L
    var shouldFailConnection: Boolean = false
    var failureErrorMessage: String = "Failed to connect"

    override suspend fun connect(config: ConnectionConfig) {
        _connectionState.value = ConnectionState.Connecting(config.host, config.port)
        if (simulatedDelayMs > 0) {
            delay(simulatedDelayMs)
        }
        if (shouldFailConnection) {
            _connectionState.value = ConnectionState.Error(failureErrorMessage)
        } else {
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
        _connectionState.value = ConnectionState.Disconnected
    }

    override suspend fun subscribe(topicFilter: String, qos: Int) {
        subscriptions.add(topicFilter)
    }

    override suspend fun unsubscribe(topicFilter: String) {
        subscriptions.remove(topicFilter)
    }

    override suspend fun publish(
        topic: String,
        payload: ByteArray,
        qos: Int,
        isRetained: Boolean,
        userProperties: Map<String, String>
    ) {
        val msg = MqttMessage(
            topic = topic,
            payload = payload,
            qos = qos,
            isRetained = isRetained,
            timestamp = System.currentTimeMillis(),
            userProperties = userProperties
        )
        publishedMessages.add(msg)
        _incomingMessages.tryEmit(msg)
    }

    fun emitMessage(message: MqttMessage) {
        _incomingMessages.tryEmit(message)
    }

    fun setConnected(config: ConnectionConfig) {
        _connectionState.value = ConnectionState.Connected(
            host = config.host,
            port = config.port,
            clientId = config.clientId,
            protocolVersion = config.protocolVersion
        )
    }
}
