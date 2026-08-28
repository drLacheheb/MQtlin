package io.github.drlacheheb.mqtlin.domain.usecase

import io.github.drlacheheb.mqtlin.domain.model.ConnectionConfig

sealed interface ValidationResult {
    data object Valid : ValidationResult
    data class Invalid(val errors: Map<Field, String>) : ValidationResult

    enum class Field {
        HOST,
        PORT,
        CLIENT_ID
    }
}

class ValidateConnectionConfigUseCase {
    operator fun invoke(config: ConnectionConfig): ValidationResult {
        val errors = mutableMapOf<ValidationResult.Field, String>()

        if (config.host.isBlank()) {
            errors[ValidationResult.Field.HOST] = "Host / IP cannot be blank"
        }

        if (config.port !in 1..65535) {
            errors[ValidationResult.Field.PORT] = "Port must be between 1 and 65535"
        }

        if (config.clientId.isBlank()) {
            errors[ValidationResult.Field.CLIENT_ID] = "Client ID cannot be blank"
        }

        return if (errors.isEmpty()) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(errors)
        }
    }
}
