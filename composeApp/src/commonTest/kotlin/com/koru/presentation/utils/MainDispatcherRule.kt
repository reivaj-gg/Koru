package com.koru.presentation.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

/**
 * Base class para inyectar el TestDispatcher en Dispatchers.Main.
 * Es crucial en Kotlin Native (iOS) porque viewModelScope por defecto
 * usa Dispatchers.Main, el cual falla en el contexto del simulador de test.
 */
@OptIn(ExperimentalCoroutinesApi::class)
open class MainDispatcherRule(
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) {
    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }
}
