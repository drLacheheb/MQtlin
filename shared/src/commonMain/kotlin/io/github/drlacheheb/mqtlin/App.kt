package io.github.drlacheheb.mqtlin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import io.github.drlacheheb.mqtlin.ui.connection.ConnectionDialog
import io.github.drlacheheb.mqtlin.ui.root.RootComponent
import io.github.drlacheheb.mqtlin.ui.theme.DarkBackground
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinTheme
import io.github.drlacheheb.mqtlin.ui.workspace.WorkspaceScreen

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun App(
    rootComponent: RootComponent
) {
    val focusManager = LocalFocusManager.current

    MqtlinTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground)
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent(PointerEventPass.Initial)
                            if (event.type == PointerEventType.Press && event.button == PointerButton.Primary) {
                                focusManager.clearFocus()
                            }
                        }
                    }
                }
        ) {
            Children(
                stack = rootComponent.childStack,
                animation = stackAnimation(fade())
            ) { child ->
                when (val instance = child.instance) {
                    is RootComponent.RootChild.Connection -> {
                        ConnectionDialog(component = instance.component)
                    }
                    is RootComponent.RootChild.Workspace -> {
                        WorkspaceScreen(component = instance.component)
                    }
                }
            }
        }
    }
}
