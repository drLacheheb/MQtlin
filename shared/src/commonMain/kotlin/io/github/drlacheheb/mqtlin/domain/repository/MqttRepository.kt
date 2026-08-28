package io.github.drlacheheb.mqtlin.domain.repository

import io.github.drlacheheb.mqtlin.domain.model.ConnectionConfig
import io.github.drlacheheb.mqtlin.domain.model.ConnectionState
import kotlinx.coroutines.flow.StateFlow

interface MqttRepository {
    val connectionState: StateFlow<ConnectionState>
    val activeConfig: ConnectionConfig?
    suspend fun connect(config: ConnectionConfig): Result<Unit>
    suspend fun disconnect(): Result<Unit>
}
