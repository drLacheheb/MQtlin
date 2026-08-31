package io.github.drlacheheb.mqtlin.data.repository

import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.MqttGlobalPublishFilter
import com.hivemq.client.mqtt.datatypes.MqttQos
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAck
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient
import com.hivemq.client.mqtt.mqtt5.message.connect.connack.Mqtt5ConnAck
import io.github.drlacheheb.mqtlin.domain.model.ConnectionConfig
import io.github.drlacheheb.mqtlin.domain.model.ConnectionState
import io.github.drlacheheb.mqtlin.domain.model.MqttMessage
import io.github.drlacheheb.mqtlin.domain.model.MqttProtocolVersion
import io.github.drlacheheb.mqtlin.domain.model.TransportProtocol
import io.github.drlacheheb.mqtlin.domain.repository.MqttRepository
import io.github.drlacheheb.mqtlin.domain.util.MqttErrorMapper
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.future.await

class HiveMqRepository : MqttRepository {

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    override val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private val _incomingMessages = MutableSharedFlow<MqttMessage>(
        extraBufferCapacity = 1024,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    override val incomingMessages: SharedFlow<MqttMessage> = _incomingMessages.asSharedFlow()

    private var mqtt5Client: Mqtt5AsyncClient? = null
    private var mqtt3Client: Mqtt3AsyncClient? = null
    private var currentConfig: ConnectionConfig? = null
    private var isIntentionalDisconnect = false

    override suspend fun connect(config: ConnectionConfig) {
        _connectionState.value = ConnectionState.Connecting(config.host, config.port)
        currentConfig = config

        // Clean up any existing connection before initiating a new one
        if (mqtt5Client?.state?.isConnected == true || mqtt3Client?.state?.isConnected == true) {
            isIntentionalDisconnect = true
            try {
                disconnect()
            } catch (_: Exception) {}
        }
        isIntentionalDisconnect = false

        try {
            when (config.protocolVersion) {
                MqttProtocolVersion.MQTT_5_0 -> connectMqtt5(config)
                MqttProtocolVersion.MQTT_3_1_1 -> connectMqtt3(config)
            }
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
        } catch (e: Exception) {
            _connectionState.value = ConnectionState.Error(
                message = MqttErrorMapper.mapConnectionError(e, config.host, config.port),
                cause = e
            )
        }
    }

    override suspend fun testConnection(config: ConnectionConfig): Result<Unit> {
        val testClientId = if (config.clientId.isNotBlank()) "${config.clientId}_test" else "mqtlin_test"
        val testConfig = config.copy(clientId = testClientId)

        return try {
            when (testConfig.protocolVersion) {
                MqttProtocolVersion.MQTT_5_0 -> {
                    val clientBuilder = MqttClient.builder()
                        .useMqttVersion5()
                        .identifier(testConfig.clientId)
                        .serverHost(testConfig.host)
                        .serverPort(testConfig.port)

                    when (testConfig.transport) {
                        TransportProtocol.TLS -> clientBuilder.sslWithDefaultConfig()
                        TransportProtocol.WS -> clientBuilder.webSocketWithDefaultConfig()
                        TransportProtocol.WSS -> {
                            clientBuilder.sslWithDefaultConfig()
                            clientBuilder.webSocketWithDefaultConfig()
                        }
                        TransportProtocol.TCP -> {}
                    }

                    val testClient = clientBuilder.buildAsync()
                    val connectBuilder = com.hivemq.client.mqtt.mqtt5.message.connect.Mqtt5Connect.builder()
                        .cleanStart(true)
                        .keepAlive(10)

                    if (!testConfig.username.isNullOrBlank()) {
                        val authBuilder = connectBuilder.simpleAuth().username(testConfig.username)
                        if (!testConfig.password.isNullOrBlank()) {
                            authBuilder.password(testConfig.password.encodeToByteArray())
                        }
                        authBuilder.applySimpleAuth()
                    }

                    try {
                        val connAck = testClient.connect(connectBuilder.build()).await()
                        if (connAck.reasonCode.isError) {
                            Result.failure(Exception("Broker rejected: ${connAck.reasonCode}"))
                        } else {
                            Result.success(Unit)
                        }
                    } finally {
                        try {
                            testClient.disconnect().await()
                        } catch (_: Exception) {}
                    }
                }
                MqttProtocolVersion.MQTT_3_1_1 -> {
                    val clientBuilder = MqttClient.builder()
                        .useMqttVersion3()
                        .identifier(testConfig.clientId)
                        .serverHost(testConfig.host)
                        .serverPort(testConfig.port)

                    when (testConfig.transport) {
                        TransportProtocol.TLS -> clientBuilder.sslWithDefaultConfig()
                        TransportProtocol.WS -> clientBuilder.webSocketWithDefaultConfig()
                        TransportProtocol.WSS -> {
                            clientBuilder.sslWithDefaultConfig()
                            clientBuilder.webSocketWithDefaultConfig()
                        }
                        TransportProtocol.TCP -> {}
                    }

                    val testClient = clientBuilder.buildAsync()
                    val connectBuilder = com.hivemq.client.mqtt.mqtt3.message.connect.Mqtt3Connect.builder()
                        .cleanSession(true)
                        .keepAlive(10)

                    if (!testConfig.username.isNullOrBlank()) {
                        val authBuilder = connectBuilder.simpleAuth().username(testConfig.username)
                        if (!testConfig.password.isNullOrBlank()) {
                            authBuilder.password(testConfig.password.encodeToByteArray())
                        }
                        authBuilder.applySimpleAuth()
                    }

                    try {
                        val connAck = testClient.connect(connectBuilder.build()).await()
                        if (connAck.returnCode.isError) {
                            Result.failure(Exception("Broker rejected: ${connAck.returnCode}"))
                        } else {
                            Result.success(Unit)
                        }
                    } finally {
                        try {
                            testClient.disconnect().await()
                        } catch (_: Exception) {}
                    }
                }
            }
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    private suspend fun connectMqtt5(config: ConnectionConfig) {
        val clientBuilder = MqttClient.builder()
            .useMqttVersion5()
            .identifier(config.clientId)
            .serverHost(config.host)
            .serverPort(config.port)
            .addConnectedListener {
                _connectionState.value = ConnectionState.Connected(
                    host = config.host,
                    port = config.port,
                    clientId = config.clientId,
                    protocolVersion = config.protocolVersion
                )
            }
            .addDisconnectedListener { context ->
                if (!isIntentionalDisconnect && _connectionState.value !is ConnectionState.Disconnected) {
                    val cause = context.cause
                    val isNormalClose = cause.message?.contains("Session expired as connection was closed", ignoreCase = true) == true ||
                        cause.message?.contains("closed", ignoreCase = true) == true

                    if (!isNormalClose) {
                        _connectionState.value = ConnectionState.Error(
                            message = MqttErrorMapper.mapConnectionError(cause, config.host, config.port),
                            cause = cause
                        )
                    } else {
                        _connectionState.value = ConnectionState.Disconnected
                    }
                }
            }

        when (config.transport) {
            TransportProtocol.TLS -> clientBuilder.sslWithDefaultConfig()
            TransportProtocol.WS -> clientBuilder.webSocketWithDefaultConfig()
            TransportProtocol.WSS -> {
                clientBuilder.sslWithDefaultConfig()
                clientBuilder.webSocketWithDefaultConfig()
            }
            TransportProtocol.TCP -> { /* Standard TCP */ }
        }

        val client = clientBuilder.buildAsync()
        mqtt5Client = client

        // Global incoming message listener for MQTT 5
        client.publishes(MqttGlobalPublishFilter.ALL) { publish ->
            val payloadBytes = publish.payloadAsBytes
            val userProps = mutableMapOf<String, String>()
            publish.userProperties.asList().forEach { prop ->
                userProps[prop.name.toString()] = prop.value.toString()
            }

            val message = MqttMessage(
                topic = publish.topic.toString(),
                payload = payloadBytes,
                qos = publish.qos.code,
                isRetained = publish.isRetain,
                timestamp = System.currentTimeMillis(),
                userProperties = userProps
            )
            _incomingMessages.tryEmit(message)
        }

        val connectBuilder = com.hivemq.client.mqtt.mqtt5.message.connect.Mqtt5Connect.builder()
            .cleanStart(config.cleanStart)
            .keepAlive(config.keepAliveSeconds)

        if (!config.username.isNullOrBlank()) {
            val authBuilder = connectBuilder.simpleAuth().username(config.username)
            if (!config.password.isNullOrBlank()) {
                authBuilder.password(config.password.encodeToByteArray())
            }
            authBuilder.applySimpleAuth()
        }

        val connAck: Mqtt5ConnAck = client.connect(connectBuilder.build()).await()
        if (connAck.reasonCode.isError) {
            _connectionState.value = ConnectionState.Error(
                message = MqttErrorMapper.mapConnectionError(Exception("Broker rejected: ${connAck.reasonCode}"), config.host, config.port),
                cause = null
            )
        } else {
            _connectionState.value = ConnectionState.Connected(
                host = config.host,
                port = config.port,
                clientId = config.clientId,
                protocolVersion = config.protocolVersion
            )
            try {
                subscribe("#", 0)
            } catch (subEx: Exception) {
                // If the broker rejects '#' via ACL in SUBACK, we keep the connection active
                println("Notice: Default '#' subscription rejected by broker ACL: ${subEx.message}")
            }
        }
    }

    private suspend fun connectMqtt3(config: ConnectionConfig) {
        val clientBuilder = MqttClient.builder()
            .useMqttVersion3()
            .identifier(config.clientId)
            .serverHost(config.host)
            .serverPort(config.port)
            .addConnectedListener {
                _connectionState.value = ConnectionState.Connected(
                    host = config.host,
                    port = config.port,
                    clientId = config.clientId,
                    protocolVersion = config.protocolVersion
                )
            }
            .addDisconnectedListener { context ->
                if (!isIntentionalDisconnect && _connectionState.value !is ConnectionState.Disconnected) {
                    val cause = context.cause
                    val isNormalClose = cause.message?.contains("Session expired as connection was closed", ignoreCase = true) == true ||
                        cause.message?.contains("closed", ignoreCase = true) == true

                    if (!isNormalClose) {
                        _connectionState.value = ConnectionState.Error(
                            message = MqttErrorMapper.mapConnectionError(cause, config.host, config.port),
                            cause = cause
                        )
                    } else {
                        _connectionState.value = ConnectionState.Disconnected
                    }
                }
            }

        when (config.transport) {
            TransportProtocol.TLS -> clientBuilder.sslWithDefaultConfig()
            TransportProtocol.WS -> clientBuilder.webSocketWithDefaultConfig()
            TransportProtocol.WSS -> {
                clientBuilder.sslWithDefaultConfig()
                clientBuilder.webSocketWithDefaultConfig()
            }
            TransportProtocol.TCP -> { /* Standard TCP */ }
        }

        val client = clientBuilder.buildAsync()
        mqtt3Client = client

        // Global incoming message listener for MQTT 3
        client.publishes(MqttGlobalPublishFilter.ALL) { publish ->
            val message = MqttMessage(
                topic = publish.topic.toString(),
                payload = publish.payloadAsBytes,
                qos = publish.qos.code,
                isRetained = publish.isRetain,
                timestamp = System.currentTimeMillis()
            )
            _incomingMessages.tryEmit(message)
        }

        val connectBuilder = com.hivemq.client.mqtt.mqtt3.message.connect.Mqtt3Connect.builder()
            .cleanSession(config.cleanStart)
            .keepAlive(config.keepAliveSeconds)

        if (!config.username.isNullOrBlank()) {
            val authBuilder = connectBuilder.simpleAuth().username(config.username)
            if (!config.password.isNullOrBlank()) {
                authBuilder.password(config.password.encodeToByteArray())
            }
            authBuilder.applySimpleAuth()
        }

        val connAck: Mqtt3ConnAck = client.connect(connectBuilder.build()).await()
        if (connAck.returnCode.isError) {
            _connectionState.value = ConnectionState.Error(
                message = MqttErrorMapper.mapConnectionError(Exception("Broker rejected: ${connAck.returnCode}"), config.host, config.port),
                cause = null
            )
        } else {
            _connectionState.value = ConnectionState.Connected(
                host = config.host,
                port = config.port,
                clientId = config.clientId,
                protocolVersion = config.protocolVersion
            )
            try {
                subscribe("#", 0)
            } catch (subEx: Exception) {
                // If the broker rejects '#' via ACL in SUBACK, we keep the connection active
                println("Notice: Default '#' subscription rejected by broker ACL: ${subEx.message}")
            }
        }
    }

    override suspend fun subscribe(topicFilter: String, qos: Int) {
        val mqttQos = when (qos) {
            1 -> MqttQos.AT_LEAST_ONCE
            2 -> MqttQos.EXACTLY_ONCE
            else -> MqttQos.AT_MOST_ONCE
        }

        mqtt5Client?.let { client ->
            client.subscribeWith()
                .topicFilter(topicFilter)
                .qos(mqttQos)
                .send()
                .await()
        }

        mqtt3Client?.let { client ->
            client.subscribeWith()
                .topicFilter(topicFilter)
                .qos(mqttQos)
                .send()
                .await()
        }
    }

    override suspend fun unsubscribe(topicFilter: String) {
        mqtt5Client?.let { client ->
            client.unsubscribeWith()
                .topicFilter(topicFilter)
                .send()
                .await()
        }

        mqtt3Client?.let { client ->
            client.unsubscribeWith()
                .topicFilter(topicFilter)
                .send()
                .await()
        }
    }

    override suspend fun publish(
        topic: String,
        payload: ByteArray,
        qos: Int,
        isRetained: Boolean,
        userProperties: Map<String, String>
    ) {
        if (mqtt5Client == null && mqtt3Client == null) {
            throw IllegalStateException("Cannot publish: Not connected to an MQTT broker. Please connect to a broker first.")
        }

        val mqttQos = when (qos) {
            1 -> MqttQos.AT_LEAST_ONCE
            2 -> MqttQos.EXACTLY_ONCE
            else -> MqttQos.AT_MOST_ONCE
        }

        try {
            mqtt5Client?.let { client ->
                val publishBuilder = client.publishWith()
                    .topic(topic)
                    .payload(payload)
                    .qos(mqttQos)
                    .retain(isRetained)

                if (userProperties.isNotEmpty()) {
                    val propsBuilder = com.hivemq.client.mqtt.mqtt5.datatypes.Mqtt5UserProperties.builder()
                    userProperties.forEach { (key, value) ->
                        propsBuilder.add(key, value)
                    }
                    publishBuilder.userProperties(propsBuilder.build())
                }

                publishBuilder.send().await()
            }

            mqtt3Client?.let { client ->
                client.publishWith()
                    .topic(topic)
                    .payload(payload)
                    .qos(mqttQos)
                    .retain(isRetained)
                    .send()
                    .await()
            }
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
        } catch (e: Exception) {
            throw Exception(MqttErrorMapper.mapPublishError(e, topic), e)
        }

        // Optimistically update local message stream
        _incomingMessages.tryEmit(
            MqttMessage(
                topic = topic,
                payload = payload,
                qos = qos,
                isRetained = isRetained,
                timestamp = System.currentTimeMillis(),
                userProperties = userProperties
            )
        )
    }

    override suspend fun disconnect() {
        isIntentionalDisconnect = true
        try {
            mqtt5Client?.disconnect()?.await()
            mqtt3Client?.disconnect()?.await()
        } catch (_: Exception) {
            // Ignore disconnect error
        } finally {
            mqtt5Client = null
            mqtt3Client = null
            isIntentionalDisconnect = false
            _connectionState.value = ConnectionState.Disconnected
        }
    }
}
