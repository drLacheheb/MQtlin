package io.github.drlacheheb.mqtlin.data.repository

import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.MqttGlobalPublishFilter
import com.hivemq.client.mqtt.datatypes.MqttQos
import com.hivemq.client.mqtt.datatypes.MqttUtf8String
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient
import com.hivemq.client.mqtt.mqtt3.Mqtt3ClientBuilder
import com.hivemq.client.mqtt.mqtt3.message.connect.Mqtt3Connect
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAck
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient
import com.hivemq.client.mqtt.mqtt5.Mqtt5ClientBuilder
import com.hivemq.client.mqtt.mqtt5.datatypes.Mqtt5UserProperties
import com.hivemq.client.mqtt.mqtt5.datatypes.Mqtt5UserProperty
import com.hivemq.client.mqtt.mqtt5.message.connect.Mqtt5Connect
import com.hivemq.client.mqtt.mqtt5.message.connect.connack.Mqtt5ConnAck
import io.github.drlacheheb.mqtlin.domain.model.ConnectionConfig
import io.github.drlacheheb.mqtlin.domain.model.ConnectionState
import io.github.drlacheheb.mqtlin.domain.model.MqttMessage
import io.github.drlacheheb.mqtlin.domain.model.MqttProtocolVersion
import io.github.drlacheheb.mqtlin.domain.model.TransportProtocol
import io.github.drlacheheb.mqtlin.domain.repository.MqttRepository
import io.github.drlacheheb.mqtlin.domain.util.MqttErrorMapper
import kotlinx.coroutines.CancellationException
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

    private val _incomingMessages = MutableSharedFlow<MqttMessage>(replay = 0, extraBufferCapacity = 1000)
    override val incomingMessages: SharedFlow<MqttMessage> = _incomingMessages.asSharedFlow()

    private var mqtt5Client: Mqtt5AsyncClient? = null
    private var mqtt3Client: Mqtt3AsyncClient? = null
    private var isIntentionalDisconnect: Boolean = false

    override suspend fun connect(config: ConnectionConfig) {
        disconnect()
        _connectionState.value = ConnectionState.Connecting(config.host, config.port)

        try {
            when (config.protocolVersion) {
                MqttProtocolVersion.MQTT_5_0 -> connectMqtt5(config)
                MqttProtocolVersion.MQTT_3_1_1 -> connectMqtt3(config)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Throwable) {
            _connectionState.value =
                ConnectionState.Error(
                    message = MqttErrorMapper.mapConnectionError(e, config.host, config.port),
                    cause = e,
                )
        }
    }

    override suspend fun testConnection(config: ConnectionConfig): Result<Unit> {
        val testClientId = if (config.clientId.isNotBlank()) "${config.clientId}_test" else "mqtlin_test"
        val testConfig = config.copy(clientId = testClientId)

        return try {
            when (testConfig.protocolVersion) {
                MqttProtocolVersion.MQTT_5_0 -> testMqtt5Connection(testConfig)
                MqttProtocolVersion.MQTT_3_1_1 -> testMqtt3Connection(testConfig)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    private suspend fun testMqtt5Connection(config: ConnectionConfig): Result<Unit> {
        val clientBuilder =
            MqttClient
                .builder()
                .useMqttVersion5()
                .identifier(config.clientId)
                .serverHost(config.host)
                .serverPort(config.port)

        applyMqtt5Transport(clientBuilder, config.transport)
        val testClient = clientBuilder.buildAsync()
        val connectMessage = buildMqtt5Connect(config, cleanStart = true, keepAlive = 10)

        return try {
            val connAck = testClient.connect(connectMessage).await()
            if (connAck.reasonCode.isError) {
                Result.failure(IllegalStateException("Broker rejected: ${connAck.reasonCode}"))
            } else {
                Result.success(Unit)
            }
        } finally {
            try {
                testClient.disconnect().await()
            } catch (ignored: Exception) {
                // Ignore test disconnect error
            }
        }
    }

    private suspend fun testMqtt3Connection(config: ConnectionConfig): Result<Unit> {
        val clientBuilder =
            MqttClient
                .builder()
                .useMqttVersion3()
                .identifier(config.clientId)
                .serverHost(config.host)
                .serverPort(config.port)

        applyMqtt3Transport(clientBuilder, config.transport)
        val testClient = clientBuilder.buildAsync()
        val connectMessage = buildMqtt3Connect(config, cleanSession = true, keepAlive = 10)

        return try {
            val connAck = testClient.connect(connectMessage).await()
            if (connAck.returnCode.isError) {
                Result.failure(IllegalStateException("Broker rejected: ${connAck.returnCode}"))
            } else {
                Result.success(Unit)
            }
        } finally {
            try {
                testClient.disconnect().await()
            } catch (ignored: Exception) {
                // Ignore test disconnect error
            }
        }
    }

    private suspend fun connectMqtt5(config: ConnectionConfig) {
        val clientBuilder =
            MqttClient
                .builder()
                .useMqttVersion5()
                .identifier(config.clientId)
                .serverHost(config.host)
                .serverPort(config.port)
                .addConnectedListener { handleConnected(config) }
                .addDisconnectedListener { context -> handleDisconnected(context.cause, config) }

        applyMqtt5Transport(clientBuilder, config.transport)
        val client = clientBuilder.buildAsync()
        mqtt5Client = client

        registerMqtt5PublishesListener(client)

        val connectMessage = buildMqtt5Connect(config, config.cleanStart, config.keepAliveSeconds)
        val connAck: Mqtt5ConnAck = client.connect(connectMessage).await()

        if (connAck.reasonCode.isError) {
            handleConnectionRejected(connAck.reasonCode.toString(), config)
        } else {
            handleConnectionSuccess(config)
        }
    }

    private suspend fun connectMqtt3(config: ConnectionConfig) {
        val clientBuilder =
            MqttClient
                .builder()
                .useMqttVersion3()
                .identifier(config.clientId)
                .serverHost(config.host)
                .serverPort(config.port)
                .addConnectedListener { handleConnected(config) }
                .addDisconnectedListener { context -> handleDisconnected(context.cause, config) }

        applyMqtt3Transport(clientBuilder, config.transport)
        val client = clientBuilder.buildAsync()
        mqtt3Client = client

        registerMqtt3PublishesListener(client)

        val connectMessage = buildMqtt3Connect(config, config.cleanStart, config.keepAliveSeconds)
        val connAck: Mqtt3ConnAck = client.connect(connectMessage).await()

        if (connAck.returnCode.isError) {
            handleConnectionRejected(connAck.returnCode.toString(), config)
        } else {
            handleConnectionSuccess(config)
        }
    }

    private fun handleConnected(config: ConnectionConfig) {
        _connectionState.value =
            ConnectionState.Connected(
                host = config.host,
                port = config.port,
                clientId = config.clientId,
                protocolVersion = config.protocolVersion,
            )
    }

    private fun handleDisconnected(
        cause: Throwable?,
        config: ConnectionConfig,
    ) {
        if (isIntentionalDisconnect || _connectionState.value is ConnectionState.Disconnected) return

        val isNormalClose =
            cause?.message?.contains("Session expired as connection was closed", ignoreCase = true) == true ||
                cause?.message?.contains("closed", ignoreCase = true) == true

        if (!isNormalClose) {
            _connectionState.value =
                ConnectionState.Error(
                    message = MqttErrorMapper.mapConnectionError(cause, config.host, config.port),
                    cause = cause,
                )
        } else {
            _connectionState.value = ConnectionState.Disconnected
        }
    }

    private fun handleConnectionRejected(
        reason: String,
        config: ConnectionConfig,
    ) {
        _connectionState.value =
            ConnectionState.Error(
                message = MqttErrorMapper.mapConnectionError(IllegalStateException("Broker rejected: $reason"), config.host, config.port),
                cause = null,
            )
    }

    private suspend fun handleConnectionSuccess(config: ConnectionConfig) {
        _connectionState.value =
            ConnectionState.Connected(
                host = config.host,
                port = config.port,
                clientId = config.clientId,
                protocolVersion = config.protocolVersion,
            )
        try {
            subscribe("#", 0)
        } catch (ignored: Exception) {
            // Broker ACL might forbid root wildcard; connection remains active
        }
    }

    private fun registerMqtt5PublishesListener(client: Mqtt5AsyncClient) {
        client.publishes(MqttGlobalPublishFilter.ALL) { publish ->
            val userProps = mutableMapOf<String, String>()
            publish.userProperties.asList().forEach { prop ->
                userProps[prop.name.toString()] = prop.value.toString()
            }

            val message =
                MqttMessage(
                    topic = publish.topic.toString(),
                    payload = publish.payloadAsBytes,
                    qos = publish.qos.code,
                    isRetained = publish.isRetain,
                    timestamp = System.currentTimeMillis(),
                    userProperties = userProps,
                )
            _incomingMessages.tryEmit(message)
        }
    }

    private fun registerMqtt3PublishesListener(client: Mqtt3AsyncClient) {
        client.publishes(MqttGlobalPublishFilter.ALL) { publish ->
            val message =
                MqttMessage(
                    topic = publish.topic.toString(),
                    payload = publish.payloadAsBytes,
                    qos = publish.qos.code,
                    isRetained = publish.isRetain,
                    timestamp = System.currentTimeMillis(),
                )
            _incomingMessages.tryEmit(message)
        }
    }

    private fun applyMqtt5Transport(
        builder: Mqtt5ClientBuilder,
        transport: TransportProtocol,
    ) {
        when (transport) {
            TransportProtocol.TLS -> builder.sslWithDefaultConfig()
            TransportProtocol.WS -> builder.webSocketWithDefaultConfig()
            TransportProtocol.WSS -> {
                builder.sslWithDefaultConfig()
                builder.webSocketWithDefaultConfig()
            }
            TransportProtocol.TCP -> {}
        }
    }

    private fun applyMqtt3Transport(
        builder: Mqtt3ClientBuilder,
        transport: TransportProtocol,
    ) {
        when (transport) {
            TransportProtocol.TLS -> builder.sslWithDefaultConfig()
            TransportProtocol.WS -> builder.webSocketWithDefaultConfig()
            TransportProtocol.WSS -> {
                builder.sslWithDefaultConfig()
                builder.webSocketWithDefaultConfig()
            }
            TransportProtocol.TCP -> {}
        }
    }

    private fun buildMqtt5Connect(
        config: ConnectionConfig,
        cleanStart: Boolean,
        keepAlive: Int,
    ): Mqtt5Connect {
        val connectBuilder =
            Mqtt5Connect
                .builder()
                .cleanStart(cleanStart)
                .keepAlive(keepAlive)

        if (!config.username.isNullOrBlank()) {
            val authBuilder = connectBuilder.simpleAuth().username(config.username)
            if (!config.password.isNullOrBlank()) {
                authBuilder.password(config.password.encodeToByteArray())
            }
            authBuilder.applySimpleAuth()
        }

        return connectBuilder.build()
    }

    private fun buildMqtt3Connect(
        config: ConnectionConfig,
        cleanSession: Boolean,
        keepAlive: Int,
    ): Mqtt3Connect {
        val connectBuilder =
            Mqtt3Connect
                .builder()
                .cleanSession(cleanSession)
                .keepAlive(keepAlive)

        if (!config.username.isNullOrBlank()) {
            val authBuilder = connectBuilder.simpleAuth().username(config.username)
            if (!config.password.isNullOrBlank()) {
                authBuilder.password(config.password.encodeToByteArray())
            }
            authBuilder.applySimpleAuth()
        }

        return connectBuilder.build()
    }

    override suspend fun subscribe(
        topicFilter: String,
        qos: Int,
    ) {
        val mqttQos = mapQos(qos)
        mqtt5Client
            ?.subscribeWith()
            ?.topicFilter(topicFilter)
            ?.qos(mqttQos)
            ?.send()
            ?.await()
        mqtt3Client
            ?.subscribeWith()
            ?.topicFilter(topicFilter)
            ?.qos(mqttQos)
            ?.send()
            ?.await()
    }

    override suspend fun unsubscribe(topicFilter: String) {
        mqtt5Client
            ?.unsubscribeWith()
            ?.topicFilter(topicFilter)
            ?.send()
            ?.await()
        mqtt3Client
            ?.unsubscribeWith()
            ?.topicFilter(topicFilter)
            ?.send()
            ?.await()
    }

    override suspend fun publish(
        topic: String,
        payload: ByteArray,
        qos: Int,
        isRetained: Boolean,
        userProperties: Map<String, String>,
    ) {
        if (mqtt5Client == null && mqtt3Client == null) {
            throw IllegalStateException("Cannot publish: Not connected to an MQTT broker. Please connect to a broker first.")
        }

        val mqttQos = mapQos(qos)
        try {
            publishToActiveClient(topic, payload, mqttQos, isRetained, userProperties)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            throw IllegalStateException(MqttErrorMapper.mapPublishError(e, topic), e)
        }

        _incomingMessages.tryEmit(
            MqttMessage(
                topic = topic,
                payload = payload,
                qos = qos,
                isRetained = isRetained,
                timestamp = System.currentTimeMillis(),
                userProperties = userProperties,
            ),
        )
    }

    private suspend fun publishToActiveClient(
        topic: String,
        payload: ByteArray,
        mqttQos: MqttQos,
        isRetained: Boolean,
        userProperties: Map<String, String>,
    ) {
        mqtt5Client?.let { client ->
            val publishBuilder =
                client
                    .publishWith()
                    .topic(topic)
                    .payload(payload)
                    .qos(mqttQos)
                    .retain(isRetained)

            if (userProperties.isNotEmpty()) {
                val propsBuilder = Mqtt5UserProperties.builder()
                userProperties.forEach { (key, value) ->
                    propsBuilder.add(Mqtt5UserProperty.of(MqttUtf8String.of(key), MqttUtf8String.of(value)))
                }
                publishBuilder.userProperties(propsBuilder.build())
            }
            publishBuilder.send().await()
        }

        mqtt3Client?.let { client ->
            client
                .publishWith()
                .topic(topic)
                .payload(payload)
                .qos(mqttQos)
                .retain(isRetained)
                .send()
                .await()
        }
    }

    private fun mapQos(qos: Int): MqttQos =
        when (qos) {
            1 -> MqttQos.AT_LEAST_ONCE
            2 -> MqttQos.EXACTLY_ONCE
            else -> MqttQos.AT_MOST_ONCE
        }

    override suspend fun disconnect() {
        isIntentionalDisconnect = true
        try {
            mqtt5Client?.disconnect()?.await()
            mqtt3Client?.disconnect()?.await()
        } catch (ignored: Exception) {
            // Ignore disconnect error
        } finally {
            mqtt5Client = null
            mqtt3Client = null
            isIntentionalDisconnect = false
            _connectionState.value = ConnectionState.Disconnected
        }
    }
}
