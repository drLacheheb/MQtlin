package io.github.drlacheheb.mqtlin.ui.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushToFront
import com.arkivanov.decompose.value.Value
import io.github.drlacheheb.mqtlin.domain.model.ConnectionConfig
import io.github.drlacheheb.mqtlin.domain.repository.MqttRepository
import io.github.drlacheheb.mqtlin.domain.repository.ProfileRepository
import io.github.drlacheheb.mqtlin.ui.connection.DefaultConnectionComponent
import io.github.drlacheheb.mqtlin.ui.workspace.DefaultWorkspaceComponent
import kotlinx.serialization.Serializable
import kotlin.coroutines.CoroutineContext

class DefaultRootComponent(
    componentContext: ComponentContext,
    private val mqttRepository: MqttRepository,
    private val profileRepository: ProfileRepository? = null,
    private val mainContext: CoroutineContext? = null
) : RootComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    override val childStack: Value<ChildStack<*, RootComponent.RootChild>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = Config.Connection,
            handleBackButton = true,
            childFactory = ::createChild
        )

    private fun createChild(config: Config, context: ComponentContext): RootComponent.RootChild =
        when (config) {
            is Config.Connection -> {
                val connectionComponent = if (mainContext != null) {
                    DefaultConnectionComponent(
                        componentContext = context,
                        mqttRepository = mqttRepository,
                        profileRepository = profileRepository,
                        onConnected = {
                            val activeState = mqttRepository.connectionState.value
                            val connConfig = ConnectionConfig(
                                host = (activeState as? io.github.drlacheheb.mqtlin.domain.model.ConnectionState.Connected)?.host ?: "127.0.0.1",
                                port = (activeState as? io.github.drlacheheb.mqtlin.domain.model.ConnectionState.Connected)?.port ?: 1883,
                                clientId = (activeState as? io.github.drlacheheb.mqtlin.domain.model.ConnectionState.Connected)?.clientId ?: "mqtlin_client"
                            )
                            navigation.pushToFront(Config.Workspace(connConfig))
                        },
                        mainContext = mainContext
                    )
                } else {
                    DefaultConnectionComponent(
                        componentContext = context,
                        mqttRepository = mqttRepository,
                        profileRepository = profileRepository,
                        onConnected = {
                            val activeState = mqttRepository.connectionState.value
                            val connConfig = ConnectionConfig(
                                host = (activeState as? io.github.drlacheheb.mqtlin.domain.model.ConnectionState.Connected)?.host ?: "127.0.0.1",
                                port = (activeState as? io.github.drlacheheb.mqtlin.domain.model.ConnectionState.Connected)?.port ?: 1883,
                                clientId = (activeState as? io.github.drlacheheb.mqtlin.domain.model.ConnectionState.Connected)?.clientId ?: "mqtlin_client"
                            )
                            navigation.pushToFront(Config.Workspace(connConfig))
                        }
                    )
                }
                RootComponent.RootChild.Connection(connectionComponent)
            }
            is Config.Workspace -> {
                val workspaceComponent = if (mainContext != null) {
                    DefaultWorkspaceComponent(
                        componentContext = context,
                        config = config.config,
                        mqttRepository = mqttRepository,
                        onDisconnect = { navigation.pop() },
                        onOpenConnectionManager = { navigation.pushToFront(Config.Connection) },
                        mainContext = mainContext
                    )
                } else {
                    DefaultWorkspaceComponent(
                        componentContext = context,
                        config = config.config,
                        mqttRepository = mqttRepository,
                        onDisconnect = { navigation.pop() },
                        onOpenConnectionManager = { navigation.pushToFront(Config.Connection) }
                    )
                }
                RootComponent.RootChild.Workspace(workspaceComponent)
            }
        }

    @Serializable
    private sealed interface Config {
        @Serializable
        data object Connection : Config

        @Serializable
        data class Workspace(val config: ConnectionConfig) : Config
    }
}
