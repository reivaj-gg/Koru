package com.koru

import androidx.compose.ui.window.ComposeUIViewController
import com.koru.di.commonModule
import com.koru.di.dataModule
import com.koru.di.platformModule
import org.koin.core.context.startKoin

/**
 * Initializes Koin for iOS.
 * Called from iOS Swift application lifecycle.
 */
fun initKoin() {
    startKoin {
        modules(platformModule, dataModule, commonModule)
    }
}

/**
 * iOS entry point for Koru application.
 *
 * Starts Koin dependency injection and delegates UI rendering to
 * the shared [KoruApp] Compose Multiplatform composable.
 */
@Suppress("FunctionName")
fun MainViewController() =
    ComposeUIViewController {
        KoruApp()
    }
