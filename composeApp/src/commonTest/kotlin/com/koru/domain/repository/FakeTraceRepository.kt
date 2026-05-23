package com.koru.domain.repository

import com.koru.domain.model.Trace
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

/**
 * A fake implementation of [TraceRepository] for use in unit tests.
 * Maintains state in memory using a MutableStateFlow.
 */
class FakeTraceRepository : TraceRepository {
    private val tracesFlow = MutableStateFlow<List<Trace>>(emptyList())

    /**
     * Observes all traces from the in-memory state.
     */
    override fun observeAll(): Flow<List<Trace>> = tracesFlow

    /**
     * Saves a trace to the in-memory state.
     */
    override suspend fun save(trace: Trace): Result<String> {
        tracesFlow.update { it + trace }
        return Result.success(trace.id)
    }

    /**
     * Searches for a trace in the in-memory state by semantic query.
     */
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

    /**
     * Deletes a trace from the in-memory state.
     */
    override suspend fun delete(traceId: String): Result<Unit> {
        tracesFlow.update { it.filterNot { trace -> trace.id == traceId } }
        return Result.success(Unit)
    }

    /**
     * Stub for getPendingSyncs.
     */
    override suspend fun getPendingSyncs(): Result<List<Trace>> {
        return Result.success(emptyList()) // Simple stub for tests
    }

    /**
     * Stub for markAsSynced.
     */
    override suspend fun markAsSynced(traceId: String): Result<Unit> {
        return Result.success(Unit) // Simple stub for tests
    }
}
