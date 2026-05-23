buildscript {
    dependencies {
        classpath("org.xerial:sqlite-jdbc:3.41.2.2")
    }
}

plugins {
    // Declared here to avoid loading plugins multiple times
    // in each subproject's classloader.
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.sqldelight) apply false
    alias(libs.plugins.ktlint) apply false
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        debug.set(false)
        verbose.set(true)
        android.set(true)
        outputToConsole.set(true)
        ignoreFailures.set(false)
        filter {
            exclude { it.file.path.contains("generated") }
            exclude { it.file.path.contains("build") }
        }
    }

    // Force exclusion at the task level because KtlintExtension filter sometimes
    // fails to apply to KMP source sets dynamically added by SQLDelight.
    tasks.withType<org.jlleitschuh.gradle.ktlint.tasks.BaseKtLintCheckTask>().configureEach {
        exclude { it.file.absolutePath.contains("generated") }
        exclude { it.file.absolutePath.contains("build") }
    }
}
