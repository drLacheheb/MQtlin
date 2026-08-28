package io.github.drlacheheb.mqtlin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.drlacheheb.mqtlin.ui.connection.ConnectionComponent
import io.github.drlacheheb.mqtlin.ui.connection.ConnectionDialog
import io.github.drlacheheb.mqtlin.ui.theme.DarkBackground
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinTheme

@Composable
fun App(
    connectionComponent: ConnectionComponent
) {
    MqtlinTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground)
        ) {
            ConnectionDialog(component = connectionComponent)
        }
    }
}