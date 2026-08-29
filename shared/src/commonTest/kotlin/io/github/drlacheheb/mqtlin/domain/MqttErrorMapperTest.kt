package io.github.drlacheheb.mqtlin.domain

import io.github.drlacheheb.mqtlin.domain.util.MqttErrorMapper
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlin.test.Test

class MqttErrorMapperTest {

    @Test
    fun `unknown host exception maps to DNS resolution guidance`() {
        val error = Exception("java.net.UnknownHostException: broker.invalid.local")
        val message = MqttErrorMapper.mapConnectionError(error, "broker.invalid.local", 1883)

        message shouldContain "Unable to resolve host 'broker.invalid.local'"
        message shouldContain "Check your internet connection"
    }

    @Test
    fun `connection refused maps to port and running broker guidance`() {
        val error = Exception("io.netty.channel.AbstractChannel\$AnnotatedConnectException: Connection refused: no further information: 127.0.0.1:1883")
        val message = MqttErrorMapper.mapConnectionError(error, "127.0.0.1", 1883)

        message shouldContain "Connection refused on 127.0.0.1:1883"
        message shouldContain "Ensure an MQTT broker is running and listening on port 1883"
    }

    @Test
    fun `connection timeout maps to server responsiveness guidance`() {
        val error = Exception("java.net.SocketTimeoutException: connect timed out")
        val message = MqttErrorMapper.mapConnectionError(error, "10.0.0.1", 1883)

        message shouldContain "Connection timed out while connecting to 10.0.0.1:1883"
        message shouldContain "The broker is not responding"
    }

    @Test
    fun `SSL handshake exception maps to TLS transport guidance`() {
        val error = Exception("javax.net.ssl.SSLHandshakeException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException")
        val message = MqttErrorMapper.mapConnectionError(error, "broker.hivemq.com", 8883)

        message shouldContain "TLS/SSL handshake failed with broker.hivemq.com:8883"
        message shouldContain "Check if the broker supports TLS, or switch transport to standard TCP"
    }

    @Test
    fun `not authorized ConnAck maps to authentication credentials guidance`() {
        val error = Exception("com.hivemq.client.mqtt.mqtt5.exceptions.Mqtt5ConnAckException: NOT_AUTHORIZED")
        val message = MqttErrorMapper.mapConnectionError(error, "broker.emqx.io", 1883)

        message shouldBe "Authentication failed: The broker rejected your username or password."
    }

    @Test
    fun `invalid client identifier maps to random ID generation suggestion`() {
        val error = Exception("Mqtt5ConnAckReasonCode: CLIENT_IDENTIFIER_NOT_VALID")
        val message = MqttErrorMapper.mapConnectionError(error, "broker.emqx.io", 1883)

        message shouldBe "The broker rejected your Client ID. Click the dice icon to generate a new unique Client ID."
    }

    @Test
    fun `suback error exception maps to wildcard ACL explanation`() {
        val error = Exception("com.hivemq.client.mqtt.mqtt5.exceptions.Mqtt5SubAckException: SUBACK contains only Error Codes")
        val message = MqttErrorMapper.mapConnectionError(error, "broker.emqx.io", 1883)

        message shouldBe "Connected to broker, but wildcard '#' subscription was rejected by broker ACL permissions."
    }

    @Test
    fun `publishing without connection returns clean connection instruction`() {
        val error = IllegalStateException("Cannot publish: Not connected to an MQTT broker.")
        val message = MqttErrorMapper.mapPublishError(error, "home/living-room")

        message shouldBe "Cannot publish: You are not connected to an MQTT broker. Connect to a broker first."
    }

    @Test
    fun `publishing to topic containing wildcard returns wildcard rejection explanation`() {
        val error = Exception("Topic contains invalid characters")
        val message = MqttErrorMapper.mapPublishError(error, "home/+/temperature")

        message shouldBe "Cannot publish: Topic 'home/+/temperature' is invalid. Topics cannot contain wildcards (+ or #) when publishing."
    }
}

