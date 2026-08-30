package io.github.drlacheheb.mqtlin.ui.workspace.tree

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Description
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import io.github.drlacheheb.mqtlin.domain.model.TopicNode
import io.github.drlacheheb.mqtlin.ui.components.MqtlinContextMenu
import io.github.drlacheheb.mqtlin.ui.components.MqtlinContextMenuDivider
import io.github.drlacheheb.mqtlin.ui.components.MqtlinContextMenuItem

/**
 * Secondary mouse click popup context menu for a topic tree node.
 */
@Composable
fun TopicNodeContextMenu(
    expanded: Boolean,
    node: TopicNode,
    onDismissRequest: () -> Unit,
    onTopicSelected: (String) -> Unit,
    onDeleteRetainedTopic: ((String) -> Unit)?,
    onOpenPurgeDialog: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current

    MqtlinContextMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        width = 200.dp
    ) {
        // 1. Copy Topic Path (Ctrl+C)
        MqtlinContextMenuItem(
            text = "Copy Topic Path",
            leadingIcon = Icons.Default.ContentCopy,
            shortcut = "Ctrl+C",
            onClick = {
                clipboardManager.setText(AnnotatedString(node.fullPath))
                onDismissRequest()
            }
        )

        // 2. Copy Payload (Ctrl+Shift+C) - shown when payload exists
        if (node.lastMessage != null) {
            MqtlinContextMenuItem(
                text = "Copy Payload",
                leadingIcon = Icons.Default.Description,
                shortcut = "Ctrl+Shift+C",
                onClick = {
                    clipboardManager.setText(AnnotatedString(node.lastMessage.payloadString))
                    onDismissRequest()
                }
            )
        }

        // 3. Publish Here (Enter)
        MqtlinContextMenuItem(
            text = "Publish Here",
            leadingIcon = Icons.AutoMirrored.Filled.Send,
            shortcut = "Enter",
            onClick = {
                onTopicSelected(node.fullPath)
                onDismissRequest()
            }
        )

        val hasRetained = node.lastMessage?.isRetained == true && onDeleteRetainedTopic != null
        val retainedDescendants = remember(node) { node.collectAllRetainedLeafPaths() }
        val hasBranchRetained = !node.isLeaf && retainedDescendants.isNotEmpty()

        // Destructive Section Divider
        if (hasRetained || hasBranchRetained) {
            MqtlinContextMenuDivider()
        }

        // 4. Delete Retained (Del)
        if (hasRetained) {
            MqtlinContextMenuItem(
                text = "Delete Retained Message",
                leadingIcon = Icons.Default.Delete,
                shortcut = "Del",
                isDestructive = true,
                onClick = {
                    onDeleteRetainedTopic?.invoke(node.fullPath)
                    onDismissRequest()
                }
            )
        }

        // 5. Purge N Retained...
        if (hasBranchRetained) {
            MqtlinContextMenuItem(
                text = "Purge ${retainedDescendants.size} Retained...",
                leadingIcon = Icons.Default.DeleteSweep,
                isDestructive = true,
                onClick = {
                    onOpenPurgeDialog()
                    onDismissRequest()
                }
            )
        }
    }
}
