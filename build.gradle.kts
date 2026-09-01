plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.detekt) apply false
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    apply(plugin = "io.gitlab.arturbosch.detekt")

    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        version.set("1.5.0")
        verbose.set(true)
        android.set(false)
        outputToConsole.set(true)
        ignoreFailures.set(false)
        enableExperimentalRules.set(false)
        filter {
            exclude("**/build/**")
            exclude("**/generated/**")
        }
    }


    dependencies {
        "detektPlugins"(rootProject.libs.detekt.compose.rules)
        "detektPlugins"(rootProject.libs.detekt.formatting)
        "ktlintRuleset"(rootProject.libs.ktlint.compose.rules)
    }

    configure<io.gitlab.arturbosch.detekt.extensions.DetektExtension> {
        buildUponDefaultConfig = true
        allRules = false
        config.setFrom(files("${rootProject.projectDir}/config/detekt/detekt.yml"))
        parallel = true
        source.setFrom(
            files(
                "src/commonMain/kotlin",
                "src/jvmMain/kotlin",
                "src/commonTest/kotlin",
                "src/jvmTest/kotlin",
                "src/main/kotlin",
            ),
        )
    }

    tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
        exclude("**/build/**")
        exclude("**/generated/**")
        reports {
            html.required.set(true)
            xml.required.set(true)
            md.required.set(true)
        }
    }
}
