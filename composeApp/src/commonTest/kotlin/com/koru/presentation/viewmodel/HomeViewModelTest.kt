package com.koru.presentation.viewmodel

import app.cash.turbine.test
import com.koru.domain.repository.FakeTraceRepository
import com.koru.domain.usecase.SaveTraceUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private lateinit var viewModel: HomeViewModel
    private lateinit var fakeRepository: FakeTraceRepository
    private lateinit var saveTraceUseCase: SaveTraceUseCase

    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeTraceRepository()
        saveTraceUseCase = SaveTraceUseCase(fakeRepository)
        viewModel = HomeViewModel(saveTraceUseCase)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `given OpenCapture intent when dispatched then isCapturing becomes true`() = runTest {
        viewModel.state.test {
            // Initial state
            assertFalse(awaitItem().isCapturing)

            // Dispatch intent
            viewModel.dispatch(HomeIntent.OpenCapture)

            // State changes to capturing
            assertTrue(awaitItem().isCapturing)
        }
    }

    @Test
    fun `given CloseCapture intent when dispatched then isCapturing becomes false`() = runTest {
        viewModel.dispatch(HomeIntent.OpenCapture)
        viewModel.state.test {
            assertTrue(awaitItem().isCapturing)

            viewModel.dispatch(HomeIntent.CloseCapture)

            assertFalse(awaitItem().isCapturing)
        }
    }

    @Test
    fun `given valid content when SaveTrace dispatched then TraceSaved effect emits and isCapturing becomes false`() = runTest {
        // Prepare to capture state first to ensure capturing is open
        viewModel.dispatch(HomeIntent.OpenCapture)
        
        viewModel.effect.test {
            viewModel.state.test {
                // Initial/Current state
                assertTrue(awaitItem().isCapturing)
                
                viewModel.dispatch(HomeIntent.SaveTrace("Valid trace content"))
                
                // Should show loading
                val loadingState = awaitItem()
                assertTrue(loadingState.isLoading)
                
                // Should finish loading and stop capturing
                val finalState = awaitItem()
                assertFalse(finalState.isLoading)
                assertFalse(finalState.isCapturing)
            }
            
            // The side effect should be emitted
            assertEquals(HomeEffect.TraceSaved, awaitItem())
        }
    }
}
