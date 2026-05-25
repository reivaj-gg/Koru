@file:OptIn(kotlin.time.ExperimentalTime::class)

package com.koru.domain.model

import kotlinx.datetime.Instant

/**
 * Represents an emotional or cognitive tag attached to a trace.
 */
enum class EmotionTag {
    /** Feeling of tension or stress. */
    TENSION,

    /** Feeling of surprise. */
    SURPRISE,

    /** Feeling of clarity or understanding. */
    CLARITY,

    /** Feeling of resistance or reluctance. */
    RESISTANCE,

    /** Feeling of gratitude. */
    GRATITUDE,
}

/**
 * A trace is a quick record of a user's reaction or interpretation of an event.
 * It is the atomic unit of the Koru application.
 *
 * @param id Unique identifier for the trace.
 * @param content The transcribed text or raw text input of the reaction.
 * @param context Optional free-text tag defining the context (e.g., "work", "family").
 * @param capturedAt The exact moment the trace was captured.
 * @param emotionTag An optional emotional classification for the trace.
 */
data class Trace(
    val id: String,
    val content: String,
    val context: String? = null,
    val capturedAt: Instant,
    val emotionTag: EmotionTag? = null,
) {
    init {
        require(id.isNotBlank()) { "Trace id must not be blank" }
        require(content.isNotBlank()) { "Trace content must not be blank" }
    }
}
