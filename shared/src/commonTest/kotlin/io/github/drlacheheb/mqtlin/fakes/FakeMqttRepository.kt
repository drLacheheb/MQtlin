package io.github.drlacheheb.mqtlin.fakes

import io.github.drlacheheb.mqtlin.domain.model.ConnectionConfig
import io.github.drlacheheb.mqtlin.domain.model.ConnectionState
import io.github.drlacheheb.mqtlin.domain.repository.MqttRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeMqttRepository : MqttRepository {

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    override val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private var _activeConfig: ConnectionConfig? = null
    override val activeConfig: ConnectionConfig?
        get() = _activeConfig

    var shouldFailConnection = false
    var failureErrorMessage = "Connection refused by broker"
    var simulatedDelayMs = 0L

    override suspend fun connect(config: ConnectionConfig): Result<Unit> {
        _activeConfig = config
        _connectionState.value = ConnectionState.Connecting(config.host, config.port)

        if (simulatedDelayMs > 0) {
            delay(simulatedDelayMs)
        }

        return if (shouldFailConnection) {
            val errorState = ConnectionState.Error(failureErrorMessage)
            _connectionState.value = errorState
            Result.failure(Exception(failureErrorMessage))
        } else {
            val connectedState = ConnectionState.Connected(
                host = config.host,
                port = config.port,
                clientId = config.clientId,
                protocolVersion = config.protocolVersion,
                pingLatencyMs = 12L
            )
            _connectionState.value = connectedState
            Result.success(Unit)
        }
    }

    override suspend fun disconnect(): Result<Unit> {
        _connectionState.value = ConnectionState.Disconnected
        _activeConfig = null
        return Result.success(Unit)
    }

    fun emitState(state: ConnectionState) {
        _connectionState.value = state
    }
}
