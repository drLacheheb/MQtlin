package io.github.drlacheheb.mqtlin.ui.workspace.tree

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.PointerMatcher
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import io.github.drlacheheb.mqtlin.ui.components.MqtlinContextMenu
import io.github.drlacheheb.mqtlin.ui.components.MqtlinContextMenuDivider
import io.github.drlacheheb.mqtlin.ui.components.MqtlinContextMenuItem
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.drlacheheb.mqtlin.domain.model.TopicNode
import io.github.drlacheheb.mqtlin.ui.components.MqtlinSymbols
import io.github.drlacheheb.mqtlin.ui.theme.DarkOnSurface
import io.github.drlacheheb.mqtlin.ui.theme.DarkOnSurfaceVariant
import io.github.drlacheheb.mqtlin.ui.theme.DarkOutline
import io.github.drlacheheb.mqtlin.ui.theme.DarkOutlineVariant
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceContainerLow
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinPrimary
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinSecondary
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinTertiary
import io.github.drlacheheb.mqtlin.ui.workspace.components.PurgeRetainedDialog

private val ContextMenuBorder = Color(0xFF2E2E35)
private val DestructiveColor = Color(0xFFF7768E)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TopicTreeNodeItem(
    node: TopicNode,
    selectedTopicPath: String?,
    depth: Int = 0,
    onTopicSelected: (String) -> Unit,
    onToggleExpand: (String) -> Unit,
    onDeleteRetainedTopic: ((String) -> Unit)? = null,
    onDeleteRetainedBranch: ((String) -> Unit)? = null
) {
    val isSelected = selectedTopicPath == node.fullPath
    val isDirectory = !node.isLeaf || node.children.isNotEmpty()
    val arrowRotation by animateFloatAsState(targetValue = if (node.isExpanded) 0f else -90f)
    val clipboardManager = LocalClipboardManager.current
    var showContextMenu by remember { mutableStateOf(false) }
    var showPurgeBranchDialog by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition()
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.80f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        )
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        // Tree Node Row
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .pointerHoverIcon(PointerIcon.Hand)
                .onClick(matcher = PointerMatcher.mouse(PointerButton.Primary)) {
                    onTopicSelected(node.fullPath)
                }
                .onClick(matcher = PointerMatcher.mouse(PointerButton.Secondary)) {
                    onTopicSelected(node.fullPath)
                    showContextMenu = true
                },
            shape = RoundedCornerShape(2.dp),
            color = if (isSelected) MqtlinTertiary.copy(alpha = 0.08f) else Color.Transparent
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Expander arrow for directories
                if (isDirectory) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Expand/Collapse",
                        tint = if (isSelected) MqtlinTertiary else DarkOutlineVariant,
                        modifier = Modifier
                            .size(16.dp)
                            .rotate(arrowRotation)
                            .pointerHoverIcon(PointerIcon.Hand)
                            .clickable { onToggleExpand(node.fullPath) }
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                }

                // Folder / Topic Icon
                if (isDirectory) {
                    val folderIcon = if (node.isExpanded) MqtlinSymbols.FolderOpen else MqtlinSymbols.Folder
                    val folderTint = if (node.isExpanded || isSelected) {
                        MqtlinTertiary // Amber #FFB95F for open folders & selected
                    } else {
                        MqtlinPrimary // Lavender #C0C1FF for closed folders
                    }

                    Icon(
                        imageVector = folderIcon,
                        contentDescription = "Directory",
                        tint = folderTint,
                        modifier = Modifier.size(16.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Tag,
                        contentDescription = "Topic",
                        tint = if (isSelected) MqtlinTertiary else DarkOutlineVariant,
                        modifier = Modifier.size(14.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Node segment name (Highlighted with #FFB95F amber when selected)
                val textColor = if (isSelected) {
                    MqtlinTertiary // Amber #FFB95F for selected topic
                } else if (isDirectory && node.isExpanded) {
                    DarkOnSurface
                } else {
                    DarkOnSurfaceVariant
                }

                Text(
                    text = node.segment,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = textColor,
                    modifier = Modifier.weight(1f)
                )

                // Trailing: Live green pulse dot and message counter badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (System.currentTimeMillis() - node.lastUpdated < 5000) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .scale(pulseScale)
                                .background(MqtlinSecondary, CircleShape)
                        )
                    }

                    if (node.messageCount > 0) {
                        Text(
                            text = if (node.messageCount >= 1000) "${node.messageCount / 1000}k" else "${node.messageCount}",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) MqtlinTertiary.copy(alpha = 0.85f) else DarkOutline.copy(alpha = 0.8f)
                        )
                    }
                }

                // Custom Desktop Edge-to-Edge Right-Click Context Menu
                MqtlinContextMenu(
                    expanded = showContextMenu,
                    onDismissRequest = { showContextMenu = false },
                    width = 200.dp
                ) {
                    // 1. Copy Topic Path (Ctrl+C)
                    MqtlinContextMenuItem(
                        text = "Copy Topic Path",
                        leadingIcon = Icons.Default.ContentCopy,
                        shortcut = "Ctrl+C",
                        onClick = {
                            clipboardManager.setText(AnnotatedString(node.fullPath))
                            showContextMenu = false
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
                                showContextMenu = false
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
                            showContextMenu = false
                        }
                    )

                    val hasRetained = node.lastMessage?.isRetained == true && onDeleteRetainedTopic != null
                    val retainedDescendants = remember(node) { node.collectAllRetainedLeafPaths() }
                    val hasBranchRetained = !node.isLeaf && retainedDescendants.isNotEmpty() && onDeleteRetainedBranch != null

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
                                onDeleteRetainedTopic(node.fullPath)
                                showContextMenu = false
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
                                showPurgeBranchDialog = true
                                showContextMenu = false
                            }
                        )
                    }
                }
            }
        }

        // Nested Children with vertical guide line (Indentation pl-4: 16.dp)
        if (isDirectory && node.isExpanded && node.children.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .padding(start = 10.dp)
            ) {
                // Vertical branch line
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(DarkOutlineVariant.copy(alpha = 0.25f))
                )

                // Indented Children Column
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 6.dp)
                ) {
                    node.children.forEach { child ->
                        TopicTreeNodeItem(
                            node = child,
                            selectedTopicPath = selectedTopicPath,
                            depth = depth + 1,
                            onTopicSelected = onTopicSelected,
                            onToggleExpand = onToggleExpand,
                            onDeleteRetainedTopic = onDeleteRetainedTopic,
                            onDeleteRetainedBranch = onDeleteRetainedBranch
                        )
                    }
                }
            }
        }

        // Purge confirmation modal dialog when triggered from branch context menu
        if (showPurgeBranchDialog && onDeleteRetainedBranch != null) {
            val retainedDescendants = remember(node) { node.collectAllRetainedLeafPaths() }
            PurgeRetainedDialog(
                branchPath = node.fullPath,
                retainedTopics = retainedDescendants,
                onConfirm = { onDeleteRetainedBranch(node.fullPath) },
                onDismiss = { showPurgeBranchDialog = false }
            )
        }
    }
}
