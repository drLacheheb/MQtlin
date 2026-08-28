package io.github.drlacheheb.mqtlin.ui.workspace.tree

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.drlacheheb.mqtlin.domain.model.FilterMode
import io.github.drlacheheb.mqtlin.domain.model.TopicTree
import io.github.drlacheheb.mqtlin.ui.components.MqtlinTextField
import io.github.drlacheheb.mqtlin.ui.theme.DarkOnSurface
import io.github.drlacheheb.mqtlin.ui.theme.DarkOnSurfaceVariant
import io.github.drlacheheb.mqtlin.ui.theme.DarkOutline
import io.github.drlacheheb.mqtlin.ui.theme.DarkOutlineVariant
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceBright
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceContainer
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceContainerHigh
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceContainerLowest
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceDim
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinPrimary
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinTertiary

@Composable
fun TopicTreePanel(
    topicTree: TopicTree,
    selectedTopicPath: String?,
    filterQuery: String,
    filterMode: FilterMode,
    onTopicSelected: (String) -> Unit,
    onToggleExpand: (String) -> Unit,
    onFilterQueryChanged: (String) -> Unit,
    onFilterModeChanged: (FilterMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(DarkSurfaceDim) // HTML line 229: bg-surface-dim (#131316)
    ) {
        // Search & Filters Header: p-panel_padding (12px), border-b border-outline-variant
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Search input: w-full bg-surface-container-lowest (#0E0E11) border border-outline-variant rounded-lg
            MqtlinTextField(
                value = filterQuery,
                onValueChange = onFilterQueryChanged,
                placeholder = "Filter topics...",
                height = 34.dp,
                isMonospace = true,
                backgroundColor = DarkSurfaceContainerLowest,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = DarkOutlineVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            )

            // Filter Chips: Regex, Wildcard, Retained
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    label = "Regex",
                    isSelected = filterMode == FilterMode.REGEX,
                    icon = ".*",
                    onClick = {
                        val next = if (filterMode == FilterMode.REGEX) FilterMode.TEXT else FilterMode.REGEX
                        onFilterModeChanged(next)
                    }
                )
                FilterChip(
                    label = "Wildcard",
                    isSelected = filterMode == FilterMode.WILDCARD,
                    icon = "*",
                    onClick = {
                        val next = if (filterMode == FilterMode.WILDCARD) FilterMode.TEXT else FilterMode.WILDCARD
                        onFilterModeChanged(next)
                    }
                )
                FilterChip(
                    label = "Retained",
                    isSelected = filterMode == FilterMode.RETAINED,
                    isAmber = true,
                    iconVector = Icons.Default.Save,
                    onClick = {
                        val next = if (filterMode == FilterMode.RETAINED) FilterMode.TEXT else FilterMode.RETAINED
                        onFilterModeChanged(next)
                    }
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(DarkOutlineVariant)
        )

        // Topic Tree Scrollable Area: py-2 px-2 custom-scrollbar
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            if (topicTree.rootNodes.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (filterQuery.isNotEmpty()) "No matching topics" else "Waiting for messages...",
                            fontSize = 13.sp,
                            color = DarkOnSurfaceVariant.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "Subscribed to #",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = DarkOnSurfaceVariant.copy(alpha = 0.4f)
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    topicTree.rootNodes.forEach { rootNode ->
                        TopicTreeNodeItem(
                            node = rootNode,
                            selectedTopicPath = selectedTopicPath,
                            depth = 0,
                            onTopicSelected = onTopicSelected,
                            onToggleExpand = onToggleExpand
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterChip(
    label: String,
    isSelected: Boolean,
    isAmber: Boolean = false,
    icon: String? = null,
    iconVector: androidx.compose.ui.graphics.vector.ImageVector? = null,
    onClick: () -> Unit
) {
    val bg = if (isAmber) {
        if (isSelected) MqtlinTertiary.copy(alpha = 0.25f) else MqtlinTertiary.copy(alpha = 0.12f)
    } else {
        if (isSelected) DarkSurfaceBright else DarkSurfaceContainerHigh
    }

    val border = if (isAmber) {
        if (isSelected) MqtlinTertiary else MqtlinTertiary.copy(alpha = 0.35f)
    } else {
        if (isSelected) MqtlinPrimary else DarkOutlineVariant
    }

    val text = if (isAmber) MqtlinTertiary else (if (isSelected) DarkOnSurface else DarkOnSurfaceVariant)

    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(4.dp),
        color = bg,
        border = BorderStroke(1.dp, border)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (iconVector != null) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = null,
                    tint = text,
                    modifier = Modifier.size(12.dp)
                )
            } else if (icon != null) {
                Text(
                    text = icon,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = text
                )
            }
            Text(
                text = label,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = text
            )
        }
    }
}
