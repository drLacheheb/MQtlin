package io.github.drlacheheb.mqtlin.ui.workspace.tree

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.drlacheheb.mqtlin.domain.model.TopicTree
import io.github.drlacheheb.mqtlin.ui.components.MqtlinTextField
import io.github.drlacheheb.mqtlin.ui.theme.DarkOnSurfaceVariant
import io.github.drlacheheb.mqtlin.ui.theme.DarkOutlineVariant
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceContainerLowest
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceDim

@Composable
fun TopicTreePanel(
    topicTree: TopicTree,
    selectedTopicPath: String?,
    filterQuery: String,
    onTopicSelected: (String) -> Unit,
    onToggleExpand: (String) -> Unit,
    onFilterQueryChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    onDeleteRetainedTopic: ((String) -> Unit)? = null,
    onDeleteRetainedBranch: ((String) -> Unit)? = null,
) {
    Column(
        modifier =
            modifier
                .fillMaxHeight()
                .background(DarkSurfaceDim),
    ) {
        // Search Header (Clean, streamlined search bar)
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
        ) {
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
                        modifier = Modifier.size(16.dp),
                    )
                },
            )
        }

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(DarkOutlineVariant),
        )

        // Topic Tree Scrollable Area
        Box(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
        ) {
            if (topicTree.rootNodes.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text(
                            text = if (filterQuery.isNotEmpty()) "No matching topics" else "Waiting for messages...",
                            fontSize = 13.sp,
                            color = DarkOnSurfaceVariant.copy(alpha = 0.6f),
                        )
                        Text(
                            text = "Subscribed to #",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = DarkOnSurfaceVariant.copy(alpha = 0.4f),
                        )
                    }
                }
            } else {
                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                ) {
                    topicTree.rootNodes.forEach { rootNode ->
                        TopicTreeNodeItem(
                            node = rootNode,
                            selectedTopicPath = selectedTopicPath,
                            depth = 0,
                            onTopicSelected = onTopicSelected,
                            onToggleExpand = onToggleExpand,
                            onDeleteRetainedTopic = onDeleteRetainedTopic,
                            onDeleteRetainedBranch = onDeleteRetainedBranch,
                        )
                    }
                }
            }
        }
    }
}
