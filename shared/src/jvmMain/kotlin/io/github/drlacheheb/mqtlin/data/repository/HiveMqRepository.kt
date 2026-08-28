package io.github.drlacheheb.mqtlin.data.repository

import co.touchlab.kermit.Logger
import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.lifecycle.MqttClientConnectedContext
import com.hivemq.client.mqtt.lifecycle.MqttClientDisconnectedContext
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient
import com.hivemq.client.mqtt.mqtt5.message.connect.Mqtt5Connect
import io.github.drlacheheb.mqtlin.domain.model.ConnectionConfig
import io.github.drlacheheb.mqtlin.domain.model.ConnectionState
import io.github.drlacheheb.mqtlin.domain.model.MqttProtocolVersion
import io.github.drlacheheb.mqtlin.domain.model.TransportProtocol
import io.github.drlacheheb.mqtlin.domain.repository.MqttRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withContext
import java.nio.charset.StandardCharsets

class HiveMqRepository(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : MqttRepository {

    private val log = Logger.withTag("HiveMqRepository")

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    override val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private var _activeConfig: ConnectionConfig? = null
    override val activeConfig: ConnectionConfig?
        get() = _activeConfig

    private var client5: Mqtt5AsyncClient? = null
    private var client3: Mqtt3AsyncClient? = null

    override suspend fun connect(config: ConnectionConfig): Result<Unit> = withContext(ioDispatcher) {
        try {
            _activeConfig = config
            _connectionState.value = ConnectionState.Connecting(config.host, config.port)
            log.i { "Connecting to ${config.host}:${config.port} as '${config.clientId}' (${config.protocolVersion.displayName})" }

            disconnectInternal()

            when (config.protocolVersion) {
                MqttProtocolVersion.MQTT_5_0 -> connectMqtt5(config)
                MqttProtocolVersion.MQTT_3_1_1 -> connectMqtt3(config)
            }

            log.i { "Connected successfully to ${config.host}:${config.port}" }
            Result.success(Unit)
        } catch (e: Throwable) {
            log.e(e) { "Connection failed to ${config.host}:${config.port}" }
            val errorMessage = e.cause?.message ?: e.message ?: "Unknown connection error"
            _connectionState.value = ConnectionState.Error(
                message = "Failed to connect to ${config.host}:${config.port}: $errorMessage",
                cause = e
            )
            Result.failure(e)
        }
    }

    private suspend fun connectMqtt5(config: ConnectionConfig) {
        val builder = MqttClient.builder()
            .useMqttVersion5()
            .identifier(config.clientId)
            .serverHost(config.host)
            .serverPort(config.port)
            .addConnectedListener { _: MqttClientConnectedContext ->
                _connectionState.value = ConnectionState.Connected(
                    host = config.host,
                    port = config.port,
                    clientId = config.clientId,
                    protocolVersion = MqttProtocolVersion.MQTT_5_0
                )
            }
            .addDisconnectedListener { context: MqttClientDisconnectedContext ->
                val cause = context.cause
                if (cause != null && _connectionState.value !is ConnectionState.Disconnected) {
                    _connectionState.value = ConnectionState.Error(
                        message = "Connection lost: ${cause.message}",
                        cause = cause
                    )
                } else if (_connectionState.value !is ConnectionState.Error) {
                    _connectionState.value = ConnectionState.Disconnected
                }
            }

        when (config.transport) {
            TransportProtocol.TLS -> builder.sslWithDefaultConfig()
            TransportProtocol.WS -> builder.webSocketWithDefaultConfig()
            TransportProtocol.WSS -> {
                builder.sslWithDefaultConfig()
                builder.webSocketWithDefaultConfig()
            }
            TransportProtocol.TCP -> { }
        }

        if (!config.username.isNullOrBlank()) {
            val authBuilder = builder.simpleAuth()
                .username(config.username)
            if (!config.password.isNullOrBlank()) {
                authBuilder.password(config.password.toByteArray(StandardCharsets.UTF_8))
            }
            authBuilder.applySimpleAuth()
        }

        val client = builder.buildAsync()
        client5 = client

        val connectBuilder = Mqtt5Connect.builder()
            .keepAlive(config.keepAliveSeconds)
            .cleanStart(config.cleanStart)
            .sessionExpiryInterval(config.sessionExpiryIntervalSeconds)

        client.connect(connectBuilder.build()).await()
    }

    private suspend fun connectMqtt3(config: ConnectionConfig) {
        val builder = MqttClient.builder()
            .useMqttVersion3()
            .identifier(config.clientId)
            .serverHost(config.host)
            .serverPort(config.port)
            .addConnectedListener { _: MqttClientConnectedContext ->
                _connectionState.value = ConnectionState.Connected(
                    host = config.host,
                    port = config.port,
                    clientId = config.clientId,
                    protocolVersion = MqttProtocolVersion.MQTT_3_1_1
                )
            }
            .addDisconnectedListener { context: MqttClientDisconnectedContext ->
                val cause = context.cause
                if (cause != null && _connectionState.value !is ConnectionState.Disconnected) {
                    _connectionState.value = ConnectionState.Error(
                        message = "Connection lost: ${cause.message}",
                        cause = cause
                    )
                } else if (_connectionState.value !is ConnectionState.Error) {
                    _connectionState.value = ConnectionState.Disconnected
                }
            }

        when (config.transport) {
            TransportProtocol.TLS -> builder.sslWithDefaultConfig()
            TransportProtocol.WS -> builder.webSocketWithDefaultConfig()
            TransportProtocol.WSS -> {
                builder.sslWithDefaultConfig()
                builder.webSocketWithDefaultConfig()
            }
            TransportProtocol.TCP -> { }
        }

        if (!config.username.isNullOrBlank()) {
            val authBuilder = builder.simpleAuth()
                .username(config.username)
            if (!config.password.isNullOrBlank()) {
                authBuilder.password(config.password.toByteArray(StandardCharsets.UTF_8))
            }
            authBuilder.applySimpleAuth()
        }

        val client = builder.buildAsync()
        client3 = client

        client.connectWith()
            .keepAlive(config.keepAliveSeconds)
            .cleanSession(config.cleanStart)
            .send()
            .await()
    }

    override suspend fun disconnect(): Result<Unit> = withContext(ioDispatcher) {
        try {
            disconnectInternal()
            _connectionState.value = ConnectionState.Disconnected
            Result.success(Unit)
        } catch (e: Throwable) {
            _connectionState.value = ConnectionState.Disconnected
            Result.failure(e)
        }
    }

    private suspend fun disconnectInternal() {
        try { client5?.disconnect()?.await() } catch (ignored: Exception) { }
        try { client3?.disconnect()?.await() } catch (ignored: Exception) { }
        client5 = null
        client3 = null
    }
}
