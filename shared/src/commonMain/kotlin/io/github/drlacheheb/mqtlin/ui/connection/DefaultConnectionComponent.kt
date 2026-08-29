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

    private var isTestMode = false
    private var isExplicitConnecting = false

    init {
        lifecycle.subscribe(
            onDestroy = {
                scope.cancel()
            }
        )

        mqttRepository.connectionState
            .onEach { connState ->
                if (isTestMode) {
                    if (connState is ConnectionState.Connected) {
                        _state.update {
                            it.copy(
                                isTesting = false,
                                testSuccessMessage = "Successfully connected to ${it.host}:${it.portText}!",
                                connectionState = ConnectionState.Disconnected
                            )
                        }
                        isTestMode = false
                        // Disconnect immediately after successful test
                        scope.launch {
                            mqttRepository.disconnect()
                        }
                    } else if (connState is ConnectionState.Error) {
                        _state.update {
                            it.copy(
                                isTesting = false,
                                testSuccessMessage = null,
                                connectionState = connState
                            )
                        }
                        isTestMode = false
                    }
                } else {
                    _state.update { it.copy(connectionState = connState) }
                    if (isExplicitConnecting && connState is ConnectionState.Connected) {
                        isExplicitConnecting = false
                        onConnected()
                    }
                }
            }
            .launchIn(scope)
    }

    override fun onNameChanged(name: String) {
        _state.update { it.copy(name = name.take(MAX_PROFILE_NAME_LENGTH)) }
    }

    override fun onHostChanged(host: String) {
        _state.update {
            it.copy(
                host = host,
                testSuccessMessage = null,
                validationErrors = it.validationErrors - ValidationResult.Field.HOST
            )
        }
    }

    override fun onPortChanged(portText: String) {
        _state.update {
            it.copy(
                portText = portText,
                testSuccessMessage = null,
                validationErrors = it.validationErrors - ValidationResult.Field.PORT
            )
        }
    }

    override fun onClientIdChanged(clientId: String) {
        _state.update {
            it.copy(
                clientId = clientId,
                testSuccessMessage = null,
                validationErrors = it.validationErrors - ValidationResult.Field.CLIENT_ID
            )
        }
    }

    override fun onProtocolVersionChanged(version: MqttProtocolVersion) {
        _state.update { it.copy(protocolVersion = version, testSuccessMessage = null) }
    }

    override fun onTransportChanged(transport: TransportProtocol) {
        _state.update {
            it.copy(
                transport = transport,
                portText = transport.defaultPort.toString(),
                testSuccessMessage = null
            )
        }
    }

    override fun onUsernameChanged(username: String) {
        _state.update { it.copy(username = username, testSuccessMessage = null) }
    }

    override fun onPasswordChanged(password: String) {
        _state.update { it.copy(password = password, testSuccessMessage = null) }
    }

    override fun onGenerateRandomClientId() {
        val randomId = "mqtlin_client_" + (1..6).map { "0123456789abcdef".random() }.joinToString("")
        onClientIdChanged(randomId)
    }

    override fun onNewProfileClicked() {
        val randomId = "mqtlin_client_" + (1..6).map { "0123456789abcdef".random() }.joinToString("")
        _state.update {
            it.copy(
                name = "New Connection",
                host = "127.0.0.1",
                portText = "1883",
                clientId = randomId,
                protocolVersion = MqttProtocolVersion.MQTT_5_0,
                transport = TransportProtocol.TCP,
                username = "",
                password = "",
                testSuccessMessage = null,
                validationErrors = emptyMap()
            )
        }
    }

    override fun onProfileSelected(profile: ConnectionConfig) {
        _state.update {
            it.copy(
                name = profile.name,
                host = profile.host,
                portText = profile.port.toString(),
                clientId = profile.clientId,
                protocolVersion = profile.protocolVersion,
                transport = profile.transport,
                username = profile.username ?: "",
                password = profile.password ?: "",
                testSuccessMessage = null,
                validationErrors = emptyMap()
            )
        }
    }

    override fun onDeleteProfileClicked(profile: ConnectionConfig) {
        _state.update { state ->
            val updated = state.savedProfiles.filter { it.name != profile.name || it.host != profile.host }
            state.copy(savedProfiles = updated)
        }
    }

    override fun onProfileSearchQueryChanged(query: String) {
        _state.update { it.copy(profileSearchQuery = query) }
    }

    override fun onTestConnectionClicked() {
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
                _state.update { it.copy(validationErrors = validation.errors, testSuccessMessage = null) }
            }
            ValidationResult.Valid -> {
                isTestMode = true
                _state.update {
                    it.copy(
                        isTesting = true,
                        testSuccessMessage = null,
                        validationErrors = emptyMap(),
                        connectionState = ConnectionState.Connecting(config.host, config.port)
                    )
                }
                scope.launch {
                    mqttRepository.connect(config)
                }
            }
        }
    }

    override fun onConnectClicked() {
        isTestMode = false
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
                _state.update { state ->
                    val existingIndex = state.savedProfiles.indexOfFirst { it.name == config.name }
                    val updatedProfiles = if (existingIndex >= 0) {
                        state.savedProfiles.toMutableList().apply { set(existingIndex, config) }
                    } else {
                        state.savedProfiles + config
                    }
                    state.copy(
                        savedProfiles = updatedProfiles,
                        validationErrors = emptyMap(),
                        testSuccessMessage = null
                    )
                }
                isExplicitConnecting = true
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
