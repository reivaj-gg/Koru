package com.koru.data.local

import app.cash.sqldelight.ColumnAdapter
import com.koru.database.KoruDatabase
import com.koru.database.TraceEntity
import com.koru.domain.model.EmotionTag

/**
 * Adapter for storing and retrieving [EmotionTag] enum values in SQLDelight.
 *
 * Maps the enum to a string in the database and parses it back during read operations.
 */
val emotionTagAdapter: ColumnAdapter<EmotionTag, String> =
    object : ColumnAdapter<EmotionTag, String> {
        override fun decode(databaseValue: String): EmotionTag = EmotionTag.valueOf(databaseValue)

        override fun encode(value: EmotionTag): String = value.name
    }

/**
 * Adapter for boolean values since SQLite uses integers internally (0 and 1).
 */
val booleanAdapter: ColumnAdapter<Boolean, Long> =
    object : ColumnAdapter<Boolean, Long> {
        override fun decode(databaseValue: Long): Boolean = databaseValue == 1L

        override fun encode(value: Boolean): Long = if (value) 1L else 0L
    }

/**
 * Creates the multiplatform [KoruDatabase] by injecting the required driver and adapters.
 *
 * @param factory Platform-specific factory capable of creating a SqlDriver.
 * @return the instantiated [KoruDatabase].
 */
fun createKoruDatabase(factory: DatabaseDriverFactory): KoruDatabase =
    KoruDatabase(
        driver = factory.createDriver(),
        TraceEntityAdapter =
            TraceEntity.Adapter(
                emotionTagAdapter = emotionTagAdapter,
            ),
    )
