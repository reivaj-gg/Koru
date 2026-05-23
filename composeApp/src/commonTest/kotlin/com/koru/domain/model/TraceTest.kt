package com.koru.domain.model

import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertFailsWith

class TraceTest {
    @Test
    fun `given blank content when Trace instantiated then throws exception`() {
        assertFailsWith<IllegalArgumentException> {
            Trace(
                id = "123",
                // Blank content — must trigger IllegalArgumentException
                content = "   ",
                context = null,
                capturedAt = Instant.fromEpochMilliseconds(0),
                emotionTag = EmotionTag.CLARITY,
            )
        }
    }

    @Test
    fun `given blank id when Trace instantiated then throws exception`() {
        assertFailsWith<IllegalArgumentException> {
            Trace(
                id = "",
                content = "Valid content",
                context = null,
                capturedAt = Instant.fromEpochMilliseconds(0),
                emotionTag = null,
            )
        }
    }
}
