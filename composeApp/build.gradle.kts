import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.sqldelight)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    // Only configure Apple targets if we are running on macOS.
    // This prevents `./gradlew check` from failing on Windows with Klib resolution errors.
    if (System.getProperty("os.name").startsWith("Mac")) {
        listOf(
            iosArm64(),
            iosSimulatorArm64(),
        ).forEach { iosTarget ->
            iosTarget.binaries.framework {
                baseName = "ComposeApp"
                isStatic = true
            }
        }
    }

    sourceSets {

        // ─── Android-only ─────────────────────────────────────────────────
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.activity.compose)
            // SQLDelight Android driver — required for AndroidSqliteDriver
            implementation(libs.sqldelight.android)
            implementation(libs.koin.android)
            implementation(libs.ktor.client.android)
        }

        // ─── Common (shared across all platforms) ─────────────────────────
        commonMain.dependencies {
            // Compose Multiplatform
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)

            // Lifecycle & ViewModel
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            // Navigation
            implementation(libs.navigation.compose)

            // KotlinX
            implementation(libs.kotlinx.datetime)

            // SQLDelight — coroutines extensions (shared queries as Flow)
            implementation(libs.sqldelight.coroutines)

            // Koin (BOM ensures all koin-* artifacts share the same version)
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            // Ktor Client
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.serialization.kotlinx.json)

            // Logging
            implementation(libs.napier)

            // Preferences
            implementation(libs.multiplatform.settings)
            implementation(libs.multiplatform.settings.coroutines)

            // MOKO Permissions — cross-platform permission handling (mic, notifications)
            implementation(libs.moko.permissions)
            implementation(libs.moko.permissions.compose)
        }

        // ─── Common tests ─────────────────────────────────────────────────
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
            implementation(libs.ktor.client.mock)
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.test)
        }

        // ─── Android unit tests (JVM — can use JDBC SQLite driver) ────────
        val androidUnitTest by getting {
            dependencies {
                implementation(libs.sqldelight.sqlite)
                implementation(libs.kotlin.testJunit)
                implementation(libs.junit)
            }
        }

        // ─── iOS-only ─────────────────────────────────────────────────────
        iosMain.dependencies {
            // SQLDelight iOS/Native driver — required for NativeSqliteDriver
            implementation(libs.sqldelight.native)
            implementation(libs.ktor.client.darwin)
            implementation(libs.kotlinx.datetime)
        }
    }
}

// ─── SQLDelight ───────────────────────────────────────────────────────────────
sqldelight {
    databases {
        create("KoruDatabase") {
            packageName.set("com.koru.database")
            verifyMigrations.set(false)
        }
    }
}

// ─── Android ──────────────────────────────────────────────────────────────────
android {
    namespace = "com.koru"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.koru"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(libs.compose.uiTooling)
}

// FIX: Disable SQLDelight VerifyMigrationTask globally to avoid Windows sqlite-jdbc crash
tasks.withType<app.cash.sqldelight.gradle.VerifyMigrationTask>().configureEach {
    enabled = false
}
