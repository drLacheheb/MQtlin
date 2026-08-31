package io.github.drlacheheb.mqtlin.ui

import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import io.github.drlacheheb.mqtlin.domain.model.ConnectionConfig
import io.github.drlacheheb.mqtlin.domain.model.MqttMessage
import io.github.drlacheheb.mqtlin.domain.model.TopicNode
import io.github.drlacheheb.mqtlin.fakes.FakeMqttRepository
import io.github.drlacheheb.mqtlin.ui.workspace.DefaultWorkspaceComponent
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TopicTreeContextMenuTest {
    @Test
    fun `publish here action selects topic and prepares workspace state`() =
        runTest {
            val testDispatcher = StandardTestDispatcher(testScheduler)
            val fakeRepo = FakeMqttRepository()
            val lifecycle = LifecycleRegistry()
            lifecycle.resume()
            val context = DefaultComponentContext(lifecycle = lifecycle)

            val component =
                DefaultWorkspaceComponent(
                    componentContext = context,
                    config = ConnectionConfig(name = "Test", host = "localhost"),
                    mqttRepository = fakeRepo,
                    onDisconnect = {},
                    onOpenConnectionManager = {},
                    mainContext = testDispatcher,
                )
            advanceUntilIdle()

            fakeRepo.emitMessage(MqttMessage("devices/camera/live", "active".encodeToByteArray()))
            advanceUntilIdle()

            // Context menu "Publish Here" triggers onTopicSelected
            component.onTopicSelected("devices/camera/live")

            component.state.value.selectedTopicPath shouldBe "devices/camera/live"
            component.state.value.selectedNode
                ?.lastMessage
                ?.payloadString shouldBe "active"
        }

    @Test
    fun `context menu delete retained triggers 0-byte deletion on single topic`() =
        runTest {
            val testDispatcher = StandardTestDispatcher(testScheduler)
            val fakeRepo = FakeMqttRepository()
            val lifecycle = LifecycleRegistry()
            lifecycle.resume()
            val context = DefaultComponentContext(lifecycle = lifecycle)

            val component =
                DefaultWorkspaceComponent(
                    componentContext = context,
                    config = ConnectionConfig(name = "Test", host = "localhost"),
                    mqttRepository = fakeRepo,
                    onDisconnect = {},
                    onOpenConnectionManager = {},
                    mainContext = testDispatcher,
                )
            advanceUntilIdle()

            fakeRepo.emitMessage(MqttMessage("sensors/temp", "22.5".encodeToByteArray(), isRetained = true))
            advanceUntilIdle()

            // Context menu "Delete Retained"
            component.onDeleteRetainedTopic("sensors/temp")
            advanceUntilIdle()

            fakeRepo.publishedMessages shouldHaveSize 1
            val published = fakeRepo.publishedMessages.first()
            published.topic shouldBe "sensors/temp"
            published.payload.size shouldBe 0
            published.isRetained shouldBe true
        }

    @Test
    fun `context menu purge branch triggers batch retained purge under selected branch`() =
        runTest {
            val testDispatcher = StandardTestDispatcher(testScheduler)
            val fakeRepo = FakeMqttRepository()
            val lifecycle = LifecycleRegistry()
            lifecycle.resume()
            val context = DefaultComponentContext(lifecycle = lifecycle)

            val component =
                DefaultWorkspaceComponent(
                    componentContext = context,
                    config = ConnectionConfig(name = "Test", host = "localhost"),
                    mqttRepository = fakeRepo,
                    onDisconnect = {},
                    onOpenConnectionManager = {},
                    mainContext = testDispatcher,
                )
            advanceUntilIdle()

            fakeRepo.emitMessage(MqttMessage("home/kitchen/temp", "22.5".encodeToByteArray(), isRetained = true))
            fakeRepo.emitMessage(MqttMessage("home/kitchen/humidity", "60%".encodeToByteArray(), isRetained = true))
            advanceUntilIdle()

            // Context menu "Purge Branch Retained"
            component.onDeleteRetainedBranch("home/kitchen")
            advanceUntilIdle()

            fakeRepo.publishedMessages shouldHaveSize 2
            fakeRepo.publishedMessages.map { it.topic } shouldContainExactlyInAnyOrder
                listOf(
                    "home/kitchen/temp",
                    "home/kitchen/humidity",
                )
            fakeRepo.publishedMessages.all { it.isRetained && it.payload.isEmpty() } shouldBe true
        }

    @Test
    fun `collectAllRetainedLeafPaths accurately filters only retained descendants`() {
        val leaf1 =
            TopicNode(
                "temp",
                "home/kitchen/temp",
                isLeaf = true,
                lastMessage = MqttMessage("home/kitchen/temp", byteArrayOf(1), isRetained = true),
            )
        val leaf2 =
            TopicNode(
                "humidity",
                "home/kitchen/humidity",
                isLeaf = true,
                lastMessage = MqttMessage("home/kitchen/humidity", byteArrayOf(2), isRetained = false),
            )
        val leaf3 =
            TopicNode(
                "light",
                "home/kitchen/light",
                isLeaf = true,
                lastMessage = MqttMessage("home/kitchen/light", byteArrayOf(3), isRetained = true),
            )

        val kitchenNode =
            TopicNode(
                segment = "kitchen",
                fullPath = "home/kitchen",
                isLeaf = false,
                children = listOf(leaf1, leaf2, leaf3),
            )

        val homeNode =
            TopicNode(
                segment = "home",
                fullPath = "home",
                isLeaf = false,
                children = listOf(kitchenNode),
            )

        val retainedPaths = homeNode.collectAllRetainedLeafPaths()
        retainedPaths shouldHaveSize 2
        retainedPaths shouldContainExactlyInAnyOrder listOf("home/kitchen/temp", "home/kitchen/light")
    }

    @Test
    fun `topic node payload is accessible for clipboard copy`() {
        val message =
            MqttMessage(
                topic = "sensors/gps",
                payload = """{"lat": 37.7749, "lng": -122.4194}""".encodeToByteArray(),
                isRetained = true,
            )
        val node = TopicNode("gps", "sensors/gps", isLeaf = true, lastMessage = message)

        node.fullPath shouldBe "sensors/gps"
        node.lastMessage?.payloadString shouldBe """{"lat": 37.7749, "lng": -122.4194}"""
    }
}
