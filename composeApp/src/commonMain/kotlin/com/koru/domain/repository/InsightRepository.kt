package com.koru.domain.repository

import com.koru.domain.model.Insight
import com.koru.domain.model.InsightType

/**
 * Repository interface for interacting with the AI service to generate [Insight]s.
 *
 * This acts as the boundary between the domain layer and the network layer (Ktor/Gemini).
 */
interface InsightRepository {
    /**
     * Generates a new insight based on a user's trace and context.
     *
     * @param traceId The ID of the trace that triggered the insight generation.
     * @param context A formatted string containing the user's historical traces (e.g. from FTS5).
     * @param type The type of insight to request from the AI.
     * @return A [Result.success] with the generated [Insight], or a [Result.failure] wrapping an [InsightError].
     */
    suspend fun generateInsight(
        traceId: String,
        context: String,
        type: InsightType,
    ): Result<Insight>
}
