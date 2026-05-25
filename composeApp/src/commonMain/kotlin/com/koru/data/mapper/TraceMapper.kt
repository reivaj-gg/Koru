@file:OptIn(kotlin.time.ExperimentalTime::class)

package com.koru.data.mapper

import com.koru.database.TraceEntity
import com.koru.domain.model.Trace
import kotlinx.datetime.Instant

/**
 * Maps a SQLDelight [TraceEntity] to a domain [Trace] model.
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
