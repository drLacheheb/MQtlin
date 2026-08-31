package io.github.drlacheheb.mqtlin.domain.model

enum class FilterMode {
    TEXT,
    REGEX,
    WILDCARD,
    RETAINED,
}

const val MAX_HISTORY_PER_TOPIC = 50

data class TopicTree(
    val rootNodes: List<TopicNode> = emptyList(),
    val totalTopicCount: Int = 0,
    val totalMessageCount: Long = 0,
) {
    fun insert(message: MqttMessage): TopicTree {
        val rawTopic = message.topic.trim().trimStart('/')
        if (rawTopic.isBlank()) return this

        val segments = rawTopic.split('/')
        val newRoots = insertRecursive(rootNodes, segments, 0, "", message)

        val newTotalTopics = countUniqueTopics(newRoots)
        val newTotalMessages = totalMessageCount + 1

        return copy(
            rootNodes = newRoots,
            totalTopicCount = newTotalTopics,
            totalMessageCount = newTotalMessages,
        )
    }

    private fun insertRecursive(
        currentLevelNodes: List<TopicNode>,
        segments: List<String>,
        index: Int,
        parentPath: String,
        message: MqttMessage,
    ): List<TopicNode> {
        val segment = segments[index]
        val fullPath = if (parentPath.isEmpty()) segment else "$parentPath/$segment"
        val isLeaf = index == segments.lastIndex

        val existingNode = currentLevelNodes.find { it.segment == segment }

        val updatedNode =
            if (existingNode != null) {
                val updatedChildren =
                    if (!isLeaf) {
                        insertRecursive(existingNode.children, segments, index + 1, fullPath, message)
                    } else {
                        existingNode.children
                    }
                val newHistory =
                    if (isLeaf) {
                        (listOf(message) + existingNode.history).take(MAX_HISTORY_PER_TOPIC)
                    } else {
                        existingNode.history
                    }

                existingNode.copy(
                    isLeaf = existingNode.isLeaf || isLeaf,
                    children = updatedChildren,
                    messageCount = existingNode.messageCount + 1,
                    lastMessage = if (isLeaf) message else existingNode.lastMessage,
                    lastUpdated = message.timestamp,
                    history = newHistory,
                )
            } else {
                val children =
                    if (!isLeaf) {
                        insertRecursive(emptyList(), segments, index + 1, fullPath, message)
                    } else {
                        emptyList()
                    }
                val newHistory = if (isLeaf) listOf(message) else emptyList()

                TopicNode(
                    segment = segment,
                    fullPath = fullPath,
                    isLeaf = isLeaf,
                    isExpanded = false,
                    children = children,
                    messageCount = 1,
                    lastMessage = if (isLeaf) message else null,
                    lastUpdated = message.timestamp,
                    history = newHistory,
                )
            }

        val remainingNodes = currentLevelNodes.filter { it.segment != segment }
        return (remainingNodes + updatedNode).sortedBy { it.segment }
    }

    fun toggleExpanded(fullPath: String): TopicTree {
        val newRoots = toggleRecursive(rootNodes, fullPath)
        return copy(rootNodes = newRoots)
    }

    fun setExpanded(
        fullPath: String,
        expanded: Boolean,
    ): TopicTree {
        val newRoots = setExpandedRecursive(rootNodes, fullPath, expanded)
        return copy(rootNodes = newRoots)
    }

    private fun toggleRecursive(
        nodes: List<TopicNode>,
        targetPath: String,
    ): List<TopicNode> =
        nodes.map { node ->
            if (node.fullPath == targetPath) {
                node.copy(isExpanded = !node.isExpanded)
            } else if (targetPath.startsWith(node.fullPath + "/")) {
                node.copy(children = toggleRecursive(node.children, targetPath))
            } else {
                node
            }
        }

    private fun setExpandedRecursive(
        nodes: List<TopicNode>,
        targetPath: String,
        expanded: Boolean,
    ): List<TopicNode> =
        nodes.map { node ->
            if (node.fullPath == targetPath) {
                node.copy(isExpanded = expanded)
            } else if (targetPath.startsWith(node.fullPath + "/")) {
                node.copy(children = setExpandedRecursive(node.children, targetPath, expanded))
            } else {
                node
            }
        }

    fun findNode(fullPath: String): TopicNode? {
        val segments = fullPath.trim().trimStart('/').split('/')
        var currentLevel = rootNodes
        var result: TopicNode? = null

        for (segment in segments) {
            val found = currentLevel.find { it.segment == segment } ?: return null
            result = found
            currentLevel = found.children
        }

        return result
    }

    fun filter(
        query: String,
        mode: FilterMode = FilterMode.TEXT,
    ): TopicTree {
        if (query.isBlank() && mode != FilterMode.RETAINED) return this

        val filteredRoots = filterNodes(rootNodes, query.trim(), mode)
        return copy(
            rootNodes = filteredRoots,
            totalTopicCount = countUniqueTopics(filteredRoots),
        )
    }

    private fun filterNodes(
        nodes: List<TopicNode>,
        query: String,
        mode: FilterMode,
    ): List<TopicNode> =
        nodes.mapNotNull { node ->
            val matchesSelf = matchesFilter(node, query, mode)
            val filteredChildren = filterNodes(node.children, query, mode)

            if (matchesSelf || filteredChildren.isNotEmpty()) {
                node.copy(children = filteredChildren, isExpanded = true)
            } else {
                null
            }
        }

    private fun matchesFilter(
        node: TopicNode,
        query: String,
        mode: FilterMode,
    ): Boolean =
        when (mode) {
            FilterMode.TEXT -> node.fullPath.contains(query, ignoreCase = true)
            FilterMode.REGEX -> runCatching { Regex(query, RegexOption.IGNORE_CASE).containsMatchIn(node.fullPath) }.getOrDefault(false)
            FilterMode.WILDCARD -> {
                val regexPattern = "^" + query.replace("+", "[^/]+").replace("#", ".*") + "$"
                runCatching { Regex(regexPattern, RegexOption.IGNORE_CASE).matches(node.fullPath) }.getOrDefault(false)
            }
            FilterMode.RETAINED -> node.lastMessage?.isRetained == true
        }

    private fun countUniqueTopics(nodes: List<TopicNode>): Int {
        var count = 0

        fun traverse(list: List<TopicNode>) {
            for (node in list) {
                if (node.isLeaf) count++
                traverse(node.children)
            }
        }
        traverse(nodes)
        return count
    }
}
