package com.koru.data.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.koru.database.KoruDatabase

/**
 * iOS implementation of [DatabaseDriverFactory].
 *
 * Creates a SqlDriver using [NativeSqliteDriver].
 */
actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(KoruDatabase.Schema, "koru.db")
    }
}
