package com.koru.domain.usecase

import com.koru.domain.model.EmotionTag
import com.koru.domain.model.Trace
import com.koru.domain.repository.FakeTraceRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.time.Clock
import kotlin.test.Test
import kotlin.test.assertEquals

class ObserveTracesUseCaseTest {

    private val repository = FakeTraceRepository()
    private val observeTracesUseCase = ObserveTracesUseCase(repository)

    @Test
    fun `given no traces when invoked then emits empty list`() = runTest {
        val traces = observeTracesUseCase().first()
        assertEquals(0, traces.size)
    }

    @Test
    fun `given saved traces when invoked then emits all traces`() = runTest {
        val trace1 = Trace(
            id = "t1",
            content = "First trace",
            context = null,
            capturedAt = Clock.System.now(),
            emotionTag = EmotionTag.CLARITY,
        )
        val trace2 = Trace(
            id = "t2",
            content = "Second trace",
            context = "context",
            capturedAt = Clock.System.now(),
            emotionTag = EmotionTag.RESISTANCE,
        )

        repository.save(trace1)
        repository.save(trace2)

        val traces = observeTracesUseCase().first()
        assertEquals(2, traces.size)
    }
}
