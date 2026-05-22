@file:OptIn(kotlin.time.ExperimentalTime::class)

package com.koru.domain.usecase

import com.koru.domain.model.EmotionTag
import com.koru.domain.model.Trace
import com.koru.domain.repository.TraceRepository
import kotlin.time.Clock

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

        val now = Clock.System.now()

        val trace =
            Trace(
                id = generateId(now.toEpochMilliseconds()),
                content = content.trim(),
                context = context?.trim()?.takeIf { it.isNotBlank() },
                capturedAt = now,
                emotionTag = emotionTag,
            )

        return repository.save(trace)
    }

    /**
     * Generates a unique identifier for a new trace.
     *
     * Format: `trace-{timestamp}-{random_suffix}`
     * The random suffix ensures uniqueness even if multiple traces are captured
     * within the same millisecond (e.g., during a fast sync or batch import).
     *
     * @param nowMillis The current epoch timestamp.
     * @return A unique string ID.
     */
    private fun generateId(nowMillis: Long): String {
        val randomSuffix = (1..6).map { ('a'..'z').random() }.joinToString("")
        return "trace-$nowMillis-$randomSuffix"
    }
}
