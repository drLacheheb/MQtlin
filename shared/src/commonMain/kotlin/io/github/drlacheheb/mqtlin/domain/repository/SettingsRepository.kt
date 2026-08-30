package io.github.drlacheheb.mqtlin.domain.repository

import io.github.drlacheheb.mqtlin.domain.model.MqttProtocolVersion
import kotlinx.serialization.Serializable

@Serializable
data class AppSettings(
    val maxHistoryPerTopic: Int = 100,
    val autoReconnect: Boolean = true,
    val defaultProtocolVersion: MqttProtocolVersion = MqttProtocolVersion.MQTT_5_0
)

/**
 * Domain repository abstraction for persisting global application settings.
 */
interface SettingsRepository {
    suspend fun getSettings(): AppSettings
    suspend fun updateSettings(settings: AppSettings)
}
