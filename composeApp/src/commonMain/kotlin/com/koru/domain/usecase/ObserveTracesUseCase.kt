package com.koru.domain.usecase

import com.koru.domain.model.Trace
import com.koru.domain.repository.TraceRepository
import kotlinx.coroutines.flow.Flow

/**
 * Exposes a reactive stream of all persisted [Trace] entries.
 *
 * ViewModels observe this flow to render the trace list.
 * The flow re-emits automatically when the underlying SQLDelight
 * data changes — no manual refresh needed.
 *
 * @param repository The [TraceRepository] implementation injected via Koin.
 */
class ObserveTracesUseCase(
    private val repository: TraceRepository,
) {
    /**
     * Returns a [Flow] that emits the full list of traces
     * in descending chronological order whenever the data changes.
     */
    operator fun invoke(): Flow<List<Trace>> = repository.observeAll()
}
