package com.koru.di

import com.koru.data.local.DatabaseDriverFactory
import com.koru.data.repository.MicrophonePermissionHelper
import com.koru.data.repository.NativeAudioRecorder
import com.koru.domain.repository.PermissionHelper
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Android-specific dependencies including Context-aware hardware and database driver.
 *
 * [DatabaseDriverFactory] requires [androidContext] and is consumed by [dataModule]
 * to construct the [KoruDatabase] singleton.
 */
actual val platformModule: Module =
    module {
        single { DatabaseDriverFactory(androidContext()) }
        factory { NativeAudioRecorder(androidContext()) }
        factory<PermissionHelper> { MicrophonePermissionHelper(androidContext()) }
    }
