package io.github.drlacheheb.mqtlin.fakes

import io.github.drlacheheb.mqtlin.domain.model.ConnectionConfig
import io.github.drlacheheb.mqtlin.domain.model.MqttProtocolVersion
import io.github.drlacheheb.mqtlin.domain.model.TransportProtocol
import io.github.drlacheheb.mqtlin.domain.repository.ProfileRepository
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * In-memory test double for ProfileRepository.
 */
class FakeProfileRepository(
    initialProfiles: List<ConnectionConfig> =
        listOf(
            ConnectionConfig(
                name = "Local Mosquitto",
                host = "127.0.0.1",
                port = 1883,
                clientId = "mqtlin_test_local",
                protocolVersion = MqttProtocolVersion.MQTT_5_0,
                transport = TransportProtocol.TCP,
            ),
        ),
) : ProfileRepository {
    private val profiles = initialProfiles.toMutableList()
    private var lastSelected: String? = initialProfiles.firstOrNull()?.name

    private val json =
        Json {
            prettyPrint = true
            ignoreUnknownKeys = true
            isLenient = true
        }

    override suspend fun getAllProfiles(): List<ConnectionConfig> = profiles.toList()

    override suspend fun saveProfile(profile: ConnectionConfig) {
        val index = profiles.indexOfFirst { it.name.equals(profile.name, ignoreCase = true) }
        if (index >= 0) {
            profiles[index] = profile
        } else {
            profiles.add(profile)
        }
        lastSelected = profile.name
    }

    override suspend fun deleteProfile(profileName: String) {
        profiles.removeAll { it.name.equals(profileName, ignoreCase = true) }
        if (lastSelected.equals(profileName, ignoreCase = true)) {
            lastSelected = profiles.firstOrNull()?.name
        }
    }

    override suspend fun getLastSelectedProfileName(): String? = lastSelected

    override suspend fun setLastSelectedProfileName(name: String) {
        lastSelected = name
    }

    override suspend fun exportProfilesJson(includePasswords: Boolean): String {
        val list = profiles.map { if (!includePasswords) it.copy(password = null) else it }
        return json.encodeToString(list)
    }

    override suspend fun importProfilesJson(jsonContent: String): List<ConnectionConfig> {
        val imported: List<ConnectionConfig> = json.decodeFromString(jsonContent)
        for (item in imported) {
            val index = profiles.indexOfFirst { it.name.equals(item.name, ignoreCase = true) }
            if (index >= 0) {
                profiles[index] = item
            } else {
                profiles.add(item)
            }
        }
        return profiles.toList()
    }
}
