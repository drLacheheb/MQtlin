package io.github.drlacheheb.mqtlin.domain.repository

import io.github.drlacheheb.mqtlin.domain.model.ConnectionConfig
import io.github.drlacheheb.mqtlin.domain.model.ConnectionState
import io.github.drlacheheb.mqtlin.domain.model.MqttMessage
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface MqttRepository {
    val connectionState: StateFlow<ConnectionState>
    val incomingMessages: SharedFlow<MqttMessage>

    suspend fun connect(config: ConnectionConfig)
    suspend fun disconnect()
    suspend fun subscribe(topicFilter: String = "#", qos: Int = 0)
    suspend fun unsubscribe(topicFilter: String)
}
