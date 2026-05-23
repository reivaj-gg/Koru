package com.koru.domain.repository

import com.koru.domain.model.Trace
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FakeTraceRepository : TraceRepository {
    private val tracesFlow = MutableStateFlow<List<Trace>>(emptyList())

    override fun observeAll(): Flow<List<Trace>> = tracesFlow

    override suspend fun save(trace: Trace): Result<String> {
        tracesFlow.update { it + trace }
        return Result.success(trace.id)
    }

    override suspend fun search(
        semanticQuery: String,
        limit: Int,
    ): Result<List<Trace>> {
        val matches =
            tracesFlow.value.filter {
                it.content.contains(semanticQuery, ignoreCase = true) ||
                    it.context?.contains(semanticQuery, ignoreCase = true) == true
            }.take(limit)
        return Result.success(matches)
    }

    override suspend fun delete(traceId: String): Result<Unit> {
        tracesFlow.update { it.filterNot { trace -> trace.id == traceId } }
        return Result.success(Unit)
    }

    override suspend fun getPendingSyncs(): Result<List<Trace>> {
        return Result.success(emptyList()) // Simple stub for tests
    }

    override suspend fun markAsSynced(traceId: String): Result<Unit> {
        return Result.success(Unit) // Simple stub for tests
    }
}
