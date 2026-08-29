import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

dependencies {
    implementation(project(":shared"))

    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutinesSwing)

    implementation(libs.decompose.core)
    implementation(libs.decompose.compose)

    implementation(libs.compose.uiToolingPreview)
}

compose.desktop {
    application {
        mainClass = "io.github.drlacheheb.mqtlin.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb, TargetFormat.Exe)
            packageName = "Mqtlin"
            packageVersion = "1.0.0"
            description = "Mqtlin - Modern MQTT Explorer & Broker Client"
            copyright = "© 2026 Mqtlin"
            vendor = "io.github.drlacheheb"

            windows {
                iconFile.set(project.file("src/main/resources/icons/icon.ico"))
                menuGroup = "Mqtlin"
                shortcut = true
            }
            macOS {
                iconFile.set(project.file("src/main/resources/icons/icon.png"))
                bundleID = "io.github.drlacheheb.mqtlin"
            }
            linux {
                iconFile.set(project.file("src/main/resources/icons/icon.png"))
                shortcut = true
            }
        }
    }
}