package com.koru

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.koru.presentation.screens.HomeScreen

/**
 * Root composable that bootstraps the Koru application.
 *
 * Wraps the navigation host inside [MaterialTheme] and serves as the
 * single entry point for both Android ([MainActivity]) and iOS
 * (ComposeUIViewController). This keeps the theming contract in
 * [commonMain] and avoids any platform-specific divergence.
 */
@Suppress("ktlint:standard:function-naming")
@Composable
fun KoruApp() {
    MaterialTheme {
        HomeScreen()
    }
}
