package com.koru.di

import com.koru.data.local.DatabaseDriverFactory
import com.koru.data.local.createKoruDatabase
import com.koru.data.remote.GeminiClient
import com.koru.data.repository.InsightRepositoryImpl
import com.koru.data.repository.TraceRepositoryImpl
import com.koru.data.repository.VoiceCaptureRepositoryImpl
import com.koru.database.KoruDatabase
import com.koru.domain.repository.InsightRepository
import com.koru.domain.repository.TraceRepository
import com.koru.domain.repository.VoiceCaptureRepository
import com.koru.domain.usecase.GetInsightUseCase
import com.koru.domain.usecase.ObserveTracesUseCase
import com.koru.domain.usecase.SaveTraceUseCase
import com.koru.domain.usecase.StartVoiceCaptureUseCase
import com.koru.presentation.utils.TreeLayoutCalculator
import com.koru.presentation.viewmodel.HomeViewModel
import com.koru.presentation.viewmodel.VoiceCaptureViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Common Koin module for Domain and Presentation layers.
 *
 * Provides use cases and the VoiceCaptureViewModel.
 * Platform-specific dependencies (DB driver, hardware) live in [platformModule].
 */
val commonModule =
    module {
        // ─── Use Cases ────────────────────────────────────────────────────────
        factory { SaveTraceUseCase(get()) }
        factory { ObserveTracesUseCase(get()) }
        factory { StartVoiceCaptureUseCase(get()) }
        factory { GetInsightUseCase(get(), get()) }

        // ─── ViewModels ───────────────────────────────────────────────────────
        factory { VoiceCaptureViewModel(get(), get(), get()) }
        factory { TreeLayoutCalculator() }
        factory { HomeViewModel(get(), get(), get()) }
    }

/**
 * Data Koin module for repository and database implementations.
 *
 * [KoruDatabase] is a singleton — one database instance for the lifetime of the app.
 * [DatabaseDriverFactory] is resolved from [platformModule] where the platform context lives.
 */
val dataModule =
    module {
        // ─── Database ─────────────────────────────────────────────────────────
        single { createKoruDatabase(get()) }

        // ─── Network ──────────────────────────────────────────────────────────
        single {
            HttpClient {
                install(ContentNegotiation) {
                    json(Json { ignoreUnknownKeys = true })
                }
            }
        }
        single { GeminiClient(get(), "YOUR_API_KEY") }

        // ─── Repositories ─────────────────────────────────────────────────────
        single<TraceRepository> { TraceRepositoryImpl(get()) }
        single<VoiceCaptureRepository> { VoiceCaptureRepositoryImpl(get()) }
        single<InsightRepository> { InsightRepositoryImpl(get()) }
    }

/**
 * Expect declaration for platform-specific dependencies.
 * Each platform provides: [DatabaseDriverFactory], [NativeAudioRecorder], [PermissionHelper].
 */
expect val platformModule: Module
