package io.github.drlacheheb.mqtlin.domain

import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import io.github.drlacheheb.mqtlin.domain.model.ConnectionConfig
import io.github.drlacheheb.mqtlin.domain.model.ConnectionState
import io.github.drlacheheb.mqtlin.domain.model.MqttMessage
import io.github.drlacheheb.mqtlin.domain.model.TopicNode
import io.github.drlacheheb.mqtlin.domain.model.TopicTree
import io.github.drlacheheb.mqtlin.domain.repository.MqttRepository
import io.github.drlacheheb.mqtlin.ui.workspace.DefaultWorkspaceComponent
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RetainedMessageManagementTest {

    private class FakeMqttRepository : MqttRepository {
        val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Connected("localhost", 1883, "test-client"))
        override val connectionState: StateFlow<ConnectionState> = _connectionState

        val _incomingMessages = MutableSharedFlow<MqttMessage>(replay = 10, extraBufferCapacity = 64)
        override val incomingMessages: SharedFlow<MqttMessage> = _incomingMessages

        val publishedMessages = mutableListOf<PublishedMessage>()

        data class PublishedMessage(
            val topic: String,
            val payload: ByteArray,
            val qos: Int,
            val isRetained: Boolean
        )

        override suspend fun connect(config: ConnectionConfig) {}
        override suspend fun testConnection(config: ConnectionConfig): Result<Unit> = Result.success(Unit)
        override suspend fun disconnect() {}
        override suspend fun subscribe(topicFilter: String, qos: Int) {}
        override suspend fun unsubscribe(topicFilter: String) {}

        override suspend fun publish(
            topic: String,
            payload: ByteArray,
            qos: Int,
            isRetained: Boolean,
            userProperties: Map<String, String>
        ) {
            publishedMessages.add(PublishedMessage(topic, payload, qos, isRetained))
        }
    }

    @Test
    fun `TopicNode collects all retained leaf paths in nested subtree`() {
        val leaf1 = TopicNode("temp", "home/living/temp", isLeaf = true, lastMessage = MqttMessage("home/living/temp", "21".encodeToByteArray(), isRetained = true))
        val leaf2 = TopicNode("humidity", "home/living/humidity", isLeaf = true, lastMessage = MqttMessage("home/living/humidity", "50".encodeToByteArray(), isRetained = false))
        val leaf3 = TopicNode("temp", "home/kitchen/temp", isLeaf = true, lastMessage = MqttMessage("home/kitchen/temp", "24".encodeToByteArray(), isRetained = true))

        val living = TopicNode("living", "home/living", isLeaf = false, children = listOf(leaf1, leaf2))
        val kitchen = TopicNode("kitchen", "home/kitchen", isLeaf = false, children = listOf(leaf3))
        val home = TopicNode("home", "home", isLeaf = false, children = listOf(living, kitchen))

        val retainedPaths = home.collectAllRetainedLeafPaths()
        retainedPaths shouldContainExactlyInAnyOrder listOf("home/living/temp", "home/kitchen/temp")
        home.countRetainedDescendants() shouldBe 2
    }

    @Test
    fun `onDeleteRetainedTopic publishes 0-byte payload with retain flag true`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val fakeRepo = FakeMqttRepository()
        val lifecycle = LifecycleRegistry()
        val context = DefaultComponentContext(lifecycle = lifecycle)

        val component = DefaultWorkspaceComponent(
            componentContext = context,
            config = ConnectionConfig(name = "Test", host = "localhost"),
            mqttRepository = fakeRepo,
            onDisconnect = {},
            onOpenConnectionManager = {},
            mainContext = testDispatcher
        )

        component.onDeleteRetainedTopic("home/living/temperature")
        testDispatcher.scheduler.advanceUntilIdle()

        fakeRepo.publishedMessages shouldHaveSize 1
        val msg = fakeRepo.publishedMessages.first()
        msg.topic shouldBe "home/living/temperature"
        msg.payload.size shouldBe 0
        msg.isRetained shouldBe true
    }

    @Test
    fun `onDeleteRetainedBranch recursively purges all retained topics under subtree`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val fakeRepo = FakeMqttRepository()
        val lifecycle = LifecycleRegistry()
        val context = DefaultComponentContext(lifecycle = lifecycle)

        val component = DefaultWorkspaceComponent(
            componentContext = context,
            config = ConnectionConfig(name = "Test", host = "localhost"),
            mqttRepository = fakeRepo,
            onDisconnect = {},
            onOpenConnectionManager = {},
            mainContext = testDispatcher
        )

        // Seed 3 messages (2 retained, 1 live)
        fakeRepo._incomingMessages.tryEmit(MqttMessage("devices/esp32/temp", "21".encodeToByteArray(), isRetained = true))
        fakeRepo._incomingMessages.tryEmit(MqttMessage("devices/esp32/status", "online".encodeToByteArray(), isRetained = true))
        fakeRepo._incomingMessages.tryEmit(MqttMessage("devices/esp32/ping", "pong".encodeToByteArray(), isRetained = false))
        testDispatcher.scheduler.advanceUntilIdle()

        // Delete branch
        component.onDeleteRetainedBranch("devices/esp32")
        testDispatcher.scheduler.advanceUntilIdle()

        fakeRepo.publishedMessages shouldHaveSize 2
        fakeRepo.publishedMessages.map { it.topic } shouldContainExactlyInAnyOrder listOf(
            "devices/esp32/temp",
            "devices/esp32/status"
        )
        fakeRepo.publishedMessages.all { it.isRetained && it.payload.isEmpty() } shouldBe true
    }

    @Test
    fun `TopicNode collects retained paths when folder itself is also a retained topic`() {
        val leaf = TopicNode("temp", "home/living/temp", isLeaf = true, lastMessage = MqttMessage("home/living/temp", "21".encodeToByteArray(), isRetained = true))
        val living = TopicNode("living", "home/living", isLeaf = false, children = listOf(leaf), lastMessage = MqttMessage("home/living", "active".encodeToByteArray(), isRetained = true))
        val home = TopicNode("home", "home", isLeaf = false, children = listOf(living))

        val retainedPaths = home.collectAllRetainedLeafPaths()
        retainedPaths shouldContainExactlyInAnyOrder listOf("home/living", "home/living/temp")
        home.countRetainedDescendants() shouldBe 2
    }
}
