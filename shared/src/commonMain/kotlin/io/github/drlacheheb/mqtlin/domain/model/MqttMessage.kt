package io.github.drlacheheb.mqtlin.domain.model

data class MqttMessage(
    val topic: String,
    val payload: ByteArray,
    val payloadString: String = payload.decodeToString(),
    val qos: Int = 0,
    val isRetained: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val userProperties: Map<String, String> = emptyMap()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as MqttMessage

        if (topic != other.topic) return false
        if (!payload.contentEquals(other.payload)) return false
        if (qos != other.qos) return false
        if (isRetained != other.isRetained) return false
        if (timestamp != other.timestamp) return false
        if (userProperties != other.userProperties) return false

        return true
    }

    override fun hashCode(): Int {
        var result = topic.hashCode()
        result = 31 * result + payload.contentHashCode()
        result = 31 * result + qos
        result = 31 * result + isRetained.hashCode()
        result = 31 * result + timestamp.hashCode()
        result = 31 * result + userProperties.hashCode()
        return result
    }
}

