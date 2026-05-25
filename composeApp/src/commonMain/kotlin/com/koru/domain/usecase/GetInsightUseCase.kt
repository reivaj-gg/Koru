package com.koru.domain.usecase

import com.koru.domain.model.Insight
import com.koru.domain.model.InsightType
import com.koru.domain.repository.InsightRepository
import com.koru.domain.repository.TraceRepository

/**
 * Use case for generating an AI insight based on recent traces.
 *
 * Implements the offline-first strategy by retrieving recent traces locally
 * before assembling the prompt context for the AI service.
 */
class GetInsightUseCase(
    private val insightRepository: InsightRepository,
    private val traceRepository: TraceRepository,
) {
    /**
     * Executes the use case.
     *
     * @param traceId The ID of the trace triggering this insight.
     * @param query The semantic query to filter traces via FTS5.
     * @param type The type of insight to request.
     * @return A [Result] wrapping the generated [Insight] or an error.
     */
    suspend operator fun invoke(
        traceId: String,
        query: String,
        type: InsightType,
    ): Result<Insight> {
        return try {
            val tracesResult = traceRepository.search(query, limit = 20)
            if (tracesResult.isFailure) {
                return Result.failure(tracesResult.exceptionOrNull() ?: Exception("Failed to load local traces"))
            }

            val traces = tracesResult.getOrNull().orEmpty()

            // Build structured context string as required by AGENTS.md
            val context =
                traces.joinToString("\n---\n") { entity ->
                    "Date: ${entity.capturedAt} | Emotion: ${entity.emotionTag}\nContent: ${entity.content}"
                }

            insightRepository.generateInsight(
                traceId = traceId,
                context = context,
                type = type,
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
