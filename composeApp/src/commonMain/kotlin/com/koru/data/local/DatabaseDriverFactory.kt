package com.koru.data.local

import app.cash.sqldelight.db.SqlDriver

/**
 * Platform-specific factory for providing the SQLDelight driver.
 * Uses `AndroidSqliteDriver` on Android and `NativeSqliteDriver` on iOS.
 */
expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}
