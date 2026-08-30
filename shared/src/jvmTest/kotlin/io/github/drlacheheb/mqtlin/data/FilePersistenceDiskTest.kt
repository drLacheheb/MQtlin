package io.github.drlacheheb.mqtlin.data

import io.github.drlacheheb.mqtlin.data.repository.FileProfileRepository
import io.github.drlacheheb.mqtlin.data.repository.FileSettingsRepository
import io.github.drlacheheb.mqtlin.domain.model.ConnectionConfig
import io.github.drlacheheb.mqtlin.domain.model.MqttProtocolVersion
import io.github.drlacheheb.mqtlin.domain.repository.AppSettings
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import java.io.File
import java.nio.file.Files
import kotlin.test.Test

class FilePersistenceDiskTest {

    @Test
    fun `FileProfileRepository writes to disk and auto provisions default profiles`() = runTest {
        val tempDir = Files.createTempDirectory("mqtlin_test_profiles").toFile()
        try {
            val repo = FileProfileRepository(baseDirectory = tempDir)
            val initial = repo.getAllProfiles()

            // Auto-provisions 3 default profiles
            initial shouldHaveSize 3
            initial.map { it.name } shouldContain "Local Mosquitto"

            // Save custom profile
            val custom = ConnectionConfig(name = "Disk Test Broker", host = "192.168.1.200", port = 1883)
            repo.saveProfile(custom)

            // Re-instantiate repository pointing to same directory to verify persistence across restarts
            val repo2 = FileProfileRepository(baseDirectory = tempDir)
            val loaded = repo2.getAllProfiles()
            loaded shouldHaveSize 4
            loaded.map { it.name } shouldContain "Disk Test Broker"
            repo2.getLastSelectedProfileName() shouldBe "Disk Test Broker"

            // Delete profile
            repo2.deleteProfile("Disk Test Broker")
            val afterDelete = repo2.getAllProfiles()
            afterDelete shouldHaveSize 3
        } finally {
            tempDir.deleteRecursively()
        }
    }

    @Test
    fun `FileSettingsRepository writes settings json to disk and loads correctly`() = runTest {
        val tempDir = Files.createTempDirectory("mqtlin_test_settings").toFile()
        try {
            val repo = FileSettingsRepository(baseDirectory = tempDir)
            val defaults = repo.getSettings()

            defaults.maxHistoryPerTopic shouldBe 100
            defaults.autoReconnect shouldBe true
            defaults.defaultProtocolVersion shouldBe MqttProtocolVersion.MQTT_5_0

            // Update settings
            val updated = AppSettings(
                maxHistoryPerTopic = 250,
                autoReconnect = false,
                defaultProtocolVersion = MqttProtocolVersion.MQTT_3_1_1
            )
            repo.updateSettings(updated)

            // Re-read from new repository instance
            val repo2 = FileSettingsRepository(baseDirectory = tempDir)
            val loaded = repo2.getSettings()
            loaded.maxHistoryPerTopic shouldBe 250
            loaded.autoReconnect shouldBe false
            loaded.defaultProtocolVersion shouldBe MqttProtocolVersion.MQTT_3_1_1
        } finally {
            tempDir.deleteRecursively()
        }
    }
}
