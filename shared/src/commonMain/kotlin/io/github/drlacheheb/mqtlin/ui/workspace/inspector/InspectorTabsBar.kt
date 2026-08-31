package io.github.drlacheheb.mqtlin.ui.workspace.inspector

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.drlacheheb.mqtlin.ui.theme.DarkOnSurfaceVariant
import io.github.drlacheheb.mqtlin.ui.theme.DarkOutlineVariant
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceContainer
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceContainerLow
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinPrimary
import io.github.drlacheheb.mqtlin.ui.theme.UiLabelBold
import io.github.drlacheheb.mqtlin.ui.theme.UiLabelReg

enum class InspectorTab { JSON, HEX, DIFF, CHART }

/**
 * Top Tab Bar for switching between payload inspection representations (JSON, Diff, Hex, Chart).
 */
@Composable
fun InspectorTabsBar(
    activeTab: InspectorTab,
    onTabSelect: (InspectorTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(DarkSurfaceContainer),
        ) {
            TabItem(
                label = "JSON",
                isSelected = activeTab == InspectorTab.JSON,
                onClick = { onTabSelect(InspectorTab.JSON) },
            )
            TabItem(
                label = "Diff",
                isSelected = activeTab == InspectorTab.DIFF,
                icon = Icons.AutoMirrored.Filled.CompareArrows,
                onClick = { onTabSelect(InspectorTab.DIFF) },
            )
            TabItem(
                label = "Hex",
                isSelected = activeTab == InspectorTab.HEX,
                onClick = { onTabSelect(InspectorTab.HEX) },
            )
            TabItem(
                label = "Chart",
                isSelected = activeTab == InspectorTab.CHART,
                icon = Icons.AutoMirrored.Filled.ShowChart,
                onClick = { onTabSelect(InspectorTab.CHART) },
            )
        }

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(DarkOutlineVariant),
        )
    }
}

@Composable
private fun TabItem(
    label: String,
    isSelected: Boolean,
    icon: ImageVector? = null,
    onClick: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxHeight()
                .width(IntrinsicSize.Max)
                .pointerHoverIcon(PointerIcon.Hand)
                .clickable(onClick = onClick)
                .background(if (isSelected) DarkSurfaceContainerLow else Color.Transparent),
        verticalArrangement = Arrangement.Bottom,
    ) {
        Row(
            modifier =
                Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                style =
                    if (isSelected) {
                        UiLabelBold.copy(
                            fontSize = 14.sp,
                            color = MqtlinPrimary,
                        )
                    } else {
                        UiLabelReg.copy(fontSize = 14.sp, color = DarkOnSurfaceVariant)
                    },
                maxLines = 1,
            )
            if (icon != null) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) MqtlinPrimary else DarkOnSurfaceVariant,
                    modifier = Modifier.size(14.dp),
                )
            }
        }
        if (isSelected) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(MqtlinPrimary),
            )
        }
    }
}
