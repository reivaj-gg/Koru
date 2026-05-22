@file:OptIn(kotlin.time.ExperimentalTime::class)

package com.koru.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.koru.database.KoruDatabase
import com.koru.domain.model.Trace
import com.koru.domain.repository.TraceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Instant

/**
 * SQLDelight-backed implementation of [TraceRepository].
 *
 * Persists traces to a local SQLite database with FTS5 full-text search.
 * All mapping between domain [Trace] and the generated `TraceEntity`
 * happens exclusively in this class — no SQLDelight types leak upstream.
 *
 * @param database The [KoruDatabase] instance provided via Koin DI.
 */
class TraceRepositoryImpl(
    private val database: KoruDatabase,
) : TraceRepository {
    /**
     * Generated SQLDelight queries for the Trace table.
     */
    private val queries = database.traceQueries

    override fun observeAll(): Flow<List<Trace>> {
        return queries.observeAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities ->
                entities.map { entity ->
                    Trace(
                        id = entity.id,
                        content = entity.content,
                        context = entity.context,
                        capturedAt = Instant.fromEpochMilliseconds(entity.capturedAt),
                        emotionTag = entity.emotionTag,
                    )
                }
            }
    }

    override suspend fun save(trace: Trace): Result<String> {
        return runCatching {
            queries.insertTrace(
                id = trace.id,
                content = trace.content,
                context = trace.context,
                capturedAt = trace.capturedAt.toEpochMilliseconds(),
                emotionTag = trace.emotionTag,
            )
            trace.id
        }
    }

    override suspend fun search(
        semanticQuery: String,
        limit: Int,
    ): Result<List<Trace>> {
        return runCatching {
            queries.searchFts(
                query = semanticQuery,
                limit = limit.toLong(),
            ).executeAsList().map { entity ->
                Trace(
                    id = entity.id,
                    content = entity.content,
                    context = entity.context,
                    capturedAt = Instant.fromEpochMilliseconds(entity.capturedAt),
                    emotionTag = entity.emotionTag,
                )
            }
        }
    }
}
