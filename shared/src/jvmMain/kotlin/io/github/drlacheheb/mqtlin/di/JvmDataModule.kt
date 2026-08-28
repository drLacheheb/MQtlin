package io.github.drlacheheb.mqtlin.di

import io.github.drlacheheb.mqtlin.data.repository.HiveMqRepository
import io.github.drlacheheb.mqtlin.domain.repository.MqttRepository
import org.koin.core.module.Module
import org.koin.dsl.module

val jvmDataModule: Module = module {
    single<MqttRepository> { HiveMqRepository() }
}
