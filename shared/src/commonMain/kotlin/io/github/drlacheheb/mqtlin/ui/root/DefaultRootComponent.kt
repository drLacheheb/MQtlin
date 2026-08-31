package io.github.drlacheheb.mqtlin.ui.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.decompose.value.Value
import io.github.drlacheheb.mqtlin.domain.model.ConnectionConfig
import io.github.drlacheheb.mqtlin.domain.repository.MqttRepository
import io.github.drlacheheb.mqtlin.domain.repository.ProfileRepository
import io.github.drlacheheb.mqtlin.ui.connection.DefaultConnectionComponent
import io.github.drlacheheb.mqtlin.ui.settings.DefaultSettingsComponent
import io.github.drlacheheb.mqtlin.ui.workspace.DefaultWorkspaceComponent
import kotlinx.serialization.Serializable
import kotlin.coroutines.CoroutineContext

class DefaultRootComponent(
    componentContext: ComponentContext,
    private val mqttRepository: MqttRepository,
    private val profileRepository: ProfileRepository? = null,
    private val mainContext: CoroutineContext? = null,
) : RootComponent,
    ComponentContext by componentContext {
    private val navigation = StackNavigation<Config>()
    private val dialogNav = SlotNavigation<DialogNavConfig>()

    override val childStack: Value<ChildStack<*, RootComponent.RootChild>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = Config.Connection,
            handleBackButton = true,
            childFactory = ::createChild,
        )

    override val dialogSlot: Value<ChildSlot<*, RootComponent.DialogChild>> =
        childSlot(
            source = dialogNav,
            serializer = DialogNavConfig.serializer(),
            handleBackButton = true,
            childFactory = ::createDialogChild,
        )

    override fun onOpenSettings() {
        dialogNav.activate(DialogNavConfig.Settings)
    }

    override fun onOpenConnectionManager() {
        dialogNav.activate(DialogNavConfig.ConnectionManager)
    }

    override fun onDismissDialog() {
        dialogNav.dismiss()
    }

    private fun createChild(
        config: Config,
        context: ComponentContext,
    ): RootComponent.RootChild =
        when (config) {
            is Config.Connection -> {
                val connectionComponent =
                    if (mainContext != null) {
                        DefaultConnectionComponent(
                            componentContext = context,
                            mqttRepository = mqttRepository,
                            profileRepository = profileRepository,
                            onConnected = { connConfig ->
                                navigation.replaceAll(Config.Workspace(connConfig))
                            },
                            mainContext = mainContext,
                        )
                    } else {
                        DefaultConnectionComponent(
                            componentContext = context,
                            mqttRepository = mqttRepository,
                            profileRepository = profileRepository,
                            onConnected = { connConfig ->
                                navigation.replaceAll(Config.Workspace(connConfig))
                            },
                        )
                    }
                RootComponent.RootChild.Connection(connectionComponent)
            }
            is Config.Workspace -> {
                val workspaceComponent =
                    if (mainContext != null) {
                        DefaultWorkspaceComponent(
                            componentContext = context,
                            config = config.config,
                            mqttRepository = mqttRepository,
                            onDisconnect = { navigation.replaceAll(Config.Connection) },
                            onOpenConnectionManager = ::onOpenConnectionManager,
                            onOpenSettings = ::onOpenSettings,
                            mainContext = mainContext,
                        )
                    } else {
                        DefaultWorkspaceComponent(
                            componentContext = context,
                            config = config.config,
                            mqttRepository = mqttRepository,
                            onDisconnect = { navigation.replaceAll(Config.Connection) },
                            onOpenConnectionManager = ::onOpenConnectionManager,
                            onOpenSettings = ::onOpenSettings,
                        )
                    }
                RootComponent.RootChild.Workspace(workspaceComponent)
            }
        }

    private fun createDialogChild(
        config: DialogNavConfig,
        context: ComponentContext,
    ): RootComponent.DialogChild =
        when (config) {
            is DialogNavConfig.Settings -> {
                val settingsComponent =
                    DefaultSettingsComponent(
                        componentContext = context,
                        onDismiss = { dialogNav.dismiss() },
                    )
                RootComponent.DialogChild.Settings(settingsComponent)
            }
            is DialogNavConfig.ConnectionManager -> {
                val connectionComponent =
                    if (mainContext != null) {
                        DefaultConnectionComponent(
                            componentContext = context,
                            mqttRepository = mqttRepository,
                            profileRepository = profileRepository,
                            onConnected = { connConfig ->
                                dialogNav.dismiss()
                                navigation.replaceAll(Config.Workspace(connConfig))
                            },
                            mainContext = mainContext,
                        )
                    } else {
                        DefaultConnectionComponent(
                            componentContext = context,
                            mqttRepository = mqttRepository,
                            profileRepository = profileRepository,
                            onConnected = { connConfig ->
                                dialogNav.dismiss()
                                navigation.replaceAll(Config.Workspace(connConfig))
                            },
                        )
                    }
                RootComponent.DialogChild.ConnectionManager(connectionComponent)
            }
        }

    @Serializable
    private sealed interface Config {
        @Serializable
        data object Connection : Config

        @Serializable
        data class Workspace(
            val config: ConnectionConfig,
        ) : Config
    }

    @Serializable
    private sealed interface DialogNavConfig {
        @Serializable
        data object Settings : DialogNavConfig

        @Serializable
        data object ConnectionManager : DialogNavConfig
    }
}
