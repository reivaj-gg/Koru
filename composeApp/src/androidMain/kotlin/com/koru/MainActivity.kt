package com.koru

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.koru.di.commonModule
import com.koru.di.dataModule
import com.koru.di.platformModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

/**
 * Android entry point for the Koru application.
 *
 * Initialises Koin with the platform, data, and common DI modules, enables
 * edge-to-edge display, and delegates all UI to [KoruApp] — the shared
 * Compose Multiplatform root composable defined in [commonMain].
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        startKoin {
            androidLogger()
            androidContext(this@MainActivity)
            modules(platformModule, dataModule, commonModule)
        }

        setContent {
            KoruApp()
        }
    }
}
