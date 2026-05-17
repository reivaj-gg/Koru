package com.koru.domain.usecase

import com.koru.domain.model.EmotionTag
import com.koru.domain.repository.FakeTraceRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class SaveTraceUseCaseTest {

    private val repository = FakeTraceRepository()
    private val saveTraceUseCase = SaveTraceUseCase(repository)

    @Test
    fun `given valid content, when invoked, then trace is saved and result is success`() = runTest {
        val result = saveTraceUseCase(
            content = "This is a meaningful trace",
            context = null,
            emotionTag = EmotionTag.CLARITY,
        )

        assertTrue(result.isSuccess)
        val savedId = result.getOrThrow()
        assertNotNull(savedId)

        val traces = repository.observeAll().first()
        assertEquals(1, traces.size)
        assertEquals("This is a meaningful trace", traces[0].content)
        assertEquals(EmotionTag.CLARITY, traces[0].emotionTag)
    }

    @Test
    fun `given blank content, when invoked, then returns failure`() = runTest {
        val result = saveTraceUseCase(
            content = "   ",
            context = null,
            emotionTag = EmotionTag.TENSION,
        )

        assertTrue(result.isFailure)
        val traces = repository.observeAll().first()
        assertEquals(0, traces.size)
    }
}
