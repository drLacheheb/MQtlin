package io.github.drlacheheb.mqtlin.di

import io.github.drlacheheb.mqtlin.data.repository.FileProfileRepository
import io.github.drlacheheb.mqtlin.data.repository.FileSettingsRepository
import io.github.drlacheheb.mqtlin.data.repository.HiveMqRepository
import io.github.drlacheheb.mqtlin.domain.repository.MqttRepository
import io.github.drlacheheb.mqtlin.domain.repository.ProfileRepository
import io.github.drlacheheb.mqtlin.domain.repository.SettingsRepository
import org.koin.core.module.Module
import org.koin.dsl.module

val jvmDataModule: Module = module {
    single<MqttRepository> { HiveMqRepository() }
    single<ProfileRepository> { FileProfileRepository() }
    single<SettingsRepository> { FileSettingsRepository() }
}
