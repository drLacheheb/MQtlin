package io.github.drlacheheb.mqtlin.domain.model

sealed interface ConnectionState {
    data object Disconnected : ConnectionState

    data class Connecting(
        val host: String,
        val port: Int
    ) : ConnectionState

    data class Connected(
        val host: String,
        val port: Int,
        val clientId: String,
        val protocolVersion: MqttProtocolVersion = MqttProtocolVersion.MQTT_5_0,
        val pingLatencyMs: Long? = null
    ) : ConnectionState

    data class Error(
        val message: String,
        val cause: Throwable? = null
    ) : ConnectionState
}
