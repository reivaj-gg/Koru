package com.koru.domain.repository

import com.koru.domain.model.Trace
import kotlinx.coroutines.flow.Flow

interface TraceRepository {
    fun observeAll(): Flow<List<Trace>>

    suspend fun save(trace: Trace): Result<String>

    suspend fun search(
        semanticQuery: String,
        limit: Int = 20,
    ): Result<List<Trace>>

    suspend fun delete(traceId: String): Result<Unit>

    suspend fun getPendingSyncs(): Result<List<Trace>>

    suspend fun markAsSynced(traceId: String): Result<Unit>
}
