package io.github.drlacheheb.mqtlin.ui.settings

import com.arkivanov.decompose.value.Value

data class SettingsUiState(
    val title: String = "MQtlin Settings",
    val isUnderConstruction: Boolean = true
)

interface SettingsComponent {
    val state: Value<SettingsUiState>
    fun onClose()
}
