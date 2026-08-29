package io.github.drlacheheb.mqtlin.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.drlacheheb.mqtlin.ui.theme.DarkBorder
import io.github.drlacheheb.mqtlin.ui.theme.DarkOnSurface
import io.github.drlacheheb.mqtlin.ui.theme.DarkOnSurfaceVariant
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinError
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinOnError
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinOnPrimary
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinPrimary
import io.github.drlacheheb.mqtlin.ui.theme.UiLabelBold

/**
 * Primary action button matching HTML specification:
 * - Exact height: 36dp
 * - Corner radius: 4dp
 * - Background: #C0C1FF
 * - Content color: #1000A9
 * - Font: Inter 14sp, SemiBold (600)
 */
@Composable
fun MqtlinPrimaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    height: Dp = 36.dp,
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .height(height)
            .pointerHoverIcon(if (enabled) PointerIcon.Hand else PointerIcon.Default),
        shape = RoundedCornerShape(4.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MqtlinPrimary,
            contentColor = MqtlinOnPrimary,
            disabledContainerColor = MqtlinPrimary.copy(alpha = 0.4f),
            disabledContentColor = MqtlinOnPrimary.copy(alpha = 0.5f)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 0.dp
        )
    ) {
        ProvideTextStyle(value = UiLabelBold.copy(color = MqtlinOnPrimary)) {
            content()
        }
    }
}

/**
 * Outlined button matching HTML specification:
 * - Exact height: 36dp
 * - Corner radius: 4dp
 * - Border: 1px #27272A
 * - Content color: #E4E1E5
 * - Font: Inter 14sp, SemiBold (600)
 */
@Composable
fun MqtlinOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    height: Dp = 36.dp,
    content: @Composable RowScope.() -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .height(height)
            .pointerHoverIcon(if (enabled) PointerIcon.Hand else PointerIcon.Default),
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, DarkBorder),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = DarkOnSurface
        )
    ) {
        ProvideTextStyle(value = UiLabelBold.copy(color = DarkOnSurface)) {
            content()
        }
    }
}

/**
 * Text / Ghost button matching HTML specification:
 * - Exact height: 36dp
 * - Content color: #C7C4D7
 * - Font: Inter 14sp, SemiBold (600)
 */
@Composable
fun MqtlinTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    height: Dp = 36.dp,
    content: @Composable RowScope.() -> Unit
) {
    TextButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .height(height)
            .pointerHoverIcon(if (enabled) PointerIcon.Hand else PointerIcon.Default),
        shape = RoundedCornerShape(4.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
        colors = ButtonDefaults.textButtonColors(
            contentColor = DarkOnSurfaceVariant
        )
    ) {
        ProvideTextStyle(value = UiLabelBold.copy(color = DarkOnSurfaceVariant)) {
            content()
        }
    }
}

/**
 * Danger button (for Disconnect):
 * - Exact height: 36dp
 * - Background: #FFB4AB
 * - Content color: #690005
 */
@Composable
fun MqtlinDangerButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    height: Dp = 36.dp,
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .height(height)
            .pointerHoverIcon(if (enabled) PointerIcon.Hand else PointerIcon.Default),
        shape = RoundedCornerShape(4.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MqtlinError,
            contentColor = MqtlinOnError
        )
    ) {
        ProvideTextStyle(value = UiLabelBold.copy(color = MqtlinOnError)) {
            content()
        }
    }
}
