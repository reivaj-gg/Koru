package com.koru.domain.model

import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertFailsWith

class TraceTest {
    @Test
    fun given_blank_content_when_Trace_instantiated_then_throws_exception() {
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
    fun given_blank_id_when_Trace_instantiated_then_throws_exception() {
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
