package com.koru.data.local

import app.cash.sqldelight.ColumnAdapter
import com.koru.database.KoruDatabase
import com.koru.database.TraceEntity
import com.koru.domain.model.EmotionTag

val emotionTagAdapter: ColumnAdapter<EmotionTag, String> =
    object : ColumnAdapter<EmotionTag, String> {
        override fun decode(databaseValue: String): EmotionTag = EmotionTag.valueOf(databaseValue)

        override fun encode(value: EmotionTag): String = value.name
    }

val booleanAdapter: ColumnAdapter<Boolean, Long> =
    object : ColumnAdapter<Boolean, Long> {
        override fun decode(databaseValue: Long): Boolean = databaseValue == 1L

        override fun encode(value: Boolean): Long = if (value) 1L else 0L
    }

fun createKoruDatabase(factory: DatabaseDriverFactory): KoruDatabase =
    KoruDatabase(
        driver = factory.createDriver(),
        TraceEntityAdapter =
            TraceEntity.Adapter(
                emotionTagAdapter = emotionTagAdapter,
            ),
    )
