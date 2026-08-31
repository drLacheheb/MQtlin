package io.github.drlacheheb.mqtlin.data.repository

import io.github.drlacheheb.mqtlin.domain.model.ConnectionConfig
import io.github.drlacheheb.mqtlin.domain.model.MqttProtocolVersion
import io.github.drlacheheb.mqtlin.domain.model.TransportProtocol
import io.github.drlacheheb.mqtlin.domain.repository.ProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

/**
 * File-based JSON implementation of ProfileRepository storing configs in ~/.mqtlin/profiles.json.
 */
class FileProfileRepository(
    private val baseDirectory: File = File(System.getProperty("user.home"), ".mqtlin"),
) : ProfileRepository {
    private val mutex = Mutex()
    private val profilesFile = File(baseDirectory, "profiles.json")
    private val lastProfileFile = File(baseDirectory, "last_profile.txt")

    private val json =
        Json {
            prettyPrint = true
            ignoreUnknownKeys = true
            isLenient = true
        }

    private val defaultProfiles: List<ConnectionConfig> =
        listOf(
            ConnectionConfig(
                name = "Local Mosquitto",
                host = "127.0.0.1",
                port = 1883,
                clientId = "mqtlin_local",
                protocolVersion = MqttProtocolVersion.MQTT_5_0,
                transport = TransportProtocol.TCP,
            ),
            ConnectionConfig(
                name = "HiveMQ Public Broker",
                host = "broker.hivemq.com",
                port = 1883,
                clientId = "mqtlin_hivemq",
                protocolVersion = MqttProtocolVersion.MQTT_5_0,
                transport = TransportProtocol.TCP,
            ),
        )

    private var cachedProfiles: List<ConnectionConfig>? = null
    private var cachedLastSelected: String? = null

    override suspend fun getAllProfiles(): List<ConnectionConfig> =
        withContext(Dispatchers.IO) {
            mutex.withLock {
                cachedProfiles?.let { return@withContext it }
                ensureDirectoryAndDefaults()
                val loaded = readProfilesFromFile()
                cachedProfiles = loaded
                loaded
            }
        }

    override suspend fun saveProfile(profile: ConnectionConfig) =
        withContext(Dispatchers.IO) {
            mutex.withLock {
                ensureDirectoryAndDefaults()
                val existing = (cachedProfiles ?: readProfilesFromFile()).toMutableList()
                val index = existing.indexOfFirst { it.name.equals(profile.name, ignoreCase = true) }
                if (index >= 0) {
                    existing[index] = profile
                } else {
                    existing.add(profile)
                }
                cachedProfiles = existing
                cachedLastSelected = profile.name
                writeProfilesToFile(existing)
                lastProfileFile.writeText(profile.name)
            }
        }

    override suspend fun deleteProfile(profileName: String) =
        withContext(Dispatchers.IO) {
            mutex.withLock {
                ensureDirectoryAndDefaults()
                val existing = (cachedProfiles ?: readProfilesFromFile()).toMutableList()
                val removed = existing.removeAll { it.name.equals(profileName, ignoreCase = true) }
                if (removed) {
                    cachedProfiles = existing
                    writeProfilesToFile(existing)
                    if (lastProfileFile.exists() && lastProfileFile.readText().trim().equals(profileName, ignoreCase = true)) {
                        lastProfileFile.delete()
                        cachedLastSelected = null
                    }
                }
            }
        }

    override suspend fun getLastSelectedProfileName(): String? =
        withContext(Dispatchers.IO) {
            mutex.withLock {
                if (cachedLastSelected != null) {
                    return@withContext cachedLastSelected
                }
                if (lastProfileFile.exists()) {
                    val name = lastProfileFile.readText().trim()
                    val result = name.ifBlank { null }
                    cachedLastSelected = result
                    result
                } else {
                    null
                }
            }
        }

    override suspend fun setLastSelectedProfileName(name: String) =
        withContext(Dispatchers.IO) {
            mutex.withLock {
                cachedLastSelected = name.trim()
                ensureDirectoryAndDefaults()
                lastProfileFile.writeText(name.trim())
            }
        }

    override suspend fun exportProfilesJson(includePasswords: Boolean): String =
        withContext(Dispatchers.IO) {
            mutex.withLock {
                val profiles =
                    readProfilesFromFile().map { profile ->
                        if (!includePasswords && profile.password != null) {
                            profile.copy(password = null)
                        } else {
                            profile
                        }
                    }
                json.encodeToString(profiles)
            }
        }

    override suspend fun importProfilesJson(jsonContent: String): List<ConnectionConfig> =
        withContext(Dispatchers.IO) {
            mutex.withLock {
                val imported: List<ConnectionConfig> = json.decodeFromString(jsonContent)
                val current = readProfilesFromFile().toMutableList()
                for (item in imported) {
                    val index = current.indexOfFirst { it.name.equals(item.name, ignoreCase = true) }
                    if (index >= 0) {
                        current[index] = item
                    } else {
                        current.add(item)
                    }
                }
                writeProfilesToFile(current)
                current
            }
        }

    private fun ensureDirectoryAndDefaults() {
        if (!baseDirectory.exists()) {
            baseDirectory.mkdirs()
        }
        if (!profilesFile.exists()) {
            writeProfilesToFile(defaultProfiles)
            if (defaultProfiles.isNotEmpty()) {
                lastProfileFile.writeText(defaultProfiles.first().name)
            }
        }
    }

    private fun readProfilesFromFile(): List<ConnectionConfig> {
        if (!profilesFile.exists()) return defaultProfiles
        return try {
            val text = profilesFile.readText()
            if (text.isBlank()) defaultProfiles else json.decodeFromString(text)
        } catch (ignored: Exception) {
            defaultProfiles
        }
    }

    private fun writeProfilesToFile(profiles: List<ConnectionConfig>) {
        val content = json.encodeToString(profiles)
        profilesFile.writeText(content)
    }
}
