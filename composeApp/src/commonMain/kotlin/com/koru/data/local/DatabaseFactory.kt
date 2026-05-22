package com.koru.data.local

import app.cash.sqldelight.ColumnAdapter
import com.koru.database.KoruDatabase
import com.koru.database.TraceEntity
import com.koru.domain.model.EmotionTag

/**
 * SQLDelight [ColumnAdapter] for [EmotionTag].
 *
 * Stores the enum as its [name] string and decodes it back via [EmotionTag.valueOf].
 * Null values in the database remain null — [EmotionTag] is optional on [TraceEntity].
 */
val emotionTagAdapter: ColumnAdapter<EmotionTag, String> =
    object : ColumnAdapter<EmotionTag, String> {
        override fun decode(databaseValue: String): EmotionTag = EmotionTag.valueOf(databaseValue)

        override fun encode(value: EmotionTag): String = value.name
    }

/**
 * Creates a fully configured [KoruDatabase] instance with all required adapters.
 *
 * This is the single place where [TraceEntity.Adapter] is instantiated.
 * All Koin modules and test setup must use this factory to guarantee
 * consistent adapter configuration.
 *
 * @param factory The platform-specific [DatabaseDriverFactory] that provides the [SqlDriver].
 * @return A ready-to-use [KoruDatabase] singleton.
 */
fun createKoruDatabase(factory: DatabaseDriverFactory): KoruDatabase =
    KoruDatabase(
        driver = factory.createDriver(),
        TraceEntityAdapter = TraceEntity.Adapter(emotionTagAdapter = emotionTagAdapter),
    )
