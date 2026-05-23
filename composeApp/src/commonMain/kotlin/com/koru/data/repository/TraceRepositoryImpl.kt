package com.koru.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.koru.data.mapper.toDomain
import com.koru.database.KoruDatabase
import com.koru.domain.model.Trace
import com.koru.domain.repository.TraceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TraceRepositoryImpl(
    private val database: KoruDatabase,
) : TraceRepository {
    private val queries = database.traceQueries

    override fun observeAll(): Flow<List<Trace>> {
        return queries.observeAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities ->
                entities.map { it.toDomain() }
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
                updatedAt = com.koru.domain.utils.currentEpochMillis(),
                isSynced = false,
                isDeleted = false,
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
            ).executeAsList().map { it.toDomain() }
        }
    }

    override suspend fun delete(traceId: String): Result<Unit> {
        return runCatching {
            queries.markAsDeleted(
                updatedAt = com.koru.domain.utils.currentEpochMillis(),
                id = traceId,
            )
        }
    }

    override suspend fun getPendingSyncs(): Result<List<Trace>> {
        return runCatching {
            queries.getPendingSync().executeAsList().map { it.toDomain() }
        }
    }

    override suspend fun markAsSynced(traceId: String): Result<Unit> {
        return runCatching {
            queries.markAsSynced(id = traceId)
        }
    }
}
