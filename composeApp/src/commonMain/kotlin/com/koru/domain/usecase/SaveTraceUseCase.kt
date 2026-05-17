package com.koru.domain.usecase

import com.koru.domain.model.EmotionTag
import com.koru.domain.model.Trace
import com.koru.domain.repository.TraceRepository

import com.koru.platform.getCurrentInstant

/**
 * Validates and persists a new [Trace] to the local store.
 *
 * This use case encapsulates the creation logic:
 * 1. Validates that [content] is not blank.
 * 2. Generates a unique ID and timestamps the trace.
 * 3. Delegates persistence to [TraceRepository].
 *
 * Downstream AI sync is not this use case's concern —
 * persistence is offline-first by design.
 *
 * @param repository The [TraceRepository] implementation injected via Koin.
 */
class SaveTraceUseCase(
    private val repository: TraceRepository,
) {

    /**
     * Creates and saves a trace with the given parameters.
     *
     * @param content The raw trace text. Must not be blank.
     * @param context Optional situational context for the trace.
     * @param emotionTag Optional emotion associated with the trace.
     * @return [Result.success] with the generated trace ID, or
     *         [Result.failure] if validation fails or persistence errors.
     */
    suspend operator fun invoke(
        content: String,
        context: String? = null,
        emotionTag: EmotionTag? = null,
    ): Result<String> {
        if (content.isBlank()) {
            return Result.failure(
                IllegalArgumentException("Trace content must not be blank"),
            )
        }

        val trace = Trace(
            id = generateId(),
            content = content.trim(),
            context = context?.trim()?.takeIf { it.isNotBlank() },
            capturedAt = getCurrentInstant(),
            emotionTag = emotionTag,
        )

        return repository.save(trace)
    }

    private fun generateId(): String =
        "trace-${getCurrentInstant().toEpochMilliseconds()}-${(1..6).map { ('a'..'z').random() }.joinToString("")}"
}
