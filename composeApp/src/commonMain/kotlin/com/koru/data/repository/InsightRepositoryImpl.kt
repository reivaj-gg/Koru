package com.koru.data.repository

import com.koru.data.remote.GeminiClient
import com.koru.domain.model.Insight
import com.koru.domain.model.InsightType
import com.koru.domain.repository.InsightRepository
import com.koru.domain.utils.currentEpochMillis
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.datetime.Instant

/**
 * Implementation of [InsightRepository] that uses [GeminiClient] to generate insights.
 */
class InsightRepositoryImpl(
    private val geminiClient: GeminiClient,
) : InsightRepository {
    override suspend fun generateInsight(
        traceId: String,
        context: String,
        type: InsightType,
    ): Result<Insight> {
        return geminiClient.generateInsight(context, type).fold(
            onSuccess = { generatedText ->
                // Split the AI response into content and facilitative question.
                // For this contest structure, we assume the AI follows the system prompt
                // and the last sentence (ending in '?') is the question.
                val parts = splitContentAndQuestion(generatedText)
                val insight =
                    Insight(
                        id = "insight-${currentEpochMillis()}",
                        traceId = traceId,
                        type = type,
                        content = parts.first,
                        generatedAt = Instant.fromEpochMilliseconds(currentEpochMillis()),
                    )
                Result.success(insight)
            },
            onFailure = { error ->
                when (error) {
                    is HttpRequestTimeoutException ->
                        Result.failure(
                            Exception("Timeout", error),
                        ) // Maps to InsightError.Timeout later if needed
                    // Map generic exceptions here
                    else -> Result.failure(error)
                }
            },
        )
    }

    private fun splitContentAndQuestion(text: String): Pair<String, String> {
        val trimmed = text.trim()
        val lastQuestionMark = trimmed.lastIndexOf('?')
        if (lastQuestionMark == -1) {
            return Pair(trimmed, "What are your thoughts on this?") // Fallback
        }

        // Find the start of the last sentence
        var startOfQuestion = trimmed.lastIndexOf(". ", lastQuestionMark)
        if (startOfQuestion == -1) {
            startOfQuestion = trimmed.lastIndexOf("\n", lastQuestionMark)
        }

        if (startOfQuestion == -1 || startOfQuestion >= lastQuestionMark) {
            return Pair("Here is an observation.", trimmed)
        }

        val content = trimmed.substring(0, startOfQuestion + 1).trim()
        val question = trimmed.substring(startOfQuestion + 1).trim()

        return Pair(content, question)
    }
}
