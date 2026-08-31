package io.github.drlacheheb.mqtlin.domain

import io.github.drlacheheb.mqtlin.domain.model.ConnectionConfig
import io.github.drlacheheb.mqtlin.domain.usecase.ValidateConnectionConfigUseCase
import io.github.drlacheheb.mqtlin.domain.usecase.ValidationResult
import io.kotest.matchers.maps.shouldContainKey
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.Test

class ValidateConnectionConfigUseCaseTest {
    private val useCase = ValidateConnectionConfigUseCase()

    @Test
    fun `valid configuration returns Valid result`() {
        val config =
            ConnectionConfig(
                host = "broker.hivemq.com",
                port = 1883,
                clientId = "test_client_123",
            )

        val result = useCase(config)

        result shouldBe ValidationResult.Valid
    }

    @Test
    fun `boundary port 1 returns Valid result`() {
        val config =
            ConnectionConfig(
                host = "broker.hivemq.com",
                port = 1,
                clientId = "test_client_123",
            )

        val result = useCase(config)

        result shouldBe ValidationResult.Valid
    }

    @Test
    fun `boundary port 65535 returns Valid result`() {
        val config =
            ConnectionConfig(
                host = "broker.hivemq.com",
                port = 65535,
                clientId = "test_client_123",
            )

        val result = useCase(config)

        result shouldBe ValidationResult.Valid
    }

    @Test
    fun `blank host returns Invalid with HOST error`() {
        val config =
            ConnectionConfig(
                host = "   ",
                port = 1883,
                clientId = "test_client_123",
            )

        val result = useCase(config)

        result.shouldBeInstanceOf<ValidationResult.Invalid>()
        val invalid = result as ValidationResult.Invalid
        invalid.errors shouldContainKey ValidationResult.Field.HOST
    }

    @Test
    fun `port below 1 returns Invalid with PORT error`() {
        val config =
            ConnectionConfig(
                host = "localhost",
                port = 0,
                clientId = "test_client_123",
            )

        val result = useCase(config)

        result.shouldBeInstanceOf<ValidationResult.Invalid>()
        val invalid = result as ValidationResult.Invalid
        invalid.errors shouldContainKey ValidationResult.Field.PORT
    }

    @Test
    fun `port above 65535 returns Invalid with PORT error`() {
        val config =
            ConnectionConfig(
                host = "localhost",
                port = 65536,
                clientId = "test_client_123",
            )

        val result = useCase(config)

        result.shouldBeInstanceOf<ValidationResult.Invalid>()
        val invalid = result as ValidationResult.Invalid
        invalid.errors shouldContainKey ValidationResult.Field.PORT
    }

    @Test
    fun `blank client ID returns Invalid with CLIENT_ID error`() {
        val config =
            ConnectionConfig(
                host = "localhost",
                port = 1883,
                clientId = "   ",
            )

        val result = useCase(config)

        result.shouldBeInstanceOf<ValidationResult.Invalid>()
        val invalid = result as ValidationResult.Invalid
        invalid.errors shouldContainKey ValidationResult.Field.CLIENT_ID
    }

    @Test
    fun `multiple invalid fields return multiple errors`() {
        val config =
            ConnectionConfig(
                host = "",
                port = -1,
                clientId = "",
            )

        val result = useCase(config)

        result.shouldBeInstanceOf<ValidationResult.Invalid>()
        val invalid = result as ValidationResult.Invalid
        invalid.errors shouldContainKey ValidationResult.Field.HOST
        invalid.errors shouldContainKey ValidationResult.Field.PORT
        invalid.errors shouldContainKey ValidationResult.Field.CLIENT_ID
    }
}
