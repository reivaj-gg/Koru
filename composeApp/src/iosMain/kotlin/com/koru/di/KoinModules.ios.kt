package com.koru.di

import com.koru.data.local.DatabaseDriverFactory
import com.koru.data.repository.MicrophonePermissionHelper
import com.koru.data.repository.NativeAudioRecorder
import com.koru.domain.repository.PermissionHelper
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * iOS-specific dependencies.
 *
 * [DatabaseDriverFactory] is context-free on iOS — no Application needed.
 */
actual val platformModule: Module =
    module {
        single { DatabaseDriverFactory() }
        factory { NativeAudioRecorder() }
        factory<PermissionHelper> { MicrophonePermissionHelper() }
    }
