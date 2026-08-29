package io.github.drlacheheb.mqtlin.ui.util

import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.semantics.Role

fun Modifier.cursorHand(): Modifier = this.pointerHoverIcon(PointerIcon.Hand)

fun Modifier.clickableHand(
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: () -> Unit
): Modifier = this
    .pointerHoverIcon(if (enabled) PointerIcon.Hand else PointerIcon.Default)
    .clickable(
        enabled = enabled,
        onClickLabel = onClickLabel,
        role = role,
        onClick = onClick
    )
