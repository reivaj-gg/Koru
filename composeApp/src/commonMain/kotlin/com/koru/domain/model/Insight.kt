@file:OptIn(kotlin.time.ExperimentalTime::class)

package com.koru.domain.model

import kotlin.time.Instant

/**
 * An AI-generated reflection attached to one or more [Trace] entries.
 *
 * Insights are produced by the Gemini API after a local FTS5 pre-filter
 * selects the most semantically relevant traces as context. They are
 * never generated without at least one matching local trace.
 *
 * Validation rules (enforced in [init]):
 * - [id] must not be blank.
 * - [traceId] must not be blank.
 * - [content] must not be blank.
 *
 * @param id Unique identifier for this insight, format: `insight-{epochMillis}-{6-char-suffix}`.
 * @param traceId The ID of the [Trace] that triggered this insight generation.
 * @param type The [InsightType] classifying the nature of this reflection.
 * @param content The AI-generated text. Always ends with a facilitative question.
 * @param generatedAt The exact moment the insight was received from the AI service.
 *
 * @see InsightType
 * @see InsightError
 * @see com.koru.domain.usecase.GetInsightUseCase
 */
data class Insight(
    val id: String,
    val traceId: String,
    val type: InsightType,
    val content: String,
    val generatedAt: Instant,
) {
    init {
        require(id.isNotBlank()) { "Insight id must not be blank" }
        require(traceId.isNotBlank()) { "Insight traceId must not be blank" }
        require(content.isNotBlank()) { "Insight content must not be blank" }
    }
}
