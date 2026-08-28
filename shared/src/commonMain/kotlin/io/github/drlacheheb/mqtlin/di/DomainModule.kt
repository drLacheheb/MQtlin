package io.github.drlacheheb.mqtlin.di

import io.github.drlacheheb.mqtlin.domain.usecase.ValidateConnectionConfigUseCase
import org.koin.core.module.Module
import org.koin.dsl.module

val domainModule: Module = module {
    single { ValidateConnectionConfigUseCase() }
}
