package io.github.drlacheheb.mqtlin.ui.connection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.drlacheheb.mqtlin.ui.components.WindowControls
import io.github.drlacheheb.mqtlin.ui.theme.DarkBorder
import io.github.drlacheheb.mqtlin.ui.theme.DarkBorderHover
import io.github.drlacheheb.mqtlin.ui.theme.DarkOnSurface
import io.github.drlacheheb.mqtlin.ui.theme.DarkOnSurfaceVariant
import io.github.drlacheheb.mqtlin.ui.theme.DarkOutlineMuted
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinPrimary

/**
 * Top header of the Connection Dialog showing breadcrumb path and window controls.
 */
@Composable
fun ConnectionFormHeader(
    profileName: String,
    onNameChange: (String) -> Unit,
    onNameSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(start = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f, fill = false)
            ) {
                Text(
                    text = "Connection Settings",
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = DarkOnSurfaceVariant
                    )
                )

                Text(
                    text = "›",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = DarkOutlineMuted
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                InlineEditableProfileName(
                    name = profileName,
                    onNameChange = onNameChange,
                    onNameSave = onNameSave
                )
            }

            WindowControls(height = 48)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(DarkBorder)
        )
    }
}

@Composable
fun InlineEditableProfileName(
    name: String,
    onNameChange: (String) -> Unit,
    onNameSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    var isHovered by remember { mutableStateOf(false) }
    var hadFocus by remember { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .widthIn(min = 60.dp, max = 240.dp)
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        when (event.type) {
                            PointerEventType.Enter -> isHovered = true
                            PointerEventType.Exit -> isHovered = false
                        }
                    }
                }
            }
            .drawBehind {
                val strokeColor = when {
                    isFocused -> MqtlinPrimary
                    isHovered -> DarkBorderHover
                    else -> Color.Transparent
                }
                if (strokeColor != Color.Transparent) {
                    drawLine(
                        color = strokeColor,
                        start = Offset(0f, size.height - 1f),
                        end = Offset(size.width, size.height - 1f),
                        strokeWidth = if (isFocused) 1.5.dp.toPx() else 1.dp.toPx()
                    )
                }
            }
            .padding(horizontal = 2.dp, vertical = 2.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        BasicTextField(
            value = name,
            onValueChange = { input ->
                onNameChange(input.take(MAX_PROFILE_NAME_LENGTH))
            },
            textStyle = TextStyle(
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkOnSurface,
                textAlign = TextAlign.Start
            ),
            singleLine = true,
            cursorBrush = SolidColor(MqtlinPrimary),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    onNameSave()
                }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                    if (hadFocus && !focusState.isFocused) {
                        onNameSave()
                    }
                    hadFocus = focusState.isFocused
                }
                .onKeyEvent { keyEvent ->
                    if (keyEvent.type == KeyEventType.KeyDown && (keyEvent.key == Key.Enter || keyEvent.key == Key.NumPadEnter)) {
                        focusManager.clearFocus()
                        onNameSave()
                        true
                    } else {
                        false
                    }
                }
        )
    }
}
