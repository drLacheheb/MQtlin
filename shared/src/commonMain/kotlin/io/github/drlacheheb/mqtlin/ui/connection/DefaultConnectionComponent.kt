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
import io.github.drlacheheb.mqtlin.domain.repository.ProfileRepository
import io.github.drlacheheb.mqtlin.domain.usecase.ValidateConnectionConfigUseCase
import io.github.drlacheheb.mqtlin.domain.usecase.ValidationResult
import io.github.drlacheheb.mqtlin.domain.util.MqttErrorMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.random.Random

class DefaultConnectionComponent(
    componentContext: ComponentContext,
    private val mqttRepository: MqttRepository,
    private val profileRepository: ProfileRepository? = null,
    private val validateConfigUseCase: ValidateConnectionConfigUseCase = ValidateConnectionConfigUseCase(),
    private val onConnected: (ConnectionConfig) -> Unit = {},
    mainContext: CoroutineContext = Dispatchers.Main
) : ConnectionComponent, ComponentContext by componentContext {

    private val scope = CoroutineScope(SupervisorJob() + mainContext)

    private val _state = MutableValue(ConnectionUiState())
    override val state: Value<ConnectionUiState> = _state

    private var isExplicitConnecting = false
    private var activeConnectingConfig: ConnectionConfig? = null
    private var currentProfileOriginalName: String? = null

    init {
        lifecycle.subscribe(
            onDestroy = {
                scope.cancel()
            }
        )

        // Load stored profiles from repository on startup
        if (profileRepository != null) {
            scope.launch {
                val profiles = profileRepository.getAllProfiles()
                val lastSelected = profileRepository.getLastSelectedProfileName()
                val activeProfile = profiles.firstOrNull { it.name.equals(lastSelected, ignoreCase = true) }
                    ?: profiles.firstOrNull()

                currentProfileOriginalName = activeProfile?.name

                _state.update { state ->
                    if (activeProfile != null) {
                        val currentRepoState = mqttRepository.connectionState.value
                        val isCurrentlyConnected = currentRepoState is ConnectionState.Connected &&
                            currentRepoState.host == activeProfile.host &&
                            currentRepoState.port == activeProfile.port

                        state.copy(
                            savedProfiles = profiles,
                            name = activeProfile.name,
                            host = activeProfile.host,
                            portText = activeProfile.port.toString(),
                            clientId = activeProfile.clientId,
                            protocolVersion = activeProfile.protocolVersion,
                            transport = activeProfile.transport,
                            username = activeProfile.username ?: "",
                            password = activeProfile.password ?: "",
                            connectionState = if (isCurrentlyConnected) currentRepoState else ConnectionState.Disconnected
                        )
                    } else {
                        state.copy(savedProfiles = profiles)
                    }
                }
            }
        }

        mqttRepository.connectionState
            .onEach { connState ->
                if (connState is ConnectionState.Connected) {
                    if (isExplicitConnecting) {
                        isExplicitConnecting = false
                        _state.update { it.copy(connectionState = connState) }
                        val finalConfig = activeConnectingConfig ?: ConnectionConfig(
                            name = _state.value.name,
                            host = connState.host,
                            port = connState.port,
                            clientId = connState.clientId,
                            protocolVersion = connState.protocolVersion
                        )
                        activeConnectingConfig = null
                        onConnected(finalConfig)
                    } else {
                        val currentConfig = _state.value
                        val isCurrentlyConnected = connState.host == currentConfig.host &&
                            connState.port == currentConfig.portText.toIntOrNull()
                        _state.update { it.copy(connectionState = if (isCurrentlyConnected) connState else ConnectionState.Disconnected) }
                    }
                } else {
                    _state.update { it.copy(connectionState = connState) }
                    if (connState is ConnectionState.Error) {
                        isExplicitConnecting = false
                    }
                }
            }
            .launchIn(scope)
    }

    override fun onNameChanged(name: String) {
        _state.update { it.copy(name = name.take(MAX_PROFILE_NAME_LENGTH)) }
    }

    override fun onSaveProfileName() {
        val currentState = _state.value
        val oldName = currentProfileOriginalName
        val trimmedName = currentState.name.trim()

        val isDuplicate = currentState.savedProfiles.any {
            it.name.equals(trimmedName, ignoreCase = true) && (oldName == null || !it.name.equals(oldName, ignoreCase = true))
        }

        val finalName = if (trimmedName.isBlank() || isDuplicate) {
            oldName ?: "New Connection"
        } else {
            trimmedName
        }

        val portInt = currentState.portText.toIntOrNull() ?: 1883
        val updatedConfig = ConnectionConfig(
            name = finalName,
            host = currentState.host.trim().ifBlank { "127.0.0.1" },
            port = portInt,
            clientId = currentState.clientId.trim(),
            protocolVersion = currentState.protocolVersion,
            transport = currentState.transport,
            username = currentState.username.ifBlank { null },
            password = currentState.password.ifBlank { null }
        )

        currentProfileOriginalName = finalName

        _state.update { state ->
            val updatedList = state.savedProfiles.toMutableList()
            val index = if (oldName != null) {
                updatedList.indexOfFirst { it.name.equals(oldName, ignoreCase = true) }
            } else {
                updatedList.indexOfFirst { it.name.equals(finalName, ignoreCase = true) }
            }

            if (index >= 0) {
                updatedList[index] = updatedConfig
            } else {
                updatedList.add(updatedConfig)
            }

            state.copy(
                name = finalName,
                savedProfiles = updatedList
            )
        }

        profileRepository?.let { repo ->
            scope.launch {
                if (oldName != null && !oldName.equals(finalName, ignoreCase = true)) {
                    repo.deleteProfile(oldName)
                }
                repo.saveProfile(updatedConfig)
                repo.setLastSelectedProfileName(finalName)
            }
        }
    }

    override fun onHostChanged(host: String) {
        _state.update { it.copy(host = host) }
    }

    override fun onPortChanged(portText: String) {
        _state.update { it.copy(portText = portText.filter { ch -> ch.isDigit() }) }
    }

    override fun onClientIdChanged(clientId: String) {
        _state.update { it.copy(clientId = clientId) }
    }

    override fun onProtocolVersionChanged(version: MqttProtocolVersion) {
        _state.update { it.copy(protocolVersion = version) }
    }

    override fun onTransportChanged(transport: TransportProtocol) {
        val defaultPort = when (transport) {
            TransportProtocol.TCP -> "1883"
            TransportProtocol.TLS -> "8883"
            TransportProtocol.WS -> "8083"
            TransportProtocol.WSS -> "8084"
        }
        _state.update { it.copy(transport = transport, portText = defaultPort) }
    }

    override fun onUsernameChanged(username: String) {
        _state.update { it.copy(username = username) }
    }

    override fun onPasswordChanged(password: String) {
        _state.update { it.copy(password = password) }
    }

    override fun onGenerateRandomClientId() {
        val randomHex = Random.nextBytes(4).joinToString("") { "%02x".format(it) }
        _state.update { it.copy(clientId = "mqtlin_client_$randomHex") }
    }

    override fun onNewProfileClicked() {
        val existingNames = _state.value.savedProfiles.map { it.name.trim().lowercase() }.toSet()
        val baseName = "New Connection"
        var uniqueName = baseName
        var counter = 2
        while (uniqueName.lowercase() in existingNames) {
            uniqueName = "$baseName $counter"
            counter++
        }

        val randomHex = Random.nextBytes(3).joinToString("") { "%02x".format(it) }
        val newProfile = ConnectionConfig(
            name = uniqueName,
            host = "127.0.0.1",
            port = 1883,
            clientId = "mqtlin_client_$randomHex",
            protocolVersion = MqttProtocolVersion.MQTT_5_0,
            transport = TransportProtocol.TCP
        )
        currentProfileOriginalName = newProfile.name
        _state.update { state ->
            state.copy(
                name = newProfile.name,
                host = newProfile.host,
                portText = newProfile.port.toString(),
                clientId = newProfile.clientId,
                protocolVersion = newProfile.protocolVersion,
                transport = newProfile.transport,
                username = "",
                password = "",
                savedProfiles = state.savedProfiles + newProfile,
                validationErrors = emptyMap(),
                testSuccessMessage = null
            )
        }
        profileRepository?.let { repo ->
            scope.launch {
                repo.saveProfile(newProfile)
                repo.setLastSelectedProfileName(newProfile.name)
            }
        }
    }

    override fun onProfileSelected(profile: ConnectionConfig) {
        currentProfileOriginalName = profile.name
        val currentRepoState = mqttRepository.connectionState.value
        val isCurrentlyConnected = currentRepoState is ConnectionState.Connected &&
            currentRepoState.host == profile.host &&
            currentRepoState.port == profile.port

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
                connectionState = if (isCurrentlyConnected) currentRepoState else ConnectionState.Disconnected,
                validationErrors = emptyMap(),
                testSuccessMessage = null
            )
        }
        profileRepository?.let { repo ->
            scope.launch {
                repo.setLastSelectedProfileName(profile.name)
            }
        }
    }

    override fun onDeleteProfileClicked(profile: ConnectionConfig) {
        _state.update { state ->
            val updated = state.savedProfiles.filter { it.name != profile.name || it.host != profile.host }
            state.copy(savedProfiles = updated)
        }
        profileRepository?.let { repo ->
            scope.launch {
                repo.deleteProfile(profile.name)
            }
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
                _state.update {
                    it.copy(
                        isTesting = true,
                        testSuccessMessage = null,
                        validationErrors = emptyMap()
                    )
                }
                scope.launch {
                    val result = mqttRepository.testConnection(config)
                    if (result.isSuccess) {
                        _state.update {
                            it.copy(
                                isTesting = false,
                                testSuccessMessage = "Successfully connected to ${config.host}:${config.port}!",
                                connectionState = ConnectionState.Disconnected
                            )
                        }
                    } else {
                        val ex = result.exceptionOrNull()
                        val errorMsg = MqttErrorMapper.mapConnectionError(ex, config.host, config.port)
                        _state.update {
                            it.copy(
                                isTesting = false,
                                testSuccessMessage = null,
                                connectionState = ConnectionState.Error(errorMsg, ex)
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onConnectClicked() {
        val currentState = _state.value
        val portInt = currentState.portText.toIntOrNull() ?: -1

        val oldName = currentProfileOriginalName
        val rawName = currentState.name.trim()
        val isDuplicate = currentState.savedProfiles.any {
            it.name.equals(rawName, ignoreCase = true) && (oldName == null || !it.name.equals(oldName, ignoreCase = true))
        }
        val resolvedName = if (rawName.isBlank() || isDuplicate) {
            oldName ?: "New Connection"
        } else {
            rawName
        }

        val config = ConnectionConfig(
            name = resolvedName,
            host = currentState.host.trim(),
            port = portInt,
            clientId = currentState.clientId.trim(),
            protocolVersion = currentState.protocolVersion,
            transport = currentState.transport,
            username = currentState.username.ifBlank { null },
            password = currentState.password.ifBlank { null }
        )

        currentProfileOriginalName = resolvedName

        when (val validation = validateConfigUseCase(config)) {
            is ValidationResult.Invalid -> {
                _state.update { it.copy(validationErrors = validation.errors) }
            }
            ValidationResult.Valid -> {
                _state.update { state ->
                    val updatedProfiles = state.savedProfiles.toMutableList()
                    val index = if (oldName != null) {
                        updatedProfiles.indexOfFirst { it.name.equals(oldName, ignoreCase = true) }
                    } else {
                        updatedProfiles.indexOfFirst { it.name.equals(config.name, ignoreCase = true) }
                    }
                    if (index >= 0) {
                        updatedProfiles[index] = config
                    } else {
                        updatedProfiles.add(config)
                    }
                    state.copy(
                        savedProfiles = updatedProfiles,
                        validationErrors = emptyMap(),
                        testSuccessMessage = null
                    )
                }
                profileRepository?.let { repo ->
                    scope.launch {
                        if (oldName != null && !oldName.equals(config.name, ignoreCase = true)) {
                            repo.deleteProfile(oldName)
                        }
                        repo.saveProfile(config)
                        repo.setLastSelectedProfileName(config.name)
                    }
                }
                activeConnectingConfig = config
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
