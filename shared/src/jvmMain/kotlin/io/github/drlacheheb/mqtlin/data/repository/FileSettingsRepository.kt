package io.github.drlacheheb.mqtlin.data.repository

import io.github.drlacheheb.mqtlin.domain.repository.AppSettings
import io.github.drlacheheb.mqtlin.domain.repository.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

/**
 * File-based implementation of SettingsRepository storing settings in ~/.mqtlin/settings.json.
 */
class FileSettingsRepository(
    private val baseDirectory: File = File(System.getProperty("user.home"), ".mqtlin"),
) : SettingsRepository {
    private val mutex = Mutex()
    private val settingsFile = File(baseDirectory, "settings.json")

    private val json =
        Json {
            prettyPrint = true
            ignoreUnknownKeys = true
            isLenient = true
        }

    override suspend fun getSettings(): AppSettings =
        withContext(Dispatchers.IO) {
            mutex.withLock {
                if (!settingsFile.exists()) {
                    val default = AppSettings()
                    ensureDirectory()
                    settingsFile.writeText(json.encodeToString(default))
                    default
                } else {
                    try {
                        val text = settingsFile.readText()
                        if (text.isBlank()) AppSettings() else json.decodeFromString(text)
                    } catch (ignored: Exception) {
                        AppSettings()
                    }
                }
            }
        }

    override suspend fun updateSettings(settings: AppSettings) =
        withContext(Dispatchers.IO) {
            mutex.withLock {
                ensureDirectory()
                settingsFile.writeText(json.encodeToString(settings))
            }
        }

    private fun ensureDirectory() {
        if (!baseDirectory.exists()) {
            baseDirectory.mkdirs()
        }
    }
}
