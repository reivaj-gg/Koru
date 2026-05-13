package com.koru.domain.repository

import com.koru.domain.model.Trace
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FakeTraceRepository : TraceRepository {
    private val _traces = MutableStateFlow<List<Trace>>(emptyList())

    override fun observeAll(): Flow<List<Trace>> = _traces

    override suspend fun save(trace: Trace): Result<String> {
        _traces.update { it + trace }
        return Result.success(trace.id)
    }

    override suspend fun search(semanticQuery: String, limit: Int): Result<List<Trace>> {
        val matches = _traces.value.filter { 
            it.content.contains(semanticQuery, ignoreCase = true) || 
            it.context?.contains(semanticQuery, ignoreCase = true) == true
        }.take(limit)
        return Result.success(matches)
    }
}
