package io.github.drlacheheb.mqtlin.ui

import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.destroy
import com.arkivanov.essenty.lifecycle.resume
import io.github.drlacheheb.mqtlin.domain.model.ConnectionConfig
import io.github.drlacheheb.mqtlin.domain.model.ConnectionState
import io.github.drlacheheb.mqtlin.domain.model.MqttProtocolVersion
import io.github.drlacheheb.mqtlin.domain.model.TransportProtocol
import io.github.drlacheheb.mqtlin.domain.usecase.ValidateConnectionConfigUseCase
import io.github.drlacheheb.mqtlin.domain.usecase.ValidationResult
import io.github.drlacheheb.mqtlin.fakes.FakeMqttRepository
import io.github.drlacheheb.mqtlin.ui.connection.DefaultConnectionComponent
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.maps.shouldContainKey
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldStartWith
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ConnectionComponentTest {

    private val fakeRepository = FakeMqttRepository()
    private val validateUseCase = ValidateConnectionConfigUseCase()

    private fun createComponent(
        testScope: TestScope,
        profileRepository: io.github.drlacheheb.mqtlin.domain.repository.ProfileRepository? = null,
        onConnected: (ConnectionConfig) -> Unit = {}
    ): Pair<DefaultConnectionComponent, LifecycleRegistry> {
        val lifecycle = LifecycleRegistry()
        lifecycle.resume()
        val context = DefaultComponentContext(lifecycle = lifecycle)
        val component = DefaultConnectionComponent(
            componentContext = context,
            mqttRepository = fakeRepository,
            profileRepository = profileRepository,
            validateConfigUseCase = validateUseCase,
            onConnected = onConnected,
            mainContext = StandardTestDispatcher(testScope.testScheduler)
        )
        return Pair(component, lifecycle)
    }

    @Test
    fun `initial state has default local mosquitto parameters`() = runTest {
        val (component, lifecycle) = createComponent(this)

        val state = component.state.value
        state.name shouldBe "Local Mosquitto"
        state.host shouldBe "127.0.0.1"
        state.portText shouldBe "1883"
        state.clientId shouldBe "mqtlin_client_8f9a2b"
        state.protocolVersion shouldBe MqttProtocolVersion.MQTT_5_0
        state.transport shouldBe TransportProtocol.TCP
        state.username shouldBe ""
        state.password shouldBe ""
        state.connectionState shouldBe ConnectionState.Disconnected
        state.validationErrors.shouldBeEmpty()

        lifecycle.destroy()
    }

    @Test
    fun `changing profile name updates state`() = runTest {
        val (component, lifecycle) = createComponent(this)

        component.onNameChanged("Production Cluster")

        component.state.value.name shouldBe "Production Cluster"

        lifecycle.destroy()
    }

    @Test
    fun `changing profile name with string exceeding max length clamps to MAX_PROFILE_NAME_LENGTH`() = runTest {
        val (component, lifecycle) = createComponent(this)

        val longName = "A".repeat(100)
        component.onNameChanged(longName)

        component.state.value.name.length shouldBe 32
        component.state.value.name shouldBe "A".repeat(32)

        lifecycle.destroy()
    }

    @Test
    fun `changing host updates state and clears field validation error`() = runTest {
        val (component, lifecycle) = createComponent(this)

        component.onHostChanged("broker.hivemq.com")

        component.state.value.host shouldBe "broker.hivemq.com"

        lifecycle.destroy()
    }

    @Test
    fun `changing port updates state and clears port validation error`() = runTest {
        val (component, lifecycle) = createComponent(this)

        component.onPortChanged("8883")

        component.state.value.portText shouldBe "8883"

        lifecycle.destroy()
    }

    @Test
    fun `changing client ID updates state and clears client ID error`() = runTest {
        val (component, lifecycle) = createComponent(this)

        component.onClientIdChanged("custom_client_42")

        component.state.value.clientId shouldBe "custom_client_42"

        lifecycle.destroy()
    }

    @Test
    fun `generating random client ID updates clientId with mqtlin_client prefix`() = runTest {
        val (component, lifecycle) = createComponent(this)

        component.onGenerateRandomClientId()

        component.state.value.clientId shouldStartWith "mqtlin_client_"

        lifecycle.destroy()
    }

    @Test
    fun `changing protocol version updates state`() = runTest {
        val (component, lifecycle) = createComponent(this)

        component.onProtocolVersionChanged(MqttProtocolVersion.MQTT_3_1_1)

        component.state.value.protocolVersion shouldBe MqttProtocolVersion.MQTT_3_1_1

        lifecycle.destroy()
    }

    @Test
    fun `changing transport updates transport and default port`() = runTest {
        val (component, lifecycle) = createComponent(this)

        component.onTransportChanged(TransportProtocol.TLS)

        component.state.value.transport shouldBe TransportProtocol.TLS
        component.state.value.portText shouldBe "8883"

        component.onTransportChanged(TransportProtocol.WSS)

        component.state.value.transport shouldBe TransportProtocol.WSS
        component.state.value.portText shouldBe "8084"

        component.onTransportChanged(TransportProtocol.WS)

        component.state.value.transport shouldBe TransportProtocol.WS
        component.state.value.portText shouldBe "8083"

        lifecycle.destroy()
    }

    @Test
    fun `changing username and password updates state`() = runTest {
        val (component, lifecycle) = createComponent(this)

        component.onUsernameChanged("admin")
        component.onPasswordChanged("secret123")

        component.state.value.username shouldBe "admin"
        component.state.value.password shouldBe "secret123"

        lifecycle.destroy()
    }

    @Test
    fun `clicking connect with invalid port sets PORT validation error without connecting`() = runTest {
        val (component, lifecycle) = createComponent(this)
        component.onPortChanged("invalid_port")

        component.onConnectClicked()
        advanceUntilIdle()

        component.state.value.validationErrors shouldContainKey ValidationResult.Field.PORT
        component.state.value.connectionState shouldBe ConnectionState.Disconnected

        lifecycle.destroy()
    }

    @Test
    fun `clicking connect with blank host sets validation error without calling repository`() = runTest {
        val (component, lifecycle) = createComponent(this)
        component.onHostChanged("")

        component.onConnectClicked()
        advanceUntilIdle()

        component.state.value.validationErrors shouldContainKey ValidationResult.Field.HOST
        component.state.value.connectionState shouldBe ConnectionState.Disconnected

        lifecycle.destroy()
    }

    @Test
    fun `successful connection transitions through Connected state and triggers callback`() = runTest {
        var onConnectedCalled = false
        val (component, lifecycle) = createComponent(this, onConnected = { onConnectedCalled = true })
        component.onHostChanged("broker.emqx.io")
        component.onPortChanged("1883")

        component.onConnectClicked()
        advanceUntilIdle()

        component.state.value.connectionState.shouldBeInstanceOf<ConnectionState.Connected>()
        val connectedState = component.state.value.connectionState as ConnectionState.Connected
        connectedState.host shouldBe "broker.emqx.io"
        connectedState.port shouldBe 1883
        onConnectedCalled shouldBe true

        lifecycle.destroy()
    }

    @Test
    fun `failed connection transitions to Error state with message`() = runTest {
        fakeRepository.shouldFailConnection = true
        fakeRepository.failureErrorMessage = "Host unreachable"

        val (component, lifecycle) = createComponent(this)
        component.onHostChanged("invalid.host")

        component.onConnectClicked()
        advanceUntilIdle()

        component.state.value.connectionState.shouldBeInstanceOf<ConnectionState.Error>()
        val errorState = component.state.value.connectionState as ConnectionState.Error
        errorState.message shouldBe "Host unreachable"

        lifecycle.destroy()
    }

    @Test
    fun `dismissing error state resets connectionState to Disconnected`() = runTest {
        fakeRepository.shouldFailConnection = true
        fakeRepository.failureErrorMessage = "TLS Handshake Failed"

        val (component, lifecycle) = createComponent(this)
        component.onConnectClicked()
        advanceUntilIdle()

        component.state.value.connectionState.shouldBeInstanceOf<ConnectionState.Error>()

        component.onDismissError()

        component.state.value.connectionState shouldBe ConnectionState.Disconnected

        lifecycle.destroy()
    }

    @Test
    fun `clicking disconnect resets state to Disconnected`() = runTest {
        val (component, lifecycle) = createComponent(this)
        component.onConnectClicked()
        advanceUntilIdle()

        component.state.value.connectionState.shouldBeInstanceOf<ConnectionState.Connected>()

        component.onDisconnectClicked()
        advanceUntilIdle()

        component.state.value.connectionState shouldBe ConnectionState.Disconnected

        lifecycle.destroy()
    }

    @Test
    fun `testing connection successfully sets success message without calling onConnected callback`() = runTest {
        var onConnectedCalled = false
        val (component, lifecycle) = createComponent(this, onConnected = { onConnectedCalled = true })

        component.onTestConnectionClicked()
        advanceUntilIdle()

        onConnectedCalled shouldBe false
        component.state.value.testSuccessMessage shouldBe "Successfully connected to 127.0.0.1:1883!"
        component.state.value.isTesting shouldBe false

        lifecycle.destroy()
    }

    @Test
    fun `testing connection with broker failure sets error state without calling onConnected callback`() = runTest {
        var onConnectedCalled = false
        fakeRepository.shouldFailConnection = true
        fakeRepository.failureErrorMessage = "Connection Refused"

        val (component, lifecycle) = createComponent(this, onConnected = { onConnectedCalled = true })

        component.onTestConnectionClicked()
        advanceUntilIdle()

        onConnectedCalled shouldBe false
        component.state.value.testSuccessMessage shouldBe null
        component.state.value.connectionState.shouldBeInstanceOf<ConnectionState.Error>()
        val error = component.state.value.connectionState as ConnectionState.Error
        error.message shouldContain "Connection refused"

        lifecycle.destroy()
    }

    @Test
    fun `clicking new profile resets state to defaults with new random client ID`() = runTest {
        val (component, lifecycle) = createComponent(this)

        component.onNameChanged("Custom Profile")
        component.onHostChanged("192.168.1.50")
        component.onPortChanged("8883")

        component.onNewProfileClicked()

        val state = component.state.value
        state.name shouldBe "New Connection"
        state.host shouldBe "127.0.0.1"
        state.portText shouldBe "1883"
        state.clientId shouldStartWith "mqtlin_client_"

        lifecycle.destroy()
    }

    @Test
    fun `selecting a saved profile populates all form fields from profile`() = runTest {
        val (component, lifecycle) = createComponent(this)
        val testProfile = io.github.drlacheheb.mqtlin.domain.model.ConnectionConfig(
            name = "Staging Cluster",
            host = "staging.emqx.io",
            port = 8883,
            clientId = "staging_client_1",
            protocolVersion = MqttProtocolVersion.MQTT_3_1_1,
            transport = TransportProtocol.TLS,
            username = "stage_user",
            password = "stage_password"
        )

        component.onProfileSelected(testProfile)

        val state = component.state.value
        state.name shouldBe "Staging Cluster"
        state.host shouldBe "staging.emqx.io"
        state.portText shouldBe "8883"
        state.clientId shouldBe "staging_client_1"
        state.protocolVersion shouldBe MqttProtocolVersion.MQTT_3_1_1
        state.transport shouldBe TransportProtocol.TLS
        state.username shouldBe "stage_user"
        state.password shouldBe "stage_password"

        lifecycle.destroy()
    }

    @Test
    fun `connecting saves the profile into savedProfiles list`() = runTest {
        val (component, lifecycle) = createComponent(this)
        component.onNameChanged("Production Server")
        component.onHostChanged("mqtt.example.com")
        component.onPortChanged("1883")

        component.onConnectClicked()
        advanceUntilIdle()

        component.state.value.savedProfiles.size shouldBe 1
        val saved = component.state.value.savedProfiles[0]
        saved.name shouldBe "Production Server"
        saved.host shouldBe "mqtt.example.com"

        lifecycle.destroy()
    }

    @Test
    fun `deleting a saved profile removes it from savedProfiles list`() = runTest {
        val (component, lifecycle) = createComponent(this)
        val profile = io.github.drlacheheb.mqtlin.domain.model.ConnectionConfig(
            name = "Temp Profile",
            host = "10.0.0.1",
            port = 1883
        )

        component.onNameChanged(profile.name)
        component.onHostChanged(profile.host)
        component.onConnectClicked()
        advanceUntilIdle()

        component.state.value.savedProfiles.size shouldBe 1

        component.onDeleteProfileClicked(profile)

        component.state.value.savedProfiles.shouldBeEmpty()

        lifecycle.destroy()
    }

    @Test
    fun `opening connection component when repository is already connected does not auto trigger onConnected`() = runTest {
        var onConnectedCalled = false
        // Simulate repository already connected from a previous workspace session
        fakeRepository.setConnected(
            io.github.drlacheheb.mqtlin.domain.model.ConnectionConfig(
                name = "Active Mosquitto",
                host = "127.0.0.1",
                port = 1883,
                clientId = "active_client",
                protocolVersion = MqttProtocolVersion.MQTT_5_0
            )
        )

        val (component, lifecycle) = createComponent(this, onConnected = { onConnectedCalled = true })
        advanceUntilIdle()

        // onConnected must NOT be called without explicit user action
        onConnectedCalled shouldBe false
        component.state.value.connectionState.shouldBeInstanceOf<ConnectionState.Connected>()

        lifecycle.destroy()
    }

    @Test
    fun `initial state loads saved profiles from ProfileRepository and populates active profile`() = runTest {
        val initialProfile = io.github.drlacheheb.mqtlin.domain.model.ConnectionConfig(
            name = "Saved AWS",
            host = "aws.iot.com",
            port = 8883
        )
        val fakeProfileRepo = io.github.drlacheheb.mqtlin.fakes.FakeProfileRepository(initialProfiles = listOf(initialProfile))

        val (component, lifecycle) = createComponent(this, profileRepository = fakeProfileRepo)
        advanceUntilIdle()

        val state = component.state.value
        state.savedProfiles.size shouldBe 1
        state.savedProfiles[0].name shouldBe "Saved AWS"
        state.name shouldBe "Saved AWS"
        state.host shouldBe "aws.iot.com"

        lifecycle.destroy()
    }

    @Test
    fun `switching between connected profile and other profile toggles between Connected and Disconnected state`() = runTest {
        val activeProfile = io.github.drlacheheb.mqtlin.domain.model.ConnectionConfig(
            name = "Local Active",
            host = "127.0.0.1",
            port = 1883,
            clientId = "active_client"
        )
        val otherProfile = io.github.drlacheheb.mqtlin.domain.model.ConnectionConfig(
            name = "Remote Inactive",
            host = "remote.broker.com",
            port = 1883,
            clientId = "remote_client"
        )
        fakeRepository.setConnected(activeProfile)

        val fakeProfileRepo = io.github.drlacheheb.mqtlin.fakes.FakeProfileRepository(
            initialProfiles = listOf(activeProfile, otherProfile)
        )

        val (component, lifecycle) = createComponent(this, profileRepository = fakeProfileRepo)
        advanceUntilIdle()

        // Active profile is initially loaded and should be Connected
        component.state.value.name shouldBe "Local Active"
        component.state.value.connectionState.shouldBeInstanceOf<ConnectionState.Connected>()

        // Switching to other profile should set state to Disconnected
        component.onProfileSelected(otherProfile)
        component.state.value.name shouldBe "Remote Inactive"
        component.state.value.connectionState shouldBe ConnectionState.Disconnected

        // Switching back to active profile should restore Connected state
        component.onProfileSelected(activeProfile)
        component.state.value.name shouldBe "Local Active"
        component.state.value.connectionState.shouldBeInstanceOf<ConnectionState.Connected>()

        lifecycle.destroy()
    }

    @Test
    fun `testing connection does not disconnect active repository session`() = runTest {
        val activeProfile = io.github.drlacheheb.mqtlin.domain.model.ConnectionConfig(
            name = "Active Mosquitto",
            host = "127.0.0.1",
            port = 1883,
            clientId = "active_client"
        )
        fakeRepository.setConnected(activeProfile)

        val (component, lifecycle) = createComponent(this)
        component.onHostChanged("192.168.1.100")
        component.onPortChanged("1883")

        component.onTestConnectionClicked()
        advanceUntilIdle()

        // Test was successful for 192.168.1.100
        component.state.value.testSuccessMessage shouldBe "Successfully connected to 192.168.1.100:1883!"

        // Main repository connection state remains Connected to the active broker!
        fakeRepository.connectionState.value.shouldBeInstanceOf<ConnectionState.Connected>()
        (fakeRepository.connectionState.value as ConnectionState.Connected).host shouldBe "127.0.0.1"

        lifecycle.destroy()
    }

    @Test
    fun `editing profile name and saving updates savedProfiles list and repository`() = runTest {
        val initialProfile = io.github.drlacheheb.mqtlin.domain.model.ConnectionConfig(
            name = "Local Mosquitto",
            host = "127.0.0.1",
            port = 1883
        )
        val fakeProfileRepo = io.github.drlacheheb.mqtlin.fakes.FakeProfileRepository(
            initialProfiles = listOf(initialProfile)
        )

        val (component, lifecycle) = createComponent(this, profileRepository = fakeProfileRepo)
        advanceUntilIdle()

        component.state.value.name shouldBe "Local Mosquitto"

        // User types new name
        component.onNameChanged("My Office Mosquitto")
        // User presses Enter or leaves focus (save name)
        component.onSaveProfileName()
        advanceUntilIdle()

        // UI state updated
        component.state.value.name shouldBe "My Office Mosquitto"
        val savedNames = component.state.value.savedProfiles.map { it.name }
        savedNames shouldContain "My Office Mosquitto"
        savedNames shouldNotContain "Local Mosquitto"

        // Repository updated
        val repoProfiles = fakeProfileRepo.getAllProfiles().map { it.name }
        repoProfiles shouldContain "My Office Mosquitto"
        repoProfiles shouldNotContain "Local Mosquitto"
        fakeProfileRepo.getLastSelectedProfileName() shouldBe "My Office Mosquitto"

        lifecycle.destroy()
    }

    @Test
    fun `saving blank profile name falls back to original profile name`() = runTest {
        val initialProfile = io.github.drlacheheb.mqtlin.domain.model.ConnectionConfig(
            name = "Production Cluster",
            host = "broker.emqx.io",
            port = 1883
        )
        val fakeProfileRepo = io.github.drlacheheb.mqtlin.fakes.FakeProfileRepository(
            initialProfiles = listOf(initialProfile)
        )

        val (component, lifecycle) = createComponent(this, profileRepository = fakeProfileRepo)
        advanceUntilIdle()

        // User clears name to blank
        component.onNameChanged("   ")
        component.onSaveProfileName()
        advanceUntilIdle()

        // Falls back to original name
        component.state.value.name shouldBe "Production Cluster"
        component.state.value.savedProfiles.map { it.name } shouldContain "Production Cluster"

        lifecycle.destroy()
    }

    @Test
    fun `editing name of newly created profile and saving commits new profile name`() = runTest {
        val fakeProfileRepo = io.github.drlacheheb.mqtlin.fakes.FakeProfileRepository()

        val (component, lifecycle) = createComponent(this, profileRepository = fakeProfileRepo)
        advanceUntilIdle()

        component.onNewProfileClicked()
        advanceUntilIdle()

        component.onNameChanged("Custom IoT Lab")
        component.onSaveProfileName()
        advanceUntilIdle()

        component.state.value.name shouldBe "Custom IoT Lab"
        component.state.value.savedProfiles.map { it.name } shouldContain "Custom IoT Lab"
        fakeProfileRepo.getLastSelectedProfileName() shouldBe "Custom IoT Lab"

        lifecycle.destroy()
    }
}
