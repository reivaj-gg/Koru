package com.koru.domain.repository

import com.koru.domain.model.Trace
import kotlinx.coroutines.flow.Flow

/**
 * Single source of truth for Trace persistence and sync.
 * All data operations for traces must flow through this interface.
 */
interface TraceRepository {
    /**
     * Observes all traces in descending chronological order.
     * Emits a new list whenever the underlying data changes.
     */
    fun observeAll(): Flow<List<Trace>>
    
    /**
     * Saves a trace locally (offline-first).
     * The AI sync happens asynchronously downstream.
     *
     * @param trace The trace to persist. [Trace.content] must not be blank.
     * @return [Result.success] containing the saved trace ID on success,
     *         or [Result.failure] with a DomainError on validation or storage failure.
     */
    suspend fun save(trace: Trace): Result<String>
    
    /**
     * Performs a local Full-Text Search on trace content.
     * Required as a pre-filter before any AI contextual insight.
     *
     * @param semanticQuery The search query to match against trace contents.
     * @param limit Maximum number of traces to return.
     * @return [Result.success] with the matching traces, or [Result.failure] on error.
     */
    suspend fun search(semanticQuery: String, limit: Int = 20): Result<List<Trace>>
}
