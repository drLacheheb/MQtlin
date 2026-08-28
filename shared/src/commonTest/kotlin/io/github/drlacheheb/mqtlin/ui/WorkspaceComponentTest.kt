package io.github.drlacheheb.mqtlin.ui

import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.destroy
import com.arkivanov.essenty.lifecycle.resume
import io.github.drlacheheb.mqtlin.domain.model.ConnectionConfig
import io.github.drlacheheb.mqtlin.domain.model.MqttMessage
import io.github.drlacheheb.mqtlin.fakes.FakeMqttRepository
import io.github.drlacheheb.mqtlin.ui.workspace.DefaultWorkspaceComponent
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WorkspaceComponentTest {

    private val fakeRepository = FakeMqttRepository()
    private val testConfig = ConnectionConfig(host = "broker.hivemq.com", port = 1883)

    private fun createComponent(
        testScope: TestScope,
        onDisconnect: () -> Unit = {},
        onOpenConnectionManager: () -> Unit = {}
    ): Pair<DefaultWorkspaceComponent, LifecycleRegistry> {
        val lifecycle = LifecycleRegistry()
        lifecycle.resume()
        val context = DefaultComponentContext(lifecycle = lifecycle)
        val component = DefaultWorkspaceComponent(
            componentContext = context,
            config = testConfig,
            mqttRepository = fakeRepository,
            onDisconnect = onDisconnect,
            onOpenConnectionManager = onOpenConnectionManager,
            mainContext = StandardTestDispatcher(testScope.testScheduler)
        )
        testScope.advanceUntilIdle()
        return Pair(component, lifecycle)
    }

    @Test
    fun `initial workspace state starts with empty topic tree and connected config`() = runTest {
        val (component, lifecycle) = createComponent(this)

        val state = component.state.value
        state.connectionConfig?.host shouldBe "broker.hivemq.com"
        state.rawTopicTree.rootNodes.shouldBeEmpty()
        state.selectedTopicPath.shouldBeNull()
        state.selectedNode.shouldBeNull()
        state.filterQuery shouldBe ""

        lifecycle.destroy()
    }

    @Test
    fun `incoming MQTT message from repository is inserted into topic tree state`() = runTest {
        val (component, lifecycle) = createComponent(this)

        fakeRepository.emitMessage(
            MqttMessage(
                topic = "home/living-room/temperature",
                payload = "21.5".encodeToByteArray()
            )
        )
        advanceUntilIdle()

        val state = component.state.value
        state.rawTopicTree.rootNodes shouldHaveSize 1
        val homeNode = state.rawTopicTree.rootNodes[0]
        homeNode.segment shouldBe "home"

        val tempNode = state.rawTopicTree.findNode("home/living-room/temperature")
        tempNode.shouldNotBeNull()
        tempNode.lastMessage?.payloadString shouldBe "21.5"

        lifecycle.destroy()
    }

    @Test
    fun `selecting topic updates selectedTopicPath and selectedNode in state`() = runTest {
        val (component, lifecycle) = createComponent(this)

        fakeRepository.emitMessage(
            MqttMessage(
                topic = "factory/sensor/pressure",
                payload = "101.3".encodeToByteArray()
            )
        )
        advanceUntilIdle()

        component.onTopicSelected("factory/sensor/pressure")

        val state = component.state.value
        state.selectedTopicPath shouldBe "factory/sensor/pressure"
        state.selectedNode.shouldNotBeNull()
        state.selectedNode?.lastMessage?.payloadString shouldBe "101.3"

        lifecycle.destroy()
    }

    @Test
    fun `toggling node expansion updates topic tree state`() = runTest {
        val (component, lifecycle) = createComponent(this)

        fakeRepository.emitMessage(
            MqttMessage(
                topic = "home/living-room/temperature",
                payload = "20".encodeToByteArray()
            )
        )
        advanceUntilIdle()

        val initialNode = component.state.value.rawTopicTree.findNode("home")
        initialNode?.isExpanded shouldBe true

        component.onToggleExpand("home")

        val collapsedNode = component.state.value.rawTopicTree.findNode("home")
        collapsedNode?.isExpanded shouldBe false

        lifecycle.destroy()
    }

    @Test
    fun `filtering topic tree queries only matching branches`() = runTest {
        val (component, lifecycle) = createComponent(this)

        fakeRepository.emitMessage(MqttMessage("zone-a/temp", "20".encodeToByteArray()))
        fakeRepository.emitMessage(MqttMessage("zone-b/temp", "22".encodeToByteArray()))
        advanceUntilIdle()

        component.onFilterQueryChanged("zone-a")

        val filtered = component.state.value.filteredTopicTree
        filtered.findNode("zone-a/temp").shouldNotBeNull()
        filtered.findNode("zone-b/temp").shouldBeNull()

        lifecycle.destroy()
    }

    @Test
    fun `publishing message invokes repository and updates topic tree`() = runTest {
        val (component, lifecycle) = createComponent(this)

        component.onPublishMessage(
            topic = "actuators/relay/1",
            payload = "{\"state\": \"ON\"}",
            qos = 1,
            isRetained = true
        )
        advanceUntilIdle()

        fakeRepository.publishedMessages shouldHaveSize 1
        val published = fakeRepository.publishedMessages[0]
        published.topic shouldBe "actuators/relay/1"
        published.payloadString shouldBe "{\"state\": \"ON\"}"
        published.qos shouldBe 1
        published.isRetained shouldBe true

        // Tree is updated automatically from the incoming message echo
        val node = component.state.value.rawTopicTree.findNode("actuators/relay/1")
        node.shouldNotBeNull()
        node.lastMessage?.payloadString shouldBe "{\"state\": \"ON\"}"

        lifecycle.destroy()
    }

    @Test
    fun `publishing message with blank topic is ignored`() = runTest {
        val (component, lifecycle) = createComponent(this)

        component.onPublishMessage(
            topic = "   ",
            payload = "data",
            qos = 0,
            isRetained = false
        )
        advanceUntilIdle()

        fakeRepository.publishedMessages.shouldBeEmpty()

        lifecycle.destroy()
    }

    @Test
    fun `publishing message with leading slash automatically trims prefix and publishes clean topic path`() = runTest {
        val (component, lifecycle) = createComponent(this)

        component.onPublishMessage(
            topic = "/home/living-room/temperature",
            payload = "24.2",
            qos = 0,
            isRetained = false
        )
        advanceUntilIdle()

        fakeRepository.publishedMessages shouldHaveSize 1
        val published = fakeRepository.publishedMessages[0]
        published.topic shouldBe "home/living-room/temperature"

        val node = component.state.value.rawTopicTree.findNode("home/living-room/temperature")
        node.shouldNotBeNull()
        node.lastMessage?.payloadString shouldBe "24.2"

        lifecycle.destroy()
    }

    @Test
    fun `accumulating multiple messages under the same topic updates message count and last message`() = runTest {
        val (component, lifecycle) = createComponent(this)

        fakeRepository.emitMessage(MqttMessage("sensors/temp", "20.0".encodeToByteArray()))
        fakeRepository.emitMessage(MqttMessage("sensors/temp", "21.5".encodeToByteArray()))
        fakeRepository.emitMessage(MqttMessage("sensors/temp", "22.8".encodeToByteArray()))
        advanceUntilIdle()

        val node = component.state.value.rawTopicTree.findNode("sensors/temp")
        node.shouldNotBeNull()
        node.messageCount shouldBe 3
        node.lastMessage?.payloadString shouldBe "22.8"

        lifecycle.destroy()
    }

    @Test
    fun `disconnecting from repository triggers onDisconnect callback`() = runTest {
        var onDisconnectCalled = false
        val (component, lifecycle) = createComponent(this, onDisconnect = { onDisconnectCalled = true })

        fakeRepository.disconnect()
        advanceUntilIdle()

        onDisconnectCalled shouldBe true

        lifecycle.destroy()
    }
}
