package com.koru.data.repository

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.koru.data.local.emotionTagAdapter
import com.koru.database.KoruDatabase
import com.koru.database.TraceEntity
import com.koru.domain.model.EmotionTag
import com.koru.domain.model.Trace
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TraceRepositoryImplTest {
    private lateinit var database: KoruDatabase
    private lateinit var repository: TraceRepositoryImpl

    @Before
    fun setup() {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        KoruDatabase.Schema.create(driver)

        database =
            KoruDatabase(
                driver = driver,
                TraceEntityAdapter =
                    TraceEntity.Adapter(
                        emotionTagAdapter = emotionTagAdapter,
                    ),
            )
        repository = TraceRepositoryImpl(database)
    }

    @Test
    fun `given new trace, when save called, then it is inserted and observable and pending sync`() =
        runTest {
            val trace =
                Trace(
                    id = "t1",
                    content = "This is a test trace",
                    context = "testing",
                    capturedAt = Clock.System.now(),
                    emotionTag = EmotionTag.CLARITY,
                )

            val result = repository.save(trace)
            assertTrue(result.isSuccess)

            val traces = repository.observeAll().first()
            assertEquals(1, traces.size)
            assertEquals("t1", traces[0].id)

            val pendingSyncs = repository.getPendingSyncs().getOrThrow()
            assertEquals(1, pendingSyncs.size)
        }

    @Test
    fun `given saved trace, when searchFts called, then matches content`() =
        runTest {
            val trace =
                Trace(
                    id = "t2",
                    content = "Apples are delicious and red",
                    context = "food",
                    capturedAt = Clock.System.now(),
                    emotionTag = EmotionTag.SURPRISE,
                )
            repository.save(trace)

            val searchResult = repository.search("delicious")
            assertTrue(searchResult.isSuccess)
            assertEquals(1, searchResult.getOrThrow().size)
            assertEquals("t2", searchResult.getOrThrow()[0].id)
        }

    @Test
    fun `given saved trace, when deleted, then it is filtered from observeAll and searchFts`() =
        runTest {
            val trace =
                Trace(
                    id = "t3",
                    content = "This should be deleted",
                    context = "food",
                    capturedAt = Clock.System.now(),
                    emotionTag = null,
                )
            repository.save(trace)

            repository.delete("t3").getOrThrow()

            val traces = repository.observeAll().first()
            assertTrue(traces.isEmpty())

            val searchResult = repository.search("deleted")
            assertTrue(searchResult.getOrThrow().isEmpty())
        }

    @Test
    fun `given synced trace, when markAsSynced, then it is removed from pendingSyncs`() =
        runTest {
            val trace = Trace("t4", "Sync test", null, Clock.System.now(), null)
            repository.save(trace)

            repository.markAsSynced("t4").getOrThrow()

            val pendingSyncs = repository.getPendingSyncs().getOrThrow()
            assertTrue(pendingSyncs.isEmpty())
        }
}
