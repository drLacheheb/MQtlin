package io.github.drlacheheb.mqtlin.domain

import io.github.drlacheheb.mqtlin.domain.model.ConnectionConfig
import io.github.drlacheheb.mqtlin.domain.model.MqttProtocolVersion
import io.github.drlacheheb.mqtlin.domain.model.TransportProtocol
import io.github.drlacheheb.mqtlin.fakes.FakeProfileRepository
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotContain
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class ProfilePersistenceTest {

    @Test
    fun `getAllProfiles returns initial stored profiles`() = runTest {
        val repository = FakeProfileRepository()
        val profiles = repository.getAllProfiles()

        profiles shouldHaveSize 1
        profiles[0].name shouldBe "Local Mosquitto"
    }

    @Test
    fun `saveProfile inserts new profile and updates last selected`() = runTest {
        val repository = FakeProfileRepository()
        val newProfile = ConnectionConfig(
            name = "Production EMQX",
            host = "broker.emqx.io",
            port = 8883,
            transport = TransportProtocol.TLS,
            protocolVersion = MqttProtocolVersion.MQTT_5_0
        )

        repository.saveProfile(newProfile)

        val profiles = repository.getAllProfiles()
        profiles shouldHaveSize 2
        profiles.map { it.name } shouldContain "Production EMQX"
        repository.getLastSelectedProfileName() shouldBe "Production EMQX"
    }

    @Test
    fun `saveProfile with existing name updates existing profile without duplicating`() = runTest {
        val repository = FakeProfileRepository()
        val updated = ConnectionConfig(
            name = "Local Mosquitto",
            host = "192.168.1.100",
            port = 1883
        )

        repository.saveProfile(updated)

        val profiles = repository.getAllProfiles()
        profiles shouldHaveSize 1
        profiles[0].host shouldBe "192.168.1.100"
    }

    @Test
    fun `deleteProfile removes profile and updates last selected if needed`() = runTest {
        val repository = FakeProfileRepository()
        val secondProfile = ConnectionConfig(name = "Staging Broker", host = "staging.local")
        repository.saveProfile(secondProfile)

        repository.deleteProfile("Staging Broker")

        val profiles = repository.getAllProfiles()
        profiles shouldHaveSize 1
        profiles[0].name shouldBe "Local Mosquitto"
        repository.getLastSelectedProfileName() shouldBe "Local Mosquitto"
    }

    @Test
    fun `exportProfilesJson exports JSON and respects password exclusion flag`() = runTest {
        val repository = FakeProfileRepository()
        val secretProfile = ConnectionConfig(
            name = "Secret Broker",
            host = "secure.io",
            username = "admin",
            password = "SuperSecretPassword123"
        )
        repository.saveProfile(secretProfile)

        val exportWithoutPasswords = repository.exportProfilesJson(includePasswords = false)
        exportWithoutPasswords shouldNotContain "SuperSecretPassword123"

        val exportWithPasswords = repository.exportProfilesJson(includePasswords = true)
        exportWithPasswords.contains("SuperSecretPassword123") shouldBe true
    }

    @Test
    fun `importProfilesJson parses JSON and merges profiles`() = runTest {
        val repository = FakeProfileRepository()
        val jsonPayload = """
            [
              {
                "name": "Imported HiveMQ",
                "host": "broker.hivemq.com",
                "port": 1883,
                "clientId": "imported_client",
                "protocolVersion": "MQTT_5_0",
                "transport": "TCP",
                "keepAliveSeconds": 60,
                "cleanStart": true,
                "sessionExpiryIntervalSeconds": 0
              }
            ]
        """.trimIndent()

        val result = repository.importProfilesJson(jsonPayload)
        result shouldHaveSize 2
        result.map { it.name } shouldContain "Imported HiveMQ"
    }
}
