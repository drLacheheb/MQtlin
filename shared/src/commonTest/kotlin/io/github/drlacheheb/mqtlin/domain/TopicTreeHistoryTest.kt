package io.github.drlacheheb.mqtlin.domain

import io.github.drlacheheb.mqtlin.domain.model.MAX_HISTORY_PER_TOPIC
import io.github.drlacheheb.mqtlin.domain.model.MqttMessage
import io.github.drlacheheb.mqtlin.domain.model.TopicTree
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class TopicTreeHistoryTest {

    private fun createMessage(topic: String, payload: String, timestamp: Long): MqttMessage {
        return MqttMessage(
            topic = topic,
            payload = payload.encodeToByteArray(),
            qos = 0,
            isRetained = false,
            timestamp = timestamp
        )
    }

    @Test
    fun `inserting multiple messages records history in reverse chronological order`() {
        var tree = TopicTree()

        tree = tree.insert(createMessage("sensors/temp", "20.0", 1000L))
        tree = tree.insert(createMessage("sensors/temp", "21.5", 2000L))
        tree = tree.insert(createMessage("sensors/temp", "22.3", 3000L))

        val node = tree.findNode("sensors/temp")
        node.shouldNotBeNull()
        node.messageCount shouldBe 3
        node.history shouldHaveSize 3

        // Newest first
        node.history[0].payloadString shouldBe "22.3"
        node.history[1].payloadString shouldBe "21.5"
        node.history[2].payloadString shouldBe "20.0"
    }

    @Test
    fun `history is capped at MAX_HISTORY_PER_TOPIC entries`() {
        var tree = TopicTree()
        val totalMessages = MAX_HISTORY_PER_TOPIC + 10

        for (i in 1..totalMessages) {
            tree = tree.insert(createMessage("device/telemetry", "payload_$i", 1000L + i))
        }

        val node = tree.findNode("device/telemetry")
        node.shouldNotBeNull()
        node.messageCount shouldBe totalMessages.toLong()
        node.history shouldHaveSize MAX_HISTORY_PER_TOPIC

        // Latest message is at index 0
        node.history[0].payloadString shouldBe "payload_$totalMessages"
    }
}
