package io.github.drlacheheb.mqtlin.ui.connection

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.arkivanov.essenty.lifecycle.subscribe
import io.github.drlacheheb.mqtlin.domain.model.ConnectionConfig
import io.github.drlacheheb.mqtlin.domain.model.ConnectionState
import io.github.drlacheheb.mqtlin.domain.model.MqttProtocolVersion
import io.github.drlacheheb.mqtlin.domain.model.TransportProtocol
import io.github.drlacheheb.mqtlin.domain.repository.MqttRepository
import io.github.drlacheheb.mqtlin.domain.usecase.ValidateConnectionConfigUseCase
import io.github.drlacheheb.mqtlin.domain.usecase.ValidationResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class DefaultConnectionComponent(
    componentContext: ComponentContext,
    private val mqttRepository: MqttRepository,
    private val validateConfigUseCase: ValidateConnectionConfigUseCase = ValidateConnectionConfigUseCase(),
    private val onConnected: () -> Unit = {},
    mainContext: CoroutineContext = Dispatchers.Main
) : ConnectionComponent, ComponentContext by componentContext {

    private val scope = CoroutineScope(SupervisorJob() + mainContext)

    private val _state = MutableValue(ConnectionUiState())
    override val state: Value<ConnectionUiState> = _state

    init {
        lifecycle.subscribe(
            onDestroy = {
                scope.cancel()
            }
        )

        mqttRepository.connectionState
            .onEach { connState ->
                _state.update { it.copy(connectionState = connState) }
                if (connState is ConnectionState.Connected) {
                    onConnected()
                }
            }
            .launchIn(scope)
    }

    override fun onNameChanged(name: String) {
        _state.update { it.copy(name = name) }
    }

    override fun onHostChanged(host: String) {
        _state.update {
            it.copy(
                host = host,
                validationErrors = it.validationErrors - ValidationResult.Field.HOST
            )
        }
    }

    override fun onPortChanged(portText: String) {
        _state.update {
            it.copy(
                portText = portText,
                validationErrors = it.validationErrors - ValidationResult.Field.PORT
            )
        }
    }

    override fun onClientIdChanged(clientId: String) {
        _state.update {
            it.copy(
                clientId = clientId,
                validationErrors = it.validationErrors - ValidationResult.Field.CLIENT_ID
            )
        }
    }

    override fun onProtocolVersionChanged(version: MqttProtocolVersion) {
        _state.update { it.copy(protocolVersion = version) }
    }

    override fun onTransportChanged(transport: TransportProtocol) {
        _state.update {
            it.copy(
                transport = transport,
                portText = transport.defaultPort.toString()
            )
        }
    }

    override fun onUsernameChanged(username: String) {
        _state.update { it.copy(username = username) }
    }

    override fun onPasswordChanged(password: String) {
        _state.update { it.copy(password = password) }
    }

    override fun onGenerateRandomClientId() {
        val randomId = "mqtlin_client_" + (1..6).map { "0123456789abcdef".random() }.joinToString("")
        onClientIdChanged(randomId)
    }

    override fun onConnectClicked() {
        val currentState = _state.value
        val portInt = currentState.portText.toIntOrNull() ?: -1

        val config = ConnectionConfig(
            name = currentState.name,
            host = currentState.host.trim(),
            port = portInt,
            clientId = currentState.clientId.trim(),
            protocolVersion = currentState.protocolVersion,
            transport = currentState.transport,
            username = currentState.username.ifBlank { null },
            password = currentState.password.ifBlank { null }
        )

        when (val validation = validateConfigUseCase(config)) {
            is ValidationResult.Invalid -> {
                _state.update { it.copy(validationErrors = validation.errors) }
            }
            ValidationResult.Valid -> {
                _state.update { it.copy(validationErrors = emptyMap()) }
                scope.launch {
                    mqttRepository.connect(config)
                }
            }
        }
    }

    override fun onDisconnectClicked() {
        scope.launch {
            mqttRepository.disconnect()
        }
    }

    override fun onDismissError() {
        _state.update {
            if (it.connectionState is ConnectionState.Error) {
                it.copy(connectionState = ConnectionState.Disconnected)
            } else {
                it
            }
        }
    }
}
