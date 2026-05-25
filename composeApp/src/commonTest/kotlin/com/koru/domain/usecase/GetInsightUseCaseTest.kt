package com.koru.domain.usecase

import com.koru.domain.model.Insight
import com.koru.domain.model.InsightType
import com.koru.domain.model.Trace
import com.koru.domain.repository.InsightRepository
import com.koru.domain.repository.TraceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FakeInsightRepository : InsightRepository {
    var insightToReturn: Result<Insight>? = null
    var lastTraceId: String? = null
    var lastContext: String? = null
    var lastType: InsightType? = null

    override suspend fun generateInsight(
        traceId: String,
        context: String,
        type: InsightType,
    ): Result<Insight> {
        lastTraceId = traceId
        lastContext = context
        lastType = type
        return insightToReturn ?: Result.failure(Exception("Not mocked"))
    }
}

class FakeTraceRepositoryForInsight : TraceRepository {
    var searchResult: Result<List<Trace>> = Result.success(emptyList())

    override fun observeAll(): Flow<List<Trace>> = flowOf(emptyList())

    override suspend fun save(trace: Trace): Result<String> = Result.success(trace.id)

    override suspend fun delete(traceId: String): Result<Unit> = Result.success(Unit)

    override suspend fun getPendingSyncs(): Result<List<Trace>> = Result.success(emptyList())

    override suspend fun markAsSynced(traceId: String): Result<Unit> = Result.success(Unit)

    override suspend fun search(
        semanticQuery: String,
        limit: Int,
    ): Result<List<Trace>> {
        return searchResult
    }
}

class GetInsightUseCaseTest {
    @Test
    fun given_valid_semantic_query_when_invoked_then_fetches_traces_and_calls_AI_repo() =
        runTest {
            val aiRepo = FakeInsightRepository()
            val traceRepo = FakeTraceRepositoryForInsight()
            val useCase = GetInsightUseCase(aiRepo, traceRepo)

            val fakeTrace =
                Trace(
                    id = "t1",
                    content = "I am stressed",
                    capturedAt = Instant.parse("2024-01-01T00:00:00Z"),
                )
            traceRepo.searchResult = Result.success(listOf(fakeTrace))

            val fakeInsight =
                Insight(
                    id = "i1",
                    traceId = "t1",
                    type = InsightType.IMMEDIATE,
                    content = "It seems you are stressed.",
                    generatedAt = Instant.parse("2024-01-01T00:00:00Z"),
                )
            aiRepo.insightToReturn = Result.success(fakeInsight)

            val result = useCase("t1", "I am stressed", InsightType.IMMEDIATE)

            assertTrue(result.isSuccess)
            assertEquals("i1", result.getOrNull()?.id)
            assertEquals("t1", aiRepo.lastTraceId)
            assertTrue(aiRepo.lastContext?.contains("I am stressed") == true)
        }

    @Test
    fun given_network_error_when_invoked_then_returns_failure() =
        runTest {
            val aiRepo = FakeInsightRepository()
            val traceRepo = FakeTraceRepositoryForInsight()
            val useCase = GetInsightUseCase(aiRepo, traceRepo)

            aiRepo.insightToReturn = Result.failure(Exception("Network Timeout")) // will simulate returning a mapped failure

            val result = useCase("t1", "query", InsightType.IMMEDIATE)
            assertTrue(result.isFailure)
        }
}
