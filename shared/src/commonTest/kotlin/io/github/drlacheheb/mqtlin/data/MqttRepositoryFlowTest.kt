package io.github.drlacheheb.mqtlin.data

import app.cash.turbine.test
import io.github.drlacheheb.mqtlin.domain.model.ConnectionConfig
import io.github.drlacheheb.mqtlin.domain.model.ConnectionState
import io.github.drlacheheb.mqtlin.fakes.FakeMqttRepository
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class MqttRepositoryFlowTest {
    private val repository = FakeMqttRepository()

    @Test
    fun `connectionState initially emits Disconnected`() =
        runTest {
            repository.connectionState.test {
                awaitItem() shouldBe ConnectionState.Disconnected
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `connect emits Connecting then Connected sequence`() =
        runTest {
            repository.simulatedDelayMs = 10L
            val config = ConnectionConfig(host = "broker.emqx.io", port = 1883)

            repository.connectionState.test {
                awaitItem() shouldBe ConnectionState.Disconnected

                repository.connect(config)

                val connecting = awaitItem()
                connecting.shouldBeInstanceOf<ConnectionState.Connecting>()
                (connecting as ConnectionState.Connecting).host shouldBe "broker.emqx.io"

                val connected = awaitItem()
                connected.shouldBeInstanceOf<ConnectionState.Connected>()
                (connected as ConnectionState.Connected).host shouldBe "broker.emqx.io"

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `disconnect emits Disconnected state`() =
        runTest {
            val config = ConnectionConfig(host = "127.0.0.1", port = 1883)

            repository.connectionState.test {
                awaitItem() shouldBe ConnectionState.Disconnected

                repository.connect(config)
                awaitItem().shouldBeInstanceOf<ConnectionState.Connecting>()
                awaitItem().shouldBeInstanceOf<ConnectionState.Connected>()

                repository.disconnect()
                awaitItem() shouldBe ConnectionState.Disconnected

                cancelAndIgnoreRemainingEvents()
            }
        }
}
