@file:OptIn(kotlin.time.ExperimentalTime::class)

package com.koru.data.mapper

import com.koru.database.TraceEntity
import com.koru.domain.model.Trace
import kotlinx.datetime.Instant

/**
 * Maps the SQLDelight generated [TraceEntity] to the pure Domain model [Trace].
 * Ensures the Domain layer never knows about SQLDelight or Data-specific concepts
 * like `isSynced` or `isDeleted` (unless explicitly modeled in Domain).
 */
fun TraceEntity.toDomain(): Trace {
    return Trace(
        id = id,
        content = content,
        context = context,
        capturedAt = Instant.fromEpochMilliseconds(capturedAt),
        emotionTag = emotionTag,
    )
}
