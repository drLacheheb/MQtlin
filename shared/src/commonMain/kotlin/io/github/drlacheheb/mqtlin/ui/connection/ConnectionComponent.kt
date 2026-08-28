package io.github.drlacheheb.mqtlin.ui.connection

import com.arkivanov.decompose.value.Value
import io.github.drlacheheb.mqtlin.domain.model.ConnectionState
import io.github.drlacheheb.mqtlin.domain.model.MqttProtocolVersion
import io.github.drlacheheb.mqtlin.domain.model.TransportProtocol
import io.github.drlacheheb.mqtlin.domain.usecase.ValidationResult

data class ConnectionUiState(
    val name: String = "Local Mosquitto",
    val host: String = "127.0.0.1",
    val portText: String = "1883",
    val clientId: String = "mqtlin_client_8f9a2b",
    val protocolVersion: MqttProtocolVersion = MqttProtocolVersion.MQTT_5_0,
    val transport: TransportProtocol = TransportProtocol.TCP,
    val username: String = "",
    val password: String = "",
    val connectionState: ConnectionState = ConnectionState.Disconnected,
    val validationErrors: Map<ValidationResult.Field, String> = emptyMap(),
    val isTesting: Boolean = false
)

interface ConnectionComponent {
    val state: Value<ConnectionUiState>

    fun onNameChanged(name: String)
    fun onHostChanged(host: String)
    fun onPortChanged(portText: String)
    fun onClientIdChanged(clientId: String)
    fun onProtocolVersionChanged(version: MqttProtocolVersion)
    fun onTransportChanged(transport: TransportProtocol)
    fun onUsernameChanged(username: String)
    fun onPasswordChanged(password: String)
    fun onGenerateRandomClientId()
    fun onConnectClicked()
    fun onDisconnectClicked()
    fun onDismissError()
}
