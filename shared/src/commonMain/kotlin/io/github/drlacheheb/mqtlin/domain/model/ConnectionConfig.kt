package io.github.drlacheheb.mqtlin.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class MqttProtocolVersion(
    val displayName: String,
) {
    MQTT_3_1_1("MQTT 3.1.1"),
    MQTT_5_0("MQTT 5.0"),
}

@Serializable
enum class TransportProtocol(
    val scheme: String,
    val defaultPort: Int,
) {
    TCP("mqtt://", 1883),
    TLS("mqtts://", 8883),
    WS("ws://", 8083),
    WSS("wss://", 8084),
}

@Serializable
data class ConnectionConfig(
    val name: String = "Local Mosquitto",
    val host: String = "127.0.0.1",
    val port: Int = 1883,
    val clientId: String = "mqtlin_client_${randomSuffix()}",
    val protocolVersion: MqttProtocolVersion = MqttProtocolVersion.MQTT_5_0,
    val transport: TransportProtocol = TransportProtocol.TCP,
    val keepAliveSeconds: Int = 60,
    val cleanStart: Boolean = true,
    val sessionExpiryIntervalSeconds: Long = 0,
    val username: String? = null,
    val password: String? = null,
) {
    companion object {
        private fun randomSuffix(): String = (1..6).map { "0123456789abcdef".random() }.joinToString("")
    }
}
