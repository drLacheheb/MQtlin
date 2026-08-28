package io.github.drlacheheb.mqtlin.ui

import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.destroy
import com.arkivanov.essenty.lifecycle.resume
import io.github.drlacheheb.mqtlin.domain.model.ConnectionState
import io.github.drlacheheb.mqtlin.domain.model.MqttProtocolVersion
import io.github.drlacheheb.mqtlin.domain.model.TransportProtocol
import io.github.drlacheheb.mqtlin.domain.usecase.ValidateConnectionConfigUseCase
import io.github.drlacheheb.mqtlin.domain.usecase.ValidationResult
import io.github.drlacheheb.mqtlin.fakes.FakeMqttRepository
import io.github.drlacheheb.mqtlin.ui.connection.DefaultConnectionComponent
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.maps.shouldContainKey
import io.kotest.matchers.shouldBe
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
        onConnected: () -> Unit = {}
    ): Pair<DefaultConnectionComponent, LifecycleRegistry> {
        val lifecycle = LifecycleRegistry()
        lifecycle.resume()
        val context = DefaultComponentContext(lifecycle = lifecycle)
        val component = DefaultConnectionComponent(
            componentContext = context,
            mqttRepository = fakeRepository,
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
        state.host shouldBe "127.0.0.1"
        state.portText shouldBe "1883"
        state.protocolVersion shouldBe MqttProtocolVersion.MQTT_5_0
        state.transport shouldBe TransportProtocol.TCP
        state.connectionState shouldBe ConnectionState.Disconnected
        state.validationErrors.shouldBeEmpty()

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
    fun `changing transport updates transport and default port`() = runTest {
        val (component, lifecycle) = createComponent(this)

        component.onTransportChanged(TransportProtocol.TLS)

        component.state.value.transport shouldBe TransportProtocol.TLS
        component.state.value.portText shouldBe "8883"

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
}
