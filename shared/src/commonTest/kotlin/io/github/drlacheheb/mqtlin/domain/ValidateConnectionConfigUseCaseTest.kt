package io.github.drlacheheb.mqtlin.domain

import io.github.drlacheheb.mqtlin.domain.model.ConnectionConfig
import io.github.drlacheheb.mqtlin.domain.usecase.ValidateConnectionConfigUseCase
import io.github.drlacheheb.mqtlin.domain.usecase.ValidationResult
import io.kotest.matchers.maps.shouldContainKey
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.Test

class ValidateConnectionConfigUseCaseTest {

    private val validateUseCase = ValidateConnectionConfigUseCase()

    @Test
    fun `valid configuration returns Valid result`() {
        val config = ConnectionConfig(
            host = "broker.emqx.io",
            port = 1883,
            clientId = "test_client_01"
        )

        val result = validateUseCase(config)

        result shouldBe ValidationResult.Valid
    }

    @Test
    fun `blank host returns Invalid with HOST error`() {
        val config = ConnectionConfig(
            host = "   ",
            port = 1883,
            clientId = "test_client"
        )

        val result = validateUseCase(config)

        result.shouldBeInstanceOf<ValidationResult.Invalid>()
        result.errors shouldContainKey ValidationResult.Field.HOST
    }

    @Test
    fun `port below 1 returns Invalid with PORT error`() {
        val config = ConnectionConfig(
            host = "127.0.0.1",
            port = 0,
            clientId = "test_client"
        )

        val result = validateUseCase(config)

        result.shouldBeInstanceOf<ValidationResult.Invalid>()
        result.errors shouldContainKey ValidationResult.Field.PORT
    }

    @Test
    fun `port above 65535 returns Invalid with PORT error`() {
        val config = ConnectionConfig(
            host = "127.0.0.1",
            port = 70000,
            clientId = "test_client"
        )

        val result = validateUseCase(config)

        result.shouldBeInstanceOf<ValidationResult.Invalid>()
        result.errors shouldContainKey ValidationResult.Field.PORT
    }

    @Test
    fun `blank client ID returns Invalid with CLIENT_ID error`() {
        val config = ConnectionConfig(
            host = "127.0.0.1",
            port = 1883,
            clientId = ""
        )

        val result = validateUseCase(config)

        result.shouldBeInstanceOf<ValidationResult.Invalid>()
        result.errors shouldContainKey ValidationResult.Field.CLIENT_ID
    }
}
