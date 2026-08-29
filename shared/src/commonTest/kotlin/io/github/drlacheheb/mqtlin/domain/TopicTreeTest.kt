package io.github.drlacheheb.mqtlin.domain

import io.github.drlacheheb.mqtlin.domain.model.FilterMode
import io.github.drlacheheb.mqtlin.domain.model.MqttMessage
import io.github.drlacheheb.mqtlin.domain.model.TopicTree
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class TopicTreeTest {

    @Test
    fun `insert single level topic creates leaf root node`() {
        val tree = TopicTree()
        val message = MqttMessage(
            topic = "sensor",
            payload = "24.5".encodeToByteArray()
        )

        val updated = tree.insert(message)

        updated.rootNodes shouldHaveSize 1
        val node = updated.rootNodes[0]
        node.segment shouldBe "sensor"
        node.fullPath shouldBe "sensor"
        node.isLeaf shouldBe true
        node.messageCount shouldBe 1
        node.lastMessage?.payloadString shouldBe "24.5"
        updated.totalTopicCount shouldBe 1
        updated.totalMessageCount shouldBe 1
    }

    @Test
    fun `insert multi-level topic creates directory hierarchy and leaf node`() {
        val tree = TopicTree()
        val message = MqttMessage(
            topic = "home/living-room/temperature",
            payload = "21.0".encodeToByteArray()
        )

        val updated = tree.insert(message)

        updated.rootNodes shouldHaveSize 1
        val homeNode = updated.rootNodes[0]
        homeNode.segment shouldBe "home"
        homeNode.fullPath shouldBe "home"
        homeNode.isLeaf shouldBe false
        homeNode.children shouldHaveSize 1

        val livingRoomNode = homeNode.children[0]
        livingRoomNode.segment shouldBe "living-room"
        livingRoomNode.fullPath shouldBe "home/living-room"
        livingRoomNode.isLeaf shouldBe false
        livingRoomNode.children shouldHaveSize 1

        val tempNode = livingRoomNode.children[0]
        tempNode.segment shouldBe "temperature"
        tempNode.fullPath shouldBe "home/living-room/temperature"
        tempNode.isLeaf shouldBe true
        tempNode.lastMessage?.payloadString shouldBe "21.0"
        updated.totalTopicCount shouldBe 1
    }

    @Test
    fun `inserting multiple sibling topics merges into common parent`() {
        var tree = TopicTree()
        tree = tree.insert(MqttMessage("home/kitchen/temperature", "22.5".encodeToByteArray()))
        tree = tree.insert(MqttMessage("home/kitchen/humidity", "55%".encodeToByteArray()))
        tree = tree.insert(MqttMessage("home/living-room/temperature", "20.1".encodeToByteArray()))

        tree.rootNodes shouldHaveSize 1
        val homeNode = tree.rootNodes[0]
        homeNode.children shouldHaveSize 2 // kitchen and living-room

        val kitchenNode = homeNode.findChild("kitchen")
        kitchenNode.shouldNotBeNull()
        kitchenNode.children shouldHaveSize 2 // temperature and humidity

        val livingRoomNode = homeNode.findChild("living-room")
        livingRoomNode.shouldNotBeNull()
        livingRoomNode.children shouldHaveSize 1

        tree.totalTopicCount shouldBe 3
        tree.totalMessageCount shouldBe 3
    }

    @Test
    fun `inserting subsequent message on existing topic increments count and updates payload`() {
        var tree = TopicTree()
        tree = tree.insert(MqttMessage("sensor/voltage", "3.3".encodeToByteArray()))
        tree = tree.insert(MqttMessage("sensor/voltage", "3.28".encodeToByteArray()))

        val node = tree.findNode("sensor/voltage")
        node.shouldNotBeNull()
        node.messageCount shouldBe 2
        node.lastMessage?.payloadString shouldBe "3.28"
        tree.totalTopicCount shouldBe 1
        tree.totalMessageCount shouldBe 2
    }

    @Test
    fun `toggleExpanded toggles node expansion state`() {
        var tree = TopicTree()
        tree = tree.insert(MqttMessage("home/kitchen/temp", "20".encodeToByteArray()))

        val homeNode = tree.findNode("home")
        homeNode.shouldNotBeNull()
        homeNode.isExpanded shouldBe false

        tree = tree.toggleExpanded("home")
        tree.findNode("home")?.isExpanded shouldBe true

        tree = tree.toggleExpanded("home")
        tree.findNode("home")?.isExpanded shouldBe false
    }

    @Test
    fun `findNode returns null for non-existent path`() {
        val tree = TopicTree().insert(MqttMessage("a/b/c", "data".encodeToByteArray()))

        tree.findNode("a/b/d").shouldBeNull()
        tree.findNode("x/y/z").shouldBeNull()
    }

    @Test
    fun `filtering by text returns matching path branch`() {
        var tree = TopicTree()
        tree = tree.insert(MqttMessage("factory/zone-1/temp", "30".encodeToByteArray()))
        tree = tree.insert(MqttMessage("factory/zone-2/temp", "32".encodeToByteArray()))
        tree = tree.insert(MqttMessage("office/room-a/light", "ON".encodeToByteArray()))

        val filtered = tree.filter("zone-1", FilterMode.TEXT)

        filtered.rootNodes shouldHaveSize 1
        filtered.rootNodes[0].segment shouldBe "factory"
        filtered.findNode("factory/zone-1/temp").shouldNotBeNull()
        filtered.findNode("office/room-a/light").shouldBeNull()
    }

    @Test
    fun `filtering by wildcard returns matching single and multi-level topic patterns`() {
        var tree = TopicTree()
        tree = tree.insert(MqttMessage("sensors/us/temp", "10".encodeToByteArray()))
        tree = tree.insert(MqttMessage("sensors/eu/temp", "12".encodeToByteArray()))
        tree = tree.insert(MqttMessage("sensors/us/humidity", "40".encodeToByteArray()))
        tree = tree.insert(MqttMessage("devices/gateway/status", "OK".encodeToByteArray()))

        // Single level '+'
        val filteredSingle = tree.filter("sensors/+/temp", FilterMode.WILDCARD)
        filteredSingle.findNode("sensors/us/temp").shouldNotBeNull()
        filteredSingle.findNode("sensors/eu/temp").shouldNotBeNull()
        filteredSingle.findNode("sensors/us/humidity").shouldBeNull()

        // Multi-level '#'
        val filteredMulti = tree.filter("sensors/#", FilterMode.WILDCARD)
        filteredMulti.findNode("sensors/us/temp").shouldNotBeNull()
        filteredMulti.findNode("sensors/eu/temp").shouldNotBeNull()
        filteredMulti.findNode("sensors/us/humidity").shouldNotBeNull()
        filteredMulti.findNode("devices/gateway/status").shouldBeNull()
    }

    @Test
    fun `filtering by regex matches regex patterns`() {
        var tree = TopicTree()
        tree = tree.insert(MqttMessage("telemetry/v1/battery", "98%".encodeToByteArray()))
        tree = tree.insert(MqttMessage("telemetry/v2/battery", "95%".encodeToByteArray()))
        tree = tree.insert(MqttMessage("telemetry/v1/signal", "-65dBm".encodeToByteArray()))

        val filtered = tree.filter("telemetry/v[0-9]+/battery", FilterMode.REGEX)

        filtered.findNode("telemetry/v1/battery").shouldNotBeNull()
        filtered.findNode("telemetry/v2/battery").shouldNotBeNull()
        filtered.findNode("telemetry/v1/signal").shouldBeNull()
    }

    @Test
    fun `filtering by retained mode returns only retained messages`() {
        var tree = TopicTree()
        tree = tree.insert(MqttMessage("home/kitchen/temp", "21".encodeToByteArray(), isRetained = true))
        tree = tree.insert(MqttMessage("home/kitchen/light", "ON".encodeToByteArray(), isRetained = false))

        val filtered = tree.filter("", FilterMode.RETAINED)

        filtered.findNode("home/kitchen/temp").shouldNotBeNull()
        filtered.findNode("home/kitchen/light").shouldBeNull()
    }
}
