package io.github.drlacheheb.mqtlin.ui.workspace.tree

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.drlacheheb.mqtlin.domain.model.TopicNode
import io.github.drlacheheb.mqtlin.ui.theme.DarkOnSurface
import io.github.drlacheheb.mqtlin.ui.theme.DarkOnSurfaceVariant
import io.github.drlacheheb.mqtlin.ui.theme.DarkOutline
import io.github.drlacheheb.mqtlin.ui.theme.DarkOutlineVariant
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceContainer
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinOnPrimaryContainer
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinPrimary
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinPrimaryContainer
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinSecondary

@Composable
fun TopicTreeNodeItem(
    node: TopicNode,
    selectedTopicPath: String?,
    depth: Int = 0,
    onTopicSelected: (String) -> Unit,
    onToggleExpand: (String) -> Unit
) {
    val isSelected = selectedTopicPath == node.fullPath
    val isDirectory = !node.isLeaf || node.children.isNotEmpty()
    val chevronRotation by animateFloatAsState(targetValue = if (node.isExpanded) 90f else 0f)

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
        // Node Row: px-2 py-1.5 rounded-lg cursor-pointer hover:bg-surface-variant transition-colors
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 1.dp),
            shape = RoundedCornerShape(4.dp),
            color = if (isSelected) MqtlinPrimaryContainer else Color.Transparent
        ) {
            Row(
                modifier = Modifier
                    .clickable {
                        if (node.isLeaf) {
                            onTopicSelected(node.fullPath)
                        } else {
                            onToggleExpand(node.fullPath)
                        }
                    }
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Chevron slot: exactly 16dp
                if (isDirectory) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Expand",
                        tint = if (isSelected) MqtlinOnPrimaryContainer else DarkOutlineVariant,
                        modifier = Modifier
                            .size(16.dp)
                            .rotate(chevronRotation)
                            .clickable { onToggleExpand(node.fullPath) }
                    )
                } else {
                    Box(modifier = Modifier.size(16.dp))
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Icon (Folder: text-primary-fixed-dim #C0C1FF, Tag: text-outline-variant #464554 / text-on-primary-container #0D0096)
                if (isDirectory) {
                    Icon(
                        imageVector = Icons.Default.Folder,
                        contentDescription = "Directory",
                        tint = if (isSelected) MqtlinOnPrimaryContainer else MqtlinPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Tag,
                        contentDescription = "Topic",
                        tint = if (isSelected) MqtlinOnPrimaryContainer else DarkOutlineVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Node segment name: font-mono-topic text-mono-topic
                val textColor = if (isSelected) {
                    MqtlinOnPrimaryContainer
                } else if (isDirectory) {
                    DarkOnSurface
                } else {
                    DarkOnSurfaceVariant
                }

                Text(
                    text = if (isDirectory) "${node.segment}/" else node.segment,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = textColor,
                    modifier = Modifier.weight(1f)
                )

                // Trailing: Green pulse dot on activity (HTML lines 269 & 280)
                if (System.currentTimeMillis() - node.lastUpdated < 5000) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .scale(pulseScale)
                            .background(MqtlinSecondary, CircleShape)
                    )
                }
            }
        }

        // Nested Children with vertical guide line: pl-6 border-l border-outline-variant/30 ml-3 flex flex-col mt-1
        if (isDirectory && node.isExpanded && node.children.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .padding(start = 14.dp, top = 2.dp)
            ) {
                // Vertical guide line
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(DarkOutlineVariant.copy(alpha = 0.35f))
                )

                // Children Column indented
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp)
                ) {
                    node.children.forEach { child ->
                        TopicTreeNodeItem(
                            node = child,
                            selectedTopicPath = selectedTopicPath,
                            depth = depth + 1,
                            onTopicSelected = onTopicSelected,
                            onToggleExpand = onToggleExpand
                        )
                    }
                }
            }
        }
    }
}
