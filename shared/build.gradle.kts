plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    compilerOptions {
        allWarningsAsErrors.set(true)
    }

    jvm {
        compilerOptions {
            allWarningsAsErrors.set(true)
        }
    }

    sourceSets {
        commonMain.dependencies {
            // Compose Multiplatform & UI
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            // Decompose Navigation (api so DesktopApp and tests access ComponentContext)
            api(libs.decompose.core)
            api(libs.decompose.compose)

            // Dependency Injection (Koin)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            // Serialization, Datetime & Logging
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.io.core)
            implementation(libs.kermit)

            // DataStore Preferences
            implementation(libs.datastore.preferences)

            // Ktor HTTP & WebSockets
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.cio)
            implementation(libs.ktor.client.websockets)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
        }

        jvmMain.dependencies {
            // HiveMQ MQTT Client (JVM Engine)
            implementation(libs.hivemq.mqtt.client)

            // Reactive Streams to Kotlin Flow bridge
            implementation(libs.kotlinx.coroutinesReactive)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutinesTest)
            implementation(libs.turbine)
            implementation(libs.kotest.assertions)
        }
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "io.github.drlacheheb.mqtlin.resources"
}
