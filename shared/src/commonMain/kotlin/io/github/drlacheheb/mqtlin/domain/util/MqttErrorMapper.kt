package io.github.drlacheheb.mqtlin.domain.util

object MqttErrorMapper {
    fun mapConnectionError(
        error: Throwable?,
        host: String,
        port: Int,
    ): String {
        if (error == null) return "Failed to connect to broker at $host:$port."

        val fullText = extractFullErrorText(error)

        return matchNetworkError(fullText, host, port)
            ?: matchAuthAndIdError(fullText)
            ?: matchProtocolAndBrokerError(fullText, host, port)
            ?: fallbackErrorMessage(error, host, port)
    }

    private fun extractFullErrorText(error: Throwable): String =
        buildString {
            append(error.message ?: "")
            append(" ")
            append(error.cause?.message ?: "")
            var current = error.cause
            while (current != null) {
                append(" ")
                append(current.message ?: "")
                current = current.cause
            }
        }.lowercase()

    private fun matchNetworkError(
        fullText: String,
        host: String,
        port: Int,
    ): String? =
        when {
            fullText.contains("unknownhostexception") ||
                fullText.contains("name or service not known") ||
                fullText.contains("nodename nor servname") -> {
                "Unable to resolve host '$host'. Check your internet connection or verify the hostname."
            }
            fullText.contains("connection refused") || fullText.contains("connectexception") -> {
                "Connection refused on $host:$port. Ensure an MQTT broker is running and listening on port $port."
            }
            fullText.contains("timed out") || fullText.contains("timeout") || fullText.contains("sockettimeoutexception") -> {
                "Connection timed out while connecting to $host:$port. The broker is not responding."
            }
            fullText.contains("sslhandshakeexception") ||
                fullText.contains("pkix path building failed") ||
                fullText.contains("sslexception") ||
                fullText.contains("certificate") -> {
                "TLS/SSL handshake failed with $host:$port. Check if the broker supports TLS, or switch transport to standard TCP."
            }
            else -> null
        }

    private fun matchAuthAndIdError(fullText: String): String? =
        when {
            fullText.contains("not_authorized") ||
                fullText.contains("bad_user_name_or_password") ||
                fullText.contains("not authorized") ||
                fullText.contains("bad username") -> {
                "Authentication failed: The broker rejected your username or password."
            }
            fullText.contains("client_identifier_not_valid") ||
                fullText.contains("identifier_rejected") ||
                fullText.contains("client id rejected") -> {
                "The broker rejected your Client ID. Click the dice icon to generate a new unique Client ID."
            }
            fullText.contains("banned") -> {
                "This client ID or IP address has been banned by the broker."
            }
            else -> null
        }

    private fun matchProtocolAndBrokerError(
        fullText: String,
        host: String,
        port: Int,
    ): String? =
        when {
            fullText.contains("server_unavailable") || fullText.contains("service unavailable") || fullText.contains("server_busy") -> {
                "The MQTT server at $host:$port is temporarily unavailable or overloaded."
            }
            fullText.contains("unsupported_protocol_version") ||
                fullText.contains("protocol_error") ||
                fullText.contains("unacceptable_protocol_version") -> {
                "Protocol version error. Try switching protocol version between MQTT 5.0 and MQTT 3.1.1."
            }
            fullText.contains("quota_exceeded") || fullText.contains("connection_rate_exceeded") -> {
                "Broker connection limit exceeded. Please wait a moment before reconnecting."
            }
            fullText.contains("suback contains only error codes") || fullText.contains("suback") -> {
                "Connected to broker, but wildcard '#' subscription was rejected by broker ACL permissions."
            }
            else -> null
        }

    private fun fallbackErrorMessage(
        error: Throwable,
        host: String,
        port: Int,
    ): String {
        val cleanMsg = cleanErrorMessage(error)
        return if (cleanMsg.isNotBlank()) {
            "Connection failed: $cleanMsg"
        } else {
            "Failed to connect to broker at $host:$port."
        }
    }

    fun mapPublishError(
        error: Throwable?,
        topic: String,
    ): String {
        if (error == null) return "Failed to publish message to topic '$topic'."

        val fullText =
            buildString {
                append(error.message ?: "")
                append(" ")
                append(error.cause?.message ?: "")
            }.lowercase()

        return matchPublishFailureReason(fullText, topic) ?: fallbackPublishMessage(error, topic)
    }

    private fun matchPublishFailureReason(
        fullText: String,
        topic: String,
    ): String? =
        when {
            fullText.contains("not connected") || fullText.contains("cannot publish") || fullText.contains("client is not connected") -> {
                "Cannot publish: You are not connected to an MQTT broker. Connect to a broker first."
            }
            fullText.contains("topic_name_invalid") || fullText.contains("invalid topic") || topic.contains("+") || topic.contains("#") -> {
                "Cannot publish: Topic '$topic' is invalid. Topics cannot contain wildcards (+ or #) when publishing."
            }
            fullText.contains("not_authorized") || fullText.contains("not authorized") -> {
                "Publish rejected: You do not have permission to publish to topic '$topic'."
            }
            fullText.contains("quota_exceeded") || fullText.contains("packet_too_large") -> {
                "Publish rejected: The message payload exceeds the broker's allowed maximum packet size."
            }
            else -> null
        }

    private fun fallbackPublishMessage(
        error: Throwable,
        topic: String,
    ): String {
        val cleanMsg = cleanErrorMessage(error)
        return if (cleanMsg.isNotBlank()) {
            "Publish failed: $cleanMsg"
        } else {
            "Failed to publish message to '$topic'."
        }
    }

    private fun cleanErrorMessage(error: Throwable): String {
        var msg = error.message ?: error.cause?.message ?: ""

        // Strip Java / Netty / HiveMQ class wrappers
        msg =
            msg
                .replace("java.util.concurrent.CompletionException:", "")
                .replace("com.hivemq.client.mqtt.exceptions.", "")
                .replace("io.netty.channel.AbstractChannel\$AnnotatedConnectException:", "")
                .replace("java.net.ConnectException:", "")
                .replace("java.net.UnknownHostException:", "")
                .replace("javax.net.ssl.SSLHandshakeException:", "")
                .trim()

        return msg
    }
}
