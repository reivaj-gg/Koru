package com.koru.presentation.viewmodel

import app.cash.turbine.test
import com.koru.domain.model.EmotionTag
import com.koru.domain.model.Trace
import com.koru.domain.repository.TraceRepository
import com.koru.presentation.utils.TreeLayoutCalculator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.time.Instant

private class FakeTraceRepository(
    private val traces: List<Trace> = emptyList(),
    private val shouldFail: Boolean = false,
) : TraceRepository {
    override fun observeAll(): Flow<List<Trace>> =
        if (shouldFail) {
            flow { throw RuntimeException("Database error") }
        } else {
            flowOf(traces)
        }

    override suspend fun save(trace: Trace): Result<String> = Result.success(trace.id)

    override suspend fun search(
        semanticQuery: String,
        limit: Int,
    ): Result<List<Trace>> = Result.success(emptyList())
}

class HomeViewModelTest {
    @Test
    fun given_initial_state_when_LoadTree_then_emits_loading_and_traces() =
        runTest {
            val fakeTraces =
                listOf(
                    Trace("1", "Hello", null, Instant.fromEpochMilliseconds(0), EmotionTag.CLARITY),
                )
            val vm = HomeViewModel(FakeTraceRepository(fakeTraces), TreeLayoutCalculator())

            vm.state.test {
                // Estado inicial
                val initial = awaitItem()
                assertFalse(initial.isLoading)
                assertTrue(initial.nodes.isEmpty())

                vm.handleIntent(HomeIntent.LoadTree)

                // Estado Loading
                val loading = awaitItem()
                assertTrue(loading.isLoading)

                // Estado Final con los nodos calculados
                val finalState = awaitItem()
                assertFalse(finalState.isLoading)
                assertEquals(1, finalState.nodes.size)
                assertEquals("1", finalState.nodes.first().traceId)
            }
        }

    @Test
    fun given_db_error_when_LoadTree_then_emits_error() =
        runTest {
            val vm = HomeViewModel(FakeTraceRepository(shouldFail = true), TreeLayoutCalculator())

            vm.effects.test {
                vm.handleIntent(HomeIntent.LoadTree)
                val effect = awaitItem()
                assertIs<HomeEffect.ShowError>(effect)
                assertEquals("Database error", effect.message)
            }

            assertEquals("Database error", vm.state.value.error)
            assertFalse(vm.state.value.isLoading)
        }

    @Test
    fun given_TapNode_then_NavigateToTraceDetail_effect_emitted() =
        runTest {
            val vm = HomeViewModel(FakeTraceRepository(), TreeLayoutCalculator())

            vm.effects.test {
                vm.handleIntent(HomeIntent.TapNode("trace-123"))

                val effect = awaitItem()
                assertIs<HomeEffect.NavigateToTraceDetail>(effect)
                assertEquals("trace-123", effect.traceId)
            }
        }
}
