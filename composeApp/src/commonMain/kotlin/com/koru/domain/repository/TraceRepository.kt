package com.koru.domain.repository

import com.koru.domain.model.Trace
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing user [Trace] records.
 *
 * Provides operations to read, write, and synchronize traces following an offline-first strategy.
 * Implementations should ensure that local storage (e.g., SQLDelight) is the single source of truth.
 */
interface TraceRepository {
    /**
     * Observes all traces from the local database as a reactive stream.
     *
     * @return A [Flow] emitting the complete list of [Trace] objects whenever the data changes.
     */
    fun observeAll(): Flow<List<Trace>>

    /**
     * Persists a single trace to the local database.
     *
     * @param trace The trace entity to save.
     * @return A [Result.success] containing the trace ID, or [Result.failure] on error.
     */
    suspend fun save(trace: Trace): Result<String>

    /**
     * Performs a full-text search across trace contents.
     *
     * @param semanticQuery The search terms to match against trace content.
     * @param limit The maximum number of results to return (defaults to 20).
     * @return A [Result.success] containing matching [Trace]s, or [Result.failure] on error.
     */
    suspend fun search(
        semanticQuery: String,
        limit: Int = 20,
    ): Result<List<Trace>>

    /**
     * Marks a trace as deleted logically without removing it from the database entirely,
     * allowing for future synchronization of the deletion.
     *
     * @param traceId The unique identifier of the trace to delete.
     * @return A [Result.success] on completion, or [Result.failure] on error.
     */
    suspend fun delete(traceId: String): Result<Unit>

    /**
     * Retrieves all traces that have not yet been synchronized with the remote server.
     *
     * @return A [Result.success] with the pending [Trace]s, or [Result.failure] on error.
     */
    suspend fun getPendingSyncs(): Result<List<Trace>>

    /**
     * Marks a specific trace as successfully synchronized.
     *
     * @param traceId The unique identifier of the synchronized trace.
     * @return A [Result.success] on completion, or [Result.failure] on error.
     */
    suspend fun markAsSynced(traceId: String): Result<Unit>
}
