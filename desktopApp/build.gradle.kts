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

val appVersion = libs.versions.app.version.get()
val semver = appVersion.substringBefore("-")

compose.desktop {
    application {
        mainClass = "io.github.drlacheheb.mqtlin.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb, TargetFormat.Exe)
            modules("java.instrument", "jdk.unsupported")
            packageName = "MQtlin"
            packageVersion = semver
            description = "MQtlin - Modern MQTT Explorer & Broker Client"
            copyright = "© 2026 MQtlin"
            vendor = "io.github.drlacheheb"

            windows {
                iconFile.set(project.file("src/main/resources/icons/icon.ico"))
                menuGroup = "MQtlin"
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

tasks.register("printVersion") {
    doLast {
        println(appVersion)
    }
}

tasks.register<Exec>("packageInnoSetup") {
    dependsOn(tasks.named("createDistributable"))
    group = "compose desktop"
    description = "Compiles custom Modern Dark Windows Setup Wizard using Inno Setup"

    val isccCandidates = listOf(
        System.getenv("LOCALAPPDATA")?.let { "$it\\Programs\\Inno Setup 6\\ISCC.exe" },
        "C:\\Program Files (x86)\\Inno Setup 6\\ISCC.exe",
        "C:\\Program Files\\Inno Setup 6\\ISCC.exe",
        "ISCC.exe"
    ).filterNotNull()

    val isccPath = isccCandidates.firstOrNull { File(it).exists() } ?: "ISCC.exe"
    val setupScript = rootProject.file("installer/windows/setup.iss").absolutePath

    commandLine(isccPath, "/DMyAppVersion=$appVersion", setupScript)
}

tasks.matching { it.name.startsWith("hotRun") }.configureEach {
    dependsOn(tasks.named("classes"))
    notCompatibleWithConfigurationCache("Compose Hot Reload runs an interactive agent with dynamic classpath mutation")
}