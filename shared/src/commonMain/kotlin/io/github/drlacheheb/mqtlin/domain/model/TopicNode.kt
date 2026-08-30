package io.github.drlacheheb.mqtlin.domain.model

data class TopicNode(
    val segment: String,
    val fullPath: String,
    val isLeaf: Boolean,
    val isExpanded: Boolean = false,
    val children: List<TopicNode> = emptyList(),
    val messageCount: Long = 0,
    val messagesPerSec: Double = 0.0,
    val lastMessage: MqttMessage? = null,
    val lastUpdated: Long = 0L,
    val history: List<MqttMessage> = emptyList()
) {
    fun findChild(segment: String): TopicNode? =
        children.find { it.segment == segment }

    fun withUpdatedChild(updatedChild: TopicNode): TopicNode {
        val newChildren = children.map { child ->
            if (child.segment == updatedChild.segment) updatedChild else child
        }
        return copy(children = newChildren)
    }

    fun withNewChild(newChild: TopicNode): TopicNode {
        val newChildren = (children + newChild).sortedBy { it.segment }
        return copy(children = newChildren)
    }

    fun collectAllRetainedLeafPaths(): List<String> {
        val list = mutableListOf<String>()
        if (lastMessage?.isRetained == true) {
            list.add(fullPath)
        }
        children.forEach { child ->
            list.addAll(child.collectAllRetainedLeafPaths())
        }
        return list
    }

    fun countRetainedDescendants(): Int {
        var count = if (lastMessage?.isRetained == true) 1 else 0
        children.forEach { child ->
            count += child.countRetainedDescendants()
        }
        return count
    }
}

