package com.koru.data.local

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.koru.database.KoruDatabase

/**
 * Android implementation of [DatabaseDriverFactory].
 *
 * Creates a SqlDriver using [AndroidSqliteDriver].
 *
 * @property context The Android application or activity context.
 */
actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(KoruDatabase.Schema, context, "koru.db")
    }
}
