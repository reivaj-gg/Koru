package com.koru.domain.model

import com.koru.platform.getCurrentInstant
import kotlin.test.Test
import kotlin.test.assertFailsWith

class TraceTest {

    @Test
    fun `given blank content when Trace instantiated then throws exception`() {
        assertFailsWith<IllegalArgumentException> {
            Trace(
                id = "123",
                content = "   ", // Blank content
                context = null,
                capturedAt = getCurrentInstant(),
                emotionTag = EmotionTag.CLARITY
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
                capturedAt = getCurrentInstant(),
                emotionTag = null
            )
        }
    }
}
