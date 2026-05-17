package com.koru.di

import com.koru.data.local.DatabaseDriverFactory
import com.koru.data.local.KoruDatabase
import com.koru.data.repository.TraceRepositoryImpl
import com.koru.domain.repository.TraceRepository
import com.koru.domain.usecase.ObserveTracesUseCase
import com.koru.domain.usecase.SaveTraceUseCase
import com.koru.presentation.viewmodel.HomeViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

/**
 * Initializes Koin dependency injection for the entire KMP application.
 *
 * Platform-specific modules can be passed to [appDeclaration] to provide
 * expect/actual dependencies like the DatabaseDriverFactory.
 */
fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(
        dataModule,
        domainModule,
        presentationModule
    )
}

/**
 * Convenience function for iOS to initialize Koin.
 * iOS does not have an Application class to configure Koin, so it calls this directly.
 */
fun initKoin() = initKoin {}

val dataModule = module {
    // Requires DatabaseDriverFactory to be provided by the platform module
    single<KoruDatabase> {
        val driverFactory = get<DatabaseDriverFactory>()
        KoruDatabase(driverFactory.createDriver())
    }
    
    single<TraceRepository> { TraceRepositoryImpl(get()) }
}

val domainModule = module {
    factoryOf(::SaveTraceUseCase)
    factoryOf(::ObserveTracesUseCase)
}

val presentationModule = module {
    viewModelOf(::HomeViewModel)
}
