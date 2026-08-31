package io.github.drlacheheb.mqtlin.ui.settings

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

class DefaultSettingsComponent(
    componentContext: ComponentContext,
    private val onDismiss: () -> Unit = {},
) : SettingsComponent,
    ComponentContext by componentContext {
    private val _state = MutableValue(SettingsUiState())
    override val state: Value<SettingsUiState> = _state

    override fun onClose() {
        onDismiss()
    }
}
